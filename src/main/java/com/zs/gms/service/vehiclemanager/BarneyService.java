package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.common.entity.QueryRequest;

import java.util.List;


public interface BarneyService extends IService<Barney> {

    void addVehicle(Barney barney);

    void updateVehicle(Barney barney);

    void deleteVehicle(Integer vehicleId);

    /**
     * 批量添加用户车辆关系
     */
    void addUserVehicles(Integer userId, String vehicleIds);

    /**
     * 查询车辆是否已分配
     */
    boolean isVehicleAllot(String vehicleIds);

    /**
     * 分页查询
     */
    IPage<Barney> getVehicleList(Barney barney, QueryRequest queryRequest);

    List<Barney> getAllVehicles();

    /**
     * 根据用户id获取车辆集合
     */
    List<Barney> getVehicleListByUserId(Integer userId);

    /**
     * 根据车辆编号查用户id
     */
    Integer getUserIdByVehicleNo(Integer vehicleNo);

    /**
     * 查询车辆是否已添加
     */
    boolean queryVehicleExist(Integer vehicleNo);

    /**
     * 查询所有车辆编号
     */
    List<Integer> getAllVehicleNos();


}
