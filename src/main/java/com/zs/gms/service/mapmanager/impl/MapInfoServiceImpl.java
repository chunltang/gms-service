package com.zs.gms.service.mapmanager.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.nettyclient.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.init.GmsGlobalConfig;
import com.zs.gms.entity.mapmanager.MapConfig;
import com.zs.gms.entity.mapmanager.MapInfo;
import com.zs.gms.mapper.mapmanager.MapInfoMapper;
import com.zs.gms.service.common.GmsConfigService;
import com.zs.gms.service.mapmanager.MapInfoService;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.enums.messagebox.ApproveType;
import com.zs.gms.service.messagebox.ApproveInterface;
import com.zs.gms.service.messagebox.ApproveService;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.messagebox.ApproveUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class MapInfoServiceImpl extends ServiceImpl<MapInfoMapper, MapInfo> implements MapInfoService, ApproveInterface {

    @Autowired
    private ApproveService approveService;

    @Autowired
    private GmsConfigService gmsConfigService;


    @Override
    @Transactional
    public void addMapInfo(MapInfo Info) {
        Date date = new Date();
        Info.setAddTime(date);
        Info.setUpdateTime(date);
        Info.setStatus(MapInfo.Status.UNUSED);
        this.save(Info);
    }

    @Override
    @Transactional
    public MapInfo.MapVersion getVersion() {
        GmsGlobalConfig gmsConfig = gmsConfigService.getGmsConfig(GmsConstant.MAP_VERSION);
        MapInfo.MapVersion mapVersion = new MapInfo.MapVersion();
        if (null != gmsConfig) {
            mapVersion = GmsUtil.toObj(gmsConfig.getConfigValue(), MapInfo.MapVersion.class);
        }
        return mapVersion;
    }

    @Override
    @Transactional
    public void setVersion(MapInfo.MapVersion version) {
        GmsGlobalConfig gmsConfig = new GmsGlobalConfig();
        gmsConfig.setConfigKey(GmsConstant.MAP_VERSION);
        gmsConfig.setConfigValue(JSON.toJSONString(version));
        gmsConfigService.addGmsConfig(gmsConfig);
    }

    @Override
    @Transactional
    public void updateLastTime(Integer mapId,Date lastTime) {
        LambdaUpdateWrapper<MapInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MapInfo::getMapId,mapId);
        updateWrapper.set(MapInfo::getUpdateTime, lastTime);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean updateInactive() {
        LambdaUpdateWrapper<MapInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MapInfo::getStatus, MapInfo.Status.USING);
        updateWrapper.set(MapInfo::getStatus, MapInfo.Status.UNUSED);
        return this.update(updateWrapper);
    }

    @Override
    public boolean existMapId(Integer mapId) {
        LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapInfo::getMapId, mapId);
        return this.list(queryWrapper).size() > 0;
    }

    @Override
    public MapInfo getMapInfo(Integer mapId) {
        LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapInfo::getMapId, mapId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public MapInfo getActiveMapInfo() {
        LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapInfo::getStatus, MapInfo.Status.USING);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void updateMapInfo(MapInfo info) {
        LambdaUpdateWrapper<MapInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MapInfo::getMapId, info.getMapId());
        this.update(info,updateWrapper);
    }

    @Override
    @Transactional
    public void deleteMapInfo(Integer mapId) {
        LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapInfo::getMapId, mapId);
        this.remove(queryWrapper);
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
    public IPage<MapInfo> getMapInfoListPage(QueryRequest request) {
        Page page = new Page();
        SortUtil.handlePageSort(request, page, GmsConstant.SORT_DESC, "addTime");
        return this.page(page);
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
        if (mapInfo == null) {
            return false;
        }
        /*LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MapInfo::getStatus, MapInfo.Status.USING, MapInfo.Status.PUBLISH);
        List<MapInfo> mapInfos = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(mapInfos)) {
            log.debug("已存在处于使用或申请发布的地图");
            return false;
        }*/
        return addApprove(mapInfo, userIds, user, ApproveType.MAPPUBLISH, MapInfo.Status.PUBLISH);
    }

    @Override
    @Transactional
    public boolean submitDeleteMap(Integer mapId, String userIds, User user) {
        MapInfo mapInfo = checkParams(mapId);
        if (mapInfo == null) {
            return false;
        }
        MapInfo.Status status = mapInfo.getStatus();
        if (!status.equals(MapInfo.Status.UNUSED)) {
            log.debug("地图处于非使用状态，不可删除");
            return false;
        }
        return addApprove(mapInfo, userIds, user, ApproveType.MAPDELETE, MapInfo.Status.DELETE);
    }

    @Override
    @Transactional
    public boolean submitInactiveMap(Integer mapId, String userIds, User user) {
        MapInfo mapInfo = checkParams(mapId);
        if (mapInfo == null) {
            return false;
        }
        MapInfo.Status status = mapInfo.getStatus();
        if (!status.equals(MapInfo.Status.USING)) {
            log.debug("地图处于非活动状态");
            return false;
        }
        return addApprove(mapInfo, userIds, user, ApproveType.MAPINACTIVE, MapInfo.Status.INACTIVE);
    }

    public boolean addApprove(MapInfo mapInfo, String userIds, User user, ApproveType approveType, MapInfo.Status nextStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("mapId", mapInfo.getMapId());
        params.put("mapName", mapInfo.getName());
        Integer id = approveService.createApprove(params, userIds, user, approveType, true);
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

    public MapInfo checkParams(Integer mapId) {
        LambdaQueryWrapper<MapInfo> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(MapInfo::getMapId, mapId);
        List<MapInfo> infos = this.list(existWrapper);
        if (CollectionUtils.isEmpty(infos)) {
            log.debug("地图id不存在");
            return null;
        }
        return infos.get(0);
    }

    public MapInfo getMapInfoByApproveId(Integer approveId) {
        LambdaQueryWrapper<MapInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MapInfo::getApproveId, approveId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateStatus(Approve approve) {
        MapInfo mapInfo = getMapInfoByApproveId(approve.getApproveId());
        MapInfo.Status mapInfoStatus = (mapInfo==null?null:mapInfo.getStatus());
        Approve.Status status = approve.getStatus();
        ApproveType approveType = approve.getApproveType();
        switch (approveType) {
            case MAPPUBLISH:
                if (!mapInfoStatus.equals(MapInfo.Status.PUBLISH)) {
                    log.debug("地图处于非申请发布状态");
                    ApproveUtil.addError(approve.getApproveId(), "地图处于非申请发布状态");
                    return false;
                }
                switch (status) {
                    case APPROVEPASS:
                        mapInfo.setStatus(MapInfo.Status.USING);
                        //设置活动地图地图id
                        boolean flag = RedisService.set(StaticConfig.MONITOR_DB, RedisKeyPool.ACTIVITY_MAP, String.valueOf(mapInfo.getMapId()));
                        if (updateInactive() && !flag) {
                            log.error("设置活动地图失败");
                            ApproveUtil.addError(approve.getApproveId(), "设置活动地图失败");
                            return false;
                        }
                        break;
                    case APPROVEREJECT:
                        mapInfo.setStatus(MapInfo.Status.UNUSED);
                        break;
                    default:
                        log.error("地图发布状态更新失败");
                        ApproveUtil.addError(approve.getApproveId(), "地图发布状态更新失败");
                        return false;
                }
                break;
            case MAPDELETE:
                if (!mapInfoStatus.equals(MapInfo.Status.DELETE)) {
                    log.debug("地图处于非申请删除状态");
                    ApproveUtil.addError(approve.getApproveId(), "地图处于非申请删除状态");
                    return false;
                }
                switch (status) {
                    case APPROVEPASS:
                        Map<String, Object> paramMap = new HashMap<>();
                        Integer mapId = mapInfo.getMapId();
                        paramMap.put("mapId", mapId);
                        MessageEntry entry = MessageFactory.getApproveEntry(approve.getApproveId());
                        entry.setHttp(false);
                        entry.setAfterHandle(() -> {
                            if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                                deleteMapInfo(mapId);
                            } else {
                                log.error(String.format("审批通过，但删除地图时远程地图服务接口deleteMap调用失败，mapId=%s,approveId=%s", mapId, approve.getApproveId()));
                                ApproveUtil.addError(approve.getApproveId(), "远程地图删除调用失败");
                            }
                            WsUtil.sendMessage(String.valueOf(approve.getSubmitUserId()), GmsUtil.toJson(approve), FunctionEnum.approve);
                        });
                        MessageFactory.getMapMessage().sendMessageNoResWithID(entry.getMessageId(), "deleteMap", JSON.toJSONString(paramMap));
                        return true;
                    case APPROVEREJECT:
                        mapInfo.setStatus(MapInfo.Status.UNUSED);
                        break;
                    default:
                        log.error("地图删除状态更新失败");
                        ApproveUtil.addError(approve.getApproveId(), "地图删除状态更新失败");
                        return false;
                }
                break;
            case MAPINACTIVE:
                if (!mapInfoStatus.equals(MapInfo.Status.INACTIVE)) {
                    log.debug("地图处于非申请解除活跃状态");
                    ApproveUtil.addError(approve.getApproveId(), "地图处于非申请解除活跃状态");
                    return false;
                }
                switch (status) {
                    case APPROVEPASS:
                        mapInfo.setStatus(MapInfo.Status.UNUSED);
                        //取消活动地图地图id
                        boolean flag = RedisService.set(StaticConfig.MONITOR_DB, RedisKeyPool.ACTIVITY_MAP, "");
                        if (!flag) {
                            log.error("redis取消活动地图失败");
                            ApproveUtil.addError(approve.getApproveId(), "redis取消活动地图失败");
                            return false;
                        }
                        break;
                    case APPROVEREJECT:
                        mapInfo.setStatus(MapInfo.Status.USING);
                        break;
                    default:
                        log.error("地图取消活跃状态更新失败");
                        ApproveUtil.addError(approve.getApproveId(), "地图取消活跃状态更新失败");
                        return false;
                }
                break;
            case OBSTACLEDELETE:
                Map<String, Object> params = approve.getParams();
                MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
                entry.setHttp(false);
                entry.setAfterHandle(()->{
                    log.debug(GmsUtil.format("删除障碍物结果:{}",entry.getHandleResult().name()));
                });
                MessageFactory.getMapMessage().sendMessageNoResWithID(entry.getMessageId(),"removeObstacle", GmsUtil.toJson(params));
                return true;
            default:
                log.error("地图更新审批失败，无匹配事件");
                ApproveUtil.addError(approve.getApproveId(), "地图更新审批失败，无匹配事件");
                return false;
        }
        WsUtil.sendMessage(String.valueOf(approve.getSubmitUserId()), GmsUtil.toJson(approve), FunctionEnum.approve);
        return this.updateById(mapInfo);
    }

    /**
     * 取消审批
     */
    @Override
    @Transactional
    public void cancel(Approve approve) {
        if (!approve.getStatus().equals(Approve.Status.WAIT)) {
            return;
        }
        MapInfo mapInfo = getMapInfoByApproveId(approve.getApproveId());
        MapInfo.Status mapInfoStatus = mapInfo.getStatus();
        switch (mapInfoStatus) {
            case PUBLISH:
            case DELETE:
                mapInfo.setStatus(MapInfo.Status.UNUSED);
                break;
            case INACTIVE:
                mapInfo.setStatus(MapInfo.Status.USING);
                break;
            default:
                throw  new GmsException(GmsUtil.format("地图取消审批失败,地图状态异常,mapInfo={}",mapInfo));
        }
        this.updateById(mapInfo);
    }
}
