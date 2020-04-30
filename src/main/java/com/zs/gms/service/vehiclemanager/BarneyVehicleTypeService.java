package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.BarneyVehicleType;

import java.util.List;

public interface BarneyVehicleTypeService extends IService<BarneyVehicleType> {

     void addVehicleVehicleType(BarneyVehicleType barneyVehicleType);

     void deleteByVehicleId(Integer vehicleId);

     void deleteByVehicleTypeId(Integer vehicleTypeId);

     List<BarneyVehicleType> getBarneyVehicleTypes(Integer vehicleTypeId);

}
