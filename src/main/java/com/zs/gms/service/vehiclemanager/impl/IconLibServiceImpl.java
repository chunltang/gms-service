package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.UseStatus;
import com.zs.gms.entity.vehiclemanager.IconLib;
import com.zs.gms.mapper.vehiclemanager.IconLibMapper;
import com.zs.gms.service.vehiclemanager.IconLibService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class IconLibServiceImpl extends ServiceImpl<IconLibMapper,IconLib> implements IconLibService {

    @Override
    @Transactional
    public void addIconLib(IconLib lib) {
        lib.setCreateTime(new Date());
        this.save(lib);
    }

    @Override
    @Transactional
    public List<IconLib> getLibs() {
        LambdaQueryWrapper<IconLib> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IconLib::getStatus, UseStatus.ENABLE);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public void delById(Integer id) {
        LambdaUpdateWrapper<IconLib> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(IconLib::getStatus,UseStatus.DISABLE);
        updateWrapper.eq(IconLib::getLibId,id);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean isExist(Integer id) {
        return null!=getLib(id);
    }

    @Override
    @Transactional
    public boolean isExistName(String name) {
        LambdaQueryWrapper<IconLib> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IconLib::getStatus,UseStatus.ENABLE);
        queryWrapper.eq(IconLib::getName,name);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    public IconLib getLib(Integer id) {
        LambdaQueryWrapper<IconLib> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IconLib::getStatus,UseStatus.ENABLE);
        queryWrapper.eq(IconLib::getLibId,id);
        return this.getOne(queryWrapper);
    }
}
