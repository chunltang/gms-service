package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.entity.vehiclemanager.Vehicle;
import com.zs.gms.service.vehiclemanager.VehicleService;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/vehicles")
@Validated
@Api(tags = {"车辆管理"},description = "Vehicle Controller")
public class VehicleController extends BaseController {

    @Autowired
    @Lazy
    private VehicleService vehicleService;

    @Log("新增车辆")
    @PostMapping
    @ApiOperation(value = "新增车辆",httpMethod = "POST")
    public GmsResponse addVehicle(@Valid @MultiRequestBody Vehicle vehicle) throws GmsException {
        try {
            if(this.vehicleService.queryVhicleExist(vehicle.getVehicleNo())){
                throw new GmsException("该车辆编号已添加");
            }
            this.vehicleService.addVehicle(vehicle);
            return new GmsResponse().message("新增车辆成功").success();
        }catch (Exception e){
            String message="新增车辆失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取车辆列表")
    @GetMapping
    @ApiOperation(value = "获取车辆列表",httpMethod = "GET")
    public GmsResponse getVehicleList(Vehicle vehicle, QueryRequest queryRquest) throws GmsException {
        try {
            Map<String, Object> dataTable = this.getDataTable(this.vehicleService.getVehicleList(vehicle, queryRquest));
            return new GmsResponse().data(dataTable).message("获取车辆列表成功").success();
        }catch (Exception e){
            String message="获取车辆列表失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("修改车辆信息")
    @PutMapping
    @ApiOperation(value = "修改车辆信息",httpMethod = "PUT")
    public GmsResponse updateVehicle(@MultiRequestBody Vehicle vehicle) throws GmsException {
        try {
            this.vehicleService.updateVehicle(vehicle);
            return new GmsResponse().message("修改车辆信息成功").success();
        }catch (Exception e){
            String message="修改车辆信息失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("删除车辆")
    @DeleteMapping(value = "/{vehicleIds}")
    @ApiOperation(value = "删除车辆",httpMethod = "DELETE")
    public GmsResponse deleteVehicle(String vehicleIds) throws GmsException {
        try {
            this.vehicleService.deleteVehicle(vehicleIds);
            return new GmsResponse().message("删除车辆成功").success();
        }catch (Exception e){
            String message="删除车辆失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("批量车辆分配")
    @PutMapping(value = "/vehicles/vehicleAllots/{userId}")
    @ApiOperation(value = "批量车辆分配",httpMethod = "PUT")
    public GmsResponse vehicleAllot(@MultiRequestBody("vehicleIds") String vehicleIds,@PathVariable Integer userId) throws GmsException {
        try {
            boolean vehicleAllot = this.vehicleService.isVehicleAllot(vehicleIds);
            if(vehicleAllot){
                return new GmsResponse().message("存在已分配车辆").badRequest();
            }
            this.vehicleService.addUserVehicles(userId,vehicleIds);
            return new GmsResponse().message("批量车辆分配成功").success();
        }catch (Exception e){
            String message="批量车辆分配失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }
}
