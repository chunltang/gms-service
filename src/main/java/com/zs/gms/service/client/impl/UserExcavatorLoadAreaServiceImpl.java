package com.zs.gms.service.client.impl;

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
    public void bindExcavator(UserExcavatorLoadArea bind) {
        bind.setCreateTime(new Date());
    }

    @Override
    @Transactional
    public void updateLoadArea(Integer excavatorId,Integer mapId, Integer loadArea) {
        LambdaUpdateWrapper<UserExcavatorLoadArea> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(UserExcavatorLoadArea::getMapId,mapId);
        updateWrapper.set(UserExcavatorLoadArea::getLoadArea,loadArea);
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
