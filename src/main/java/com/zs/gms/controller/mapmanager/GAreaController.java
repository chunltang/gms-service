package com.zs.gms.controller.mapmanager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.HttpContextUtil;
import com.zs.gms.entity.mapmanager.Point;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.mapmanager.point.AnglePoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/maps")
@Validated
@Slf4j
@Api(tags = {"地图管理"},description = "Map Controller")
public class GAreaController {

    @Log("获取可通行区域")
    @GetMapping(value = "/{mapId}/areas/passableAreas/{areaId}")
    @ApiOperation(value = "获取可通行区域",httpMethod = "GET")
    public void getPassableArea(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getPassableArea",jsonStr,"获取可通行区域成功");
        }catch (Exception e){
            String message="获取可通行区域失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取不可通行区域")
    @GetMapping(value = "/{mapId}/areas/impassableAreas/{areaId}")
    @ApiOperation(value = "获取不可通行区域",httpMethod = "GET")
    public void getImpassableArea(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getImpassableArea",jsonStr,"获取不可通行区域成功");
        }catch (Exception e){
            String message="获取不可通行区域失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取地图单元")
    @GetMapping(value = "/{mapId}/areas/{areaId}")
    @ApiOperation(value = "获取地图单元",httpMethod = "GET")
    public void getArea(@PathVariable Integer mapId, @PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getArea",jsonStr,"获取地图单元成功");
        }catch (Exception e){
            String message="获取地图单元失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取保护区")
    @GetMapping(value = "/{mapId}/areas/saftyAreas/{saftyAreaId}")
    @ApiOperation(value = "获取保护区",httpMethod = "GET")
    public void getSafetyArea(@PathVariable Integer mapId, @PathVariable Integer saftyAreaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,saftyAreaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("saftyAreaId",saftyAreaId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getSafetyArea",jsonStr,"获取保护区成功");
        }catch (Exception e){
            String message="获取保护区失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取临时限速区")
    @GetMapping(value = "/{mapId}/areas/speedZones/{speedZoneId}")
    @ApiOperation(value = "获取临时限速区",httpMethod = "GET")
    public void getSpeedZones(@PathVariable Integer mapId,@PathVariable Integer speedZoneId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,speedZoneId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("speedZoneId",speedZoneId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getSpeedZones",jsonStr,"获取临时限速区成功");
        }catch (Exception e){
            String message="获取临时限速区失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取边界")
    @GetMapping(value = "/{mapId}/areas/{areaId}/border")
    @ApiOperation(value = "获取边界",httpMethod = "GET")
    public void getBorder(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","border");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getBorder",jsonStr,"获取边界成功");
        }catch (Exception e){
            String message="获取边界失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取区域名称")
    @GetMapping(value = "/{mapId}/areas/{areaId}/name")
    @ApiOperation(value = "获取区域名称",httpMethod = "GET")
    public void getAreaName(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","name");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getAreaName",jsonStr,"获取区域名称成功");
        }catch (Exception e){
            String message="获取区域名称失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取区域限速")
    @GetMapping(value = "/{mapId}/areas/{areaId}/speed")
    @ApiOperation(value = "获取区域限速",httpMethod = "GET")
    public void getAreaSpeed(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getAreaSpeed",jsonStr,"获取区域限速成功");
        }catch (Exception e){
            String message="获取区域限速失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取电铲")
    @GetMapping(value = "/{mapId}/areas/{areaId}/eshovel/{eShovelId}")
    @ApiOperation(value = "获取电铲",httpMethod = "GET")
    public void getEshovel(@PathVariable Integer mapId,@PathVariable Integer areaId,@PathVariable Integer eShovelId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("eShovelId",eShovelId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getEshovel",jsonStr,"获取电铲成功");
        }catch (Exception e){
            String message="获取电铲失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取排队点")
    @GetMapping(value = "/{mapId}/areas/{areaId}/queueSpot")
    @ApiOperation(value = "获取排队点",httpMethod = "GET")
    public void getQueueSpot(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","queueSpot");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getQueueSpot",jsonStr,"获取排队点成功");
        }catch (Exception e){
            String message="获取排队点失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取装载点")
    @GetMapping(value = "/{mapId}/areas/{areaId}/loadSpot")
    @ApiOperation(value = "获取装载点",httpMethod = "GET")
    public void getLoadSpot(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","loadSpot");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getLoadSpot",jsonStr,"获取装载点成功");
        }catch (Exception e){
            String message="获取装载点失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取卸矿点")
    @GetMapping(value = "/{mapId}/areas/{areaId}/unloadMineralSpot/{spotId}")
    @ApiOperation(value = "获取卸矿点",httpMethod = "GET")
    public void getUnloadMineralSpot(@PathVariable Integer mapId,@PathVariable Integer areaId,@PathVariable Integer spotId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","unloadMineralSpot");
            paramMap.put("spotId",spotId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getUnloadMineralSpot",jsonStr,"获取卸矿点成功");
        }catch (Exception e){
            String message="获取卸矿点失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("批量获取卸矿点")
    @GetMapping(value = "/{mapId}/areas/{areaId}/unloadMineralSpots")
    @ApiOperation(value = "批量获取卸矿点",httpMethod = "GET")
    public void getUnloadMineralSpots(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","unloadMineralSpot");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getUnloadMineralSpots",jsonStr,"批量获取卸矿点成功");
        }catch (Exception e){
            String message="批量获取卸矿点失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取排土块")
    @GetMapping(value = "/{mapId}/areas/{areaId}/unloadBlock")
    @ApiOperation(value = "获取排土块",httpMethod = "GET")
    public void getUnloadBlock(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","unloadBlock");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getUnloadBlock",jsonStr,"获取排土块成功");
        }catch (Exception e){
            String message="获取排土块失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取排土点")
    @GetMapping(value = "/{mapId}/areas/{areaId}/unloadSpots/{spotId}")
    @ApiOperation(value = "获取排土点",httpMethod = "GET")
    public void getUnloadSpots(@PathVariable Integer mapId,@PathVariable Integer areaId,Integer unloadBlockId,@PathVariable Integer spotId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId,unloadBlockId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("unloadBlockId",unloadBlockId);
            paramMap.put("attributeType","unloadWasteSpot");
            paramMap.put("spotId",spotId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getUnloadSpots",jsonStr,"获取排土点成功");
        }catch (Exception e){
            String message="获取排土点失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取安全线")
    @GetMapping(value = "/{mapId}/areas/{areaId}/securityLine")
    @ApiOperation(value = "获取安全线",httpMethod = "GET")
    public void getSecurityLine(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","securityLine");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getSecurityLine",jsonStr,"获取安全线成功");
        }catch (Exception e){
            String message="获取安全线失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取最大排土次数")
    @GetMapping(value = "/{mapId}/areas/{areaId}/unloadMax")
    @ApiOperation(value = "获取最大排土次数",httpMethod = "GET")
    public void getUnloadMax(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","unloadMax");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getUnloadMax",jsonStr,"获取最大排土次数成功");
        }catch (Exception e){
            String message="获取最大排土次数失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取停车位")
    @GetMapping(value = "/{mapId}/areas/{areaId}/parkingSpots")
    @ApiOperation(value = "获取停车位",httpMethod = "GET")
    public void getParkingSpots(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","parkingSpots");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getParkingSpots",jsonStr,"获取停车位成功");
        }catch (Exception e){
            String message="获取停车位失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取加油车")
    @GetMapping(value = "/{mapId}/areas/{areaId}/refueller")
    @ApiOperation(value = "获取加油车",httpMethod = "GET")
    public void getRefueller(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","refueller");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getRefueller",jsonStr,"获取加油车成功");
        }catch (Exception e){
            String message="获取加油车失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取加油点")
    @GetMapping(value = "/{mapId}/areas/{areaId}/refuelSpot/{spotId}")
    @ApiOperation(value = "获取加油点",httpMethod = "GET")
    public void getRefuelSpot(@PathVariable Integer mapId,@PathVariable Integer areaId,@PathVariable Integer spotId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","refueller");
            paramMap.put("spotId",spotId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getRefuelSpot",jsonStr,"获取加油点成功");
        }catch (Exception e){
            String message="获取加油点失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取加水车")
    @GetMapping(value = "/{mapId}/areas/{areaId}/refiller")
    @ApiOperation(value = "获取加水车",httpMethod = "GET")
    public void getRefiller(@PathVariable Integer mapId,@PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","refiller");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getRefiller",jsonStr,"获取加水车成功");
        }catch (Exception e){
            String message="获取加水车失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取加水点")
    @GetMapping(value = "/{mapId}/areas/{areaId}/refillSpot/{spotId}")
    @ApiOperation(value = "获取加水点",httpMethod = "GET")
    public void getRefillSpot(@PathVariable Integer mapId,@PathVariable Integer areaId,@PathVariable Integer spotId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","refillSpot");
            paramMap.put("spotId",spotId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getRefillSpot",jsonStr,"获取加水点成功");
        }catch (Exception e){
            String message="获取加水点失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取参考路径")
    @GetMapping(value = "/{mapId}/areas/referencePaths/{referencePathId}")
    @ApiOperation(value = "获取参考路径",httpMethod = "GET")
    public void getReferencePath(@PathVariable Integer mapId,@PathVariable Integer referencePathId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,referencePathId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("referencePathId",referencePathId);
            paramMap.put("attributeType","referencePath");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getReferencePath",jsonStr,"获取参考路径成功");
        }catch (Exception e){
            String message="获取参考路径失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取一个路径点")
    @GetMapping(value = "/{mapId}/areas/referencePaths/{referencePathId}/vertex")
    @ApiOperation(value = "获取一个路径点",httpMethod = "GET")
    public void getVertex(@PathVariable Integer mapId, @PathVariable Integer referencePathId, Point vertex) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,referencePathId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("referencePathId",referencePathId);
            paramMap.put("vertex",vertex);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getVertex",jsonStr,"获取一个路径点成功");
        }catch (Exception e){
            String message="获取一个路径点失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取道路连接")
    @GetMapping(value = "/{mapId}/areas/{areaId}/roadLink")
    @ApiOperation(value = "获取道路连接",httpMethod = "GET")
    public void getRoadLink(@PathVariable Integer mapId, @PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","link");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getRoadLink",jsonStr,"获取道路连接成功");
        }catch (Exception e){
            String message="获取道路连接失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取路口连接")
    @GetMapping(value = "/{mapId}/areas/{areaId}/connections")
    @ApiOperation(value = "获取路口连接",httpMethod = "GET")
    public void getJunctionConnection(@PathVariable Integer mapId, @PathVariable Integer areaId) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,areaId)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("areaId",areaId);
            paramMap.put("attributeType","connection");
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getJunctionConnection",jsonStr,"获取路口连接成功");
        }catch (Exception e){
            String message="获取路口连接失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取全局路径")
    @GetMapping(value = "/{mapId}/paths/globalPath")
    @ApiOperation(value = "获取全局路径",httpMethod = "GET")
    public void getGlobalPath(@PathVariable Integer mapId, AnglePoint start, AnglePoint end) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,start,end)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("start",start);
            paramMap.put("end",end);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getGlobalPath",jsonStr,"获取全局路径成功");
        }catch (Exception e){
            String message="获取全局路径失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取交互式全局路径规划")
    @GetMapping(value = "/{mapId}/paths/interactiveGlobalPath")
    @ApiOperation(value = "获取交互式全局路径规划",httpMethod = "GET")
    public void getInteractiveGlobalPath(@PathVariable Integer mapId, AnglePoint start, AnglePoint[] ends) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,start,ends)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("start",start);
            paramMap.put("ends",ends);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getInteractiveGlobalPath",jsonStr,"获取交互式全局路径规划成功");
        }catch (Exception e){
            String message="获取全获取交互式全局路径规划失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("局部路径规划")
    @GetMapping(value = "/{mapId}/paths/trajectory")
    @ApiOperation(value = "局部路径规划",httpMethod = "GET")
    public void getTrajectory(@PathVariable Integer mapId, AnglePoint start, AnglePoint end) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId,start,end)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            paramMap.put("start",start);
            paramMap.put("end",end);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getTrajectory",jsonStr,"局部路径规划成功");
        }catch (Exception e){
            String message="局部路径规划失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取静态层信息")
    @GetMapping(value = "/{mapId}/layerInfo/static")
    @ApiOperation(value = "获取静态层信息",httpMethod = "GET")
    public void getStaticLayerInfo(@PathVariable Integer mapId) throws GmsException {
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getStaticLayerInfo",jsonStr,"获取静态层信息成功");
        }catch (Exception e){
            String message="获取静态层信息失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取半静态分层信息")
    @GetMapping(value = "/{mapId}/layerInfo/semiStatic")
    @ApiOperation(value = "获取半静态分层信息",httpMethod = "GET")
    public void getSemiStaticLayerInfo(@PathVariable Integer mapId) throws GmsException {
        try {
            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getSemiStaticLayerInfo",jsonStr,"获取半静态分层信息成功");
            saveSemiStaticData(mapId);
        }catch (Exception e){
            String message="获取半静态分层信息失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("获取动态层信息")
    @GetMapping(value = "/{mapId}/layerInfo/dynamic")
    @ApiOperation(value = "获取动态层信息",httpMethod = "GET")
    public void getDynamicLayerInfo(@PathVariable Integer mapId) throws GmsException {
        try {

            Map<String,Object> paramMap=new HashMap<>();
            paramMap.put("mapId",mapId);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getDynamicLayerInfo",jsonStr,"获取动态层信息成功");
        }catch (Exception e){
            String message="获取动态层信息失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Async(value = "gmsAsyncThreadPool")
    public void saveSemiStaticData(Integer mapId){
        log.debug("redis存储地图半静态层数据,mapId={}",mapId);
        MessageEntry entry = MessageFactory.getMessageEntry(HttpContextUtil.getHttpServletResponse());
        if(entry!=null){
            String data = entry.getReturnData();
            JSONObject jsonObject = JSON.parseObject(data);
            if(null!=jsonObject){
                JSONArray areas = jsonObject.getJSONArray("areas");
                if(null!=areas){
                    ArrayList<SemiStatic> semiStatics = new ArrayList<>();
                    for (Object area : areas) {
                        SemiStatic semiStatic = GmsUtil.toObjIEnum(area, SemiStatic.class);
                        semiStatics.add(semiStatic);
                    }
                    if(GmsUtil.CollectionNotNull(semiStatics)){
                        RedisService.set(StaticConfig.KEEP_DB, RedisKeyPool.SEMI_STATIC_DATA+mapId,GmsUtil.toJson(semiStatics));
                    }
                }
            }
        }
    }
}

