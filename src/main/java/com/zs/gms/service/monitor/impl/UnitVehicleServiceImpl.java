package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.monitor.UnitVehicle;
import com.zs.gms.mapper.monitor.UnitVehicleMapper;
import com.zs.gms.service.monitor.UnitVehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class UnitVehicleServiceImpl extends ServiceImpl<UnitVehicleMapper, UnitVehicle> implements UnitVehicleService {

    @Override
    @Transactional
    public void addUnitVehicles(List<UnitVehicle> unitVehicles) {
        this.saveBatch(unitVehicles);
    }

    @Override
    public List<Map<String, Integer>> getAllVehicles() {
        return this.baseMapper.getAllVehicles();
    }

    @Override
    @Transactional
    public void clearVehiclesByUnitId(Integer unitId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getUnitId,unitId);
        this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public boolean isExistVehicleId(Integer vehicleId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getVehicleId,vehicleId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public boolean isExistVehicleId(Integer unitId, Integer vehicleId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getVehicleId,vehicleId);
        queryWrapper.ne(UnitVehicle::getUnitId,unitId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public List<UnitVehicle> getUnitVehicleListUnitId(Integer unitId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getUnitId,unitId);
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public List<UnitVehicle> getAllocatedVehicles() {
        return this.list();
    }

    @Override
    @Transactional
    public List<UnitVehicle> getUnitVehicleListByUserId(Integer userId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getCreateUserId,userId);
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public void removeVehicleId(Integer unitId, Integer vehicleId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getUnitId,unitId);
        queryWrapper.eq(UnitVehicle::getVehicleId,vehicleId);
        this.remove(queryWrapper);
    }

    @Override
    @Transactional
    public Unit getUnitByVehicleId(Integer vehicleId) {
        return this.baseMapper.getUnitByVehicleId(vehicleId);
    }
}
