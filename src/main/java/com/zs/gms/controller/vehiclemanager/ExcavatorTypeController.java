package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.Mark;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.entity.vehiclemanager.Excavator;
import com.zs.gms.entity.vehiclemanager.ExcavatorType;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.service.init.SyncRedisData;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.system.UserService;
import com.zs.gms.service.vehiclemanager.ExcavatorService;
import com.zs.gms.service.vehiclemanager.ExcavatorTypeService;
import com.zs.gms.service.vehiclemanager.UserExcavatorLoadAreaService;
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
@RequestMapping(value = "/excavatorTypes")
@Validated
@Api(tags = {"车辆管理"},description = "vehicleType Controller")
public class ExcavatorTypeController extends BaseController {

    @Autowired
    private ExcavatorTypeService excavatorTypeService;

    @Log("新增挖掘机类型")
    @PostMapping
    @ApiOperation(value = "新增挖掘机类型", httpMethod = "POST")
    public GmsResponse addExcavatorType(@MultiRequestBody @Valid ExcavatorType excavatorType) throws GmsException {
        try {
            boolean exist = this.excavatorTypeService.isExistName(excavatorType.getExcavatorTypeName());
            if (exist) {
                return new GmsResponse().message("该挖掘机类型名称已添加").badRequest();
            }
            this.excavatorTypeService.addExcavatorType(excavatorType);
            return new GmsResponse().message("新增挖掘机类型成功").success();
        } catch (Exception e) {
            String message = "新增挖掘机类型失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取挖掘机类型列表")
    @GetMapping
    @ApiOperation(value = "获取挖掘机类型列表", httpMethod = "GET")
    public GmsResponse gtExcavatorTypeList(QueryRequest queryRequest) throws GmsException {
        try {
            Map<String, Object> dataTable = super.getDataTable(this.excavatorTypeService.getExcavatorTypeListPage(queryRequest));
            return new GmsResponse().data(dataTable).message("获取挖掘机类型列表成功").success();
        } catch (Exception e) {
            String message = "获取挖掘机类型列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("修改挖掘机类型")
    @PutMapping
    @ApiOperation(value = "修改挖掘机类型", httpMethod = "PUT")
    public GmsResponse updateExcavatorType(@MultiRequestBody ExcavatorType excavatorType) throws GmsException {
        try {
            boolean exist = this.excavatorTypeService.isExistTypeId(excavatorType.getExcavatorTypeId());
            if (!exist) {
                return new GmsResponse().message("该挖掘机类型不存在").badRequest();
            }
            boolean existName = this.excavatorTypeService.isExistName(excavatorType.getExcavatorTypeId(), excavatorType.getExcavatorTypeName());
            if(existName){
                return new GmsResponse().message("该挖掘机类型名称已存在").badRequest();
            }
            this.excavatorTypeService.updateExcavatorType(excavatorType);
            return new GmsResponse().message("修改挖掘机类型成功").success();
        } catch (Exception e) {
            String message = "修改挖掘机类型失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("删除挖掘机类型")
    @DeleteMapping("/{excavatorTypeId}")
    @ApiOperation(value = "删除挖掘机类型", httpMethod = "DELETE")
    public GmsResponse deleteExcavatorType(@PathVariable("excavatorTypeId") Integer excavatorTypeId) throws GmsException {
        try {
            boolean exist = this.excavatorTypeService.isExistTypeId(excavatorTypeId);
            if (!exist) {
                return new GmsResponse().message("该挖掘机类型不存在").badRequest();
            }
            this.excavatorTypeService.deleteExcavatorType(excavatorTypeId);
            return new GmsResponse().message("删除挖掘机类型成功").success();
        } catch (Exception e) {
            String message = "删除挖掘机类型失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}
