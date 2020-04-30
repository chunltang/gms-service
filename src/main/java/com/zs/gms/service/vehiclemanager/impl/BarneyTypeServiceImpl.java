package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.vehiclemanager.BarneyType;
import com.zs.gms.entity.vehiclemanager.BarneyVehicleType;
import com.zs.gms.mapper.vehiclemanager.VehicleTypeMapper;
import com.zs.gms.service.vehiclemanager.BarneyService;
import com.zs.gms.service.vehiclemanager.BarneyTypeService;
import com.zs.gms.service.vehiclemanager.BarneyVehicleTypeService;
import com.zs.gms.service.vehiclemanager.UserBarneyService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BarneyVehicleTypeService barneyVehicleTypeService;


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
    public IPage<BarneyType> getVehicleTypeList(QueryRequest queryRequest) {
        Page<BarneyType> page=new Page<>();
        SortUtil.handlePageSort(queryRequest,page, GmsConstant.SORT_DESC,"vehicleTypeId");
        return this.page(page);
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
     * 删除车辆类型,并清除车辆和类型的关系
     * */
    @Override
    @Transactional
    public void deleteVehicleType(Integer VehicleTypeId) {
        boolean remove = this.removeById(VehicleTypeId);
        if(remove){
            barneyVehicleTypeService.deleteByVehicleTypeId(VehicleTypeId);
        }
    }
}
