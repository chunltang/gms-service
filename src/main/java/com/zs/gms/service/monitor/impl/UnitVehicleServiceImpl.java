package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.annotation.Mark;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.common.interfaces.MarkInterface;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.monitor.UnitVehicle;
import com.zs.gms.mapper.monitor.UnitVehicleMapper;
import com.zs.gms.service.monitor.UnitVehicleService;
import com.zs.gms.service.vehiclemanager.BarneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class UnitVehicleServiceImpl extends ServiceImpl<UnitVehicleMapper, UnitVehicle> implements UnitVehicleService, MarkInterface {

    @Autowired
    private BarneyService barneyService;

    @Override
    @Transactional
    public void addUnitVehicles(List<UnitVehicle> unitVehicles) {
        this.saveBatch(unitVehicles);
    }

    @Override
    @Transactional
    public List<Map<String, Integer>> getAllVehicles(Integer mapId) {
        return this.baseMapper.getAllVehicles();
    }

    @Override
    @Transactional
    public void updateVehicleActive() {
        List<UnitVehicle> unitVehicles = this.list();
        //获取在调度单元中的所有车辆编号
        Set<Integer> collect = unitVehicles.stream().map(UnitVehicle::getVehicleId).collect(Collectors.toSet());
        barneyService.updateVehicleStatus(collect, WhetherEnum.YES);
    }

    @Override
    @Transactional
    public void deleteUnitVehicles(Integer unitId, String vehicleIds) {
        if(GmsUtil.StringNotNull(vehicleIds)){
            String[] split = vehicleIds.split(StringPool.COMMA);
            List<Integer> collect = Arrays.stream(split).map(Integer::valueOf).collect(Collectors.toList());
            LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UnitVehicle::getUnitId,unitId);
            queryWrapper.in(UnitVehicle::getVehicleId,collect);
            this.remove(queryWrapper);
        }

    }


    @Override
    @Transactional
    @Mark(value = "清除调度单元车辆", markImpl = UnitVehicleServiceImpl.class)
    public void clearVehiclesByUnitId(Integer unitId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getUnitId,unitId);
        if(this.remove(queryWrapper)){
            updateStatus(unitId, WhetherEnum.NO);
        }
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
    @CacheEvict(cacheNames = "unitVehicles",key = "'getUnitByVehicleId'+#p1")
    public void removeVehicleId(Integer unitId, Integer vehicleId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getUnitId,unitId);
        queryWrapper.eq(UnitVehicle::getVehicleId,vehicleId);
        this.remove(queryWrapper);
    }

    @Override
    @Transactional
    @Mark(value = "清除调度单元车辆", markImpl = UnitVehicleServiceImpl.class)
    public void removeAllVehicleId(Integer unitId) {
        LambdaQueryWrapper<UnitVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UnitVehicle::getUnitId,unitId);
        this.remove(queryWrapper);
    }

    @Override
    @Transactional
    @Cacheable(cacheNames = "unitVehicles", key = "'getUnitByVehicleId'+#p0", unless = "#result==null")
    public Unit getUnitByVehicleId(Integer vehicleId) {
        return this.baseMapper.getUnitByVehicleId(vehicleId);
    }

    @Override
    @Transactional
    public void updateStatus(Integer unitId, WhetherEnum status) {
        LambdaUpdateWrapper<UnitVehicle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UnitVehicle::getUnitId,unitId);
        updateWrapper.set(UnitVehicle::getStatus,status);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "unitVehicles",key = "'getUnitByVehicleId'+#p1")
    public void updateStatus(Integer unitId, Integer vehicleId, WhetherEnum status) {
        LambdaUpdateWrapper<UnitVehicle> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UnitVehicle::getUnitId,unitId);
        updateWrapper.set(UnitVehicle::getStatus,status);
        updateWrapper.eq(UnitVehicle::getVehicleId,vehicleId);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public void execute() {
        RedisService.deleteLikeKey(StaticConfig.CACHE_DB, "getUnitByVehicleId");//删除缓存数据
    }
}
