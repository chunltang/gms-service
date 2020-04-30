package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.Assert;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.vehiclemanager.VehicleMaintainTask;
import com.zs.gms.enums.vehiclemanager.DateEnum;
import com.zs.gms.mapper.vehiclemanager.VehicleMaintainTaskMapper;
import com.zs.gms.service.vehiclemanager.VehicleMaintainTaskService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class VehicleMaintainTaskServiceImpl extends ServiceImpl<VehicleMaintainTaskMapper, VehicleMaintainTask> implements VehicleMaintainTaskService {

    private final static long INTERVAL = 60 * 1000;

    @Override
    @Transactional
    public void addMaintainTask(VehicleMaintainTask vehicleMaintainTask) {
        vehicleMaintainTask.setAddTime(new Date());
        vehicleMaintainTask.setStatus(VehicleMaintainTask.Status.PROCESSED);
        this.save(vehicleMaintainTask);
    }

    @Override
    public boolean isExist(String maintainTaskName) {
        LambdaQueryWrapper<VehicleMaintainTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VehicleMaintainTask::getMaintainTaskName, maintainTaskName);
        return this.list(queryWrapper).size()>0;
    }

    @Override
    @Transactional
    public void updateMaintainTask(Integer id, Integer num, DateEnum units) {
        LambdaUpdateWrapper<VehicleMaintainTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(VehicleMaintainTask::getNum, num);
        updateWrapper.set(VehicleMaintainTask::getUnits, units);
        updateWrapper.eq(VehicleMaintainTask::getId, id);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public boolean deleteMaintainTask(Integer id) {
        return this.removeById(id);
    }

    @Override
    public boolean isExistId(Integer id) {
        return null != this.getMaintainTaskById(id);
    }

    @Override
    @Transactional
    public void updateNextTime(Integer id, Date nextTime) {
        LambdaUpdateWrapper<VehicleMaintainTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(VehicleMaintainTask::getId, id);
        updateWrapper.set(VehicleMaintainTask::getNextTime, nextTime);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public IPage getMaintainTasks(QueryRequest queryRequest) {
        Page page = new Page();
        SortUtil.handlePageSort(queryRequest, page, GmsConstant.SORT_DESC, "addTime");
        return this.page(page);
    }

    @Override
    @Transactional
    public List<VehicleMaintainTask> getMaintainTasks() {
        return this.list();
    }

    @Override
    @Transactional
    public VehicleMaintainTask getMaintainTaskById(Integer id) {
        return this.getById(id);
    }

    @Override
    @Transactional
    public List<VehicleMaintainTask> getMaintainTaskByVehicleId(Integer vehicleId) {
        LambdaQueryWrapper<VehicleMaintainTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VehicleMaintainTask::getVehicleId, vehicleId);
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public List<VehicleMaintainTask> getMaintainTaskByUserId(Integer userId) {
        LambdaQueryWrapper<VehicleMaintainTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VehicleMaintainTask::getUserId, userId);
        return this.list(queryWrapper);
    }


    @Override
    @Transactional
    public void updateHandleStatus(Integer id, VehicleMaintainTask.Status status) {
        LambdaUpdateWrapper<VehicleMaintainTask> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(VehicleMaintainTask::getId, id);
        updateWrapper.set(VehicleMaintainTask::getStatus, status);
        this.update(updateWrapper);
    }

    /**
     * 加载延迟任务
     */
    @Transactional
    public void loadMaintainTasks() {
        List<VehicleMaintainTask> maintainTasks = this.getMaintainTasks();
        for (VehicleMaintainTask maintainTask : maintainTasks) {
            load(maintainTask);
        }
    }

    private void load(VehicleMaintainTask maintainTask) {
        Assert.AllNotNull("maintainTask字段异常:{}",
                new Object[]{maintainTask.getId(),
                        maintainTask.getVehicleId(),
                        maintainTask.getNextTime()},
                maintainTask
        );
        //任务id=maintain_task_id_vehicleId
        DelayedService.Task task = DelayedService.buildTask()
                .withTaskId(GmsUtil.format("{}{}_{}", VehicleMaintainTask.TASK_PREFIX, maintainTask.getId(), maintainTask.getVehicleId()))
                .withDesc(GmsUtil.format("车[{}]维护任务信息推送", maintainTask.getVehicleId()))
                .withPrintLog(true)
                .withKeep(true)
                .withNum(-1)
                .withRepetition(false)
                .withDelay(INTERVAL)//半小时提醒一次
                .withAddTime(maintainTask.getAddTime().getTime())
                .withNextTime(maintainTask.getNextTime().getTime())
                .withMethodTask("com.zs.gms.service.vehiclemanager.impl.VehicleMaintainTaskServiceImpl",
                        "pushMessage", new Object[]{maintainTask.getId()});
        DelayedService.addInitializedTask(task, false);

    }

    @Override
    @Transactional
    public void loadMaintainTask(Integer id) {
        VehicleMaintainTask maintainTask = getMaintainTaskById(id);
        if (null != maintainTask) {
            load(maintainTask);
        }
    }

    @Override
    @Transactional
    public void loadMaintainTask(VehicleMaintainTask maintainTask) {
        if (null != maintainTask) {
            load(maintainTask);
        }
    }

    @Override
    @Transactional
    public void clearCacheTask(String taskId) {
        if (GmsUtil.StringNotNull(taskId)) {
            DelayedService.removeTask(taskId);
            RedisService.deleteKey(StaticConfig.KEEP_DB, RedisKeyPool.DELAY_TASK_PREFIX + taskId);
            return;
        }
        Collection<String> keys = RedisService.getLikeKey(StaticConfig.KEEP_DB, RedisKeyPool.DELAY_TASK_PREFIX + VehicleMaintainTask.TASK_PREFIX);
        for (String key : keys) {
            DelayedService.removeTask(key.substring(RedisKeyPool.DELAY_TASK_PREFIX.length()));
            RedisService.deleteKey(StaticConfig.KEEP_DB, key);
        }
    }

    @Override
    @Transactional
    public void reloadTask(VehicleMaintainTask maintainTask) {
        clearCacheTask(getTaskId(maintainTask.getId(), maintainTask.getVehicleId()));
        loadMaintainTask(maintainTask);
    }

    @Override
    public String getTaskId(Integer id, Integer vehicleId) {
        Assert.AllNotNull("参数不能为空:id={},vehicleId={}", new Object[]{id, vehicleId}, id, vehicleId);
        return VehicleMaintainTask.TASK_PREFIX + id + "_" + vehicleId;
    }

    /**
     * 推送消息
     */
    @Transactional
    public void pushMessage(Integer id) {
        VehicleMaintainTask maintainTask = this.getMaintainTaskById(id);
        if (null != maintainTask) {
            if (maintainTask.getStatus().equals(VehicleMaintainTask.Status.PROCESSED)) {
                updateHandleStatus(id, VehicleMaintainTask.Status.UNDISPOSED);//触发任务后设置为未处理
                maintainTask.setStatus(VehicleMaintainTask.Status.UNDISPOSED);
            }
            WsUtil.sendMessage(maintainTask.getUserId().toString(), GmsUtil.toJsonIEnumDesc(maintainTask), FunctionEnum.maintainTask);
        }
    }
}
