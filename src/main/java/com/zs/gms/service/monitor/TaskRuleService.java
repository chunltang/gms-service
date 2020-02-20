package com.zs.gms.service.monitor;

import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.entity.monitor.TaskRule;

import java.util.List;

public interface TaskRuleService {

    public void addTaskRule(TaskRule taskRule);

    /**
     * 根据车辆编号修改规则执行状态
     * */
    public void updateTaskRuleStatus(Integer userId,String vehicleId, TaskRule.Status status);

    /**
     * 修改指定单元所有车辆规则状态
     * */
    public void updateTaskRulesStatus(Integer unitId,TaskRule.Status status);

    public boolean queryVehicleUsed(String vehicleId);

    /**
     * 获取指定单元所有车辆规则
     * */
    public List<TaskRule> getTaskRulesByUnitId(Integer unitId);

    /**
     * 获取指定调度单元车辆集合
     * */
    public List<Barney> getUnitVehicleList(Integer unitId);

    /**
     * 获取指定用户已分配车辆
     * */
    public List<Barney> getVehiclesByUserId(Integer userId);

    /**
     * 获取装卸调度单元最新规则
     * */
     public TaskRule getLastLoadTaskRule(Integer unitId);

     /**
      * 获取指定车辆调度任务
      * */
     public TaskRule getTaskRuleByVehicleId(Integer userId,Integer vehicleId);
}
