package com.zs.gms.controller.mapmanager;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.utils.IOUtil;
import com.zs.gms.entity.mapmanager.MapInfo;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.AreaInfo;
import com.zs.gms.entity.mapmanager.area.*;
import com.zs.gms.entity.mapmanager.other.*;
import com.zs.gms.entity.mapmanager.spot.*;
import com.zs.gms.service.mapmanager.MapInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/maps")
@Validated
@Slf4j
@Api(tags = {"地图管理"}, description = "Map Controller")
public class SAreaController extends BaseController {

    @Autowired
    private MapInfoService mapInfoService;

    @Log("创建地图单元")//可以设置不可通行区域
    @PostMapping(value = "/{mapId}/areas")
    @ApiOperation(value = "创建地图单元", httpMethod = "POST")
    public void createArea(@MultiRequestBody AreaInfo areaInfo,
                           @PathVariable Long mapId) throws GmsException {
        try {
            areaInfo.setMapId(mapId);
            ObjectMapper mapper = new ObjectMapper();//需要转化枚举类
            String jsonStr = mapper.writeValueAsString(areaInfo);
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
            MessageFactory.getMapMessage().sendMessageWithID(entry.getMessageId(), "createArea", jsonStr, "创建地图单元成功");
        } catch (Exception e) {
            String message = "创建地图单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    /*该接口和createArea合并
    @Log("创建可通行区域")//无设置可通行区域
    @PostMapping(value = "/{mapId}/passableArea")
    @ApiOperation(value = "创建可通行区域", httpMethod = "POST")
    public void createPassableArea(@Valid @MultiRequestBody AreaInfo areaInfo, @PathVariable Long mapId) throws GmsException {
        try {
            areaInfo.setMapId(mapId);
            ObjectMapper mapper = new ObjectMapper();//需要转化枚举类
            String jsonStr = mapper.writeValueAsString(areaInfo);
            MessageFactory.getMapMessage().sendMessageNoID("createPassableArea", jsonStr, "创建可通行区域成功");
        } catch (Exception e) {
            String message = "创建可通行区域失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }*/

    @Log("设置不可通行区域")
    @PutMapping(value = "/{mapId}/impassableArea")
    @ApiOperation(value = "设置不可通行区域", httpMethod = "PUT")
    public void setImPassableArea(@Valid @MultiRequestBody ImpassableArea impassableArea, @PathVariable Long mapId) throws GmsException {
        try {
            impassableArea.setMapId(mapId);
            ObjectMapper mapper = new ObjectMapper();//需要转化枚举类
            String jsonStr = mapper.writeValueAsString(impassableArea);
            MessageFactory.getMapMessage().sendMessageNoID("setImpassableArea", jsonStr, "设置不可通行区域成功");
        } catch (Exception e) {
            String message = "设置不可通行区域失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置地图版本")
    @PutMapping(value = "/{mapId}/version")
    @ApiOperation(value = "设置地图版本", httpMethod = "PUT")
    public void setMapVersion(@PathVariable("mapId") Integer mapId,
                              @MultiRequestBody("majorVersion") Integer majorVersion,
                              @MultiRequestBody("minorVersion") Integer minorVersion) throws GmsException {
        if (!ObjectUtils.allNotNull(majorVersion, minorVersion)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("mapId", mapId);
            params.put("majorVersion", majorVersion);
            params.put("minorVersion", minorVersion);
            String jsonStr = JSONObject.toJSON(params).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setMapVersion", jsonStr, "设置地图版本成功");
        } catch (Exception e) {
            String message = "设置地图版本失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }


    @Log("设置装载区")
    @PutMapping(value = "/{mapId}/areas/loadArea")
    @ApiOperation(value = "设置装载区", httpMethod = "PUT")
    public void setLoadArea(@Valid @MultiRequestBody LoadArea loadArea, @PathVariable Long mapId) throws GmsException {
        try {
            loadArea.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(loadArea).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setLoadArea", jsonStr, "设置装载区成功");
        } catch (Exception e) {
            String message = "设置装载区失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置排土场")
    @PutMapping(value = "/{mapId}/areas/unLoadWasteArea")
    @ApiOperation(value = "设置排土场", httpMethod = "PUT")
    public void setUnLoadWasteArea(@Valid @MultiRequestBody UnLoadWasteArea unLoadWasteArea, @PathVariable Long mapId) throws GmsException {
        try {
            unLoadWasteArea.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(unLoadWasteArea).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setUnloadWasteArea", jsonStr, "设置排土场成功");
        } catch (Exception e) {
            String message = "设置排土场失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置卸矿区")
    @PutMapping(value = "/{mapId}/areas/unLoadMineralArea")
    @ApiOperation(value = "设置卸矿区", httpMethod = "PUT")
    public void setUnLoadMineralArea(@Valid @MultiRequestBody UnLoadMineralArea unLoadMineralArea, @PathVariable Long mapId) throws GmsException {
        try {
            unLoadMineralArea.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(unLoadMineralArea).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setUnloadMineralArea", jsonStr, "设置卸矿区成功");
        } catch (Exception e) {
            String message = "设置卸矿区失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置停车场")
    @PutMapping(value = "/{mapId}/areas/parkingLot")
    @ApiOperation(value = "设置停车场", httpMethod = "PUT")
    public void setParkingLot(@Valid @MultiRequestBody ParkingLot parkingLot, @PathVariable Long mapId) throws GmsException {
        try {
            parkingLot.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(parkingLot).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setParkingLot", jsonStr, "设置停车场成功");
        } catch (Exception e) {
            String message = "设置停车场失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置加油站")
    @PutMapping(value = "/{mapId}/areas/petrolStation")
    @ApiOperation(value = "设置加油站", httpMethod = "PUT")
    public void setPetrolStation(@Valid @MultiRequestBody PetrolStation petrolStation, @PathVariable Long mapId) throws GmsException {
        try {
            petrolStation.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(petrolStation).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setPetrolStation", jsonStr, "设置加油站成功");
        } catch (Exception e) {
            String message = "设置加油站失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置加水站")
    @PutMapping(value = "/{mapId}/areas/refillStation")
    @ApiOperation(value = "设置加水站", httpMethod = "PUT")
    public void setRefillStation(@Valid @MultiRequestBody RefillStation refillStation, @PathVariable Long mapId) throws GmsException {
        try {
            refillStation.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(refillStation).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setRefillStation", jsonStr, "设置加水站成功");
        } catch (Exception e) {
            String message = "设置加水站失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置道路")
    @PutMapping(value = "/{mapId}/areas/road")
    @ApiOperation(value = "设置道路", httpMethod = "PUT")
    public void setRoad(@Valid @MultiRequestBody Road road, @PathVariable Long mapId) throws GmsException {
        try {
            road.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(road).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setRoad", jsonStr, "设置道路成功");
        } catch (Exception e) {
            String message = "设置道路失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置路口")
    @PutMapping(value = "/{mapId}/areas/junction")
    @ApiOperation(value = "设置路口", httpMethod = "PUT")
    public void setJunction(@Valid @MultiRequestBody Junction junction, @PathVariable Long mapId) throws GmsException {
        try {
            junction.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(junction).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setJunction", jsonStr, "设置路口成功");
        } catch (Exception e) {
            String message = "设置路口失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置保护区")
    @PutMapping(value = "/{mapId}/areas/safetyArea")
    @ApiOperation(value = "设置保护区", httpMethod = "PUT")
    public void createSafetyArea(@Valid @MultiRequestBody SafetyArea safetyArea, @PathVariable Long mapId) throws GmsException {
        try {
            safetyArea.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(safetyArea).toString();
            MessageFactory.getMapMessage().sendMessageNoID("createSafetyArea", jsonStr, "设置保护区成功");
        } catch (Exception e) {
            String message = "设置保护区失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置临时限速区")
    @PutMapping(value = "/{mapId}/areas/speedZone")
    @ApiOperation(value = "设置临时限速区", httpMethod = "PUT")
    public void createSpeedZone(@Valid @MultiRequestBody SpeedZone speedZone, @PathVariable Long mapId) throws GmsException {
        try {
            speedZone.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(speedZone).toString();
            MessageFactory.getMapMessage().sendMessageNoID("createSpeedZone", jsonStr, "设置临时限速区成功");
        } catch (Exception e) {
            String message = "设置临时限速区失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置名称")
    @PutMapping(value = "/{mapId}/areas/name")
    @ApiOperation(value = "设置名称", httpMethod = "PUT")
    public void setName(@PathVariable Long mapId,
                        @MultiRequestBody("loadAreaId") Long areaId,
                        @MultiRequestBody("name") String name) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId, areaId, name)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("mapId", mapId);
            paramMap.put("areaId", areaId);
            paramMap.put("name", name);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setName", jsonStr, "设置名称成功");
        } catch (Exception e) {
            String message = "设置名称失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置区域速度")
    @PutMapping(value = "/{mapId}/areas/speed")
    @ApiOperation(value = "设置区域速度", httpMethod = "PUT")
    public void setAreaSpeed(@PathVariable Long mapId,
                             @MultiRequestBody("loadAreaId") Long areaId,
                             @MultiRequestBody("speed") Float speed) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId, areaId, speed)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("mapId", mapId);
            paramMap.put("areaId", areaId);
            paramMap.put("speed", speed);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setAreaSpeed", jsonStr, "设置区域速度成功");
        } catch (Exception e) {
            String message = "设置区域速度失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置电铲")
    @PutMapping(value = "/{mapId}/areas/eshovel")
    @ApiOperation(value = "设置电铲", httpMethod = "PUT")
    public void setEshovel(@Valid @MultiRequestBody Eshovel eshovel,
                           @PathVariable Long mapId) throws GmsException {
        try {
            eshovel.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(eshovel).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setEshovel", jsonStr, "设置电铲成功");
        } catch (Exception e) {
            String message = "设置电铲失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置排队点")
    @PutMapping(value = "/{mapId}/areas/queueSpot")
    @ApiOperation(value = "设置排队点", httpMethod = "PUT")
    public void setQueueSpot(@Valid @MultiRequestBody QueueSpot queueSpot,
                             @PathVariable Long mapId) throws GmsException {
        try {
            queueSpot.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(queueSpot).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setQueueSpot", jsonStr, "设置排队点成功");
        } catch (Exception e) {
            String message = "设置排队点失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置装载点")
    @PutMapping(value = "/{mapId}/areas/loadSpot")
    @ApiOperation(value = "设置装载点", httpMethod = "PUT")
    public void setLoadSpot(@Valid @MultiRequestBody LoadSpot loadSpot,
                            @PathVariable Long mapId) throws GmsException {
        try {
            loadSpot.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(loadSpot).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setLoadSpot", jsonStr, "设置装载点成功");
        } catch (Exception e) {
            String message = "设置装载点失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置卸矿点")
    @PutMapping(value = "/{mapId}/areas/unloadMineralSpot")
    @ApiOperation(value = "设置卸矿点", httpMethod = "PUT")
    public void setUnloadMineralSpot(@Valid @MultiRequestBody UnloadMineralSpot unloadMineralSpot,
                                     @PathVariable Long mapId) throws GmsException {
        try {
            unloadMineralSpot.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(unloadMineralSpot).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setUnloadMineralSpot", jsonStr, "设置卸矿点成功");
        } catch (Exception e) {
            String message = "设置卸矿点失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置排土块")
    @PutMapping(value = "/{mapId}/areas/unloadBlock")
    @ApiOperation(value = "设置排土块", httpMethod = "PUT")
    public void setUnloadBlock(@Valid @MultiRequestBody UnloadBlock unloadBlock,
                               @PathVariable Long mapId) throws GmsException {
        try {
            unloadBlock.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(unloadBlock).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setUnloadBlock", jsonStr, "设置排土块成功");
        } catch (Exception e) {
            String message = "设置排土块失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置排土点")
    @PutMapping(value = "/{mapId}/areas/unloadSpot")
    @ApiOperation(value = "设置排土点", httpMethod = "PUT")
    public void setUnloadSpot(@Valid @MultiRequestBody UnloadSpot unloadSpot,
                              @PathVariable Long mapId) throws GmsException {
        try {
            unloadSpot.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(unloadSpot).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setUnloadSpot", jsonStr, "设置排土点成功");
        } catch (Exception e) {
            String message = "设置排土点失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置安全线")
    @PutMapping(value = "/{mapId}/areas/securityLine")
    @ApiOperation(value = "设置安全线", httpMethod = "PUT")
    public void setSecurityLine(@Valid @MultiRequestBody SecurityLine securityLine,
                                @PathVariable Long mapId) throws GmsException {
        try {
            securityLine.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(securityLine).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setSecurityLine", jsonStr, "设置安全线成功");
        } catch (Exception e) {
            String message = "设置安全线失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置排土最大次数")
    @PutMapping(value = "/{mapId}/areas/unloadMax")
    @ApiOperation(value = "设置排土最大次数", httpMethod = "PUT")
    public void setUnloadMax(@PathVariable Long mapId, @MultiRequestBody("loadAreaId") Long areaId,
                             @MultiRequestBody("attributeValue") Integer attributeValue) throws GmsException {
        if (!ObjectUtils.allNotNull(mapId, areaId, attributeValue)) {
            throw new GmsException("参数异常");
        }
        try {
            Map<String, Object> paramMap = new HashMap();
            paramMap.put("mapId", mapId);
            paramMap.put("areaId", areaId);
            paramMap.put("attributeType", "unloadMax");
            paramMap.put("attributeValue", attributeValue);
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setUnloadMax", jsonStr, "设置排土最大次数成功");
        } catch (Exception e) {
            String message = "设置排土最大次数失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置停车位")
    @PutMapping(value = "/{mapId}/areas/parkingSpot")
    @ApiOperation(value = "设置停车位", httpMethod = "PUT")
    public void setParkingSpot(@Valid @MultiRequestBody ParkingSpot parkingSpot,
                               @PathVariable Long mapId) throws GmsException {
        try {
            parkingSpot.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(parkingSpot).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setParkingSpot", jsonStr, "设置停车位成功");
        } catch (Exception e) {
            String message = "设置停车位失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置加油车")
    @PutMapping(value = "/{mapId}/areas/refueller")
    @ApiOperation(value = "设置加油车", httpMethod = "PUT")
    public void setRefueller(@Valid @MultiRequestBody Refueller refueller,
                             @PathVariable Long mapId) throws GmsException {
        try {
            refueller.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(refueller).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setRefueller", jsonStr, "设置加油车成功");
        } catch (Exception e) {
            String message = "设置加油车失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置加油点")
    @PutMapping(value = "/{mapId}/areas/refuelSpot")
    @ApiOperation(value = "设置加油点", httpMethod = "PUT")
    public void setRefuelSpot(@Valid @MultiRequestBody RefuelSpot refuelSpot,
                              @PathVariable Long mapId) throws GmsException {
        try {
            refuelSpot.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(refuelSpot).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setRefuelSpot", jsonStr, "设置加油点成功");
        } catch (Exception e) {
            String message = "设置加油点失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置加水车")
    @PutMapping(value = "/{mapId}/areas/refiller")
    @ApiOperation(value = "设置加水车", httpMethod = "PUT")
    public void setRefiller(@Valid @MultiRequestBody Refiller refiller,
                            @PathVariable Long mapId) throws GmsException {
        try {
            refiller.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(refiller).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setRefiller", jsonStr, "设置加水车成功");
        } catch (Exception e) {
            String message = "设置加水车失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置加水点")
    @PutMapping(value = "/{mapId}/areas/refillSpot")
    @ApiOperation(value = "设置加水点", httpMethod = "PUT")
    public void setRefillSpot(@Valid @MultiRequestBody RefillSpot refillSpot,
                              @PathVariable Long mapId) throws GmsException {
        try {
            refillSpot.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(refillSpot).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setRefillSpot", jsonStr, "设置加水点成功");
        } catch (Exception e) {
            String message = "设置加水点失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置参考路径")
    @PutMapping(value = "/{mapId}/areas/referencePath")
    @ApiOperation(value = "设置参考路径", httpMethod = "PUT")
    public void setReferencePath(@Valid @MultiRequestBody ReferencePath referencePath,
                                 @PathVariable Long mapId) throws GmsException {
        try {
            referencePath.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(referencePath).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setReferencePath", jsonStr, "设置参考路径成功");
        } catch (Exception e) {
            String message = "设置参考路径失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置动作组")
    @PutMapping(value = "/{mapId}/areas/actions")
    @ApiOperation(value = "设置动作组", httpMethod = "PUT")
    public void setActions(@Valid @MultiRequestBody Actions actions,
                           @PathVariable Long mapId) throws GmsException {
        try {
            actions.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(actions).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setActions", jsonStr, "设置动作组成功");
        } catch (Exception e) {
            String message = "设置动作组失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置道路连接")
    @PutMapping(value = "/{mapId}/areas/roadLink")
    @ApiOperation(value = "设置道路连接", httpMethod = "PUT")
    public void setRoadLink(@Valid @MultiRequestBody RoadLink roadLink,
                            @PathVariable Long mapId) throws GmsException {
        try {
            roadLink.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(roadLink).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setRoadLink", jsonStr, "设置道路连接成功");
        } catch (Exception e) {
            String message = "设置道路连接失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("设置路口连接")
    @PutMapping(value = "/{mapId}/areas/junctionConnection")
    @ApiOperation(value = "设置路口连接", httpMethod = "PUT")
    public void setJunctionConnection(@PathVariable Long mapId,
                                      @Valid @MultiRequestBody JunctionConnection junctionConnection) throws GmsException {
        try {
            junctionConnection.setMapId(mapId);
            String jsonStr = JSONObject.toJSON(junctionConnection).toString();
            MessageFactory.getMapMessage().sendMessageNoID("setJunctionConnection", jsonStr, "设置路口连接成功");
        } catch (Exception e) {
            String message = "设置路口连接失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("生成参考路径")
    @PutMapping(value = "/{mapId}/areas/createReferencePath")
    @ApiOperation(value = "设置路口连接", httpMethod = "PUT")
    public void generateReferencePath(@PathVariable Long mapId,
                                      @MultiRequestBody("roadId") Integer roadId) throws GmsException {
        if(null== roadId){
            throw new GmsException("参数异常");
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("mapId",mapId);
            params.put("roadId",roadId);
            MessageFactory.getMapMessage().sendMessageNoID("generateRoadReferencePath", GmsUtil.toJson(params), "生成参考路径成功");
        } catch (Exception e) {
            String message = "生成参考路径失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("地图开始编辑")
    @PutMapping(value = "/startEdit")
    @ApiOperation(value = "地图开始编辑", httpMethod = "PUT")
    public GmsResponse mapStartEdit(@MultiRequestBody("mapId") Integer mapId) throws GmsException {
        try {
            boolean existMapId = mapInfoService.existMapId(mapId);
            if(!existMapId){
                return new GmsResponse().message("地图id不存在").badRequest();
            }
            return new GmsResponse().message("地图开始编辑设置成功").success();
        } catch (Exception e) {
            String message = "地图开始编辑设置失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("地图结束编辑")
    @PutMapping(value = "/endEdit")
    @ApiOperation(value = "地图结束编辑", httpMethod = "PUT")
    public GmsResponse mapEndEdit(@MultiRequestBody("mapId") Integer mapId,
                                  @MultiRequestBody(value = "imageStr",required = false,parseAllFields = false) String imageStr) throws GmsException {
        try {
            boolean existMapId = mapInfoService.existMapId(mapId);
            if(!existMapId){
                return new GmsResponse().message("地图id不存在").badRequest();
            }
            MapDataUtil.releaseLock(mapId,super.getCurrentUser().getUserId().toString());
            generateImage(mapId,imageStr);
            return new GmsResponse().message("地图保存成功").success();
        } catch (Exception e) {
            String message = "地图保存失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    private void generateImage(Integer mapId,String imageStr){
        if(GmsUtil.StringNotNull(imageStr)){
            String str = imageStr.substring(imageStr.indexOf(",")+1);
            String relativePath=GmsConstant.STATIC_IMAGES+"map_"+mapId+(GmsUtil.getCurTime())+".png";
            String filePath=GmsUtil.getAppPath()+relativePath;
            boolean result = GmsUtil.generateImage(str, filePath);
            if(result){
                MapInfo mapInfo = mapInfoService.getMapInfo(mapId);
                String imagePath = mapInfo.getImagePath();
                File file = new File(GmsUtil.getAppPath() + imagePath);
                if(file.exists()){
                    file.delete();
                }
                mapInfoService.setImagePath(mapId,relativePath);
            }
        }
    }
}
