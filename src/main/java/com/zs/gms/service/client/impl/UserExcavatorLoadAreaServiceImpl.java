package com.zs.gms.service.client.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.mapper.client.UserExcavatorLoadAreaMapper;
import com.zs.gms.service.client.UserExcavatorLoadAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class UserExcavatorLoadAreaServiceImpl extends ServiceImpl<UserExcavatorLoadAreaMapper, UserExcavatorLoadArea> implements UserExcavatorLoadAreaService {

    @Override
    @Transactional
    public boolean isExistUser(Integer userId) {
        return null!= getBindByUser(userId);
    }

    @Override
    @Transactional
    public void bindExcavator(UserExcavatorLoadArea bind) {
        bind.setCreateTime(new Date());
        this.save(bind);
    }

    @Override
    @Transactional
    public UserExcavatorLoadArea getBindByUser(Integer userId) {
        LambdaQueryWrapper<UserExcavatorLoadArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserExcavatorLoadArea::getUserId,userId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public UserExcavatorLoadArea getBindByLoad(Integer loadId) {
        LambdaQueryWrapper<UserExcavatorLoadArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserExcavatorLoadArea::getLoadAreaId,loadId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void updateLoadArea(Integer excavatorId,Integer mapId, Integer loadArea) {
        LambdaUpdateWrapper<UserExcavatorLoadArea> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserExcavatorLoadArea::getMapId,mapId);
        updateWrapper.set(UserExcavatorLoadArea::getLoadAreaId,loadArea);
        updateWrapper.eq(UserExcavatorLoadArea::getExcavatorId,excavatorId);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public void updateUser(Integer excavatorId, Integer userId) {
        LambdaUpdateWrapper<UserExcavatorLoadArea> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserExcavatorLoadArea::getExcavatorId,excavatorId);
        updateWrapper.set(UserExcavatorLoadArea::getUserId,userId);
        this.update(updateWrapper);
    }
}
