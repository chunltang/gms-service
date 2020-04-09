package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.vehiclemanager.VehicleMaintainTask;
import com.zs.gms.enums.vehiclemanager.DateEnum;

import java.util.Date;
import java.util.List;

public interface VehicleMaintainTaskService {

    /**
     * 新增维护任务
     * */
    void addMaintainTask(VehicleMaintainTask vehicleMaintainTask);

    /**
     * 修改维修任务
     * */
    void updateMaintainTask(Integer id, Integer num, DateEnum units);

    /**
     * 删除维护任务
     * */
    boolean deleteMaintainTask(Integer id);

    /**
     * 判断维护任务是否存在,true为存在
     * */
    boolean isExistId(Integer id);

    /**
     * 修改下次任务执行时间
     * */
    void updateNextTime(Integer id, Date nextTime);

    /**
     * 分页获取所有维护任务
     * */
    IPage getMaintainTasks(QueryRequest queryRequest);

    /**
     * 获取所有维护任务
     * */
    List<VehicleMaintainTask> getMaintainTasks();

    /**
     * 根据数据id查询
     * */
    VehicleMaintainTask getMaintainTaskById(Integer id);

    /**
     * 根据车辆编号查询车辆的维护任务
     * */
    List<VehicleMaintainTask> getMaintainTaskByVehicleId(Integer vehicleId);

    /**
     * 根据用户id查询车辆的维护任务
     * */
    List<VehicleMaintainTask> getMaintainTaskByUserId(Integer userId);
    /**
     * 修改处理状态
     * */
    void updateHandleStatus(Integer id,VehicleMaintainTask.Status status);

    /**
     * 加载所有维护任务
     * */
    void loadMaintainTasks();

    /**
     * 加载单个维护任务
     * */
    void loadMaintainTask(Integer id);

    void loadMaintainTask(VehicleMaintainTask maintainTask);

    /**
     * @param taskId 为null时清除所所有缓存的任务
     * */
    void clearCacheTask(String taskId);

    //重新加载指定任务
    void reloadTask(VehicleMaintainTask maintainTask);

    /**
     * 获取taskId
     * */
    public String getTaskId(Integer id,Integer vehicleId);
}
