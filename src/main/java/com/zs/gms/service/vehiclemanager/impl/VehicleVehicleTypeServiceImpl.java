package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.vehiclemanager.VehicleVehicleType;
import com.zs.gms.mapper.vehiclemanager.VehicleVehicleTypeMapper;
import com.zs.gms.service.vehiclemanager.VehicleVehicleTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class VehicleVehicleTypeServiceImpl extends ServiceImpl<VehicleVehicleTypeMapper, VehicleVehicleType> implements VehicleVehicleTypeService {

    @Override
    @Transactional
    public void addVehicleVehicleType(VehicleVehicleType vehicleVehicleType) {
        this.save(vehicleVehicleType);
    }

    @Override
    @Transactional
    public void deteleByVehicleId(long vehicleId) {
        this.remove(new LambdaQueryWrapper<VehicleVehicleType>().eq(VehicleVehicleType::getVehicleId,vehicleId));
    }

    @Override
    @Transactional
    public void deteleByVehicleIdS(String[] vehicleIds) {
        this.remove(new LambdaQueryWrapper<VehicleVehicleType>().eq(VehicleVehicleType::getVehicleId,vehicleIds));
    }
}
