package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.UserVehicle;

public interface UserBarneyService extends IService<UserVehicle> {


    void addUserVehicle(UserVehicle userVehicle);

    void deleteByVehicleId(Integer vehicleId);

    /**
     * 查询车辆是否已分配
     */
    boolean isVehiclesAllot(String vehicleIds);
}
