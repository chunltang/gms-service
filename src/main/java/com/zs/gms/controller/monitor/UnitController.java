package com.zs.gms.controller.monitor;

import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.vehiclemanager.Vehicle;
import com.zs.gms.service.vehiclemanager.VehicleService;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.monitor.DispatchTask;
import com.zs.gms.entity.monitor.TaskRule;
import com.zs.gms.service.monitor.DispatchTaskService;
import com.zs.gms.service.monitor.TaskRuleService;
import com.zs.gms.entity.system.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/units")
@Slf4j
@Api(tags = "运营监控",description = "Dispatch Controller")
public class UnitController extends BaseController {

    @Autowired
    @Lazy
    private DispatchTaskService dispatchTaskService;

    @Autowired
    @Lazy
    private TaskRuleService taskRuleService;

    @Autowired
    @Lazy
    private VehicleService vehicleService;


    @Log("获取调度单元列表")
    @GetMapping
    @ApiOperation(value = "获取调度单元列表",httpMethod = "GET")
    public GmsResponse getDispatchTaskList() throws GmsException {
        User currentUser = this.getCurrentUser();
        if(null==currentUser){
            throw  new GmsException("当前用户未登录");
        }
        try {
            List<DispatchTask> list = dispatchTaskService.getDispatchTaskList(currentUser.getUserId(), GmsUtil.getActiveMap());
            return new GmsResponse().data(list).message("获取调度单元列表成功").success();
        }catch ( Exception e){
            String message="获取调度单元列表失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }


    @Log("获取指定调度单元车辆")
    @GetMapping(value = "/{unitId}/vehicles")
    @ApiOperation(value = "获取指定调度单元车辆",httpMethod = "GET")
    public GmsResponse getUnitVehicleList(@PathVariable Integer unitId) throws GmsException {
        if(null==unitId){
            throw  new GmsException("参数异常");
        }
        try {
            List<Vehicle> vehicles = taskRuleService.getUnitVehicleList(unitId);
            return new GmsResponse().data(vehicles).message("获取指定调度单元车辆成功").success();
        }catch (Exception e){
            String message="获取指定调度单元车辆失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("获取指定调度员所有车辆")
    @GetMapping(value = "/vehicles/all")
    @ApiOperation(value = "获取指定调度员所有车辆",httpMethod = "GET")
    public GmsResponse getVehiclesByUserId(Integer  userId) throws GmsException {
        if(null==userId){
            throw  new GmsException("参数异常");
        }
        try {
            List<Vehicle> vehicles = vehicleService.getVehicleListByUserId(userId);
            return new GmsResponse().data(vehicles).message("获取指定调度员所有车辆成功").success();
        }catch (Exception e){
            String message="获取指定调度员所有车辆失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("获取指定调度员所有空闲车辆")
    @GetMapping(value = "/vehicles/leisure")
    @ApiOperation(value = "获取指定调度员所有空闲车辆",httpMethod = "GET")
    public GmsResponse getLeisureVehiclesByUserId(Integer  userId) throws GmsException {
        if(null==userId){
            throw  new GmsException("参数异常");
        }
        try {
            List<Vehicle> vehicles = taskRuleService.getVehiclesByUserId(userId);
            return new GmsResponse().data(vehicles).message("获取指定调度员所有空闲车辆成功").success();
        }catch (Exception e){
            String message="获取指定调度员所有空闲车辆失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("获取指定车辆调度任务")
    @GetMapping(value = "/vehicles/{vehicleId}")
    @ApiOperation(value = "获取指定车辆调度任务",httpMethod = "GET")
    public GmsResponse getTaskRule(@PathVariable Integer vehicleId) throws GmsException {
        try {
            User currentUser = this.getCurrentUser();
            if(currentUser==null){
                throw new GmsException("当前用户未登录");
            }
            TaskRule taskRule = taskRuleService.getTaskRuleByVehicleId(currentUser.getUserId(), vehicleId);
            return new GmsResponse().data(taskRule).message("获取指定车辆调度任务成功").success();
        }catch (Exception e){
            String message="获取指定车辆调度任务失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}

