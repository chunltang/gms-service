package com.zs.gms.controller.remote;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.RemoteParam;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.remote.RemoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/models")
@Api(tags ="车辆控制",description = "Remote Controller")
@Slf4j
public class RemoteController extends BaseController {

    @Autowired
    private RemoteService remoteService;

    /*@Log("申请进入控制台")
    @PostMapping(value = "/remoteModel/accesses/{vehicleId}")
    @ApiOperation(value = "申请进入控制台",httpMethod = "POST")
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
    }*/

    @Log("切换到远程模式")
    @PostMapping(value = "/remoteModel/vehModeRemote")
    @ApiOperation(value = "切换到远程模式",httpMethod = "POST")
    public GmsResponse vehModeRemote(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehModeRemote", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="切换到远程模式失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("切换到自动模式")
    @PostMapping(value = "/remoteModel/vehModeAuto")
    @ApiOperation(value = "切换到自动模式",httpMethod = "POST")
    public GmsResponse vehModeAuto(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehModeAuto", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="切换到自动模式失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }


    @Log("紧急停车")
    @PostMapping(value = "/remoteModel/vehRemoteEmergencyParking")
    @ApiOperation(value = "紧急停车",httpMethod = "POST")
    public GmsResponse vehRemoteEmergencyParking(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteEmergencyParking", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="紧急停车失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("车辆行驶")
    @PostMapping(value = "/remoteModel/vehRemoteForward")
    @ApiOperation(value = "车辆行驶",httpMethod = "POST")
    public GmsResponse vehRemoteForward(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteForward", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="车辆行驶失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    /*@Log("远程模式:后退")
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleBackward")
    @ApiOperation(value = "远程模式:后退",httpMethod = "POST")
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
    }*/

    @Log("车辆转向")
    @PostMapping(value = "/remoteModel/vehRemoteTurnDirection")
    @ApiOperation(value = "车辆转向",httpMethod = "POST")
    public GmsResponse vehRemoteTurnDirection(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteTurnDirection", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="车辆转向失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    /*@Log("远程模式:右转")
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleRightTurn")
    @ApiOperation(value = "远程模式:右转",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleDrive")
    @ApiOperation(value = "远程模式:纵向驱动",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleBrake")
    @ApiOperation(value = "远程模式:纵向制动",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleEngineStop")
    @ApiOperation(value = "远程模式:发动机停止",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleCloseBrake")
    @ApiOperation(value = "远程模式:取消驻车制动",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleOpenBrake")
    @ApiOperation(value = "远程模式:开启驻车制动",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageUp")
    @ApiOperation(value = "远程模式:货舱控制:起升",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageKeep")
    @ApiOperation(value = "远程模式:货舱控制:保持",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageFloat")
    @ApiOperation(value = "远程模式:货舱控制:浮动",httpMethod = "POST")
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
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageDown")
    @ApiOperation(value = "远程模式:货舱控制:下降",httpMethod = "POST")
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

    @Log(value = "远程模式:货舱控制")
    @PostMapping(value = "/remoteModel/{vehicleId}/vehicleCarriageControl")
    @ApiOperation(value = "远程模式:货舱控制",httpMethod = "POST")
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
    }*/

    @Log(value = "左转向灯")
    @PostMapping(value = "/remoteModel/vehRemoteTurnLeftLight")
    @ApiOperation(value = "左转向灯控制",httpMethod = "POST")
    public GmsResponse vehRemoteTurnLeftLight(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteTurnLeftLight", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="左转向灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "右转向灯控制")
    @PostMapping(value = "/remoteModel/vehRemoteTurnRightLight")
    @ApiOperation(value = "右转向灯控制",httpMethod = "POST")
    public GmsResponse vehRemoteTurnRightLight(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteTurnRightLight",GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="右转向灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "近光灯控制")
    @PostMapping(value = "/remoteModel/vehRemoteNearight")
    @ApiOperation(value = "近光灯控制",httpMethod = "POST")
    public GmsResponse vehRemoteNearight(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteNearight", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="近光灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "示廓灯控制")
    @PostMapping(value = "/remoteModel/vehRemoteContourLight")
    @ApiOperation(value = "示廓灯控制",httpMethod = "POST")
    public GmsResponse vehRemoteContourLight(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteContourLight", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="示廓灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "刹车灯控制")
    @PostMapping(value = "/remoteModel/vehRemoteBrakeLight")
    @ApiOperation(value = "刹车灯控制",httpMethod = "POST")
    public GmsResponse vehRemoteBrakeLight(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteBrakeLight", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="刹车灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "紧急信号灯控制")
    @PostMapping(value = "/remoteModel/vehRemoteEmergencyLight")
    @ApiOperation(value = "紧急信号灯控制",httpMethod = "POST")
    public GmsResponse vehRemoteEmergencyLight(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteEmergencyLight", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="紧急信号灯控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "鸣笛控制")
    @PostMapping(value = "/remoteModel/vehRemoteHorn")
    @ApiOperation(value = "鸣笛控制",httpMethod = "POST")
    public GmsResponse vehRemoteHorn(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteHorn", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="鸣笛控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "行车制动控制")
    @PostMapping(value = "/remoteModel/vehRemoteBrakeForword")
    @ApiOperation(value = "行车制动控制",httpMethod = "POST")
    public GmsResponse vehRemoteBrakeForword(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteBrakeForword", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="行车制动控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "电缓制动控制")
    @PostMapping(value = "/remoteModel/vehRemoteBrakeElectric")
    @ApiOperation(value = "电缓制动控制",httpMethod = "POST")
    public GmsResponse vehRemoteBrakeElectric(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteBrakeElectric", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="电缓制动控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "停车制动控制")
    @PostMapping(value = "/remoteModel/vehRemoteBrakeParking")
    @ApiOperation(value = "停车制动控制",httpMethod = "POST")
    public GmsResponse vehRemoteBrakeParking(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteBrakeParking", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="停车制动控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "装载制动控制")
    @PostMapping(value = "/remoteModel/vehRemoteBrakeLoad")
    @ApiOperation(value = "装载制动控制",httpMethod = "POST")
    public GmsResponse vehRemoteBrakeLoad(@MultiRequestBody RemoteParam remoteParam) throws GmsException {
        try{
            MessageFactory.getVapMessage().sendMessageNoResNoID("VehRemoteBrakeLoad", GmsUtil.toJson(remoteParam));
            return new GmsResponse().success();
        }catch (Exception e){
            String message="装载制动控制失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
