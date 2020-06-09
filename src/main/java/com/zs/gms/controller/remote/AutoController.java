package com.zs.gms.controller.remote;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/models")
@Api(tags ="车辆控制",description = "Remote Controller")
@Slf4j
public class AutoController {

    @Log("自动模式:停机")
    @PutMapping(value = "/autoModel/{vehicleId}/vehicleStop")
    @ApiOperation(value = "自动模式:停机",httpMethod = "PUT")
    public void autoVehicleStop(@PathVariable Long vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getDispatchMessage().sendMessageNoID("VehAutoStop", JSONObject.toJSONString(paramMap),"自动模式:停机成功");
        }catch (Exception e){
            String message="自动模式:停机失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("自动模式:启动")
    @PutMapping(value = "/autoModel/{vehicleId}/vehicleStart")
    @ApiOperation(value = "自动模式:启动",httpMethod = "PUT")
    public void autoVehicleStart(@PathVariable Long vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getDispatchMessage().sendMessageNoID("VehAutoStart", JSONObject.toJSONString(paramMap),"自动模式:启动成功");
        }catch (Exception e){
            String message="自动模式:启动失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("自动模式:待机")
    @PutMapping(value = "/autoModel/{vehicleId}/vehicleStandby")
    @ApiOperation(value = "自动模式:待机",httpMethod = "PUT")
    public void autoVehicleStandby(@PathVariable Long vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getDispatchMessage().sendMessageNoID("VehAutoStandby", JSONObject.toJSONString(paramMap),"自动模式:待机成功");
        }catch (Exception e){
            String message="自动模式:待机失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "自动模式:驻车制动",append = "value")
    @PutMapping(value = "/autoModel/{vehicleId}/vehicleBrake")
    @ApiOperation(value = "自动模式:驻车制动",httpMethod = "PUT")
    public void autoVehicleBrake(@PathVariable Long vehicleId,@MultiRequestBody("value") String value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getDispatchMessage().sendMessageNoID("VehAutoParkingBrake", JSONObject.toJSONString(paramMap),"自动模式:驻车制动成功");
        }catch (Exception e){
            String message="自动模式:驻车制动失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "自动模式:直线急停")
    @PutMapping(value = "/autoModel/{vehicleId}/vehicleEmergencyStop")
    @ApiOperation(value = "自动模式:直线急停",httpMethod = "PUT")
    public void autoVehicleEmergencyStop(@PathVariable Long vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getDispatchMessage().sendMessageNoID("VehAutoEmergencyParking", JSONObject.toJSONString(paramMap),"自动模式:直线急停成功");
        }catch (Exception e){
            String message="自动模式:直线急停失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "自动模式:原路径急停")
    @PutMapping(value = "/autoModel/{vehicleId}/vehicleEmergencyStopByPath")
    @ApiOperation(value = "自动模式:原路径急停",httpMethod = "PUT")
    public void autoVehicleEmergencyStopByPath(@PathVariable Long vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getDispatchMessage().sendMessageNoID("VehAutoEmergencyParkingByPath", JSONObject.toJSONString(paramMap),"自动模式:原路径急停成功");
        }catch (Exception e){
            String message="自动模式:原路径急停失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "自动模式:原路径安全停车")
    @PutMapping(value = "/autoModel/{vehicleId}/vehicleSafeStopByPath")
    @ApiOperation(value = "自动模式:原路径安全停车",httpMethod = "PUT")
    public void autoVehicleSafeStopByPath(@PathVariable Long vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getDispatchMessage().sendMessageNoID("VehAutoSafeParking", JSONObject.toJSONString(paramMap),"自动模式:原路径安全停车成功");
        }catch (Exception e){
            String message="自动模式:原路径安全停车失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "自动模式:强制启动")
    @PutMapping(value = "/autoModel/{vehicleId}/vehicleForcedStart")
    @ApiOperation(value = "自动模式:强制启动",httpMethod = "PUT")
    public void autoVehicleForcedStart(@PathVariable Long vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getDispatchMessage().sendMessageNoID("VehAutoForcedStart", JSONObject.toJSONString(paramMap),"自动模式:强制启动成功");
        }catch (Exception e){
            String message="自动模式:强制启动失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
