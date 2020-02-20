package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.vehiclemanager.UserVehicle;
import com.zs.gms.entity.vehiclemanager.Vehicle;
import com.zs.gms.entity.vehiclemanager.VehicleVehicleType;
import com.zs.gms.mapper.vehiclemanager.VehicleMapper;
import com.zs.gms.service.vehiclemanager.UserVehicleService;
import com.zs.gms.service.vehiclemanager.VehicleService;
import com.zs.gms.service.vehiclemanager.VehicleVehicleTypeService;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.PropertyUtil;
import com.zs.gms.common.utils.SortUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "vehicles")
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {

    @Autowired
    @Lazy
    private UserVehicleService userVehicleService;

    @Autowired
    @Lazy
    private VehicleVehicleTypeService vehicleVehicleTypeService;

    /**
     * 添加车辆
     * */
    @Override
    @Transactional
    public void addVehicle(Vehicle vehicle) {
        vehicle.setVehicleStatus(Vehicle.DEFAULT_STATUS);
        vehicle.setAddTime(new Date());
        this.save(vehicle);
        Integer userId = vehicle.getUserId();
        Integer vehicleId = vehicle.getVehicleId();
        Integer vehicleTypeId = vehicle.getVehicleTypeId();
        addVehicleVehicleType(vehicleTypeId,vehicleId);
        if(null!=userId){
            addUserVehicle(userId,vehicleId);
        }
    }

    /**
     * 修改车辆信息
     * */
    @Override
    @Transactional
    @CacheEvict(cacheNames = "vehicles",key = "'getUserIdByVehicleNo'+#p0.vehicleNo")
    public void updateVehicle(Vehicle vehicle) {
        Integer userId = vehicle.getUserId();
        Integer vehicleId = vehicle.getVehicleId();
        //修改车辆归属
        if(null!=userId){
           userVehicleService.deteleByVehicleId(vehicleId);
           addUserVehicle(userId,vehicleId);
            vehicle.setUserId(null);
       }
        //修改车辆类型
        Integer vehicleTypeId = vehicle.getVehicleTypeId();
        if(null!=vehicleTypeId){
            vehicleVehicleTypeService.deteleByVehicleId(vehicleId);
            addVehicleVehicleType(vehicleTypeId,vehicleId);
            vehicle.setVehicleTypeId(null);
        }
        if(!PropertyUtil.isAllFieldNull(vehicle,"vehicleId")){
            this.updateById(vehicle);
        }

    }

    /**
     * 添加用户车辆关系
     * */
    @Transactional
    public void addUserVehicle(Integer userId,Integer vehicleId){
        UserVehicle userVehicle=new UserVehicle();
        userVehicle.setUserId(userId);
        userVehicle.setVehicleId(vehicleId);
        userVehicleService.addUserVehicle(userVehicle);
    }

    /**
     * 批量分配用户车辆
     * */
    public void addUserVehicles(Integer userId,String vehicleIds){
        String[] ids = StringUtils.split(vehicleIds, StringPool.COMMA);
        for (String id : ids) {
            UserVehicle userVehicle=new UserVehicle();
            userVehicle.setUserId(userId);
            userVehicle.setVehicleId(Integer.valueOf(id));
            userVehicleService.addUserVehicle(userVehicle);
        }
        RedisService.deleteLikeKey(GmsConstant.KEEP_DB,"getUserIdByVehicleNo");//删除缓存数据
    }

    @Override
    public boolean isVehicleAllot(String vehicleIds) {
        return userVehicleService.isVehiclesAllot(vehicleIds);
    }

    /**
     * 添加车辆和车辆类型关系
     * */
    @Transactional
    public void addVehicleVehicleType(Integer vehicleTypeId,Integer vehicleId){
        VehicleVehicleType vehicleVehicleType=new VehicleVehicleType();
        vehicleVehicleType.setVehicleTypeId(vehicleTypeId);
        vehicleVehicleType.setVehicleId(vehicleId);
        vehicleVehicleTypeService.addVehicleVehicleType(vehicleVehicleType);
    }

    /**
     *删除车辆信息
     * */
    @Override
    @Transactional
    public void deleteVehicle(String vehicleIds) {
        String[] ids = vehicleIds.split(StringPool.COMMA);
        this.baseMapper.deleteBatchIds(Arrays.asList(ids));
        userVehicleService.deteleByVehicleIds(ids);
        vehicleVehicleTypeService.deteleByVehicleIdS(ids);
        RedisService.deleteLikeKey(GmsConstant.KEEP_DB,"getUserIdByVehicleNo");//删除缓存数据
    }

    /**
     * 分页获取车辆列表
     * @param queryRquest 分页对象
     * */
    @Override
    @Transactional
    public IPage<Vehicle> getVehicleList(Vehicle vehicle, QueryRequest queryRquest) {
        Page page=new Page();
        SortUtil.handlePageSort(queryRquest,page, GmsConstant.SORT_DESC,"VEHICLEID");
        return this.baseMapper.findVehicleListPage(page,vehicle);
    }

    /**
     * 根据用户id获取车辆列表
     * */
    @Override
    @Transactional
    public List<Vehicle> getVehicleListByUserId(Integer userId) {
        Vehicle vehicle=new Vehicle();
        vehicle.setUserId(userId);
        List<Vehicle> vehicleList = this.baseMapper.findVehicleList(vehicle);
        return vehicleList.stream().filter(v->{
            return v.getVehicleStatus().equals("1");//0停用，1再用
        }).collect(Collectors.toList());
    }

    /**
     * 根据车辆编号获取用户id
     * */
    @Override
    @Transactional
    @Cacheable(cacheNames = "vehicles",key = "'getUserIdByVehicleNo'+#p0",unless = "#result==null")
    public Integer getUserIdByVehicleNo(Integer vehicleNo) {
         return this.baseMapper.findUserIdByVehicleNo(vehicleNo);
    }

    /**
     * 根据车牌号是否已添加
     * */
    @Override
    @Transactional
    public boolean queryVhicleExist(Integer vhicleNo) {
        Integer count = this.baseMapper.selectCount(new LambdaQueryWrapper<Vehicle>().eq(Vehicle::getVehicleNo, vhicleNo));
        return count > 0? true : false;
    }

    @Override
    @Transactional
    public List<Integer> getAllVehicleNos() {
        LambdaQueryWrapper<Vehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Vehicle::getVehicleStatus,"1");
        queryWrapper.select(Vehicle::getVehicleNo);
        List<Vehicle> vehicles = this.baseMapper.selectList(queryWrapper);
        return vehicles.stream().map(vehicle ->vehicle.getVehicleNo()).collect(Collectors.toList());
    }
}
