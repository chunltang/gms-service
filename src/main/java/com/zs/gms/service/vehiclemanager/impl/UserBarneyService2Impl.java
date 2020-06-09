package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.annotation.Mark;
import com.zs.gms.entity.vehiclemanager.UserVehicle2;
import com.zs.gms.mapper.vehiclemanager.UserBarneyMapper2;
import com.zs.gms.service.vehiclemanager.UserBarneyService2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class UserBarneyService2Impl extends ServiceImpl<UserBarneyMapper2, UserVehicle2> implements UserBarneyService2 {

    @Override
    @Transactional
    public void addUserVehicle(UserVehicle2 userVehicle) {
        this.save(userVehicle);
    }

    @Override
    @Transactional
    @Mark(value = "删除车辆分配信息",markImpl = BarneyServiceImpl.class)
    public void deleteByVehicleId(Integer vehicleId) {
        this.remove(new LambdaQueryWrapper<UserVehicle2>().eq(UserVehicle2::getVehicleId,vehicleId));
    }

    @Override
    public boolean isVehiclesAllot(String vehicleIds) {
        LambdaQueryWrapper<UserVehicle2> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserVehicle2::getVehicleId, StringUtils.split(vehicleIds,StringPool.COMMA));
        int count = this.count(queryWrapper);
        return count>0?true:false;
    }
}
