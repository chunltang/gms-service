package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.entity.monitor.VehicleStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * 车辆异常状态处理
 * */
@Slf4j
public class VehicleErrorStatusHandle extends AbstractVehicleStatusHandle {


    public VehicleErrorStatusHandle() {
        super();
    }

    @Override
    public void handleStatus(VehicleStatus vehicleStatus) {
        //log.debug("{}车辆异常状态:{}",vehicleStatus.getVehicleId(),vehicleStatus.getObj());
    }
}
