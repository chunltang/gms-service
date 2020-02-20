package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.VehicleVehicleType;

public interface VehicleVehicleTypeService extends IService<VehicleVehicleType> {

    public void addVehicleVehicleType(VehicleVehicleType vehicleVehicleType);

    public void deteleByVehicleId(long vehicleId);

    public void deteleByVehicleIdS(String[] vehicleIds);
}
