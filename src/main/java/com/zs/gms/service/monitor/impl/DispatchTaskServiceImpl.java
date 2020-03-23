package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.DispatchTask;
import com.zs.gms.entity.monitor.TaskRule;
import com.zs.gms.enums.monitor.UnitTypeEnum;
import com.zs.gms.mapper.monitor.DispatchTaskMapper;
import com.zs.gms.service.monitor.DispatchTaskService;
import com.zs.gms.service.monitor.TaskRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.zs.gms.entity.monitor.DispatchTask.Status.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class DispatchTaskServiceImpl extends ServiceImpl<DispatchTaskMapper, DispatchTask> implements DispatchTaskService {


    @Autowired
    private TaskRuleService taskRuleService;

    @Override
    @Transactional
    public Integer addDispatchTask(DispatchTask dispatchTask) {
        dispatchTask.setAddTime(new Date());
        this.save(dispatchTask);
        return dispatchTask.getUnitId();
    }

    @Override
    @Transactional
    public List<DispatchTask> getUnitByType(UnitTypeEnum dispatchTaskType) {
        LambdaQueryWrapper<DispatchTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DispatchTask::getDispatchTaskType, dispatchTaskType);
        queryWrapper.ne(DispatchTask::getStatus, DELETE);
        return this.list(queryWrapper);
    }

    @Override
    public DispatchTask getUnitByVehicleId(Integer vehicleId) {
        return this.baseMapper.getUnitByVehicleId(vehicleId);
    }

    @Override
    @Transactional
    public Integer getUnitIdByUserIdAndType(Integer userId, UnitTypeEnum unitType,Integer mapId) {
        LambdaQueryWrapper<DispatchTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DispatchTask::getUserId, userId);
        queryWrapper.eq(DispatchTask::getMapId, mapId);
        queryWrapper.eq(DispatchTask::getDispatchTaskType, unitType);
        DispatchTask dispatchTask = this.baseMapper.selectOne(queryWrapper);
        return dispatchTask==null?null:dispatchTask.getUnitId();
    }

    @Override
    @Transactional
    public List<DispatchTask> getDispatchTaskList(Integer userId, String dispatchTaskType,Integer mapId) {
        LambdaQueryWrapper<DispatchTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DispatchTask::getUserId, userId);
        queryWrapper.eq(DispatchTask::getMapId, mapId);
        queryWrapper.eq(DispatchTask::getDispatchTaskType, dispatchTaskType);
        queryWrapper.ne(DispatchTask::getStatus, DispatchTask.Status.DELETE);
        return this.list(queryWrapper);
    }


    @Override
    @Transactional
    public List<DispatchTask> getDispatchTaskList(Integer userId,Integer mapId) {
        LambdaQueryWrapper<DispatchTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DispatchTask::getUserId, userId);
        queryWrapper.eq(DispatchTask::getMapId, mapId);
        queryWrapper.ne(DispatchTask::getStatus, DispatchTask.Status.DELETE);
        queryWrapper.ne(DispatchTask::getDispatchTaskType, UnitTypeEnum.INTERACTIVE_DISPATCHTASK);
        queryWrapper.orderByDesc(DispatchTask::getAddTime);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public List<DispatchTask> getDispatchTaskList(Integer mapId) {
        LambdaQueryWrapper<DispatchTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(DispatchTask::getStatus, DispatchTask.Status.DELETE);
        queryWrapper.eq(DispatchTask::getMapId,mapId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public void deleteByUnitId(Integer unitId) {
        updateUnitStatusByUnitId(unitId,DispatchTask.Status.DELETE);
    }

    /**
     * 修改单元状态,所有相关车辆规则触发改变状态
     */
    @Override
    @Transactional
    public void updateUnitStatusByUnitId(Integer unitId, DispatchTask.Status status) {
        LambdaUpdateWrapper<DispatchTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DispatchTask::getUnitId, unitId);
        updateWrapper.set(DispatchTask::getStatus, status);
        this.update(updateWrapper);
        switch (status) {
            case DELETE:
                taskRuleService.updateTaskRulesStatus(unitId, TaskRule.Status.DELETE);
                break;
            case STOP:
                taskRuleService.updateTaskRulesStatus(unitId, TaskRule.Status.STOP);
                break;
            case RUNING:
                taskRuleService.updateTaskRulesStatus(unitId, TaskRule.Status.RUNING);
                break;
            default:
                break;
        }
    }

    @Override
    @Transactional
    public DispatchTask getUnitByLoadId(Integer loadAreaId) {
        LambdaQueryWrapper<DispatchTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DispatchTask::getLoadAreaId,loadAreaId);
        queryWrapper.ne(DispatchTask::getStatus, DELETE);
        queryWrapper.orderByDesc(DispatchTask::getAddTime);
        List<DispatchTask> dispatchTasks = this.list(queryWrapper);
        if(GmsUtil.CollectionNotNull(dispatchTasks)){
            return dispatchTasks.get(0);
        }
        return null;
    }

    @Override
    @Transactional
    public void updateUnloadId(Integer unitId, Integer unloadId) {
        LambdaUpdateWrapper<DispatchTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(DispatchTask::getUnLoadAreaId,unloadId);
        updateWrapper.eq(DispatchTask::getUnitId,unitId);
        updateWrapper.ne(DispatchTask::getStatus, DELETE);
        this.update(updateWrapper);
    }
}
