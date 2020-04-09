package com.zs.gms.service.mineralmanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.messagebox.ApproveProcess;
import com.zs.gms.entity.mineralmanager.AreaMineral;
import com.zs.gms.entity.mineralmanager.Mineral;
import com.zs.gms.mapper.mineralmanager.AreaMineralMapper;
import com.zs.gms.service.messagebox.ApproveInterface;
import com.zs.gms.service.messagebox.ApproveUtil;
import com.zs.gms.service.mineralmanager.AreaMineralService;
import com.zs.gms.service.mineralmanager.MineralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class AreaMineralServiceImpl extends ServiceImpl<AreaMineralMapper, AreaMineral> implements ApproveInterface, AreaMineralService {


    @Autowired
    private MineralService mineralService;

    @Autowired
    private AreaMineralService areaMineralService;

    /**
     * 添加对应关系
     */
    @Override
    @Transactional
    public void addAreaMineral(AreaMineral areaMineral) {
        Mineral mineral = mineralService.getMineral(areaMineral.getMineralId());
        if (null != mineral) {
            removeArea(areaMineral.getAreaId());
            areaMineral.setAddTime(new Date());
            areaMineral.setMineralName(mineral.getMineralName());
            this.save(areaMineral);
            return;
        }
        log.error("添加矿种和装载区关系时，没找到对应矿种");
    }

    /**
     * 根据卸矿区id获取对应关系
     */
    @Override
    @Transactional
    public AreaMineral getAreaMineral(Integer areaId) {
        LambdaQueryWrapper<AreaMineral> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AreaMineral::getAreaId, areaId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void deleteAreaMineral(Integer id) {
        this.baseMapper.deleteById(id);
    }

    /**
     * 根据矿种id获取所有卸载区
     * */
    @Override
    @Transactional
    public List<AreaMineral> getUnAreaIds(Integer mineralId) {
        LambdaQueryWrapper<AreaMineral> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AreaMineral::getMineralId,mineralId);
        return this.list(queryWrapper);
    }

    /**
     * 逻辑删除
     */
    private void removeArea(Integer areaId) {
        LambdaQueryWrapper<AreaMineral> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AreaMineral::getAreaId, areaId);
        this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateStatus(Approve approve) {
        AreaMineral areaMineral = new AreaMineral();
        Map params = approve.getParams();
        if (!GmsUtil.mapContains(params, "unitId", "unLoadAreaId", "mineralId")) {
            ApproveUtil.addError(approve.getApproveId(),"跟新状态时，检查请求参数异常");
            return false;
        }
        Object unitId = params.get("unitId");
        Object unLoadAreaId = params.get("unLoadAreaId");
        Object mineralId = params.get("mineralId");
        areaMineral.setMineralId((Integer) mineralId);
        areaMineral.setAreaId((Integer) unLoadAreaId);
        areaMineralService.addAreaMineral(areaMineral);
        Integer id = areaMineral.getId();
        if (null == id) {
            log.debug("更换矿种失败");
            ApproveUtil.addError(approve.getApproveId(),"更换矿种失败");
            return false;
        }
        Map<String, Object> dispatchMap = new HashMap<>();
        dispatchMap.put("unitId", unitId);
        dispatchMap.put("loaderType", areaMineral.getMineralName());
        dispatchMap.put("unLoaderAreaId", unLoadAreaId);
        MessageEntry entry = MessageFactory.getApproveEntry(approve.getApproveId());
        entry.setAfterHandle(() -> {
            if (!entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                areaMineralService.deleteAreaMineral(id);
                ApproveUtil.addError(approve.getApproveId(),"远程调用失败");
            }else{
                //修改装卸调度单元的卸载区id

            }
            Integer submitUserId = approve.getSubmitUserId();
            WsUtil.sendMessage(String.valueOf(submitUserId),GmsUtil.toJson(approve), FunctionEnum.approve);
            String userIds = approve.getApproveUserIds();
            List<ApproveProcess> process = approve.getApproveProcess();
            Set<String> aids = new HashSet<>();
            if(null!=process){
                for (ApproveProcess approveProcess : process) {
                    aids.add(approveProcess.getUserId());
                }
            }
            String[] ids = userIds.split(StringPool.COMMA);
            for (String s : ids) {
                if(!aids.contains(s)){
                    WsUtil.sendMessage(String.valueOf(submitUserId),GmsUtil.toJson(approve), FunctionEnum.approve);
                }
            }
        });
        MessageFactory.getDispatchMessage().sendMessageNoResWithID(entry.getMessageId(), "ChangeLoadType", GmsUtil.toJson(dispatchMap));
        return true;
    }

}
