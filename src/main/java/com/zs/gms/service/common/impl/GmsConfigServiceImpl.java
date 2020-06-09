package com.zs.gms.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.init.GmsGlobalConfig;
import com.zs.gms.mapper.init.GmsConfigMapper;
import com.zs.gms.service.common.GmsConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class GmsConfigServiceImpl extends ServiceImpl<GmsConfigMapper, GmsGlobalConfig> implements GmsConfigService {

    @Override
    @Transactional
    public void addGmsConfig(GmsGlobalConfig gmsGlobalConfig) {
        String key = gmsGlobalConfig.getConfigKey();
        if(isExistKey(key)){
            LambdaUpdateWrapper<GmsGlobalConfig> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(GmsGlobalConfig::getConfigKey,key);
            updateWrapper.set(GmsGlobalConfig::getConfigValue,gmsGlobalConfig.getConfigValue());
            this.update(updateWrapper);
        }else{
            this.save(gmsGlobalConfig);
        }
    }

    @Override
    @Transactional
    public GmsGlobalConfig getGmsConfig(String key) {
        LambdaQueryWrapper<GmsGlobalConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GmsGlobalConfig::getConfigKey,key);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional
    public void updateGmsGlobalConfig(GmsGlobalConfig gmsGlobalConfig) {
        this.updateById(gmsGlobalConfig);
    }

    @Override
    @Transactional
    public boolean isExistKey(String key) {
        LambdaQueryWrapper<GmsGlobalConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GmsGlobalConfig::getConfigKey,key);
        return this.list(queryWrapper).size()>0;
    }
}
