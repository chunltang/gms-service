package com.zs.gms.service.mapmanager.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.RedisKey;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.MapInfo;
import com.zs.gms.mapper.mapmanager.MapInfoMapper;
import com.zs.gms.service.mapmanager.MapInfoService;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.enums.messagebox.ApproveType;
import com.zs.gms.service.messagebox.ApproveInterface;
import com.zs.gms.service.messagebox.ApproveService;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.messagebox.ApproveUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class MapInfoServiceImpl extends ServiceImpl<MapInfoMapper, MapInfo> implements MapInfoService, ApproveInterface {

    @Autowired
    private ApproveService approveService;


    @Override
    @Transactional
    public void addMapInfo(MapInfo Info) {
        Info.setAddTime(new Date());
        Info.setStatus(MapInfo.Status.UNUSED);
        this.save(Info);
    }

    @Override
    @Transactional
    public void updateMapInfo(MapInfo info) {
        info.setUpdateTime(new Date());
        this.update(info, new LambdaQueryWrapper<MapInfo>().eq(MapInfo::getMapId, info.getMapId()));
    }

    @Override
    @Transactional
    public void deleteMapInfo(Integer mapId) {
        this.remove(new LambdaQueryWrapper<MapInfo>().eq(MapInfo::getMapId, mapId));
    }

    @Override
    public MapInfo.Status getMapStatus(Integer mapId) {
        LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapInfo::getMapId, mapId);
        List<MapInfo> infos = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(infos)) {
            return infos.get(0).getStatus();
        }
        return null;
    }

    @Override
    @Transactional
    public IPage<MapInfo> getMapInfoListPage(QueryRequest request) {//改
        Page page = new Page();
        page.setSize(1);
        page.setCurrent(1);
        page.setDesc("addTime");
        //SortUtil.handlePageSort(request,page, GmsConstant.SORT_DESC,"addTime");
        IPage iPage = this.page(page);
        iPage.setTotal(1);
        return iPage;
    }

    @Override
    @Transactional
    public void updateMapStatus(Integer mapId, MapInfo.Status status) {
        LambdaUpdateWrapper<MapInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MapInfo::getMapId, mapId);
        updateWrapper.set(MapInfo::getStatus, status);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean submitPublishMap(Integer mapId, String userIds, User user) {
        MapInfo mapInfo = checkParams(mapId);
        if(mapInfo==null){
            return false;
        }
        LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MapInfo::getStatus, MapInfo.Status.USING, MapInfo.Status.PUBLISH);
        List<MapInfo> mapInfos = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(mapInfos)) {
            log.debug("已存在处于使用或申请发布的地图");
            return false;
        }
        return addApprove(mapInfo,userIds, user, ApproveType.MAPPUBLISH,MapInfo.Status.PUBLISH);
    }

    @Override
    @Transactional
    public boolean submitDeleteMap(Integer mapId, String userIds, User user) {
        MapInfo mapInfo = checkParams(mapId);
        if(mapInfo==null){
            return false;
        }
        MapInfo.Status status =mapInfo.getStatus();
        if(!status.equals(MapInfo.Status.UNUSED)){
            log.debug("地图处于非使用状态，不可删除");
            return false;
        }
        return addApprove(mapInfo,userIds, user, ApproveType.MAPDELETE,MapInfo.Status.DELETE);
    }

    @Override
    @Transactional
    public boolean submitInactiveMap(Integer mapId, String userIds, User user) {
        MapInfo mapInfo = checkParams(mapId);
        if(mapInfo==null){
            return false;
        }
        MapInfo.Status status =mapInfo.getStatus();
        if(!status.equals(MapInfo.Status.USING)){
            log.debug("地图处于非活动状态");
            return false;
        }
        return addApprove(mapInfo,userIds, user, ApproveType.MAPINACTIVE,MapInfo.Status.INACTIVE);
    }

    public boolean addApprove(MapInfo mapInfo,String userIds,User user,ApproveType approveType,MapInfo.Status nextStatus){
        Map<String,Object> params=new HashMap<>();
        params.put("mapId",mapInfo.getMapId());
        params.put("mapName",mapInfo.getName());
        Integer id = approveService.createApprove(params,userIds,user,approveType,true);
        if (id != null) {
            LambdaUpdateWrapper<MapInfo> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(MapInfo::getMapId, mapInfo.getMapId());
            updateWrapper.set(MapInfo::getStatus, nextStatus);
            updateWrapper.set(MapInfo::getApproveId, id);
            return this.update(updateWrapper);
        }
        log.debug("审批id生成失败");
        return false;
    }

    public MapInfo checkParams(Integer mapId){
        LambdaQueryWrapper<MapInfo> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(MapInfo::getMapId, mapId);
        List<MapInfo> infos = this.list(existWrapper);
        if (CollectionUtils.isEmpty(infos)) {
            log.debug("地图id不存在");
            return null;
        }
        return infos.get(0);
    }

    @Override
    @Transactional
    public boolean updateStatus(Approve approve) {
        LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapInfo::getApproveId, approve.getApproveId());
        MapInfo mapInfo = this.getOne(queryWrapper);
        MapInfo.Status mapInStatus = mapInfo.getStatus();
        Approve.Status status = approve.getStatus();
        ApproveType approveType = approve.getApproveType();
        switch (approveType) {
            case MAPPUBLISH:
                if(!mapInStatus.equals(MapInfo.Status.PUBLISH)){
                    log.debug("地图处于非申请发布状态");
                    ApproveUtil.addError(approve.getApproveId(),"地图处于非申请发布状态");
                    return false;
                }
                switch (status) {
                    case APPROVEPASS:
                        mapInfo.setStatus(MapInfo.Status.USING);
                        //设置活动地图地图id
                        boolean flag = RedisService.set(GmsConstant.MONITOR_DB, RedisKey.ACTIVITY_MAP, String.valueOf(mapInfo.getMapId()));
                        if (!flag) {
                            log.error("设置活动地图失败");
                            ApproveUtil.addError(approve.getApproveId(),"设置活动地图失败");
                            return false;
                        }
                        break;
                    case APPROVEREJECT:
                        mapInfo.setStatus(MapInfo.Status.UNUSED);
                        break;
                    default:
                        log.error("地图发布状态更新失败");
                        ApproveUtil.addError(approve.getApproveId(),"地图发布状态更新失败");
                        return false;
                }
                break;
            case MAPDELETE:
                if(!mapInStatus.equals(MapInfo.Status.DELETE)){
                    log.debug("地图处于非申请删除状态");
                    ApproveUtil.addError(approve.getApproveId(),"地图处于非申请删除状态");
                    return false;
                }
                switch (status) {
                    case APPROVEPASS:
                        mapInfo.setStatus(MapInfo.Status.ABANDON);
                        Map<String, Object> paramMap = new HashMap<>();
                        Integer mapId = mapInfo.getMapId();
                        paramMap.put("mapId",mapId);
                        MessageEntry entry = MessageFactory.getApproveEntry(approve.getApproveId());
                        entry.setAfterHandle(() -> {
                            if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                                deleteMapInfo(mapId);
                            }else{
                                log.error(String.format("删除地图失败，mapId=%s,approveId=%s",mapId,approve.getApproveId()));
                                ApproveUtil.addError(approve.getApproveId(),"远程地图删除调用失败");

                            }
                        });
                        MessageFactory.getMapMessage().sendMessageNoResWithID(entry.getMessageId(),"deleteMap", JSON.toJSONString(paramMap));
                        break;
                    case APPROVEREJECT:
                        mapInfo.setStatus(MapInfo.Status.UNUSED);
                        break;
                    default:
                        log.error("地图删除状态更新失败");
                        ApproveUtil.addError(approve.getApproveId(),"地图删除状态更新失败");
                        return false;
                }
                break;
            case MAPINACTIVE:
                if(!mapInStatus.equals(MapInfo.Status.INACTIVE)){
                    log.debug("地图处于非申请解除活跃状态");
                    ApproveUtil.addError(approve.getApproveId(),"地图处于非申请解除活跃状态");
                    return false;
                }
                switch (status) {
                    case APPROVEPASS:
                        mapInfo.setStatus(MapInfo.Status.UNUSED);
                        //取消活动地图地图id
                        boolean flag = RedisService.set(GmsConstant.MONITOR_DB, RedisKey.ACTIVITY_MAP, "");
                        if (!flag) {
                            log.error("redis取消活动地图失败");
                            ApproveUtil.addError(approve.getApproveId(),"redis取消活动地图失败");
                            return false;
                        }
                        break;
                    case APPROVEREJECT:
                        mapInfo.setStatus(MapInfo.Status.USING);
                        break;
                    default:
                        log.error("地图取消活跃状态更新失败");
                        ApproveUtil.addError(approve.getApproveId(),"地图取消活跃状态更新失败");
                        return false;
                }
                break;
            default:
                log.error("地图更新审批失败，无匹配事件");
                ApproveUtil.addError(approve.getApproveId(),"地图更新审批失败，无匹配事件");
                return false;
        }
        WsUtil.sendMessage(String.valueOf(approve.getSubmitUserId()), GmsUtil.toJson(approve), FunctionEnum.approve);
        return this.updateById(mapInfo);
    }
}
