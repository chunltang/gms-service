package com.zs.gms.service.monitor.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.annotation.Mark;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.mapper.monitor.UnitMapper;
import com.zs.gms.service.monitor.UnitService;
import com.zs.gms.service.monitor.UnitVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements UnitService {

    @Autowired
    private UnitVehicleService unitVehicleService;

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
    public List<Unit> getUnitListByUserId(Integer userId,Integer mapId) {
        clearUnitSAndVehicles(mapId);
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUserId,userId);
        queryWrapper.eq(Unit::getMapId,mapId);
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public List<Unit> getUnitListByLoadId(Integer loadId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getLoadAreaId,loadId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public Unit getUnitByLoadId(Integer mapId, Integer loadId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getLoadAreaId,loadId);
        queryWrapper.eq(Unit::getMapId,mapId);
        return this.getOne(queryWrapper);
    }

    @Override
    public Unit getUnitByUnloadId(Integer mapId, Integer unloadId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUnLoadAreaId,unloadId);
        queryWrapper.eq(Unit::getMapId,mapId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    @Mark(value = "清除调度单元车辆", markImpl = UnitVehicleServiceImpl.class)
    public void clearUnitSAndVehicles(Integer mapId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Unit::getMapId,mapId);
        List<Unit> units = this.list(queryWrapper);
        if(GmsUtil.CollectionNotNull(units)){
            for (Unit unit : units) {
                deleteUnit(unit.getUnitId());
            }
        }
    }

    @Override
    @Transactional
    public boolean isExistName(String unitName) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUnitName,unitName);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public boolean isExistLoadId(Integer loadId,Integer mapId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getLoadAreaId,loadId);
        queryWrapper.eq(Unit::getMapId,mapId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    public boolean isExistUnloadId(Integer unloadId,Integer mapId){
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getUnLoadAreaId,unloadId);
        queryWrapper.eq(Unit::getMapId,mapId);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
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
    public IPage<Unit> getUnitList(QueryRequest queryRequest,Integer mapId) {
        Page<Unit> page=new Page<>();
        SortUtil.handlePageSort(queryRequest,page, GmsConstant.SORT_DESC,"unitId");
        return this.baseMapper.getUnitListPage(page,mapId);
    }

    @Override
    @Transactional
    public List<Unit> getUnitListByMapId(Integer mapId) {
        LambdaQueryWrapper<Unit> queryWrapper = new LambdaQueryWrapper<Unit>();
        queryWrapper.eq(Unit::getMapId,mapId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    @Mark(value = "清除调度单元车辆", markImpl = UnitVehicleServiceImpl.class)
    public void updateUnit(Unit unit) {
        LambdaUpdateWrapper<Unit> updateWrapper = new LambdaUpdateWrapper<>();
        if(null==unit.getCycleTimes()||unit.getCycleTimes()<0){
            updateWrapper.set(Unit::getCycleTimes,null);
        }
        if(null==unit.getEndTime()){
            updateWrapper.set(Unit::getEndTime,null);
        }
        this.update(unit,updateWrapper);
    }

    @Override
    @Transactional
    @Mark(value = "清除调度单元车辆", markImpl = UnitVehicleServiceImpl.class)
    public void updateUnitUnloadId(Integer unitId, Integer unloadId) {
        LambdaUpdateWrapper<Unit> updateWrapper = new LambdaUpdateWrapper<Unit>();
        updateWrapper.eq(Unit::getUnitId,unitId);
        updateWrapper.set(Unit::getUnLoadAreaId,unloadId);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    @Mark(value = "清除调度单元车辆", markImpl = UnitVehicleServiceImpl.class)
    public void deleteUnit(Integer unitId) {
       if(this.removeById(unitId)){
           unitVehicleService.removeAllVehicleId(unitId);
           MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
           entry.setHttp(false);
           Map<String, Object> paramMap = new HashMap<>();
           paramMap.put("unitId", unitId);
           MessageFactory.getDispatchMessage().sendMessageNoResWithID(entry.getMessageId(), "RemoveAIUnit", JSONObject.toJSONString(paramMap));
       }
    }

    @Override
    @Transactional
    public void updateStatus(Integer unitId, Unit.Status status) {
        LambdaUpdateWrapper<Unit> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Unit::getUnitId,unitId);
        updateWrapper.set(Unit::getStatus,status);
        if(status.equals(Unit.Status.STOP)||status.equals(Unit.Status.DELETE)){
            unitVehicleService.updateStatus(unitId, WhetherEnum.NO);
        }
        this.update(updateWrapper);
    }
}
