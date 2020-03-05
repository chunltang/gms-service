package com.zs.gms.service.terminalmanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.terminalmanager.Gps;
import com.zs.gms.mapper.terminalmanager.GpsMapper;
import com.zs.gms.service.terminalmanager.GpsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
@Service
public class GpsServiceImpl extends ServiceImpl<GpsMapper, Gps> implements GpsService {

    @Override
    @Transactional
    public void addGps(Gps gps) {
        gps.setCreateTime(new Date());
        this.save(gps);
    }

    @Override
    @Transactional
    public void delGps(Integer gpsId) {
        this.removeById(gpsId);
    }

    @Override
    @Transactional
    public boolean isExistNo(Integer gpsNo) {
        LambdaQueryWrapper<Gps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Gps::getGpsNo,gpsNo);
        return null!=this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistId(Integer gpsId) {
        LambdaQueryWrapper<Gps> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Gps::getGpsId,gpsId);
        return null!=this.getOne(queryWrapper);
    }

    @Override
    public List<Gps> getAllGps() {
        return this.list();
    }

    @Override
    @Transactional
    public void updateGps(Gps gps) {
        this.updateById(gps);
    }
}
