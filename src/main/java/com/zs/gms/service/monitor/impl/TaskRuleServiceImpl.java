package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.entity.monitor.TaskRule;
import com.zs.gms.mapper.monitor.TaskRuleMapper;
import com.zs.gms.service.monitor.TaskRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class TaskRuleServiceImpl extends ServiceImpl<TaskRuleMapper, TaskRule> implements TaskRuleService {

    /**
     * 添加任务规则
     * */
    @Override
    @Transactional
    public void addTaskRule(TaskRule taskRule) {
        taskRule.setAddTime(new Date());
        this.save(taskRule);
    }

    /**
     * 根据车辆编号修改规则执行状态
     * */
    @Override
    @Transactional
    public void updateTaskRuleStatus(Integer userId,String vehicleId, TaskRule.Status status) {
        LambdaUpdateWrapper<TaskRule> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TaskRule::getVehicleId,vehicleId);
        updateWrapper.set(TaskRule::getStatus,status);
        updateWrapper.eq(TaskRule::getUserId,userId);
        updateWrapper.in(TaskRule::getStatus,TaskRule.Status.RUNING,TaskRule.Status.UNEXECUTED,TaskRule.Status.STOP);//只能修改任务进行状态或未执行为其他状态
        this.update(updateWrapper);
    }

    /**
     * 修改指定单元所有车辆规则状态
     * */
    @Override
    @Transactional
    public void updateTaskRulesStatus(Integer unitId,TaskRule.Status status) {
        LambdaUpdateWrapper<TaskRule> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(TaskRule::getStatus,status);
        updateWrapper.eq(TaskRule::getUnitId,unitId);
        updateWrapper.notIn(TaskRule::getStatus,TaskRule.Status.FINISH,TaskRule.Status.DELETE);//已完成或删除的任务不可修改状态
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public void updateVehicleStatus(Integer vehicleId, TaskRule.Status status) {
        LambdaUpdateWrapper<TaskRule> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TaskRule::getVehicleId,vehicleId);
        updateWrapper.ne(TaskRule::getStatus,TaskRule.Status.DELETE);
        updateWrapper.set(TaskRule::getStatus,status);
        this.update(updateWrapper);
    }

    /**
     * 查询车辆是否已分配任务,false为未分配
     * */
    @Override
    @Transactional
    public boolean queryVehicleUsed(String vehicleId) {
        TaskRule taskRule = this.baseMapper.findVehicleLastTimeRule(vehicleId);
        return null==taskRule?false:true;
    }

    @Override
    @Transactional
    public List<TaskRule> getTaskRulesByUnitId(Integer unitId) {
        LambdaQueryWrapper<TaskRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskRule::getUnitId,unitId);
        queryWrapper.ne(TaskRule::getStatus,TaskRule.Status.DELETE);
        return this.list(queryWrapper);
    }

    /**
     * 获取指定调度单元车辆集合
     * */
    @Override
    @Transactional
    public List<Barney> getUnitVehicleList(Integer unitId) {
        return this.baseMapper.getUnitVehicleList(unitId);
    }

    @Override
    @Transactional
    public List<Barney> getVehiclesByUserId(Integer userId) {
        return this.baseMapper.getVehiclesByUserId(userId);
    }

    /**
     * 获取装卸调度单元最新规则
     * */
    @Override
    @Transactional
    public TaskRule getLastLoadTaskRule(Integer unitId) {
        return this.baseMapper.getLastLoadTaskRule(unitId);
    }

    @Override
    @Transactional
    public TaskRule getTaskRuleByVehicleId(Integer userId,Integer vehicleId) {
        LambdaQueryWrapper<TaskRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TaskRule::getVehicleId,vehicleId);
        queryWrapper.eq(TaskRule::getUserId,userId);
        queryWrapper.ne(TaskRule::getStatus,TaskRule.Status.DELETE);
        List<TaskRule> taskRules = this.list(queryWrapper);
        return GmsUtil.CollectionNotNull(taskRules)?taskRules.get(0):null;
    }
}
