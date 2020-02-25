package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.client.Excavator;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.service.client.ExcavatorService;
import com.zs.gms.service.client.UserExcavatorLoadAreaService;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.system.UserService;
import com.zs.gms.service.vehiclemanager.BarneyService;
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
@Api(tags = {"车辆管理"},description = "vehicle Controller")
public class ExcavatorController extends BaseController {

    @Autowired
    private ExcavatorService excavatorService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserExcavatorLoadAreaService bindExcavatorService;


    @Log("新增挖掘机")
    @PostMapping(value = "/excavators")
    @ApiOperation(value = "新增挖掘机", httpMethod = "POST")
    public GmsResponse addExcavator(@MultiRequestBody("excavator") @Valid Excavator excavator) throws GmsException {
        try {
            boolean exist = this.excavatorService.isExistNo(excavator.getExcavatorNo());
            if (exist) {
                return new GmsResponse().message("该挖掘机编号已添加").badRequest();
            }
            this.excavatorService.addExcavator(excavator);
            return new GmsResponse().message("新增挖掘机成功").success();
        } catch (Exception e) {
            String message = "新增挖掘机失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("删除挖掘机")
    @DeleteMapping(value = "/excavators/{excavatorId}")
    @ApiOperation(value = "删除挖掘机", httpMethod = "DELETE")
    public GmsResponse delExcavator(@PathVariable(value = "excavatorId") Integer excavatorId) throws GmsException {
        try {
            this.excavatorService.delExcavator(excavatorId);
            return new GmsResponse().message("删除挖掘机成功").success();
        } catch (Exception e) {
            String message = "删除挖掘机失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("修改挖掘机参数")
    @PutMapping(value = "/excavators")
    @ApiOperation(value = "修改挖掘机参数", httpMethod = "PUT")
    public GmsResponse updateExcavator(@MultiRequestBody("excavator") @Valid Excavator excavator) throws GmsException {
        try {
            boolean exist = this.excavatorService.isExistNo(excavator.getExcavatorNo());
            if (exist) {
                return new GmsResponse().message("该挖掘机编号不存在").badRequest();
            }
            this.excavatorService.updateExcavator(excavator);
            return new GmsResponse().message("修改挖掘机参数成功").success();
        } catch (Exception e) {
            String message = "修改挖掘机参数失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("挖掘机绑定用户和装载区")
    @PostMapping(value = "/excavators/bindExcavators")
    @ApiOperation(value = "挖掘机绑定用户和装载区", httpMethod = "POST")
    public GmsResponse bindExcavatorAndUserAndLoadArea(@MultiRequestBody("bindExcavator") @Valid UserExcavatorLoadArea bindExcavator) throws GmsException {
        try {
            boolean excavatorExist = this.excavatorService.isExistId(bindExcavator.getExcavatorId());
            if (excavatorExist) {
                return new GmsResponse().message("该挖掘机不存在").badRequest();
            }

            boolean areaExist = MapDataUtil.isAreaExist(bindExcavator.getMapId(), bindExcavator.getLoadArea(), AreaTypeEnum.LOAD_AREA);
            if (areaExist) {
                return new GmsResponse().message("该地图不存在需设置的装载区").badRequest();
            }

            User user = userService.findUserById(bindExcavator.getUserId());
            if (user == null) {
                return new GmsResponse().message("该挖掘机操作员用户不存在").badRequest();
            }
            if (!Role.RoleSign.EXCAVATORPERSON_ROLE.equals(user.getRoleSign())) {
                return new GmsResponse().message("选择绑定的用户不是挖掘机操作员角色").badRequest();
            }
            bindExcavatorService.bindExcavator(bindExcavator);
            return new GmsResponse().message("挖掘机绑定用户和装载区成功").success();
        } catch (Exception e) {
            String message = "挖掘机绑定用户和装载区失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("修改挖掘机工作区域")
    @PutMapping(value = "/excavators/bindExcavators/loadArea")
    @ApiOperation(value = "修改挖掘机工作区域", httpMethod = "PUT")
    public GmsResponse updateExcavatorLoadArea(@MultiRequestBody("excavatorId") Integer excavatorId,
                                               @MultiRequestBody("loadAreaId") Integer loadAreaId,
                                               @MultiRequestBody("mapId") Integer mapId) throws GmsException {
        try {
            boolean exist = this.excavatorService.isExistId(excavatorId);
            if (exist) {
                return new GmsResponse().message("该挖掘机编号不存在").badRequest();
            }

            boolean areaExist = MapDataUtil.isAreaExist(mapId, loadAreaId, AreaTypeEnum.LOAD_AREA);
            if (areaExist) {
                return new GmsResponse().message("该地图不存在需设置的装载区").badRequest();
            }
            this.bindExcavatorService.updateLoadArea(excavatorId, mapId, loadAreaId);
            return new GmsResponse().message("修改挖掘机工作区域成功").success();
        } catch (Exception e) {
            String message = "修改挖掘机工作区域失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("修改挖掘机操作员")
    @PutMapping(value = "/excavators/bindExcavators/user")
    @ApiOperation(value = "修改挖掘机操作员", httpMethod = "PUT")
    public GmsResponse updateExcavatorUser(@MultiRequestBody("excavatorId") Integer excavatorId,
                                           @MultiRequestBody("userId") Integer userId) throws GmsException {
        try {
            boolean exist = this.excavatorService.isExistId(excavatorId);
            if (exist) {
                return new GmsResponse().message("该挖掘机编号不存在").badRequest();
            }

            User user = userService.findUserById(userId);
            if (user == null) {
                return new GmsResponse().message("该挖掘机操作员用户不存在").badRequest();
            }
            if (!Role.RoleSign.EXCAVATORPERSON_ROLE.equals(user.getRoleSign())) {
                return new GmsResponse().message("选择绑定的用户不是挖掘机操作员角色").badRequest();
            }
            this.bindExcavatorService.updateUser(excavatorId, userId);
            return new GmsResponse().message("修改挖掘机操作员成功").success();
        } catch (Exception e) {
            String message = "修改挖掘机操作员失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}