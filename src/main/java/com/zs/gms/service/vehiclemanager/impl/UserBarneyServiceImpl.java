package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.annotation.Mark;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.entity.vehiclemanager.UserVehicle;
import com.zs.gms.mapper.vehiclemanager.UserBarneyMapper;
import com.zs.gms.service.vehiclemanager.UserBarneyService;
import com.zs.gms.common.service.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class UserBarneyServiceImpl extends ServiceImpl<UserBarneyMapper, UserVehicle> implements UserBarneyService {

    @Override
    @Transactional
    public void addUserVehicle(UserVehicle userVehicle) {
        this.save(userVehicle);
    }

    @Override
    @Transactional
    @Mark(value = "删除车辆分配信息",markImpl = BarneyServiceImpl.class)
    public void deleteByVehicleId(Integer vehicleId) {
        this.remove(new LambdaQueryWrapper<UserVehicle>().eq(UserVehicle::getVehicleId,vehicleId));
    }

    @Override
    public boolean isVehiclesAllot(String vehicleIds) {
        LambdaQueryWrapper<UserVehicle> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserVehicle::getVehicleId, StringUtils.split(vehicleIds,StringPool.COMMA));
        int count = this.count(queryWrapper);
        return count>0?true:false;
    }
}
