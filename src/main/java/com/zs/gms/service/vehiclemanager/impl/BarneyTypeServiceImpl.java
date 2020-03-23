package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.vehiclemanager.BarneyType;
import com.zs.gms.mapper.vehiclemanager.VehicleTypeMapper;
import com.zs.gms.service.vehiclemanager.BarneyTypeService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
@Lazy
public class BarneyTypeServiceImpl extends ServiceImpl<VehicleTypeMapper, BarneyType> implements BarneyTypeService {

    /**
     * 添加车辆类型
     * */
    @Override
    @Transactional
    public void addVehicleType(BarneyType barneyType) {
        this.save(barneyType);
    }

    /**
     * 获取车辆类型列表
     * */
    @Override
    @Transactional
    public List<BarneyType> getVehicleTypeList() {
        return this.list(new LambdaQueryWrapper<BarneyType>().orderByDesc(BarneyType::getVehicleTypeId));
    }

    /**
     * 更新车辆类型参数
     * */
    @Override
    @Transactional
    public void updateVehicleType(BarneyType barneyType) {
        this.updateById(barneyType);
    }

    /**
     * 删除车辆类型
     * */
    @Override
    @Transactional
    public void deleteVehicleType(Integer VehicleTypeId) {
        this.removeById(VehicleTypeId);
    }
}
