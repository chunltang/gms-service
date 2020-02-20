package com.zs.gms.controller.remote;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.remote.RemoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RemoteController extends BaseController {

    @Autowired
    private RemoteService remoteService;

    @Log("申请进入控制台")
    @PutMapping(value = "/remoteModel/accesses/{vehicleId}")
    @ApiOperation(value = "申请进入控制台",httpMethod = "PUT")
    public GmsResponse remoteAccess(@PathVariable  Integer vehicleId) throws GmsException {
        if(null==vehicleId){
            throw new GmsException("车辆id为空");
        }
        try{
            User user = super.getCurrentUser();
            boolean access = remoteService.remoteAccess(vehicleId,user);
            if(access){
                return new GmsResponse().message("申请进入控制台成功").success();
            }
            return new GmsResponse().message("申请进入控制台失败").badRequest();
        }catch (Exception e){
            String message="申请进入控制台失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("切换到远程模式")
    @PutMapping(value = "/remoteModel/{vehicleId}")
    @ApiOperation(value = "切换到远程模式",httpMethod = "PUT")
    public GmsResponse switchToRemoteModel(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getDispatchMessage().sendMessageNoResNoID("VehModeRemote", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="切换到远程模式失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }


    @Log("远程模式:急停")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleStop")
    @ApiOperation(value = "远程模式:急停",httpMethod = "PUT")
    public GmsResponse remoteVehicleStop(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteEmergencyParking", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:急停失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:前进")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleForward")
    @ApiOperation(value = "远程模式:前进",httpMethod = "PUT")
    public GmsResponse remoteVehicleForward(@PathVariable  Integer vehicleId,@MultiRequestBody("value")double value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteForward", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:前进失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:后退")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleBackward")
    @ApiOperation(value = "远程模式:后退",httpMethod = "PUT")
    public GmsResponse remoteVehicleBackword(@PathVariable  Integer vehicleId,@MultiRequestBody("value") double value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteBackward", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:后退失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:左转")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleLeftTurn")
    @ApiOperation(value = "远程模式:左转",httpMethod = "PUT")
    public GmsResponse remoteVehicleLeftTurn(@PathVariable  Integer vehicleId,@MultiRequestBody("value")double value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteTurnDirection", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:左转失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:右转")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleRightTurn")
    @ApiOperation(value = "远程模式:右转",httpMethod = "PUT")
    public GmsResponse remoteVehicleRightTurn(@PathVariable  Integer vehicleId,@MultiRequestBody("value")double value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteTurnDirection", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:右转失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:纵向驱动")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleDrive")
    @ApiOperation(value = "远程模式:纵向驱动",httpMethod = "PUT")
    public GmsResponse remoteVehicleDrive(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteDrive", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:纵向驱动失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:纵向制动")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleBrake")
    @ApiOperation(value = "远程模式:纵向制动",httpMethod = "PUT")
    public GmsResponse remoteVehicleBrake(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteBrake", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:纵向制动失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:发动机停止")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleEngineStop")
    @ApiOperation(value = "远程模式:发动机停止",httpMethod = "PUT")
    public GmsResponse remoteVehicleEngineStop(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteStop", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:发动机停止失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:取消驻车制动")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleCloseBrake")
    @ApiOperation(value = "远程模式:取消驻车制动",httpMethod = "PUT")
    public GmsResponse remoteVehicleCloseBrake(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value","0");
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteParkingBrake", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:取消驻车制动失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:开启驻车制动")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleOpenBrake")
    @ApiOperation(value = "远程模式:开启驻车制动",httpMethod = "PUT")
    public GmsResponse remoteVehicleOpenBrake(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value","1");
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteParkingBrake", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:开启驻车制动失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:货舱控制:起升")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageUp")
    @ApiOperation(value = "远程模式:货舱控制:起升",httpMethod = "PUT")
    public GmsResponse remoteVehicleCarriageUp(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value","1");
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteUnload", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:货舱控制:起升失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:货舱控制:保持")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageKeep")
    @ApiOperation(value = "远程模式:货舱控制:保持",httpMethod = "PUT")
    public GmsResponse remoteVehicleCarriageKeep(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value","2");
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteUnload", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:货舱控制:保持失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:货舱控制:浮动")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageFloat")
    @ApiOperation(value = "远程模式:货舱控制:浮动",httpMethod = "PUT")
    public GmsResponse remoteVehicleCarriageFloat(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value","3");
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteUnload", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:货舱控制:浮动失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("远程模式:货舱控制:下降")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageDown")
    @ApiOperation(value = "远程模式:货舱控制:下降",httpMethod = "PUT")
    public GmsResponse remoteVehicleCarriageDown(@PathVariable  Integer vehicleId) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value","4");
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteUnload", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:货舱控制:下降失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "远程模式:货舱控制",append = "value")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageControl")
    @ApiOperation(value = "远程模式:货舱控制",httpMethod = "PUT")
    public GmsResponse remoteVehicleCarriageControl(@PathVariable  Integer vehicleId,
                                                    @MultiRequestBody("value") String value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteUnload", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:货舱控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "远程模式:转向灯控制",append = "value")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleTurnLightControl")
    @ApiOperation(value = "远程模式:转向灯控制",httpMethod = "PUT")
    public GmsResponse remoteVehicleTurnLightControl(@PathVariable  Integer vehicleId,
                                                     @MultiRequestBody("value") String value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteTurnLight", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:转向灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "远程模式:近光灯控制",append = "value")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleDippedLightControl")
    @ApiOperation(value = "远程模式:近光灯控制",httpMethod = "PUT")
    public GmsResponse remoteVehicleDippedLightControl(@PathVariable  Integer vehicleId,
                                                       @MultiRequestBody("value") String value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteDippedLight", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:近光灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "远程模式:示廓灯控制",append = "value")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleHighLightControl")
    @ApiOperation(value = "远程模式:示廓灯控制",httpMethod = "PUT")
    public GmsResponse remoteVehicleHighLightControl(@PathVariable  Integer vehicleId,
                                                     @MultiRequestBody("value") String value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteHighLight", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:示廓灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "远程模式:刹车灯控制",append = "value")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleBrakeLightControl")
    @ApiOperation(value = "远程模式:刹车灯控制",httpMethod = "PUT")
    public GmsResponse remoteVehicleBrakeLightControl(@PathVariable  Integer vehicleId,
                                                      @MultiRequestBody("value") String value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteBrakeLight", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:刹车灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "远程模式:紧急信号灯控制",append = "value")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleEmergencyLightControl")
    @ApiOperation(value = "远程模式:紧急信号灯控制",httpMethod = "PUT")
    public GmsResponse remoteVehicleEmergencyLightControl(@PathVariable  Integer vehicleId,
                                                          @MultiRequestBody("value") String value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteEmergencyLight", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:紧急信号灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "远程模式:喇叭控制",append = "value")
    @PutMapping(value = "/remoteModel/{vehicleId}/vehicleHornControl")
    @ApiOperation(value = "远程模式:喇叭控制",httpMethod = "PUT")
    public GmsResponse remoteVehicleHornControl(@PathVariable  Integer vehicleId,
                                                @MultiRequestBody("value") String value) throws GmsException {
        try{
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("vehicleId",vehicleId);
            paramMap.put("value",value);
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteHorn", JSONObject.toJSONString(paramMap));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="远程模式:喇叭控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
