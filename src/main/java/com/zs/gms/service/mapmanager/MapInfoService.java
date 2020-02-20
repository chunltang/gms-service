package com.zs.gms.service.mapmanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.mapmanager.MapInfo;
import com.zs.gms.entity.system.User;

public interface MapInfoService  {

    /**
     * 保存地图信息
     * */
    public void addMapInfo(MapInfo  Info);

    /**
     * 修改地图信息
     * */
    public void updateMapInfo(MapInfo info);

    /**
     * 删除地图信息
     * */
    public void deleteMapInfo(Integer mapId);

    public MapInfo.Status getMapStatus(Integer mapId);

    /**
     * 分页获取地图信息
     * */
    public IPage<MapInfo>  getMapInfoListPage(QueryRequest request);

    /**
     * 修改地图状态
     * */
    public void updateMapStatus(Integer mapId, MapInfo.Status status);

    /**
     * 提交申请发布地图
     * */
    public boolean submitPublishMap(Integer mapId, String approveUserIds, User user);

    /**
     * 提交申请删除地图
     * */
    public boolean submitDeleteMap(Integer mapId, String approveUserIds, User user);

    public boolean submitInactiveMap(Integer mapId, String approveUserIds, User user);
}
