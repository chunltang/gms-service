package com.zs.gms.service.mapmanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.mapmanager.MapInfo;
import com.zs.gms.entity.system.User;

import java.util.Date;

public interface MapInfoService {

    /**
     * 保存地图信息
     */
    void addMapInfo(MapInfo Info);

    MapInfo.MapVersion getVersion();

    void setVersion(MapInfo.MapVersion version);

    /**
     * 重置地图修改时间
     * */
    void updateLastTime(Integer mapId,Date lastTime);

    /**
     * 将激活地图设置为未激活
     * */
    boolean updateInactive();

    /**
     * 查询地图id是否已存在
     */
    boolean existMapId(Integer mapId);

    MapInfo getMapInfo(Integer mapId);

    MapInfo getActiveMapInfo();

    /**
     * 修改地图信息
     */
    void updateMapInfo(MapInfo info);

    /**
     * 删除地图信息
     */
    void deleteMapInfo(Integer mapId);

    MapInfo.Status getMapStatus(Integer mapId);

    /**
     * 分页获取地图信息
     */
    IPage<MapInfo> getMapInfoListPage(QueryRequest request);

    /**
     * 修改地图状态
     */
    void updateMapStatus(Integer mapId, MapInfo.Status status);

    /**
     * 提交申请发布地图
     */
    boolean submitPublishMap(Integer mapId, String approveUserIds, User user);

    /**
     * 提交申请删除地图
     */
    boolean submitDeleteMap(Integer mapId, String approveUserIds, User user);

    boolean submitInactiveMap(Integer mapId, String approveUserIds, User user);
}
