package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import com.zs.gms.entity.vehiclemanager.ExcavatorType;
import com.zs.gms.mapper.vehiclemanager.ExcavatorTypeMapper;
import com.zs.gms.service.vehiclemanager.ExcavatorTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class ExcavatorTypeServiceImpl extends ServiceImpl<ExcavatorTypeMapper, ExcavatorType> implements ExcavatorTypeService {


    @Override
    @Transactional
    public void addExcavatorType(ExcavatorType excavatorType) {
        excavatorType.setCreateTime(new Date());
        this.save(excavatorType);
    }

    @Override
    @Transactional
    public ExcavatorType getExcavatorType(Integer excavatorTypeId) {
        return this.getById(excavatorTypeId);
    }

    @Override
    @Transactional
    public IPage<ExcavatorType> getExcavatorTypeListPage(QueryRequest queryRequest) {
        Page<ExcavatorType> page=new Page<>();
        SortUtil.handlePageSort(queryRequest,page,GmsConstant.SORT_DESC,"active");
        return this.page(page);
    }

    @Override
    @Transactional
    public void deleteExcavatorType(Integer excavatorTypeId) {
        this.removeById(excavatorTypeId);
    }

    @Override
    @Transactional
    public void updateExcavatorType(ExcavatorType excavatorType) {
       this.updateById(excavatorType);
    }

    @Override
    @Transactional
    public boolean isExistName(String excavatorTypeName) {
        LambdaQueryWrapper<ExcavatorType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExcavatorType::getExcavatorTypeName,excavatorTypeName);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public boolean isExistName(Integer excavatorTypeId, String excavatorTypeName) {
        LambdaQueryWrapper<ExcavatorType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExcavatorType::getExcavatorTypeName,excavatorTypeName);
        queryWrapper.ne(ExcavatorType::getExcavatorTypeId,excavatorTypeId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public boolean isExistTypeId(Integer excavatorTypeId) {
        LambdaQueryWrapper<ExcavatorType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExcavatorType::getExcavatorTypeId,excavatorTypeId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public void updateActive(Collection<Integer> excavatorTypeIds) {
        LambdaUpdateWrapper<ExcavatorType> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.set(ExcavatorType::getActive, WhetherEnum.NO);
        this.update(updateWrapper1);
        if(GmsUtil.CollectionNotNull(excavatorTypeIds)){
            LambdaUpdateWrapper<ExcavatorType> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(ExcavatorType::getActive, WhetherEnum.YES);
            updateWrapper.in(ExcavatorType::getExcavatorTypeId,excavatorTypeIds);
            this.update(updateWrapper);
        }
    }
}
