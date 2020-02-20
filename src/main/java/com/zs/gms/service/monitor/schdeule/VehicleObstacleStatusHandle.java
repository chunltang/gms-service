package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.entity.monitor.VehicleStatus;
import lombok.extern.slf4j.Slf4j;

/**
 * 车辆障碍物处理
 * */
@Slf4j
public class VehicleObstacleStatusHandle  extends AbstractVehicleStatusHandle {

    public VehicleObstacleStatusHandle() {
        super();
    }

    @Override
    public void handleStatus(VehicleStatus vehicleStatus) {
       //log.debug("{}车辆障碍状态改变:{}",vehicleStatus.getVehicleId(),vehicleStatus.getStatus());
    }
}
