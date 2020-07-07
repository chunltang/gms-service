package com.zs.gms.controller.monitor;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.utils.DateUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.monitor.UnitService;
import com.zs.gms.service.monitor.UnitVehicleService;
import com.zs.gms.service.system.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/units")
@Slf4j
@Validated
@Api(tags = "运营监控", description = "Unit Controller")
public class UnitController extends BaseController {

    @Autowired
    private UnitService unitService;

    @Autowired
    private UserService userService;

    @Autowired
    private UnitVehicleService unitVehicleService;

    @Log("添加调度单元")
    @PostMapping
    @ApiOperation(value = "添加调度单元", httpMethod = "POST")
    public void addUnit(@Valid @MultiRequestBody Unit unit, HttpServletResponse response) throws GmsException {
        try {
            User currentUser = super.getCurrentUser();
            GmsResponse gmsResponse = null;
            if (!currentUser.getRoleSign().equals(Role.RoleSign.CHIEFDESPATCHER_ROLE.getValue())) {
                gmsResponse = new GmsResponse().message("非调度长不能管理调度单元!").badRequest();
            }
            boolean existName = unitService.isExistName(unit.getUnitName());
            if (existName) {
                gmsResponse = new GmsResponse().message("调度单元名称已存在").badRequest();
            }
            Integer id = unit.getUserId();
            if(null!=id){
                User user = userService.findUserById(id);
                if (user == null || !user.getRoleSign().equals(Role.RoleSign.DESPATCHER_ROLE.getValue())) {
                    gmsResponse = new GmsResponse().message("选择的调度员不存在").badRequest();
                }
            }
            Integer activeMap = MapDataUtil.getActiveMap();
            if (null == activeMap) {
                gmsResponse = new GmsResponse().message("没有处于活动中的地图!").badRequest();
            }
            SemiStatic loadAreaInfo = MapDataUtil.getAreaInfo(activeMap, unit.getLoadAreaId());
            if (null == loadAreaInfo) {
                gmsResponse = new GmsResponse().message("选择的装载区不存在!").badRequest();
            }
            boolean existLoadId = unitService.isExistLoadId(unit.getLoadAreaId(),activeMap);
            if (existLoadId) {
                gmsResponse = new GmsResponse().message("该选定装载区已在其他调度单元中使用!").badRequest();
            }
            SemiStatic unloadAreaInfo = MapDataUtil.getAreaInfo(activeMap, unit.getUnLoadAreaId());
            if (null == unloadAreaInfo) {
                gmsResponse = new GmsResponse().message("选择的卸载区不存在!").badRequest();
            }
            if (null != gmsResponse) {
                GmsService.callResponse(gmsResponse, response);
                return;
            }
            unit.setMapId(activeMap);
            unit.setCreateUserId(currentUser.getUserId());
            unitService.addUnit(unit);

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("unitId", unit.getUnitId());
            paramMap.put("loaderAreaId", unit.getLoadAreaId());
            paramMap.put("unLoaderAreaId", unit.getUnLoadAreaId());
            paramMap.put("cycleTimes", unit.getCycleTimes());
            paramMap.put("endTime", GmsUtil.objNotNull(unit.getEndTime())? DateUtil.getDateFormat(unit.getEndTime(),DateUtil.FULL_TIME_SPLIT_PATTERN):"");
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (!entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作失败
                    unitService.deleteUnit(unit.getUnitId());
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "CreateLoaderAIUnit", JSONObject.toJSONString(paramMap), "添加调度单元成功");
        } catch (Exception e) {
            String message = "添加调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取调度单元列表")
    @GetMapping
    @ApiOperation(value = "获取调度单元列表", httpMethod = "GET")
    @ResponseBody
    public String getDispatchTaskList(QueryRequest queryRequest) throws GmsException {
        try {
            Integer mapId = MapDataUtil.getActiveMap();
            if(null==mapId){
                return GmsUtil.toJsonIEnumDesc(new GmsResponse().message("活动地图不存在!").badRequest());
            }
            Map<String, Object> dataTable = super.getDataTable(unitService.getUnitList(queryRequest,mapId));
            return GmsUtil.toJsonIEnumDesc(new GmsResponse().data(dataTable).message("获取调度单元列表成功").success());
        } catch (Exception e) {
            String message = "获取调度单元列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("修改调度单元")
    @PutMapping
    @ApiOperation(value = "修改调度单元", httpMethod = "PUT")
    public void updateUnit(@MultiRequestBody Unit unit,HttpServletResponse response) throws GmsException {
        try {
            boolean existId = unitService.isExistId(unit.getUnitId());
            if (!existId) {
                 GmsService.callResponse(new GmsResponse().message("调度单元不存在").badRequest(),response);
                 return;
            }
            String unitName = unit.getUnitName();
            if (GmsUtil.StringNotNull(unitName)) {
                boolean existName = unitService.isExistName(unitName, unit.getUnitId());
                if (existName) {
                    GmsService.callResponse(new GmsResponse().message("调度单元名称已存在").badRequest(),response);
                    return;
                }
            }
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("unitId", unit.getUnitId());
            paramMap.put("cycleTimes", unit.getCycleTimes());
            paramMap.put("endTime", GmsUtil.objNotNull(unit.getEndTime())? DateUtil.getDateFormat(unit.getEndTime(),DateUtil.FULL_TIME_SPLIT_PATTERN):"");
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
            entry.setAfterHandle(()->{
                if(MessageResult.SUCCESS.equals(entry.getHandleResult())){
                    unitService.updateUnit(unit);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(),"ModifyLoaderAIUnit",GmsUtil.toJson(paramMap),"修改调度单元成功");
        } catch (Exception e) {
            String message = "修改调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("删除调度单元")
    @DeleteMapping("/{unitId}")
    @ApiOperation(value = "删除调度单元", httpMethod = "DELETE")
    public void deleteUnit(@PathVariable("unitId") Integer unitId, HttpServletResponse response) throws GmsException {
        try {
            boolean existId = unitService.isExistId(unitId);
            if (!existId) {
                GmsService.callResponse(new GmsResponse().message("调度单元不存在").badRequest(), response);
                return;
            }
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("unitId", unitId);
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                    unitService.deleteUnit(unitId);
                    unitVehicleService.clearVehiclesByUnitId(unitId);
                }
            });
            MessageFactory.getDispatchMessage().sendMessageWithID(entry.getMessageId(), "RemoveAIUnit", JSONObject.toJSONString(paramMap), "删除调度单元成功");
        } catch (Exception e) {
            String message = "删除调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取调度员管理的调度单元")
    @GetMapping(value = "/dispatcher")
    @ApiOperation(value = "获取调度员管理的调度单元", httpMethod = "GET")
    @ResponseBody
    public String getUnits() throws GmsException {
        try {
            Integer mapId = MapDataUtil.getActiveMap();
            if(null==mapId){
                return GmsUtil.toJsonIEnumDesc(new GmsResponse().message("活动地图不存在!").badRequest());
            }
            User currentUser = this.getCurrentUser();
            List<Unit> units = unitService.getUnitListByUserId(currentUser.getUserId(),mapId);
            return GmsUtil.toJsonIEnumDesc(new GmsResponse().data(units).message("获取调度员管理的调度单元成功").success());
        } catch (Exception e) {
            String message = "获取调度员管理的调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}

