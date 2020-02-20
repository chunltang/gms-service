package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.entity.vehiclemanager.VehicleType;
import com.zs.gms.service.vehiclemanager.VehicleTypeService;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/vehicleTypes")
@Validated
@Api(tags = {"车辆管理"},description = "Vehicle Controller")
public class VehicleTypeController {

    @Autowired
    @Lazy
    private VehicleTypeService vehicleTypeService;

    @Log("新增车辆类型")
    @PostMapping
    @ApiOperation(value = "新增车辆类型",httpMethod = "POST")
    public GmsResponse addVehicleType(@Valid @MultiRequestBody VehicleType vehicleType) throws GmsException {
        try {
            this.vehicleTypeService.addVehicleType(vehicleType);
            return new GmsResponse().message("新增车辆类型成功").success();
        }catch (Exception e){
            String message="新增车辆类型失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("获取车辆类型列表")
    @GetMapping
    @ApiOperation(value = "获取车辆类型列表",httpMethod = "GET")
    public GmsResponse getVehicleTypeList() throws GmsException {
        try {
            List<VehicleType> list = this.vehicleTypeService.getVehicleTypeList();
            return new GmsResponse().data(list).success();
        }catch (Exception e){
            String message="获取车辆类型列表失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("修改车辆类型参数")
    @PutMapping
    @ApiOperation(value = "修改车辆类型参数",httpMethod = "PUT")
    public GmsResponse updateVehicleType(@MultiRequestBody VehicleType vehicleType) throws GmsException {
        if(null==vehicleType.getVehicleTypeId()){
            throw new GmsException("车辆类型ID为空");
        }
        try {
            this.vehicleTypeService.updateVehicleType(vehicleType);
            return new GmsResponse().message("修改车辆类型参数成功").success();
        }catch (Exception e){
            String message="修改车辆类型参数失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("删除车辆类型")
    @DeleteMapping(value = "/{VehicleTypeIds}")
    @ApiOperation(value = "删除车辆类型",httpMethod = "DELETE")
    public GmsResponse deleteVehicleType(@PathVariable String VehicleTypeIds) throws GmsException {
        if(StringUtils.isEmpty(VehicleTypeIds)){
            throw new GmsException("车辆类型ID为空");
        }
        try {
            this.vehicleTypeService.deleteVehicleType(VehicleTypeIds);
            return new GmsResponse().message("删除车辆类型成功").success();
        }catch (Exception e){
            String message="删除车辆类型失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
