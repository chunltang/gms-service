package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.BarneyType;

import java.util.List;


public interface BarneyTypeService extends IService<BarneyType> {

    public void addVehicleType(BarneyType barneyType);

    public List<BarneyType> getVehicleTypeList();

    public void updateVehicleType(BarneyType barneyType);

    public void deleteVehicleType(String VehicleTypeIds);
}
