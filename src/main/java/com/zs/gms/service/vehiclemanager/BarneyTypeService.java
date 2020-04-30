package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.vehiclemanager.BarneyType;

import java.util.List;


public interface BarneyTypeService extends IService<BarneyType> {

     void addVehicleType(BarneyType barneyType);

     IPage<BarneyType> getVehicleTypeList(QueryRequest queryRequest);

     void updateVehicleType(BarneyType barneyType);

     void deleteVehicleType(Integer VehicleTypeId);
}
