package com.zs.gms.service.vehiclemanager;

import com.zs.gms.entity.vehiclemanager.MaintainTaskInfo;

import java.util.List;

public interface MaintainTaskInfoService {

    /**
     * 新增处理信息
     * */
    boolean addMaintainTaskInfo(MaintainTaskInfo taskInfo);

    /**
     * 获取维护日志
     * */
    List<MaintainTaskInfo> getInfos(Integer vehicleId,Integer userId);
}
