package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.vehiclemanager.BarneyType;
import com.zs.gms.service.vehiclemanager.BarneyTypeService;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/vehicleTypes")
@Validated
@Api(tags = {"车辆管理"},description = "vehicle Controller")
public class BarneyTypeController extends BaseController {

    @Autowired
    @Lazy
    private BarneyTypeService barneyTypeService;

    @Log("新增车辆类型")
    @PostMapping
    @ApiOperation(value = "新增车辆类型",httpMethod = "POST")
    public GmsResponse addVehicleType(@Valid @MultiRequestBody BarneyType barneyType) throws GmsException {
        try {
            boolean existId = barneyTypeService.isExistId(barneyType.getVehicleTypeName());
            if(existId){
                return new GmsResponse().message("该车辆类型名称已添加!").badRequest();
            }
            this.barneyTypeService.addVehicleType(barneyType);
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
    public GmsResponse getVehicleTypeList(QueryRequest queryRequest) throws GmsException {
        try {
            Map<String, Object> dataTable = super.getDataTable(this.barneyTypeService.getVehicleTypeList(queryRequest));
            return new GmsResponse().data(dataTable).message("获取车辆类型列表成功").success();
        }catch (Exception e){
            String message="获取车辆类型列表失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("修改车辆类型参数")
    @PutMapping
    @ApiOperation(value = "修改车辆类型参数",httpMethod = "PUT")
    public GmsResponse updateVehicleType(@MultiRequestBody BarneyType barneyType) throws GmsException {
        if(null== barneyType.getVehicleTypeId()){
            throw new GmsException("车辆类型ID为空");
        }
        try {
            this.barneyTypeService.updateVehicleType(barneyType);
            return new GmsResponse().message("修改车辆类型参数成功").success();
        }catch (Exception e){
            String message="修改车辆类型参数失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("删除车辆类型")
    @DeleteMapping(value = "/{vehicleTypeId}")
    @ApiOperation(value = "删除车辆类型",httpMethod = "DELETE")
    public GmsResponse deleteVehicleType(@PathVariable Integer vehicleTypeId) throws GmsException {
        if(!GmsUtil.objNotNull(vehicleTypeId)){
            throw new GmsException("车辆类型ID为空");
        }
        try {
            BarneyType barneyType = this.barneyTypeService.getBarneyType(vehicleTypeId);
            if(null==barneyType){
                return new GmsResponse().message("不存在的车辆类型!").badRequest();
            }
            if(barneyType.getActive().equals(WhetherEnum.YES)){
                return new GmsResponse().message("激活的车辆类型不能删除!").badRequest();
            }
            this.barneyTypeService.deleteVehicleType(vehicleTypeId);
            return new GmsResponse().message("删除车辆类型成功").success();
        }catch (Exception e){
            String message="删除车辆类型失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
