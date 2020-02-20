package com.zs.gms.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.gms.entity.vehiclemanager.Vehicle;
import com.zs.gms.entity.monitor.TaskRule;

import java.util.List;

public interface TaskRuleMapper extends BaseMapper<TaskRule> {

    /**
     * 根据车辆id获取最新它最新的任务
     * */
    public TaskRule  findVehicleLastTimeRule(String vehicleId);

    /**
     * 获取指定调度单元车辆集合
     * */
    public List<Vehicle> getUnitVehicleList(Integer unitId);

    /**
     * 根据unitId获取装卸单元的最新规则
     * */
    public TaskRule getLastLoadTaskRule(Integer unitId);

    /**
     * 获取指定用户已分配车辆
     * */
    public List<Vehicle> getVehiclesByUserId(Integer userId);
}
