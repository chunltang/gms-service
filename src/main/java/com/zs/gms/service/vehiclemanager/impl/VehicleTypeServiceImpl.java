package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.vehiclemanager.VehicleType;
import com.zs.gms.mapper.vehiclemanager.VehicleTypeMapper;
import com.zs.gms.service.vehiclemanager.VehicleTypeService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
@Lazy
public class VehicleTypeServiceImpl extends ServiceImpl<VehicleTypeMapper, VehicleType> implements VehicleTypeService {

    /**
     * 添加车辆类型
     * */
    @Override
    @Transactional
    public void addVehicleType(VehicleType vehicleType) {
        this.save(vehicleType);
    }

    /**
     * 获取车辆类型列表
     * */
    @Override
    @Transactional
    public List<VehicleType> getVehicleTypeList() {
        return this.list(new LambdaQueryWrapper<VehicleType>().orderByDesc(VehicleType::getVehicleTypeId));
    }

    /**
     * 更新车辆类型参数
     * */
    @Override
    @Transactional
    public void updateVehicleType(VehicleType vehicleType) {
        this.updateById(vehicleType);
    }

    /**
     * 删除车辆类型
     * */
    @Override
    @Transactional
    public void deleteVehicleType(String VehicleTypeIds) {
        String[] ids = VehicleTypeIds.split(StringPool.COMMA);
        this.baseMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
