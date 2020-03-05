package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.vehiclemanager.BarneyVehicleType;
import com.zs.gms.mapper.vehiclemanager.BarneyVehicleTypeMapper;
import com.zs.gms.service.vehiclemanager.BarneyVehicleTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class BarneyVehicleTypeServiceImpl extends ServiceImpl<BarneyVehicleTypeMapper, BarneyVehicleType> implements BarneyVehicleTypeService {

    @Override
    @Transactional
    public void addVehicleVehicleType(BarneyVehicleType barneyVehicleType) {
        this.save(barneyVehicleType);
    }

    @Override
    @Transactional
    public void deleteByVehicleId(Integer vehicleId) {
        this.remove(new LambdaQueryWrapper<BarneyVehicleType>().eq(BarneyVehicleType::getVehicleId,vehicleId));
    }
}
