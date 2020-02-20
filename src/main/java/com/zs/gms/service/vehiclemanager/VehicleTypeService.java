package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.VehicleType;

import java.util.List;


public interface VehicleTypeService extends IService<VehicleType> {

    public void addVehicleType(VehicleType vehicleType);

    public List<VehicleType> getVehicleTypeList();

    public void updateVehicleType(VehicleType vehicleType);

    public void deleteVehicleType(String VehicleTypeIds);
}
