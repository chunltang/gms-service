package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.common.entity.QueryRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public interface BarneyService extends IService<Barney> {

    void addVehicle(Barney barney);

    void updateTypeActive();

    void updateVehicle(Barney barney);

    void deleteVehicle(Integer vehicleId);

    Barney getBarneyById(Integer vehicleId);

    /**
     * 获取车辆基本信息
     * */
    List<Map<String,Object>> getBarneyBaseInfos();

    /**
     * 改变车辆的激活状态
     * */
    void updateVehicleStatus(Collection<Integer> vehicleNos, WhetherEnum status);

    /**
     * 批量添加用户车辆关系
     */
    void addUserVehicles(Integer userId, String vehicleIds);

    /**
     * 查询车辆编号是否存在
     * */
    boolean isExistVehicleNo(Integer vehicleNo);


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
    //Integer getUserIdByVehicleNo(Integer vehicleNo);

    /**
     * 查询车辆是否已添加
     */
    boolean queryVehicleExistNo(Integer vehicleNo);

    boolean queryVehicleExistId(Integer vehicleId);

    /**
     * 查询所有车辆编号
     */
    List<Integer> getAllVehicleNos();


}
