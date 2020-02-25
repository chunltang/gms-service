package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.UserVehicle;

public interface UserBarneyService extends IService<UserVehicle> {

    public Integer getUserIdByVehicleId(Integer vehicleId);

    public void deteleByVehicleIds(String[] vehicleIds);

    public void addUserVehicle(UserVehicle userVehicle);

    public void deteleByVehicleId(Integer vehicleId);

    /**
     * 查询车辆是否已分配
     * */
    public boolean isVehiclesAllot(String vehicleIds);
}
