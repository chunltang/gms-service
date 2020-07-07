package com.zs.gms.controller.monitor;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.Mark;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.HttpContextUtil;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.monitor.UnitVehicle;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.monitor.UnitService;
import com.zs.gms.service.monitor.UnitVehicleService;
import com.zs.gms.service.monitor.impl.UnitVehicleServiceImpl;
import com.zs.gms.service.vehiclemanager.BarneyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    @Log("调度单元分配车辆")
    @PostMapping
    @ApiOperation(value = "调度单元分配车辆", httpMethod = "POST")
    public void updateUnitVehicle(@MultiRequestBody("unitId") Integer unitId,
                                  @MultiRequestBody("vehicleIds") String vehicleIds,
                                  HttpServletResponse response) throws GmsException {
        try {
            boolean existId = unitService.isExistId(unitId);
            if (!existId) {
                GmsService.callResponse(new GmsResponse().message("调度单元不存在").badRequest(), response);
                return;
            }
            List<UnitVehicle> unitVehicleList = unitVehicleService.getUnitVehicleListUnitId(unitId);
            Set<Integer> preVehicleIds = unitVehicleList.stream().map(UnitVehicle::getVehicleId).collect(Collectors.toSet());
            if (GmsUtil.StringNotNull(vehicleIds)) {
                List<UnitVehicle> unitVehicles = new ArrayList<>();
                MessageEntry messageEntry=addVehicles(unitId,vehicleIds,unitVehicles);
                if(null!=messageEntry){
                    messageEntry.setFirstHandle(()->{
                        unitVehicleService.clearVehiclesByUnitId(unitId);
                    });
                    MessageFactory.getDispatchMessage().sendMessageWithID(messageEntry.getMessageId(), "AddLoadAIVeh", JSONObject.toJSONString(messageEntry.getParams()), "调度单元分配车辆成功");
                }
                //添加后的ids
                Set<Integer> afterVehicleIds = unitVehicles.stream().map(UnitVehicle::getVehicleId).collect(Collectors.toSet());
                //删除车辆
                preVehicleIds.removeAll(afterVehicleIds);
                if(GmsUtil.CollectionNotNull(preVehicleIds)){
                    MessageEntry entry = removeVehiclesEntry(unitId, preVehicleIds);
                    if(null!=entry){
                        entry.setHttp(false);
                        MessageFactory.getDispatchMessage().sendMessageNoResWithID(entry.getMessageId(), "RemoveAIVeh", JSONObject.toJSONString(entry.getParams()));
                    }
                }
            } else {
                //删除车辆
                MessageEntry messageEntry = removeVehiclesEntry(unitId, preVehicleIds);
                if(null!=messageEntry){
                    messageEntry.setFirstHandle(() -> {
                        if(MessageResult.SUCCESS.equals(messageEntry.getHandleResult())){
                            unitVehicleService.clearVehiclesByUnitId(unitId);
                        }
                    });
                    MessageFactory.getDispatchMessage().sendMessageWithID(messageEntry.getMessageId(), "RemoveAIVeh", JSONObject.toJSONString(messageEntry.getParams()), "调度单元分配车辆成功");
                }
            }
        } catch (Exception e) {
            String message = "修改调度单元分配车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("调度单元增加车辆")
    @PostMapping(value = "/vehicles/unit")
    @ApiOperation(value = "调度单元增加车辆", httpMethod = "POST")
    public void addUnitVehicles(@MultiRequestBody("unitId") Integer unitId,@MultiRequestBody("vehicleIds") String vehicleIds) throws GmsException {
        List<UnitVehicle> unitVehicles=new ArrayList<>();
        MessageEntry messageEntry=addVehicles(unitId,vehicleIds,unitVehicles);
        if(null!=messageEntry){
            MessageFactory.getDispatchMessage().sendMessageWithID(messageEntry.getMessageId(), "AddLoadAIVeh", JSONObject.toJSONString(messageEntry.getParams()), "调度单元分配车辆成功");
        }
    }

    private MessageEntry addVehicles(Integer unitId,String vehicleIds,List<UnitVehicle> unitVehicles){
        try {
            String[] vehIds = vehicleIds.split(StringPool.COMMA);
            Set<String> set = new HashSet<>(Arrays.asList(vehIds));
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()){
                String str=iterator.next();
                Integer vehicleId = Integer.valueOf(str);
                boolean existVehicleNo = barneyService.isExistVehicleNo(vehicleId);
                if (!existVehicleNo) {
                    log.error("[{}]矿车编号不存在!", str);
                    continue;
                }
                boolean existVehicleId = unitVehicleService.isExistVehicleId(unitId, vehicleId);
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
                unitVehicle.setStatus(WhetherEnum.NO);
            }
            if(!GmsUtil.CollectionNotNull(unitVehicles)){
                log.debug("无新增车辆!");
                return null;
            }
            Set<Integer> ids = unitVehicles.stream().map(UnitVehicle::getVehicleId).collect(Collectors.toSet());
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("vehicleIds", ids);
            paramMap.put("unitId", unitId);
            MessageEntry messageEntry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            messageEntry.setAfterHandle(() -> {
                if (messageEntry.getHandleResult().equals(MessageResult.SUCCESS)) {//操作成功
                    unitVehicleService.addUnitVehicles(unitVehicles);
                    unitVehicleService.updateVehicleActive();
                }
            });
            messageEntry.setParams(paramMap);
            return messageEntry;
        } catch (Exception e) {
            String message = "调度单元增加车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("调度单元移除车辆")
    @DeleteMapping(value = "/vehicles/unit/{unitId}/{vehicleIds}")
    @ApiOperation(value = "调度单元移除车辆", httpMethod = "DELETE")
    public void delUnitVehicles(@PathVariable("unitId") Integer unitId,@PathVariable("vehicleIds") String vehicleIds) throws GmsException {
        try {
            String[] split = vehicleIds.split(StringPool.COMMA);
            Set<Integer> ids=new HashSet<>();
            for (String str : split) {
                Integer vehicleId = Integer.valueOf(str);
                boolean existVehicleId = unitVehicleService.isExistVehicleId(vehicleId);
                if(!existVehicleId){
                    log.debug("[{}]该车辆没有分配，不需要删除",vehicleId);
                    continue;
                }
                ids.add(vehicleId);
            }
            MessageEntry entry = removeVehiclesEntry(unitId, ids);
            if(null!=entry){
                entry.setFirstHandle(()->{
                    unitVehicleService.deleteUnitVehicles(unitId,vehicleIds);
                });
                MessageFactory.getDispatchMessage().sendMessageNoResWithID(entry.getMessageId(), "RemoveAIVeh", JSONObject.toJSONString(entry.getParams()));
            }else{
                GmsService.callResponse(new GmsResponse().message("无可删除车辆!").badRequest(),HttpContextUtil.getHttpServletResponse());
            }
        } catch (Exception e) {
            String message = "调度单元移除车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    private MessageEntry removeVehiclesEntry(Integer unitId, Set<Integer> ids){
        Map<String, Object> paramMap = new HashMap<>();
        if (GmsUtil.CollectionNotNull(ids)) {
            paramMap.put("vehicleIds", ids);
            paramMap.put("unitId", unitId);
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setParams(paramMap);
            entry.setAfterHandle(()->{
                //改变车辆的激活状态
                unitVehicleService.updateVehicleActive();
            });
            return entry;
        }
        return null;
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
            Integer mapId = MapDataUtil.getActiveMap();
            List<Map<String, Integer>> allVehicles = unitVehicleService.getAllVehicles(mapId);
            return new GmsResponse().data(allVehicles).message("获取系统中所有车辆成功").success();
        } catch (Exception e) {
            String message = "获取系统中所有车辆失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}

