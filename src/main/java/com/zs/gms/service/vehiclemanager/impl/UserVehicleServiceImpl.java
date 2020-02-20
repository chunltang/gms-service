package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.entity.vehiclemanager.UserVehicle;
import com.zs.gms.mapper.vehiclemanager.UserVehicleMapper;
import com.zs.gms.service.vehiclemanager.UserVehicleService;
import com.zs.gms.common.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class UserVehicleServiceImpl extends ServiceImpl<UserVehicleMapper, UserVehicle> implements UserVehicleService {


    @Override
    @Transactional
    @Cacheable(cacheNames = "vehicles",key = "targetClass+#p0",unless = "#result==null")
    public Integer getUserIdByVehicleId(Integer vehicleId) {
        UserVehicle userVehicle = this.getOne(new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getVehicleId, vehicleId));
        if(null!=userVehicle){
            return userVehicle.getUserId();
        }
        return null;
    }

    @Override
    @Transactional
    public void deteleByVehicleIds(String[] vehicleIds) {
        this.remove(new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getVehicleId,vehicleIds));
        RedisService.deleteLikeKey(GmsConstant.KEEP_DB,this.getClass().getName());
        RedisService.deleteLikeKey(GmsConstant.KEEP_DB,"getUserIdByVehicleNo");
    }

    @Override
    @Transactional
    public void addUserVehicle(UserVehicle userVehicle) {
        this.save(userVehicle);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "vehicles",key = "targetClass+#p0")
    public void deteleByVehicleId(Integer vehicleId) {
        this.remove(new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getVehicleId,vehicleId));
        RedisService.deleteLikeKey(GmsConstant.KEEP_DB,"getUserIdByVehicleNo");
    }

    @Override
    public boolean isVehiclesAllot(String vehicleIds) {
        LambdaQueryWrapper<UserVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserVehicle::getVehicleId, StringUtils.split(vehicleIds,StringPool.COMMA));
        int count = this.count(queryWrapper);
        return count>0?true:false;
    }
}
