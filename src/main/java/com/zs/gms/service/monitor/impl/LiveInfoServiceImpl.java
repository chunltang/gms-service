package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.annotation.RedisLock;
import com.zs.gms.entity.monitor.VehicleLiveInfo;
import com.zs.gms.mapper.monitor.LiveInfoMapper;
import com.zs.gms.service.monitor.LiveInfoService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class LiveInfoServiceImpl extends ServiceImpl<LiveInfoMapper, VehicleLiveInfo> implements LiveInfoService {

    @Override
    @Transactional
    @Async(value = "gmsAsyncThreadPool")
    @RedisLock(key = "vehicleLiveInfo",seconds = 30L)
    public void addLiveInfo(VehicleLiveInfo vehicleLiveInfo) {
        Map<String,Object> params=new HashMap<>();
        getParams(vehicleLiveInfo.getMonitor(),params);
        getParams(vehicleLiveInfo,params,"monitor");
        //DataUtil.pushDataToDruid(vehicleLiveInfo.getVehicleId().toString(),JSON.toJSONString(params));
    }

    public void getParams(Object obj,Map<String,Object> params,String ...excludes){
        List<String> list = Arrays.asList(excludes);
        Field[] mFields = obj.getClass().getDeclaredFields();
        for (Field mField : mFields) {
            String name = mField.getName();
            if(list.contains(name)){
                continue;
            }
            mField.setAccessible(true);
            try {
                params.put(name,mField.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
