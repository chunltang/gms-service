package com.zs.gms.controller.monitor;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.monitor.UnitVehicle;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.monitor.UnitService;
import com.zs.gms.service.monitor.UnitVehicleService;
import com.zs.gms.service.vehiclemanager.BarneyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/unitVehicles")
@Slf4j
@Validated
@Api(tags = "运营监控", description = "UnitVehicle Controller")
public class UnitVehicleController extends BaseController {


    @Autowired
    private UnitVehicleService unitVehicleService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private BarneyService barneyService;

    @Log("调度单元分配车俩")
    @PostMapping
    @ApiOperation(value = "调度单元分配车俩", httpMethod = "POST")
    public GmsResponse updateUnitVehicle(@MultiRequestBody("unitId") Integer unitId,
                                         @MultiRequestBody("vehicleIds") String vehicleIds) throws GmsException {
        try {
            boolean existId = unitService.isExistId(unitId);
            if (!existId) {
                return new GmsResponse().message("调度单元不存在").badRequest();
            }
            if (GmsUtil.StringNotNull(vehicleIds)) {
                List<UnitVehicle> unitVehicles = new ArrayList<>();
                String[] split = vehicleIds.split(StringPool.COMMA);
                for (String str : split) {
                    Integer vehicleId = Integer.valueOf(str);
                    boolean existVehicleNo = barneyService.isExistVehicleNo(vehicleId);
                    if (!existVehicleNo) {
                        log.error("[{}]矿车编号不存在!", str);
                        continue;
                    }
                    boolean existVehicleId = unitVehicleService.isExistVehicleId(unitId,vehicleId);
                    if (existVehicleId) {
                        log.error("[{}]矿车已分配!", str);
                        continue;
                    }
                    UnitVehicle unitVehicle = new UnitVehicle();
                    User currentUser = super.getCurrentUser();
                    unitVehicle.setCreateUserId(currentUser.getUserId());
                    unitVehicle.setAddTime(new Date());
                    unitVehicle.setUnitId(unitId);
                    unitVehicle.setVehicleId(vehicleId);
                    unitVehicles.add(unitVehicle);
                }
                if (GmsUtil.CollectionNotNull(unitVehicles)) {
                    unitVehicleService.clearVehiclesByUnitId(unitId);
                    unitVehicleService.addUnitVehicles(unitVehicles);
                }
            } else {
                unitVehicleService.clearVehiclesByUnitId(unitId);
            }
            return new GmsResponse().message("调度单元分配车俩成功").success();
        } catch (Exception e) {
            String message = "修改调度单元分配车俩失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取指定调度单元车辆")
    @GetMapping(value = "/vehicles/unit/{unitId}")
    @ApiOperation(value = "获取指定调度单元车辆", httpMethod = "GET")
    public GmsResponse getUnitVehicleList(@PathVariable Integer unitId) throws GmsException {
        try {
            List<UnitVehicle> vehicles = unitVehicleService.getUnitVehicleListUnitId(unitId);
            Set<Integer> collects = vehicles.stream().map(UnitVehicle::getVehicleId).collect(Collectors.toSet());
            return new GmsResponse().data(collects).message("获取指定调度单元车辆成功").success();
        } catch (Exception e) {
            String message = "获取指定调度单元车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取指定调度员所有车辆")
    @GetMapping(value = "/vehicles/user/{userId}")
    @ApiOperation(value = "获取指定调度员所有车辆", httpMethod = "GET")
    public GmsResponse getVehiclesByUserId(@PathVariable("userId") Integer userId) throws GmsException {
        try {
            List<UnitVehicle> vehicles = unitVehicleService.getUnitVehicleListByUserId(userId);
            Set<Integer> collects = vehicles.stream().map(UnitVehicle::getVehicleId).collect(Collectors.toSet());
            return new GmsResponse().data(collects).message("获取指定调度员所有车辆成功").success();
        } catch (Exception e) {
            String message = "获取指定调度员所有车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取指定车辆所在调度单元")
    @GetMapping(value = "/vehicles/{vehicleId}")
    @ApiOperation(value = "获取指定车辆所在调度单元", httpMethod = "GET")
    @ResponseBody
    public String getTaskRule(@PathVariable("vehicleId") Integer vehicleId) throws GmsException {
        try {
            Unit unit = unitVehicleService.getUnitByVehicleId(vehicleId);
            return GmsUtil.toJsonIEnumDesc(new GmsResponse().data(unit).message("获取指定车辆所在调度单元成功").success());
        } catch (Exception e) {
            String message = "获取指定车辆所在调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取所有已分配车辆")
    @GetMapping(value = "/vehicles/allocated")
    @ApiOperation(value = "获取所有已分配车辆", httpMethod = "GET")
    public GmsResponse getAllocatedVehicles() throws GmsException {
        try {
            List<UnitVehicle> allocatedVehicles = unitVehicleService.getAllocatedVehicles();
            Set<Integer> collects = allocatedVehicles.stream().map(UnitVehicle::getVehicleId).collect(Collectors.toSet());
            return new GmsResponse().data(collects).message("获取所有已分配车辆成功").success();
        } catch (Exception e) {
            String message = "获取所有已分配车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取系统中所有车辆")
    @GetMapping(value = "/vehicles/all")
    @ApiOperation(value = "获取系统中所有车辆", httpMethod = "GET")
    public GmsResponse getAllVehicles() throws GmsException {
        try {
            List<Map<String, Integer>> allVehicles = unitVehicleService.getAllVehicles();
            return new GmsResponse().data(allVehicles).message("获取系统中所有车辆成功").success();
        } catch (Exception e) {
            String message = "获取系统中所有车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}

