package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.BarneyVehicleType;

public interface BarneyVehicleTypeService extends IService<BarneyVehicleType> {

     void addVehicleVehicleType(BarneyVehicleType barneyVehicleType);

     void deleteByVehicleId(Integer vehicleId);

}
