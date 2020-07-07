package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.Mark;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.vehiclemanager.Excavator;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.entity.vehiclemanager.ExcavatorType;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.service.vehiclemanager.ExcavatorService;
import com.zs.gms.service.vehiclemanager.ExcavatorTypeService;
import com.zs.gms.service.vehiclemanager.UserExcavatorLoadAreaService;
import com.zs.gms.service.init.SyncRedisData;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.system.UserService;
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

    @Autowired
    private ExcavatorTypeService excavatorTypeService;

    @Log("新增挖掘机")
    @Mark(value = "新增挖掘机",markImpl = SyncRedisData.class)
    @PostMapping(value = "/excavators")
    @ApiOperation(value = "新增挖掘机", httpMethod = "POST")
    public GmsResponse addExcavator(@MultiRequestBody @Valid Excavator excavator) throws GmsException {
        try {
            boolean exist = this.excavatorService.isExistNo(excavator.getExcavatorNo());
            if (exist) {
                return new GmsResponse().message("该挖掘机编号已添加!").badRequest();
            }
            boolean existTypeId = this.excavatorTypeService.isExistTypeId(excavator.getExcavatorTypeId());
            if(!existTypeId){
                return new GmsResponse().message("不存在的挖掘机类型").badRequest();
            }
            this.excavatorService.addExcavator(excavator);
            return new GmsResponse().message("新增挖掘机成功").success();
        } catch (Exception e) {
            String message = "新增挖掘机失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取挖掘机列表")
    @GetMapping("/excavators")
    @ApiOperation(value = "获取挖掘机列表", httpMethod = "GET")
    @ResponseBody
    public String getExcavatorList(Excavator excavator, QueryRequest queryRequest) throws GmsException {
        try {
            Map<String, Object> dataTable = this.getDataTable(this.excavatorService.getExcavatorList(excavator,queryRequest));
            return GmsUtil.toJsonIEnumDesc(new GmsResponse().data(dataTable).message("获取挖掘机列表成功").success());
        } catch (Exception e) {
            String message = "获取挖掘机列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("删除挖掘机")
    @Mark(value = "删除挖掘机",markImpl = SyncRedisData.class)
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
    @Mark(value = "修改挖掘机参数",markImpl = SyncRedisData.class)
    @PutMapping(value = "/excavators")
    @ApiOperation(value = "修改挖掘机参数", httpMethod = "PUT")
    public GmsResponse updateExcavator(@MultiRequestBody Excavator excavator) throws GmsException {
        try {
            Excavator ex = excavatorService.getExcavatorById(excavator.getExcavatorId());
            if(null==ex){
                return new GmsResponse().message("该车辆信息不存在!").badRequest();
            }
            if(WhetherEnum.YES.equals(ex.getVehicleStatus())){
                return new GmsResponse().message("该车辆处于激活状态，不能修改!").badRequest();
            }

            Integer excavatorTypeId = excavator.getExcavatorTypeId();
            if(null!=excavatorTypeId){
                boolean existTypeId = excavatorTypeService.isExistTypeId(excavatorTypeId);
                if(!existTypeId){
                    return new GmsResponse().message("改挖掘机类型不存在").badRequest();
                }
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
    public GmsResponse bindExcavatorAndUserAndLoadArea(@MultiRequestBody @Valid UserExcavatorLoadArea bindExcavator) throws GmsException {
        try {
            boolean existUser = this.bindExcavatorService.isExistUser(bindExcavator.getUserId());
            if(existUser){
                return new GmsResponse().message("该用户已绑定挖掘机").badRequest();
            }

            boolean excavatorExist = this.excavatorService.isExistId(bindExcavator.getExcavatorId());
            if (excavatorExist) {
                return new GmsResponse().message("该挖掘机不存在").badRequest();
            }

            boolean areaExist = MapDataUtil.isAreaExist(bindExcavator.getMapId(), bindExcavator.getLoadAreaId(), AreaTypeEnum.LOAD_AREA);
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

    @Log("获取挖掘机类型")
    @GetMapping("/excavators/types/{excavatorTypeId}")
    @ApiOperation(value = "获取矿车列表", httpMethod = "GET")
    public GmsResponse getExcavatorType(@PathVariable("excavatorTypeId")Integer excavatorTypeId) throws GmsException {
        try {
            ExcavatorType excavatorType = excavatorTypeService.getExcavatorType(excavatorTypeId);
            return new GmsResponse().data(excavatorType).message("获取挖掘机类型成功").success();
        } catch (Exception e) {
            String message = "获取挖掘机类型失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("根据用户获取挖掘机绑定信息")
    @GetMapping("/excavators/bindInfo/{userId}")
    @ApiOperation(value = "根据用户获取挖掘机绑定信息", httpMethod = "GET")
    public GmsResponse getBindInfo(@PathVariable("userId") Integer userId) throws GmsException {
        try {
            UserExcavatorLoadArea bind = this.bindExcavatorService.getBindByUser(userId);
            return new GmsResponse().data(bind).message("获取绑定信息成功").success();
        } catch (Exception e) {
            String message = "获取绑定信息失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}
