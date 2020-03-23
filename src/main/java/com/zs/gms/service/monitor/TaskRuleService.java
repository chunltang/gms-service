package com.zs.gms.service.monitor;

import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.entity.monitor.TaskRule;

import java.util.List;

public interface TaskRuleService {

    void addTaskRule(TaskRule taskRule);

    /**
     * 修改指定单元所有车辆规则状态
     */
    void updateTaskRulesStatus(Integer unitId, TaskRule.Status status);

    /**
     * 修改指定车辆状态
     */
    void updateVehicleStatus(Integer vehicleId, TaskRule.Status status);

    boolean queryVehicleUsed(String vehicleId);

    /**
     * 获取指定单元所有车辆规则
     */
    List<TaskRule> getTaskRulesByUnitId(Integer unitId);

    /**
     * 获取指定调度单元车辆集合
     */
    List<Barney> getUnitVehicleList(Integer unitId);

    /**
     * 获取指定用户已分配车辆
     */
    List<Barney> getVehiclesByUserId(Integer userId);

    /**
     * 获取装卸调度单元最新规则
     */
    TaskRule getLastLoadTaskRule(Integer unitId);

    /**
     * 获取指定车辆调度任务
     */
    TaskRule getTaskRuleByVehicleId(Integer userId, Integer vehicleId);
}
