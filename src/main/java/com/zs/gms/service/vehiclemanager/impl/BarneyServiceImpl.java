package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.annotation.Mark;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.interfaces.MarkInterface;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.PropertyUtil;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.entity.vehiclemanager.BarneyVehicleType;
import com.zs.gms.entity.vehiclemanager.UserVehicle2;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.mapper.vehiclemanager.BarneyMapper;
import com.zs.gms.service.init.SyncRedisData;
import com.zs.gms.service.vehiclemanager.BarneyService;
import com.zs.gms.service.vehiclemanager.BarneyTypeService;
import com.zs.gms.service.vehiclemanager.BarneyVehicleTypeService;
import com.zs.gms.service.vehiclemanager.UserBarneyService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "vehicles")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BarneyServiceImpl extends ServiceImpl<BarneyMapper, Barney> implements BarneyService, MarkInterface {


    @Autowired
    @Lazy
    private BarneyVehicleTypeService barneyVehicleTypeService;

    @Autowired
    private BarneyTypeService barneyTypeService;

    /**
     * 添加车辆
     */
    @Override
    @Transactional
    public void addVehicle(Barney barney) {
        barney.setVehicleStatus(Barney.DEFAULT_STATUS);
        barney.setAddTime(new Date());
        this.save(barney);
        Integer userId = barney.getUserId();
        Integer vehicleId = barney.getVehicleId();
        Integer vehicleTypeId = barney.getVehicleTypeId();
        addVehicleVehicleType(vehicleTypeId, vehicleId);
        if (null != userId) {
            addUserVehicle(userId, vehicleId);
        }
        updateTypeActive();
    }

    @Override
    @Transactional
    public void updateTypeActive(){
        List<Barney> barneys = this.baseMapper.findVehicleList(null);
        Set<Integer> collect = barneys.stream().map(Barney::getVehicleTypeId).collect(Collectors.toSet());
        barneyTypeService.updateActive(collect);
    }


    @Override
    @Transactional
    public void updateVehicleStatus(Collection<Integer> vehicleNos, WhetherEnum status) {
        updateNoStatus();
        if(vehicleNos.size()>0){
            LambdaUpdateWrapper<Barney> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Barney::getVehicleStatus,status);
            updateWrapper.in(Barney::getVehicleNo,vehicleNos);
            this.update(updateWrapper);
        }
    }

    /**
     * 将系统里的车都设为非激活
     * */
    @Transactional
    public void updateNoStatus(){
        LambdaUpdateWrapper<Barney> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Barney::getVehicleStatus, WhetherEnum.NO);
        this.update(updateWrapper);
    }

    /**
     * 修改车辆信息
     */
    @Override
    @Transactional
    @Mark(value = "修改车辆信息", markImpl = BarneyServiceImpl.class)
    public void updateVehicle(Barney barney) {
        Integer userId = barney.getUserId();
        Integer vehicleId = barney.getVehicleId();
        //修改车辆归属
        if (null != userId && null != vehicleId) {
            addUserVehicle(userId, vehicleId);
            barney.setUserId(null);
        }
        //修改车辆类型
        Integer vehicleTypeId = barney.getVehicleTypeId();
        if (null != vehicleTypeId && null != vehicleId) {
            barneyVehicleTypeService.deleteByVehicleId(vehicleId);
            addVehicleVehicleType(vehicleTypeId, vehicleId);
            barney.setVehicleTypeId(null);
        }
        if (!PropertyUtil.isAllFieldNull(barney, "vehicleId","userId","vehicleTypeId")) {
            this.updateById(barney);
        }

    }

    /**
     * 添加用户车辆关系
     */
    @Transactional
    public void addUserVehicle(Integer userId, Integer vehicleId) {
        UserVehicle2 userVehicle = new UserVehicle2();
        userVehicle.setUserId(userId);
        userVehicle.setVehicleId(vehicleId);
    }

    /**
     * 批量分配用户车辆
     */
    @Mark(value = "批量分配用户车辆", markImpl = BarneyServiceImpl.class)
    public void addUserVehicles(Integer userId, String vehicleIds) {
        String[] ids = StringUtils.split(vehicleIds, StringPool.COMMA);
        for (String id : ids) {
            UserVehicle2 userVehicle = new UserVehicle2();
            userVehicle.setUserId(userId);
            userVehicle.setVehicleId(Integer.valueOf(id));
        }
    }

    @Override
    @Transactional
    public boolean isExistVehicleNo(Integer vehicleNo) {
        LambdaQueryWrapper<Barney> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Barney::getVehicleNo, vehicleNo);
        return this.list(queryWrapper).size()>0;
    }

    /**
     * 添加车辆和车辆类型关系
     */
    @Transactional
    public void addVehicleVehicleType(Integer vehicleTypeId, Integer vehicleId) {
        BarneyVehicleType barneyVehicleType = new BarneyVehicleType();
        barneyVehicleType.setVehicleTypeId(vehicleTypeId);
        barneyVehicleType.setVehicleId(vehicleId);
        barneyVehicleTypeService.addVehicleVehicleType(barneyVehicleType);
    }

    /**
     * 删除车辆信息
     */
    @Override
    @Transactional
    public void deleteVehicle(Integer vehicleId) {
        this.removeById(vehicleId);
        barneyVehicleTypeService.deleteByVehicleId(vehicleId);
        updateTypeActive();
    }

    @Override
    @Transactional
    public Barney getBarneyById(Integer vehicleId) {
        return this.getById(vehicleId);
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getBarneyBaseInfos() {
        return this.baseMapper.getBarneyBaseInfos();
    }


    /**
     * 分页获取车辆列表
     *
     * @param queryRequest 分页对象
     */
    @Override
    @Transactional
    public IPage<Barney> getVehicleList(Barney barney, QueryRequest queryRequest) {
        LinkedHashMap<String, Boolean> sortFiledMap=new LinkedHashMap<>();
        sortFiledMap.put("vehicleStatus",false);
        sortFiledMap.put("vap",false);
        return this.baseMapper.findVehicleListPage(SortUtil.getPage(queryRequest,sortFiledMap,Barney.class), barney);
    }


    @Override
    public List<Barney> getAllVehicles() {
        /*LambdaQueryWrapper<Barney> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Barney::getVehicleStatus, WhetherEnum.YES);*/
        return this.list();
    }

    /**
     * 根据用户id获取车辆列表
     */
    @Override
    @Transactional
    public List<Barney> getVehicleListByUserId(Integer userId) {
        Barney barney = new Barney();
        barney.setUserId(userId);
        return this.baseMapper.findVehicleList(barney);
       /* return barneyList.stream().filter(v -> {
            return v.getVehicleStatus().equals(WhetherEnum.YES);
        }).collect(Collectors.toList());*/
    }

    /**
     * 根据车辆编号获取用户id
     */
    /*@Override
    @Transactional
    @Cacheable(cacheNames = "vehicles", key = "'getUserIdByVehicleNo'+#p0", unless = "#result==null")
    public Integer getUserIdByVehicleNo(Integer vehicleNo) {
        return this.baseMapper.findUserIdByVehicleNo(vehicleNo);
    }*/

    /**
     * 根据车牌号是否已添加
     */
    @Override
    @Transactional
    public boolean queryVehicleExistNo(Integer vehicleNo) {
        Integer count = this.baseMapper.selectCount(new LambdaQueryWrapper<Barney>().eq(Barney::getVehicleNo, vehicleNo));
        return count > 0;
    }

    @Override
    public boolean queryVehicleExistId(Integer vehicleId) {
        Integer count = this.baseMapper.selectCount(new LambdaQueryWrapper<Barney>().eq(Barney::getVehicleId, vehicleId));
        return count > 0;
    }

    @Override
    @Transactional
    public List<Integer> getAllVehicleNos() {
        List<Barney> barneys = getAllVehicles();
        return barneys.stream().map(vehicle -> vehicle.getVehicleNo()).collect(Collectors.toList());
    }

    @Override
    public void execute() {
        RedisService.deleteLikeKey(StaticConfig.CACHE_DB, "getUserIdByVehicleNo");//删除缓存数据
        SyncRedisData syncRedisData = SpringContextUtil.getBean(SyncRedisData.class);
        syncRedisData.syncBarneyBaseInfos();
    }
}
