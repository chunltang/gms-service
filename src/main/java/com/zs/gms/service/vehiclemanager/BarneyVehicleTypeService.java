package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.BarneyVehicleType;

public interface BarneyVehicleTypeService extends IService<BarneyVehicleType> {

    public void addVehicleVehicleType(BarneyVehicleType barneyVehicleType);

    public void deteleByVehicleId(long vehicleId);

    public void deteleByVehicleIdS(String[] vehicleIds);
}
