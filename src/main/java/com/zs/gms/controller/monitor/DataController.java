package com.zs.gms.controller.monitor;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.RedisKey;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.monitor.VehicleLive;
import com.zs.gms.service.monitor.schdeule.LiveVapHandle;
import com.zs.gms.service.monitor.VehicleLiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/dispatchs")
@Api(tags = "运营监控",description = "Dispatch Controller")
public class DataController extends BaseController{

    @Autowired
    @Lazy
    private VehicleLiveService vehicleLiveService;

    @Log("车辆历史数据查询")
    @GetMapping(value = "/historyDatas")
    @ApiOperation(value = "车辆历史数据查询",httpMethod = "GET")
    public GmsResponse getVehicleHistoryList(@MultiRequestBody VehicleLive vehicleLive,@MultiRequestBody QueryRequest request) throws GmsException {
        try {
            Map<String, Object> dataTable = this.getDataTable(vehicleLiveService.getVehicleLiveListPage(vehicleLive, request));
            return new GmsResponse().data(dataTable).message("车辆历史数据查询成功").success();
        }catch (Exception e){
            String message="车辆历史数据查询失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取车辆数据")
    @GetMapping(value = "/liveInfo")
    @ApiOperation(value = "获取车辆数据",httpMethod = "GET")
    public Map<String,Object> getLiveInfo() throws GmsException {
        try {
            Map<String,Object> result=new HashMap<>();
            result.put("test","这是一条测试数据");
            result.put("num",123);
            result.put("str","2432134");
            return result;
        }catch (Exception e){
            String message="获取车辆数据";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取车辆全局路径")
    @GetMapping(value = "/globalPaths/{vehicleId}")
    @ApiOperation(value = "获取车辆全局路径",httpMethod = "GET")
    public GmsResponse getGlobalPath(@PathVariable Integer vehicleId) throws GmsException {
        try {
            Map<String, Object> path = LiveVapHandle.getGlobalPath(RedisKey.VAP_PATH_PREFIX + vehicleId);
            return new GmsResponse().data(path).message("获取车辆全局路径成功").success();
        }catch (Exception e){
            String message="获取车辆全局路径失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }
}
