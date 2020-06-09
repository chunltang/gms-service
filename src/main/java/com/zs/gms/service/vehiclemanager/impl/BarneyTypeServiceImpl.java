package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.vehiclemanager.BarneyType;
import com.zs.gms.mapper.vehiclemanager.VehicleTypeMapper;
import com.zs.gms.service.vehiclemanager.BarneyTypeService;
import com.zs.gms.service.vehiclemanager.BarneyVehicleTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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

    @Override
    @Transactional
    public BarneyType getBarneyType(Integer barneyTypeId) {
        return getById(barneyTypeId);
    }

    /**
     * 获取车辆类型列表
     * */
    @Override
    @Transactional
    public IPage<BarneyType> getVehicleTypeList(QueryRequest queryRequest) {
        Page<BarneyType> page=new Page<>();
        SortUtil.handlePageSort(queryRequest,page, GmsConstant.SORT_DESC,"active");
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

    @Override
    @Transactional
    public void updateActive(Collection<Integer> barneyTypeIds) {
        LambdaUpdateWrapper<BarneyType> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.set(BarneyType::getActive, WhetherEnum.NO);
        this.update(updateWrapper1);
        if(GmsUtil.CollectionNotNull(barneyTypeIds)){
            LambdaUpdateWrapper<BarneyType> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(BarneyType::getActive, WhetherEnum.YES);
            updateWrapper.in(BarneyType::getVehicleTypeId,barneyTypeIds);
            this.update(updateWrapper);
        }
    }
}
