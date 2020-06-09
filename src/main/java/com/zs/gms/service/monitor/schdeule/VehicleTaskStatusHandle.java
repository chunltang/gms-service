package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.entity.monitor.VehicleStatus;
import com.zs.gms.common.interfaces.Desc;
import lombok.extern.slf4j.Slf4j;

/**
 * 车辆任务状态处理
 * */
@Slf4j
public class VehicleTaskStatusHandle  extends AbstractVehicleStatusHandle {

    public VehicleTaskStatusHandle() {
        super();
    }

    @Override
    public void handleStatus(VehicleStatus vehicleStatus) {
        super.handleStatus(vehicleStatus);
    }


    @Override
    public void changed(VehicleStatus vehicleStatus) {
        log.debug("{}车辆任务状态改变:{}",vehicleStatus.getVehicleId(),((Desc)(vehicleStatus.getObj())).getDesc());
        super.changed(vehicleStatus);
    }

    @Override
    public void save(VehicleStatus vehicleStatus) {
        //do nothing
    }
}
