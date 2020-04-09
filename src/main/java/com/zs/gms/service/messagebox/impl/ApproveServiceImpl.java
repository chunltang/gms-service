package com.zs.gms.service.messagebox.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.Assert;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.messagebox.ApproveProcess;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.messagebox.ApproveType;
import com.zs.gms.mapper.messagebox.ApproveMapper;
import com.zs.gms.service.messagebox.ApproveInterface;
import com.zs.gms.service.messagebox.ApproveService;
import com.zs.gms.service.system.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class ApproveServiceImpl extends ServiceImpl<ApproveMapper, Approve> implements ApproveService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Integer addApprove(Approve approve) {
        approve.setCreateTime(new Date());
        approve.setStatus(Approve.Status.WAIT);
        this.save(approve);
        return approve.getApproveId();
    }

    @Override
    @Transactional
    public Integer createApprove(Map<String,Object> params, String userIds, User user,ApproveType approveType,boolean rule) {
        if(!ObjectUtils.allNotNull(userIds,user,approveType,rule)){
            log.debug("创建审批参数异常");
            return null;
        }
        Approve approve = new Approve();
        approve.setParams(params);
        approve.setApproveUserIds(userIds);
        approve.setRule(rule);
        approve.setSubmitUserId(user.getUserId());
        approve.setSubmitUserName(user.getUserName());
        approve.setApproveType(approveType);
        approve.setApproveDesc(approveType.getDesc());
        Integer integer = addApprove(approve);
        if(null!=integer){
            sendMessage(approve);
        }
        return integer;
    }

    @Override
    @Transactional
    public Approve getApprove(Integer approveId) {
        return this.getById(approveId);
    }

    /**
     * 审批创建成功，推送消息给提交人和审批人
     * */
    public void sendMessage(Approve approve){
        Assert.notNull(approve,"approve不能为空");
        String userIds = approve.getApproveUserIds();
        //发给提交人
        WsUtil.sendMessage(String.valueOf(approve.getSubmitUserId()), GmsUtil.toJson(approve), FunctionEnum.approve);
        //发给审批人
        if(StringUtils.isNotEmpty(userIds)){
            String[] ids = userIds.split(StringPool.COMMA);
            for (String id : ids) {
                WsUtil.sendMessage(id, GmsUtil.toJson(approve), FunctionEnum.approve);
            }
        }
    }

    @Override
    @Transactional
    public boolean deleteApprove(Integer approveId, Approve.Status status) {
        if(status.equals(Approve.Status.DELETE)){
            LambdaUpdateWrapper<Approve> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Approve::getApproveId,approveId);
            updateWrapper.set(Approve::isApproveMark,true);
            updateWrapper.set(Approve::getStatus,status);
            this.cancel(this.getApprove(approveId));
            return this.update(updateWrapper);
        }
        return false;
    }

    @Override
    @Transactional
    public Approve updateProcess(Integer approveId,String userId, Approve.Status status,String reason) {
        Approve approve = this.getById(approveId);
        if(approve==null||!approve.getStatus().equals(Approve.Status.WAIT)){
            log.debug("不是等待审批状态不可修改");
            return null;
        }
        if(approve!=null && (status.equals(Approve.Status.APPROVEPASS)||status.equals(Approve.Status.APPROVEREJECT))){
            List<ApproveProcess> processList = approve.getApproveProcess();
            User user = userService.findUserById(Integer.valueOf(userId));
            ApproveProcess approveProcess=new ApproveProcess();
            approveProcess.setUserId(userId);
            approveProcess.setUserName(user.getUserName());
            approveProcess.setRoleName(user.getRoleName());
            approveProcess.setStatus(status);
            approveProcess.setSuggestion(reason);
            approveProcess.setApproveTime(new Date());
            if(CollectionUtils.isEmpty(processList)){
                processList=new ArrayList<>();
                processList.add(approveProcess);
            }else{
                boolean exist=false;
                for (ApproveProcess process : processList) {
                    if(process.getUserId().equals(userId)){
                        exist=true;
                    }
                }
                if(!exist){
                    processList.add(approveProcess);
                }else{
                    return null;
                }
            }

            boolean rule = approve.isRule();
            int sucLen=0;
            int failLen=0;
            for (ApproveProcess process : processList) {
                if(process.getStatus().equals(Approve.Status.APPROVEPASS)){
                    if(!rule){//有人通过
                        approve.setStatus(Approve.Status.APPROVEPASS);
                        approve.setApproveTime(new Date());
                        break;
                    }else{
                        sucLen++;
                    }
                }else if(process.getStatus().equals(Approve.Status.APPROVEREJECT)){
                    if(!rule){//有人驳回
                        approve.setStatus(Approve.Status.APPROVEREJECT);
                        approve.setApproveTime(new Date());
                        break;
                    }else{
                        failLen++;
                    }
                }
            }
            if(rule){//需要审批人都通过或驳回才改变状态
                String userIds = approve.getApproveUserIds();
                if(StringUtils.isNotEmpty(userIds)){
                    String[] split = userIds.split(StringPool.COMMA);
                    if(!Arrays.asList(split).contains(userId)){
                        log.debug("非审批人员");
                        return null;
                    }
                    if(split.length==sucLen){
                        approve.setStatus(Approve.Status.APPROVEPASS);
                        approve.setApproveTime(new Date());
                    }else if(split.length==failLen){
                        approve.setStatus(Approve.Status.APPROVEREJECT);
                        approve.setApproveTime(new Date());
                    }
                }
            }
            approve.setApproveProcess(processList);
            this.updateById(approve);
        }
        return approve;
    }

    @Override
    @Transactional
    public void sendApproveResult(Approve approve) {
        if(approve.getStatus().equals(Approve.Status.WAIT)){
           return;
        }
        updateStatus(approve);
    }

    @Override
    @Transactional
    public void cancel(Approve approve) {
        if(approve.getStatus().equals(Approve.Status.WAIT)){
            ApproveType approveType = approve.getApproveType();
            if(approveType!=null){
                ApproveInterface approveInterface=SpringContextUtil.getBean(approveType.getHandler());
                approveInterface.cancel(approve);
            }
        }
    }

    /**
     * 根据审批类型进行状态改变
     * */
    @Transactional
    public void updateStatus(Approve approve){
         ApproveType approveType = approve.getApproveType();
         if(approveType!=null){
             ApproveInterface approveInterface=SpringContextUtil.getBean(approveType.getHandler());
             approveInterface.updateStatus(approve);
         }
    }

    @Override
    @Transactional
    public List<Approve> getApproveRemaining(String userId) {
        List<Approve> result=new ArrayList<>();
        LambdaQueryWrapper<Approve> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Approve::getStatus,Approve.Status.WAIT);
        queryWrapper.eq(Approve::isApproveMark,false);
        List<Approve> approves = this.list(queryWrapper);
        if(CollectionUtils.isNotEmpty(approves)){
            for (Approve approve : approves) {
                boolean exist=false;//false表示该用户没有待审批处理
                String ids = approve.getApproveUserIds();
                if(StringUtils.isNotEmpty(ids)){
                    String[] split = ids.split(StringPool.COMMA);
                    if(Arrays.asList(split).contains(userId)){
                        List<ApproveProcess> process = approve.getApproveProcess();
                        if(CollectionUtils.isNotEmpty(process)){
                            boolean flag=true;
                            for (ApproveProcess approveProcess : process) {
                                if(approveProcess.getUserId().equals(userId)){
                                    flag=false;
                                    break;
                                }
                            }
                            exist=flag;
                        }else{
                            exist=true;
                        }
                    }
                }
                if(exist){
                    result.add(approve);
                }
            }
        }
        return result;
    }

    @Override
    public List<Approve> getApproveNoMark(String userId) {
        LambdaQueryWrapper<Approve> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Approve::getSubmitUserId,userId);
        queryWrapper.eq(Approve::isApproveMark,false);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public void updateApproveResult(Integer approveId) {
        LambdaUpdateWrapper<Approve> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Approve::getApproveId,approveId);
        updateWrapper.set(Approve::isApproveMark,true);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public void addError(Integer approveId, String error) {
        LambdaUpdateWrapper<Approve> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Approve::getApproveId,approveId);
        updateWrapper.set(Approve::getStatus,Approve.Status.APPROVEERROR);
        updateWrapper.set(Approve::getApproveErrorDesc,error);
        this.update(updateWrapper);
    }
}
