package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.mapper.client.UserExcavatorLoadAreaMapper;
import com.zs.gms.service.vehiclemanager.UserExcavatorLoadAreaService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "bindExecvators")
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class UserExcavatorLoadAreaServiceImpl extends ServiceImpl<UserExcavatorLoadAreaMapper, UserExcavatorLoadArea> implements UserExcavatorLoadAreaService {

    @Override
    @Transactional
    public boolean isExistUser(Integer userId) {
        return null!= getBindByUser(userId);
    }

    @Override
    @Transactional
    public Collection<Integer> getAllExcavatorNos() {
        List<UserExcavatorLoadArea> list = this.list();
        return list.stream().map(UserExcavatorLoadArea::getExcavatorId).collect(Collectors.toSet());
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
    //@Cacheable(key = "'getBindByLoad'+#p0",unless = "#result==null")
    public UserExcavatorLoadArea getBindByLoad(Integer loadId) {
        LambdaQueryWrapper<UserExcavatorLoadArea> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserExcavatorLoadArea::getLoadAreaId,loadId);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    //@CacheEvict(key = "'getBindByLoad'+#p2")
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
