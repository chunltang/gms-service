package com.zs.gms.service.monitor;

import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.monitor.UnitVehicle;

import java.util.List;
import java.util.Map;

public interface UnitVehicleService {

    /**
     * 调度单元添加车辆
     * */
    void addUnitVehicles(List<UnitVehicle> unitVehicles);

    List<Map<String,Integer>> getAllVehicles();

    /**
     * 清除调度单元所有车辆
     * */
    void clearVehiclesByUnitId(Integer unitId);

    /**
     * 判断车辆是否已分配调度单元
     * */
    boolean isExistVehicleId(Integer vehicleId);

    boolean isExistVehicleId(Integer unitId,Integer vehicleId);

    /**
     * 获取调度单元所有车辆
     * */
    List<UnitVehicle> getUnitVehicleListUnitId(Integer unitId);

    /**
     * 获取所有已分配车辆
     * */
    List<UnitVehicle> getAllocatedVehicles();

    /**
     * 获取调度员所有车辆分配车辆
     * */
    List<UnitVehicle> getUnitVehicleListByUserId(Integer userId);

    /**
     * 移除调度单元指定车辆
     * */
    void removeVehicleId(Integer unitId,Integer vehicleId);

    /**
     * 获取矿车调度单元
     * */
    Unit getUnitByVehicleId(Integer vehicleId);
}
