package com.zs.gms.controller.monitor;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.point.AnglePoint;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.monitor.UnitVehicleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/dispatchTasks")
@Slf4j
@Api(tags = "运营监控", description = "Dispatch Controller")
public class DispatchController extends BaseController {

    @Autowired
    private UnitVehicleService unitVehicleService;

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
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.getParams().put("vehicleId",vehicleId);
            entry.getParams().put("points",points);
            entry.setCollect(2);
            if (null != currentUser) {
                entry.setAfterHandle(() -> {

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
                    //dispatchTaskService.updateUnitStatusByUnitId(unitId, DispatchTask.Status.RUNING);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "StartAIUnit", JSONObject.toJSONString(paramMap), "启动调度单元成功");
        } catch (Exception e) {
            String message = "启动调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }


    @Log("启动车辆")
    @PutMapping(value = "/vehicles/start")
    @ApiOperation(value = "启动车辆", httpMethod = "PUT")
    public void startVehicle(@MultiRequestBody("vehicleId") Integer vehicleId, HttpServletResponse response) throws GmsException {
        try {
            startOrStopVehicle(vehicleId, response, WhetherEnum.YES, "StartVehTask", "启动车辆成功");
        } catch (Exception e) {
            String message = "启动车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("停止车辆")
    @PutMapping(value = "/vehicles/stop")
    @ApiOperation(value = "停止车辆", httpMethod = "PUT")
    public void stopVehicle(@MultiRequestBody("vehicleId") Integer vehicleId, HttpServletResponse response) throws GmsException {
        try {
            startOrStopVehicle(vehicleId, response, WhetherEnum.NO, "StopVehTask", "成功发送停车指令");
        } catch (
                Exception e) {
            String message = "发送停车指令失败";
            log.error(message, e);
            throw new GmsException(message);
        }

    }

    private void startOrStopVehicle(Integer vehicleId, HttpServletResponse response, WhetherEnum stauts, String routeKey, String resultDesc) throws Exception {
        Unit unit = unitVehicleService.getUnitByVehicleId(vehicleId);
        if (null == unit) {
            GmsService.callResponse(new GmsResponse().message("该车辆没有分配调度单元!").badRequest(), response);
            return;
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vehicleId", vehicleId);
        MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
        entry.setAfterHandle(() -> {
            if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                unitVehicleService.updateStatus(unit.getUnitId(), vehicleId, stauts);
            }
        });
        MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), routeKey, JSONObject.toJSONString(paramMap), resultDesc);
    }

    //tcl 05-09 新增接口
    @Log("绕障路径运行")
    @PutMapping(value = "/vehicles/around")
    @ApiOperation(value = "绕障路径运行", httpMethod = "PUT")
    public void aroundRunPath(@MultiRequestBody("vehicleId") Integer vehicleId,HttpServletResponse response)throws GmsException{
        try {
            Integer mapId = MapDataUtil.getActiveMap();
            if(null==mapId){
                GmsService.callResponse(new GmsResponse().message("活动地图不存在!").badRequest(),response);
                return;
            }
            Map<String,Object> params=new HashMap<>();
            params.put("vehicleId",vehicleId);
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            MessageFactory.getMapMessage().sendMessageWithID(entry.getMessageId(),"aroundCreatePath", GmsUtil.toJson(params),"绕障路径运行提交成功");
        } catch (Exception e) {
            String message = "绕障路径运行提交失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

}
