package com.zs.gms.service.monitor;

import com.zs.gms.entity.monitor.DispatchTask;
import com.zs.gms.enums.monitor.UnitTypeEnum;

import java.util.List;

 public interface DispatchTaskService {

     Integer addDispatchTask(DispatchTask dispatchTask);

    /**
     * 根据用户id和单元类型获取unitId
     * */
     Integer getUnitIdByUserIdAndType(Integer userId, UnitTypeEnum unitType,Integer mapId);

    /**
     * 获取用户调度单元列表
     * */
     List<DispatchTask> getDispatchTaskList(Integer userId,Integer mapId);

    /**
     * 获取调度单元列表,非删除状态
     * */
     List<DispatchTask> getDispatchTaskList(Integer mapId);

    /**
     * 查询指定类型调度单元
     * @param dispatchTaskType 调度单元类型
     * */
     List<DispatchTask> getDispatchTaskList(Integer userId,String dispatchTaskType,Integer mapId);

    /**
     * 删除调度单元,将状态置位删除状态
     * */
     void deleteByUnitId(Integer unitId);

    /**
     * 修改单元状态,所有相关车辆规则触发改变状态
     */
     void updateUnitStatusByUnitId(Integer unitId, DispatchTask.Status status);
    
    /**
     * 根据装载区id获取运行中的调度单元
     * */
    DispatchTask getUnitByLoadId(Integer loadAreaId);

    void updateUnloadId(Integer unitId,Integer unloadId);
}
