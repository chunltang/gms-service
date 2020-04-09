package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.utils.DateUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.User;
import com.zs.gms.entity.vehiclemanager.MaintainTaskInfo;
import com.zs.gms.entity.vehiclemanager.VehicleMaintainTask;
import com.zs.gms.enums.vehiclemanager.DateEnum;
import com.zs.gms.service.vehiclemanager.MaintainTaskInfoService;
import com.zs.gms.service.vehiclemanager.VehicleMaintainTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/vehicles")
@Validated
@Api(tags = {"车辆管理"}, description = "vehicle Controller")
public class VehicleMaintainTaskController extends BaseController {

    @Autowired
    private VehicleMaintainTaskService maintainTaskService;

    @Autowired
    private MaintainTaskInfoService taskInfoService;

    @Log("添加矿车维护任务")
    @PostMapping("/maintainTasks")
    @ApiOperation(value = "添加矿车维护任务", httpMethod = "POST")
    public GmsResponse barneyMaintainTask(@MultiRequestBody @Valid VehicleMaintainTask maintainTask) throws GmsException {
        try {
            User user = super.getCurrentUser();
            maintainTask.setUserId(user.getUserId());
            maintainTask.setUserName(user.getUserName());
            maintainTaskService.addMaintainTask(maintainTask);
            maintainTaskService.loadMaintainTask(maintainTask);
            return new GmsResponse().message("添加矿车维护任务成功").success();
        } catch (Exception e) {
            String message = "添加矿车维护任务失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("修改维护任务执行周期")
    @PutMapping("/maintainTasks/interval")
    @ApiOperation(value = "修改维护任务执行周期", httpMethod = "PUT")
    public GmsResponse updateMaintainTask(@MultiRequestBody("id") Integer id,
                                          @MultiRequestBody("num") Integer num,
                                          @MultiRequestBody("units") DateEnum units) throws GmsException {
        try {
            VehicleMaintainTask maintainTask = maintainTaskService.getMaintainTaskById(id);
            if (null == maintainTask) {
                return new GmsResponse().message("维护id不存在").badRequest();
            }
            if (num <= 0 || null == units) {
                return new GmsResponse().message("参数异常").badRequest();
            }
            maintainTaskService.updateMaintainTask(id, num, units);
            return new GmsResponse().message("修改维护任务成功").success();
        } catch (Exception e) {
            String message = "修改维护任务失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("修改维护任下次执行时间")
    @PutMapping("/maintainTasks/nextTime")
    @ApiOperation(value = "修改维护任下次执行时间", httpMethod = "PUT")
    public GmsResponse updateMaintainTask(@MultiRequestBody("id") Integer id,
                                          @MultiRequestBody("nextTime")
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date nextTime) throws GmsException {
        try {
            VehicleMaintainTask maintainTask = maintainTaskService.getMaintainTaskById(id);
            if (null == maintainTask) {
                return new GmsResponse().message("维护id不存在").badRequest();
            }

            if (null == nextTime || nextTime.getTime() < GmsUtil.getCurTime()) {
                return new GmsResponse().message("参数异常").badRequest();
            }
            maintainTaskService.updateNextTime(id, nextTime);
            maintainTask.setNextTime(nextTime);
            maintainTaskService.reloadTask(maintainTask);
            return new GmsResponse().message("修改成功").success();
        } catch (Exception e) {
            String message = "修改失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取维护任务信息列表")
    @GetMapping("/maintainTasks")
    @ApiOperation(value = "获取维护信息", httpMethod = "GET")
    @ResponseBody
    public String getMaintainTask(QueryRequest queryRequest) throws GmsException {
        try {
            Map<String, Object> table = this.getDataTable(maintainTaskService.getMaintainTasks(queryRequest));
            return GmsUtil.toJsonIEnumDesc(new GmsResponse().data(table).message("获取维护信息成功").success());
        } catch (Exception e) {
            String message = "获取维护信息失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("删除维护任务")
    @DeleteMapping("/maintainTasks/{id}")
    @ApiOperation(value = "删除维护任务", httpMethod = "DELETE")
    public GmsResponse getMaintainTask(@PathVariable Integer id) throws GmsException {
        try {
            VehicleMaintainTask maintainTask = maintainTaskService.getMaintainTaskById(id);
            if (null == maintainTask) {
                return new GmsResponse().message("该任务id不存在").badRequest();
            }
            if (maintainTaskService.deleteMaintainTask(id)) {
                String taskId = maintainTaskService.getTaskId(id, maintainTask.getVehicleId());
                maintainTaskService.clearCacheTask(taskId);
                return new GmsResponse().message("删除维护任务成功").success();
            }
            return new GmsResponse().message("删除维护任务失败").badRequest();
        } catch (Exception e) {
            String message = "删除维护任务失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("开始处理本次维护任务")
    @PutMapping("/maintainTasks/begin/{id}")
    @ApiOperation(value = "开始处理本次维护任务", httpMethod = "PUT")
    public GmsResponse beginHandler(@PathVariable Integer id) throws GmsException {
        try {
            VehicleMaintainTask maintainTask = maintainTaskService.getMaintainTaskById(id);
            if (null == maintainTask) {
                return new GmsResponse().message("维护任务不存在").badRequest();
            }
            if (!maintainTask.getStatus().equals(VehicleMaintainTask.Status.UNDISPOSED)) {
                return new GmsResponse().message("维护任务不是未处理状态").badRequest();
            }
            maintainTaskService.updateHandleStatus(id, VehicleMaintainTask.Status.PROCESSING);
            return new GmsResponse().message("提交成功").success();
        } catch (Exception e) {
            String message = "提交失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("确认本次维护任务已完成")
    @PutMapping("/maintainTasks/end/{id}")
    @ApiOperation(value = "确认本次维护任务已完成", httpMethod = "PUT")
    public GmsResponse endHandler(@PathVariable Integer id,
                                  @MultiRequestBody(value = "remark", required = false, parseAllFields = false) String remark) throws GmsException {
        try {
            VehicleMaintainTask maintainTask = maintainTaskService.getMaintainTaskById(id);
            if (null == maintainTask) {
                return new GmsResponse().message("维护任务不存在").badRequest();
            }
            if (!maintainTask.getStatus().equals(VehicleMaintainTask.Status.PROCESSING)) {
                return new GmsResponse().message("维护任务不是处理中的状态").badRequest();
            }
            User user = super.getCurrentUser();
            MaintainTaskInfo taskInfo = new MaintainTaskInfo();
            taskInfo.setRemark(remark);
            taskInfo.setMaintainTaskId(id);
            taskInfo.setUserId(user.getUserId());
            taskInfo.setUserName(user.getUserName());
            taskInfo.setVehicleId(maintainTask.getVehicleId());
            if (taskInfoService.addMaintainTaskInfo(taskInfo)) {
                String taskId = maintainTaskService.getTaskId(maintainTask.getId(), maintainTask.getVehicleId());
                maintainTaskService.clearCacheTask(taskId);
                DelayedService.removeTask(taskId);
                maintainTaskService.updateHandleStatus(id, VehicleMaintainTask.Status.PROCESSED);
                //计算下次执行时间
                Integer num = maintainTask.getNum();
                DateEnum units = maintainTask.getUnits();
                long nextLongTime = DateEnum.getTime(units) * num;
                Date date = DateUtil.formatLongToDate(GmsUtil.getCurTime() + nextLongTime);
                maintainTaskService.updateNextTime(id, date);
                //触发任务重新加载
                maintainTaskService.loadMaintainTasks();
                return new GmsResponse().message("提交成功").success();
            }
            return new GmsResponse().message("提交失败").badRequest();
        } catch (Exception e) {
            String message = "提交失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取维护日志")
    @GetMapping("/maintainTasks/infos")
    @ApiOperation(value = "获取维护日志", httpMethod = "GET")
    public GmsResponse getMaintainInfos(@MultiRequestBody(value = "vehicleId", required = false, parseAllFields = false) Integer vehicleId,
                                        @MultiRequestBody(value = "userId", required = false, parseAllFields = false) Integer userId) throws GmsException {
        try {
            List<MaintainTaskInfo> infos = taskInfoService.getInfos(vehicleId, userId);
            return new GmsResponse().data(infos).message("获取维护日志成功").success();
        } catch (Exception e) {
            String message = "获取维护日志失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}
