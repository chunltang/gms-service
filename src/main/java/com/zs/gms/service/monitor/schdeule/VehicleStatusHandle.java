package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.entity.monitor.VehicleStatus;

/**
 * 车辆所有状态处理
 * */
public interface VehicleStatusHandle {


    public void handleStatus(VehicleStatus vehicleStatus);

    public void noChanged(VehicleStatus vehicleStatus);

    public void changed(VehicleStatus vehicleStatus);

    public void save(VehicleStatus vehicleStatus);

    public String push(Integer vehicleId);
}
