package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.entity.client.Excavator;
import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.service.client.ExcavatorService;
import com.zs.gms.service.vehiclemanager.BarneyService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/vehicles")
@Validated
@Api(tags = {"车辆管理"},description = "Barney Controller")
public class VehicleController extends BaseController {

    @Autowired
    private BarneyService barneyService;

    @Autowired
    private ExcavatorService excavatorService;

    @Log("新增矿车")
    @PostMapping("/barneys")
    @ApiOperation(value = "新增矿车",httpMethod = "POST")
    public GmsResponse addVehicle(@Valid @MultiRequestBody Barney barney) throws GmsException {
        try {
            if(this.barneyService.queryVhicleExist(barney.getVehicleNo())){
                throw new GmsException("该车辆编号已添加");
            }
            this.barneyService.addVehicle(barney);
            return new GmsResponse().message("新增矿车成功").success();
        }catch (Exception e){
            String message="新增矿车失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取矿车列表")
    @GetMapping("/barneys")
    @ApiOperation(value = "获取矿车列表",httpMethod = "GET")
    public GmsResponse getVehicleList(Barney barney, QueryRequest queryRequest) throws GmsException {
        try {
            Map<String, Object> dataTable = this.getDataTable(this.barneyService.getVehicleList(barney, queryRequest));
            return new GmsResponse().data(dataTable).message("获取矿车列表成功").success();
        }catch (Exception e){
            String message="获取矿车列表失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("修改矿车信息")
    @PutMapping("/barneys")
    @ApiOperation(value = "修改矿车信息",httpMethod = "PUT")
    public GmsResponse updateVehicle(@MultiRequestBody Barney barney) throws GmsException {
        try {
            this.barneyService.updateVehicle(barney);
            return new GmsResponse().message("修改矿车信息成功").success();
        }catch (Exception e){
            String message="修改矿车信息失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("删除矿车")
    @DeleteMapping(value = "/barneys/{vehicleIds}")
    @ApiOperation(value = "删除矿车",httpMethod = "DELETE")
    public GmsResponse deleteVehicle(@PathVariable(value = "vehicleIds") String vehicleIds) throws GmsException {
        try {
            this.barneyService.deleteVehicle(vehicleIds);
            return new GmsResponse().message("删除矿车成功").success();
        }catch (Exception e){
            String message="删除矿车失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("批量矿车分配")
    @PutMapping(value = "/barneys/vehicleAllots/{userId}")
    @ApiOperation(value = "批量矿车分配",httpMethod = "PUT")
    public GmsResponse vehicleAllot(@MultiRequestBody("vehicleIds") String vehicleIds,@PathVariable Integer userId) throws GmsException {
        try {
            boolean vehicleAllot = this.barneyService.isVehicleAllot(vehicleIds);
            if(vehicleAllot){
                return new GmsResponse().message("存在已分配矿车").badRequest();
            }
            this.barneyService.addUserVehicles(userId,vehicleIds);
            return new GmsResponse().message("批量矿车分配成功").success();
        }catch (Exception e){
            String message="批量矿车分配失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("新增挖掘机")
    @PostMapping(value = "/excavators")
    @ApiOperation(value = "新增挖掘机",httpMethod = "POST")
    public GmsResponse addExcavator(@MultiRequestBody("excavator") @Valid Excavator excavator) throws GmsException {
        try {
            boolean exist = this.excavatorService.isExist(excavator.getExcavatorNo());
            if(exist){
                return new GmsResponse().message("该挖掘机编号已添加").badRequest();
            }
            this.excavatorService.addExcavator(excavator);
            return new GmsResponse().message("新增挖掘机成功").success();
        }catch (Exception e){
            String message="新增挖掘机失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("删除挖掘机")
    @DeleteMapping(value = "/excavators/{excavatorId}")
    @ApiOperation(value = "删除挖掘机",httpMethod = "DELETE")
    public GmsResponse delExcavator(@PathVariable(value = "excavatorId") Integer excavatorId) throws GmsException {
        try {
            this.excavatorService.delExcavator(excavatorId);
            return new GmsResponse().message("删除挖掘机成功").success();
        }catch (Exception e){
            String message="删除挖掘机失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("修改挖掘机参数")
    @PutMapping(value = "/excavators")
    @ApiOperation(value = "修改挖掘机参数",httpMethod = "PUT")
    public GmsResponse updateExcavator(@MultiRequestBody("excavator") @Valid Excavator excavator) throws GmsException {
        try {
            boolean exist = this.excavatorService.isExist(excavator.getExcavatorNo());
            if(exist){
                return new GmsResponse().message("该挖掘机编号不存在").badRequest();
            }
            this.excavatorService.updateExcavator(excavator);
            return new GmsResponse().message("修改挖掘机参数成功").success();
        }catch (Exception e){
            String message="修改挖掘机参数失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }
}
