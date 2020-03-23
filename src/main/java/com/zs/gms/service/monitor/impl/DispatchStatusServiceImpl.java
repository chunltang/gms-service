package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.annotation.RedisLock;
import com.zs.gms.entity.monitor.DispatchStatus;
import com.zs.gms.mapper.monitor.DispatchStatusMapper;
import com.zs.gms.service.monitor.DispatchStatusService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class DispatchStatusServiceImpl extends ServiceImpl<DispatchStatusMapper, DispatchStatus> implements DispatchStatusService{


    @Override
    @Transactional
    @RedisLock(key = "dispatchStatus")
    public void addDispatchStatus(DispatchStatus dispatchStatus) {
        LambdaQueryWrapper<DispatchStatus> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DispatchStatus::getVehicleId,dispatchStatus.getVehicleId());
        queryWrapper.eq(DispatchStatus::getCreateTime,dispatchStatus.getCreateTime());
        List<DispatchStatus> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            this.save(dispatchStatus);
        }
    }

    @Override
    public DispatchStatus getBaseInfo(Integer vehicleId) {
        return this.baseMapper.getBaseInfo(vehicleId);
    }
}
