package com.zs.gms.controller.mapmanager;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.MapConfig;
import com.zs.gms.entity.init.GmsGlobalConfig;
import com.zs.gms.entity.mapmanager.MapInfo;
import com.zs.gms.service.common.GmsConfigService;
import com.zs.gms.service.mapmanager.MapInfoService;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.system.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@Api(tags = {"地图管理"}, description = "Map Controller")
@RequestMapping("/maps")
@Validated
public class MapController extends BaseController {

    @Autowired
    @Lazy
    private MapInfoService mapInfoService;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private GmsConfigService gmsConfigService;

    @Log("开始地图采集")
    @PutMapping("/startCollection")
    @ApiOperation(value = "开始地图采集", httpMethod = "PUT")
    public void startCollection(Integer vehicleId) throws GmsException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("vehicleId", vehicleId);
            MessageFactory.getMapMessage().sendMessageNoID("startCollection", GmsUtil.toJson(params), "开始地图采集提交成功");
        } catch (Exception e) {
            String message = "开始地图采集提交失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("结束地图采集")
    @PutMapping("/endCollection")
    @ApiOperation(value = "结束地图采集", httpMethod = "PUT")
    public void endCollection(Integer vehicleId) throws GmsException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("vehicleId", vehicleId);
            MessageFactory.getMapMessage().sendMessageNoID("endCollection", GmsUtil.toJson(params), "结束地图采集提交成功");
        } catch (Exception e) {
            String message = "结束地图采集提交失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    /*@Log("创建地图")
    @PostMapping
    @ApiOperation(value = "创建地图", httpMethod = "POST")
    public void createMap(@MultiRequestBody("mapName") String mapName,HttpServletResponse response) throws GmsException {
        try {
            GmsGlobalConfig gmsConfig = gmsConfigService.getGmsConfig(GmsConstant.MAP_GLOBAL_CONFIG);
            if(null==gmsConfig){
                GmsService.callResponse(new GmsResponse().badRequest().message("请先添加全局属性!"),response);
                return;
            }
            MapConfig mapConfig = GmsUtil.toObj(gmsConfig.getConfigValue(), MapConfig.class);
            MapInfo mapInfo = new MapInfo();
            mapInfo.setName(mapName);
            mapInfo.setCoordinateOrigin(mapConfig.getCoordinateOrigin());
            mapInfo.setLeftDring(mapConfig.isLeftDring());
            mapInfo.setSpeed(mapConfig.getSpeed());
            String jsonStr = JSONObject.toJSON(mapInfo).toString();
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
            User user = super.getCurrentUser();
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                    if (entry.getReturnData() != null) {
                        String str = entry.getReturnData();
                        JSONObject json = JSONObject.parseObject(str);
                        log.debug("创建地图返回数据:{}", str);
                        mapInfo.setMapId(json.getInteger("mapId"));
                        mapInfo.setUserId(user.getUserId());
                        mapInfo.setUserName(user.getName());
                        mapInfoService.addMapInfo(mapInfo);
                    }
                }
            });
            MessageFactory.getMapMessage().sendMessageWithID(entry.getMessageId(), "createMap", jsonStr, "创建地图成功");
        } catch (Exception e) {
            String message = "创建地图失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }*/

    @Log("创建地图")
    @PostMapping
    @ApiOperation(value = "创建地图", httpMethod = "POST")
    public void createMap(@MultiRequestBody("isBigVersion") Boolean isBigVersion,HttpServletResponse response) throws GmsException {
        try {
            GmsGlobalConfig gmsConfig = gmsConfigService.getGmsConfig(GmsConstant.MAP_GLOBAL_CONFIG);
            if(null==gmsConfig){
                GmsService.callResponse(new GmsResponse().badRequest().message("请先添加全局属性!"),response);
                return;
            }
            MapInfo.MapVersion version = mapInfoService.getVersion();
            if(null!=isBigVersion && isBigVersion){
                version.setBigVersion(version.getBigVersion()+1);
            }else{
                version.setSmallVersion(version.getSmallVersion()+1);
            }
            MapConfig mapConfig = GmsUtil.toObj(gmsConfig.getConfigValue(), MapConfig.class);
            MapInfo mapInfo = new MapInfo();
            mapInfo.setName(mapConfig.getMapName());
            mapInfo.setVersion(version);
            mapInfo.setCoordinateOrigin(mapConfig.getCoordinateOrigin());
            mapInfo.setLeftDring(mapConfig.isLeftDring());
            mapInfo.setSpeed(mapConfig.getSpeed());
            String jsonStr = GmsUtil.toJson(mapInfo);
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
            User user = super.getCurrentUser();
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                    if (entry.getReturnData() != null) {
                        String str = entry.getReturnData();
                        JSONObject json = JSONObject.parseObject(str);
                        log.debug("创建地图返回数据:{}", str);
                        mapInfo.setMapId(json.getInteger("mapId"));
                        mapInfo.setUserId(user.getUserId());
                        mapInfo.setUserName(user.getName());
                        mapInfoService.addMapInfo(mapInfo);
                        mapInfoService.setVersion(version);
                    }
                }
            });
            MessageFactory.getMapMessage().sendMessageWithID(entry.getMessageId(), "createMap", jsonStr, "创建地图成功");
        } catch (Exception e) {
            String message = "创建地图失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    /*@Log("设置地图信息")
    @PutMapping(value = "/{mapId}")
    @ApiOperation(value = "设置地图信息", httpMethod = "PUT")
    public void setMap(@MultiRequestBody("mapName") String mapName, @PathVariable Integer mapId,HttpServletResponse response) throws GmsException {
        try {
            MapInfo mapInfo = mapInfoService.getMapInfo(mapId);
            if(null==mapInfo){
                GmsService.callResponse(new GmsResponse().message("地图不存在!").badRequest(),response);
                return;
            }
            mapInfo.setName(mapName);
            String jsonStr = JSONObject.toJSON(mapInfo).toString();
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                    mapInfo.setUpdateTime(new Date());
                    mapInfoService.updateMapInfo(mapInfo);
                }
            });
            MessageFactory.getMapMessage().sendMessageWithID(entry.getMessageId(), "setMap", jsonStr, "设置地图信息成功");
        } catch (Exception e) {
            String message = "设置地图信息失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }*/

    @Log("获取地图信息")
    @GetMapping(value = "/{mapId}/mapInfo")
    @ApiOperation(value = "获取地图信息", httpMethod = "GET")
    public void getMapInfo(@PathVariable Long mapId) throws GmsException {
        if (null == mapId) {
            throw new GmsException("地图id为空");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mapId", mapId);
        try {
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getMapInfo", jsonStr, "获取地图信息成功");
        } catch (Exception e) {
            String message = "获取地图信息失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("保存地图")
    @PutMapping(value = "/{mapId}/saveMap")
    @ApiOperation(value = "保存地图", httpMethod = "PUT")
    public void saveMap(@PathVariable Long mapId) throws GmsException {
        if (null == mapId) {
            throw new GmsException("地图id为空");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mapId", mapId);
        try {
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("saveMap", jsonStr, "保存地图成功");
        } catch (Exception e) {
            String message = "保存地图失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("加载地图")
    @GetMapping(value = "/{mapId}")
    @ApiOperation(value = "加载地图", httpMethod = "GET")
    public void getMap(@PathVariable Long mapId) throws GmsException {
        if (null == mapId) {
            throw new GmsException("地图id为空");
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("mapId", mapId);
        try {
            String jsonStr = JSONObject.toJSON(paramMap).toString();
            MessageFactory.getMapMessage().sendMessageNoID("getMap", jsonStr, "加载地图成功");
        } catch (Exception e) {
            String message = "加载地图失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }


    @Log("地图导出")
    @GetMapping(value = "/{mapId}/{version}")
    @ApiOperation(value = "地图导出", httpMethod = "GET")
    public void exportMap(@PathVariable("mapId") String mapId, @PathVariable("version") String version, String filePath) throws GmsException {
        if (null == filePath) {
            throw new GmsException("地图id为空");
        }

        try {

        } catch (Exception e) {
            String message = "地图导出失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("申请发布地图提交")
    @PutMapping(value = "/{mapId}/mapPublish")
    @ApiOperation(value = "申请发布地图", httpMethod = "PUT")
    public GmsResponse mapPublish(@PathVariable Integer mapId, @MultiRequestBody("userIds") String userIds) throws GmsException {
        submitCheck(userIds);
        try {
            mapInfoService.submitPublishMap(mapId, userIds, super.getCurrentUser());
            return new GmsResponse().message("发布地图提交成功").success();
            /*boolean result = mapInfoService.submitPublishMap(mapId, userIds, super.getCurrentUser());
            if (result) {
                return new GmsResponse().message("发布地图提交成功").success();
            } else {
                return new GmsResponse().message("已存在处于使用或申请发布的地图").badRequest();
            }*/
        } catch (Exception e) {
            String message = "申请发布地图提交失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }


    @Log("申请解除活动地图")
    @PutMapping(value = "/{mapId}/statuses")
    @ApiOperation(value = "申请解除活动地图", httpMethod = "PUT")
    public GmsResponse mapToInactive(@PathVariable("mapId") Integer mapId, @MultiRequestBody("userIds") String userIds) throws GmsException {
        submitCheck(userIds);
        try {
            boolean result = mapInfoService.submitInactiveMap(mapId, userIds, super.getCurrentUser());
            if (result) {
                return new GmsResponse().message("申请解除活动地图提交成功").success();
            } else {
                return new GmsResponse().message("地图不存在或地图不是活动状态").badRequest();
            }
        } catch (Exception e) {
            String message = "申请解除活动地图提交失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("地图删除")
    @DeleteMapping(value = "/{mapId}")
    @ApiOperation(value = "地图删除", httpMethod = "DELETE")
    public void deleteMap(@PathVariable Integer mapId, HttpServletResponse response) throws GmsException {
        try {
            boolean existMapId = mapInfoService.existMapId(mapId);
            if (existMapId){
                MapInfo activeMapInfo = mapInfoService.getActiveMapInfo();
                if(null!=activeMapInfo&&activeMapInfo.getMapId().equals(mapId)){
                    GmsService.callResponse(new GmsResponse().message("活动地图不能删除!").badRequest(),response);
                    return;
                }
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("mapId", mapId);
                MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
                entry.setAfterHandle(() -> {
                    if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                        mapInfoService.deleteMapInfo(mapId);
                    }
                });
                MessageFactory.getMapMessage().sendMessageWithID(entry.getMessageId(),"deleteMap", JSON.toJSONString(paramMap),"地图删除成功");
            }else{
                GmsService.callResponse(new GmsResponse().message("该地图不存在!").badRequest(),response);
            }
        } catch (Exception e) {
            String message = "地图删除失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    public void submitCheck(String userIds) throws GmsException {
        if (StringUtils.isEmpty(userIds)) {
            throw new GmsException("参数异常，请选择审批对象!");
        }
        User user = super.getCurrentUser();
        if (user == null) {
            throw new GmsException("当前用户未登录!");
        }
        String sign = user.getRoleSign();
        if (!Role.RoleSign.MAPMAKER_ROLE.getValue().equals(sign)) {
            throw new GmsException("非地图编辑员角色不能提交申请");
        }
        String[] ids = userIds.split(StringPool.COMMA);
        if (Arrays.asList(ids).contains(user.getUserId().toString())) {
            throw new GmsException("不能选择自身作为审批对象!");
        }
        for (String id : ids) {
            User byId = userService.findUserById(Integer.valueOf(id));
            if (byId != null) {
                String roleSign = byId.getRoleSign();
                if (!(roleSign.equals(Role.RoleSign.CHIEFDESPATCHER_ROLE.getValue()))) {
                    throw new GmsException("请选择调度长作为审批对象!");
                }
            }
        }
    }
}
