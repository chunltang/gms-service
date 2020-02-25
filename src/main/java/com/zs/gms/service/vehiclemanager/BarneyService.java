package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.common.entity.QueryRequest;

import java.util.List;


public interface BarneyService extends IService<Barney> {

    public void addVehicle(Barney barney);

    public void updateVehicle(Barney barney);

    public void deleteVehicle(String vehicleIds);

    /**
     * 批量添加用户车辆关系
     * */
    public void addUserVehicles(Integer userId,String vehicleIds);

    /**
     * 查询车辆是否已分配
     * */
    public boolean isVehicleAllot(String vehicleIds);

    /**
     * 分页查询
     * */
    public IPage<Barney> getVehicleList(Barney barney, QueryRequest queryRquest);

    /**
     * 根据用户id获取车辆集合
     * */
    public List<Barney> getVehicleListByUserId(Integer userId);

    /**
     * 根据车辆编号查用户id
     * */
    public Integer getUserIdByVehicleNo(Integer vehicleNo);

    /**
     * 查询车辆是否已添加
     * */
    public boolean queryVhicleExist(Integer vehicleNo);

    /**
     * 查询所有车辆编号
     * */
    public List<Integer> getAllVehicleNos();
}
