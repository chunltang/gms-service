package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.UserVehicle2;

public interface UserBarneyService2 extends IService<UserVehicle2> {


    void addUserVehicle(UserVehicle2 userVehicle2);

    void deleteByVehicleId(Integer vehicleId);

    /**
     * 查询车辆是否已分配
     */
    boolean isVehiclesAllot(String vehicleIds);
}
