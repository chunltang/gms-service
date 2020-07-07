package com.zs.gms.service.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.monitor.Unit;

import java.util.List;

public interface UnitService {

    void addUnit(Unit unit);

    /**
     * 判断用户是否已分配调度单元
     * */
    boolean isExistUser(Integer userId);

    /**
     * 根据调度员id获取调度单元
     * */
    List<Unit> getUnitListByUserId(Integer userId,Integer mapId);

    List<Unit> getUnitListByLoadId(Integer loadId);

    Unit getUnitByLoadId(Integer mapId,Integer loadId);

    Unit getUnitByUnloadId(Integer mapId,Integer unloadId);

    /**
     * 清除非指定地图的调度单元和车辆，如传的mapId为活动地图id
     * */
    void clearUnitSAndVehicles(Integer mapId);

    /**
     * 判断名字是否存在
     * */
    boolean isExistName(String unitName);

    /**
     * 判断装载区是否已使用
     * */
    boolean isExistLoadId(Integer loadId,Integer mapId);

    boolean isExistUnloadId(Integer unloadId,Integer mapId);

    boolean isExistName(String unitName,Integer unitId);

    /**
     * 判断id是否存在
     * */
    boolean isExistId(Integer unitId);

    IPage<Unit> getUnitList(QueryRequest queryRequest,Integer mapId);

    List<Unit> getUnitListByMapId(Integer mapId);

    void updateUnit(Unit unit);

    /**
     * 修改调度单元卸载区id
     * */
    void updateUnitUnloadId(Integer unitId,Integer unloadId);

    void deleteUnit(Integer unitId);

    void updateStatus(Integer unitId,Unit.Status status);
}
