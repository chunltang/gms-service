package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.vehiclemanager.MaintainTaskInfo;
import com.zs.gms.mapper.vehiclemanager.MaintainTaskInfoMapper;
import com.zs.gms.service.vehiclemanager.MaintainTaskInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class MaintainTaskInfoServiceImpl  extends ServiceImpl<MaintainTaskInfoMapper, MaintainTaskInfo> implements MaintainTaskInfoService {

    @Override
    @Transactional
    public boolean addMaintainTaskInfo(MaintainTaskInfo taskInfo) {
        taskInfo.setHandleTime(new Date());
        return this.save(taskInfo);
    }

    @Override
    public List<MaintainTaskInfo> getInfos(Integer vehicleId, Integer userId) {
        LambdaQueryWrapper<MaintainTaskInfo> queryWrapper = new LambdaQueryWrapper<>();
        if(null!=vehicleId){
            queryWrapper.eq(MaintainTaskInfo::getVehicleId,vehicleId);
        }
        if(null!=userId){
            queryWrapper.eq(MaintainTaskInfo::getUserId,userId);
        }
        queryWrapper.orderByDesc(MaintainTaskInfo::getHandleTime);
        return list(queryWrapper);
    }

}
