package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.mapper.monitor.UnitMapper;
import com.zs.gms.service.monitor.UnitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements UnitService {


    @Override
    @Transactional
    public void addUnit(Unit unit) {
        unit.setAddTime(new Date());
        unit.setStatus(Unit.Status.UNUSED);
        this.save(unit);
    }

    @Override
    @Transactional
    public boolean isExistUser(Integer userId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUserId,userId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public List<Unit> getUnitListByUserId(Integer userId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUserId,userId);
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean isExistName(String unitName) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUnitName,unitName);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    public boolean isExistName(String unitName, Integer unitId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUnitName,unitName);
        queryWrapper.ne(Unit::getUnitId,unitId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public boolean isExistId(Integer unitId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUnitId,unitId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public IPage<Unit> getUnitList(QueryRequest queryRequest) {
        Page<Unit> page=new Page<>();
        SortUtil.handlePageSort(queryRequest,page, GmsConstant.SORT_DESC,"unitId");
        return this.baseMapper.getUnitListPage(page);
    }

    @Override
    @Transactional
    public void updateUnit(Unit unit) {
        this.updateById(unit);
    }

    @Override
    @Transactional
    public void deleteUnit(Integer unitId) {
       this.removeById(unitId);
    }

    @Override
    @Transactional
    public void updateStatus(Integer unitId, Unit.Status status) {
        LambdaUpdateWrapper<Unit> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Unit::getUnitId,unitId);
        updateWrapper.set(Unit::getStatus,status);
        this.update(updateWrapper);
    }
}
