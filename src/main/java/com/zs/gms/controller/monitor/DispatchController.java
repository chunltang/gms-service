package com.zs.gms.controller.monitor;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.point.AnglePoint;
import com.zs.gms.entity.monitor.DispatchTask;
import com.zs.gms.entity.monitor.TaskRule;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.monitor.TaskTypeEnum;
import com.zs.gms.enums.monitor.UnitTypeEnum;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.monitor.DispatchTaskService;
import com.zs.gms.service.monitor.TaskRuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/dispatchTasks")
@Slf4j
@Api(tags = "运营监控", description = "Dispatch Controller")
public class DispatchController extends BaseController {

    @Autowired
    private DispatchTaskService dispatchTaskService;

    @Autowired
    private TaskRuleService taskRuleService;

    @Log("交互式路径请求")
    @PostMapping(value = "/InteractiveDispatchTasks")
    @ApiOperation(value = "交互式路径请求", httpMethod = "POST")
    public void createInteractiveTask(@MultiRequestBody("vehicleId") Integer vehicleId,
                                      @MultiRequestBody(value = "planType", required = false, parseAllFields = false) Integer planType,
                                      @MultiRequestBody("points") AnglePoint[] points) throws GmsException {
        if (vehicleId == null) {
            throw new GmsException("参数异常");
        }
        Integer mapId = MapDataUtil.getActiveMap();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vehicleId", vehicleId);
        paramMap.put("points", points);
        paramMap.put("planType", planType);
        try {
            User currentUser = getCurrentUser();
            MessageEntry entry = MessageFactory.getMessageEntryById(GmsConstant.DISPATCH+"_"+vehicleId);
            entry.setCollect(2);
            if (null != currentUser) {
                entry.setAfterHandle(() -> {
                    if (!entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                        return;
                    }
                    Integer unitId = dispatchTaskService.getUnitIdByUserIdAndType(currentUser.getUserId(), UnitTypeEnum.INTERACTIVE_DISPATCHTASK, mapId);
                    if (unitId != null) {
                        TaskRule taskRule = new TaskRule();
                        taskRule.setPoints(Arrays.toString(points));
                        taskRule.setVehicleId(vehicleId);
                        taskRule.setStatus(TaskRule.Status.INTERACTION);
                        taskRule.setUserId(currentUser.getUserId());
                        taskRule.setUnitId(Integer.valueOf(String.valueOf(unitId.longValue())));
                        taskRuleService.addTaskRule(taskRule);
                    }
                });
            }
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "CreatePath", JSONObject.toJSONString(paramMap), "交互式路径请求成功");
        } catch (Exception e) {
            String message = "交互式路径请求失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("执行交互式任务")
    @PutMapping(value = "/InteractiveDispatchTasks/statuses/runStatus")
    @ApiOperation(value = "执行交互式任务", httpMethod = "PUT")
    public void runInteractiveTask(@MultiRequestBody("vehicleId") Integer vehicleId) throws GmsException {
        if (!ObjectUtils.allNotNull(vehicleId)) {
            throw new GmsException("参数异常");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vehicleId", vehicleId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "RunPath", JSONObject.toJSONString(paramMap), "执行交互式任务成功");
        } catch (Exception e) {
            String message = "执行交互式任务失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("交互式任务停止")
    @PutMapping(value = "/InteractiveDispatchTasks/statuses/stopStatus")
    @ApiOperation(value = "交互式任务停止", httpMethod = "PUT")
    public void stopInteractiveTask(@MultiRequestBody("vehicleId") Integer vehicleId) throws GmsException {
        if (!ObjectUtils.allNotNull(vehicleId)) {
            throw new GmsException("参数异常");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vehicleId", vehicleId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "StopVeh", JSONObject.toJSONString(paramMap), "交互式任务停止成功");
        } catch (Exception e) {
            String message = "交互式任务停止失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("创建装卸调度单元")
    @PostMapping(value = "/loadDispatchTasks")
    @ApiOperation(value = "创建装卸调度单元", httpMethod = "POST")
    public void createLoadDispatchTask(@MultiRequestBody("name") String name,
                                       @MultiRequestBody("loadAreaId") Integer loadAreaId,
                                       @MultiRequestBody("unLoadAreaId") Integer unLoadAreaId) throws GmsException {
        if (!ObjectUtils.allNotNull(loadAreaId, unLoadAreaId)) {
            throw new GmsException("参数异常");
        }
        Integer mapId = MapDataUtil.getActiveMap();
        User currentUser = getCurrentUser();
        DispatchTask dispatchTask = new DispatchTask();
        dispatchTask.setMapId(mapId);
        dispatchTask.setDispatchTaskType(UnitTypeEnum.LOAD_DISPATCHTASK);
        dispatchTask.setUserId(currentUser.getUserId());
        dispatchTask.setStatus(DispatchTask.Status.UNUSED);
        dispatchTask.setUnLoadAreaId(unLoadAreaId);
        dispatchTask.setLoadAreaId(loadAreaId);
        dispatchTask.setName(name);
        Integer unitId = dispatchTaskService.addDispatchTask(dispatchTask);
        if (null == unitId) {
            throw new GmsException("创建装卸调度单元失败");
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("unitId", unitId);
        paramMap.put("loaderAreaId", loadAreaId);
        paramMap.put("unLoaderAreaId", unLoadAreaId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (!entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作失败
                    dispatchTaskService.deleteByUnitId(GmsUtil.typeTransform(unitId, Integer.class));
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "CreateLoaderAIUnit", JSONObject.toJSONString(paramMap), "创建装卸调度单元成功");
        } catch (Exception e) {
            String message = "创建装卸调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("启动调度单元")
    @PutMapping(value = "/{unitId}/statuses/runStatus")
    @ApiOperation(value = "启动调度单元", httpMethod = "PUT")
    public void runDispatchTask(@PathVariable Integer unitId) throws GmsException {
        if (!ObjectUtils.allNotNull(unitId)) {
            throw new GmsException("参数异常");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("unitId", unitId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                    dispatchTaskService.updateUnitStatusByUnitId(unitId, DispatchTask.Status.RUNING);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "StartAIUnit", JSONObject.toJSONString(paramMap), "启动调度单元成功");
        } catch (Exception e) {
            String message = "启动调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("停止调度单元")
    @PutMapping(value = "/{unitId}/statuses/stopStatus")
    @ApiOperation(value = "停止调度单元", httpMethod = "PUT")
    public void stopDispatchTask(@PathVariable Integer unitId) throws GmsException {
        if (!ObjectUtils.allNotNull(unitId)) {
            throw new GmsException("参数异常");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("unitId", unitId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "StopAIUnit", JSONObject.toJSONString(paramMap), "停止调度单元成功");
        } catch (Exception e) {
            String message = "停止调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("取消调度单元")
    @PutMapping(value = "/{unitId}/statuses/cancelStatus")
    @ApiOperation(value = "取消调度单元", httpMethod = "PUT")
    public void cancelDispatchTask(@PathVariable Integer unitId) throws GmsException {
        if (!ObjectUtils.allNotNull(unitId)) {
            throw new GmsException("参数异常");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("unitId", unitId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                    dispatchTaskService.updateUnitStatusByUnitId(unitId, DispatchTask.Status.DELETE);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "RemoveAIUnit", JSONObject.toJSONString(paramMap), "取消调度单元成功");
        } catch (Exception e) {
            String message = "取消调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }


    @Log("装卸单元添加车辆")
    @PostMapping(value = "/loadDispatchTasks/vehicles")
    @ApiOperation(value = "装卸单元添加车辆", httpMethod = "POST")
    public void addLoadDispatchVehicle(@MultiRequestBody("unitId") Integer unitId,
                                       @MultiRequestBody("vehicleId") Integer vehicleId,
                                       @MultiRequestBody(value = "cycleTimes", required = false, parseAllFields = false) Integer cycleTimes,
                                       @MultiRequestBody(value = "endTime", required = false, parseAllFields = false) String endTime,
                                       @MultiRequestBody(value = "parkingAreaId", required = false, parseAllFields = false) Integer parkingAreaId) throws GmsException {
        if (!ObjectUtils.allNotNull(unitId, vehicleId)) {
            throw new GmsException("参数异常");
        }

        if (cycleTimes == null && StringUtils.isEmpty(endTime)) {
            throw new GmsException("循环次数和结束时间必须一个");
        }

        User currentUser = getCurrentUser();
        try {
            boolean used = taskRuleService.queryVehicleUsed(String.valueOf(vehicleId));
            if (used) {
                throw new GmsException("指定车辆存在未完成任务");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GmsException(e.getMessage());
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("unitId", unitId);
        paramMap.put("vehicleId", vehicleId);
        paramMap.put("cycleTimes", cycleTimes);
        paramMap.put("parkingAreaId", parkingAreaId);
        paramMap.put("endTime", endTime.replaceAll("[-|\\s+]+", ""));
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                    TaskRule taskRule = new TaskRule();
                    taskRule.setUserId(currentUser.getUserId());
                    taskRule.setStatus(TaskRule.Status.RUNING);
                    taskRule.setVehicleId(vehicleId);
                    taskRule.setCycleTimes(cycleTimes);
                    taskRule.setUnitId(unitId);
                    taskRule.setEndTime(endTime);
                    taskRuleService.addTaskRule(taskRule);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "AddLoadAIVeh", JSONObject.toJSONString(paramMap), "装卸单元添加车辆成功");
        } catch (Exception e) {
            String message = "装卸单元添加车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("调度单元移除车辆")
    @DeleteMapping(value = "/{unitId}/vehicles/{vehicleId}")
    @ApiOperation(value = "调度单元移除车辆", httpMethod = "DELETE")
    public void removeDispatchVehicle(@PathVariable Integer unitId,
                                      @PathVariable Integer vehicleId) throws GmsException {
        if (!ObjectUtils.allNotNull(unitId, vehicleId)) {
            throw new GmsException("参数异常");
        }
        User currentUser = getCurrentUser();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vehicleId", vehicleId);
        paramMap.put("unitId", unitId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                    taskRuleService.updateVehicleStatus(vehicleId, TaskRule.Status.DELETE);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "RemoveAIVeh", JSONObject.toJSONString(paramMap), "调度单元移除车辆成功");
        } catch (Exception e) {
            String message = "调度单元移除车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("启动车辆")
    @PutMapping(value = "/vehicles/start")
    @ApiOperation(value = "启动车辆", httpMethod = "PUT")
    public void startVehicle(@MultiRequestBody("vehicleId") Integer vehicleId) throws GmsException {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vehicleId", vehicleId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                    taskRuleService.updateVehicleStatus(vehicleId, TaskRule.Status.RUNING);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "StartVehTask", JSONObject.toJSONString(paramMap), "启动车辆成功");
        } catch (Exception e) {
            String message = "启动车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("停止车辆")
    @PutMapping(value = "/vehicles/stop")
    @ApiOperation(value = "停止车辆", httpMethod = "PUT")
    public void stopVehicle(@MultiRequestBody("vehicleId") Integer vehicleId) throws GmsException {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vehicleId", vehicleId);
        try {
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                    taskRuleService.updateVehicleStatus(vehicleId, TaskRule.Status.STOP);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "StopVehTask", JSONObject.toJSONString(paramMap), "停止车辆成功");
        } catch (Exception e) {
            String message = "停止车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}
