package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.common.configure.EventPublisher;
import com.zs.gms.common.entity.*;
import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.message.EventType;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.properties.ErrorCode;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.monitor.VehicleLiveInfo;
import com.zs.gms.entity.monitor.Vertex;
import com.zs.gms.service.vehiclemanager.BarneyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实时数据处理
 */
@Slf4j
public class LiveVapHandle implements RedisListener {

    private static ListOperations<String, Object> listOperations;

    private static BarneyService barneyService;

    private static ErrorCode errorCode;

    private static LiveVapHandle instance = new LiveVapHandle();

    static {
        barneyService = SpringContextUtil.getBean(BarneyService.class);
        errorCode = SpringContextUtil.getBean(ErrorCode.class);
        listOperations = RedisService.listOperations(RedisService.getTemplate(StaticConfig.MONITOR_DB));
    }

    public static LiveVapHandle getInstance() {
        return instance;
    }


    /**
     * 处理所有车辆监听数据
     */
    private static void handleVehMessage(String key) {
        String prefix = GmsUtil.subIndexStr(key, "_");
        String vehicleNo = GmsUtil.subLastStr(key, "_");
        Integer userId = barneyService.getUserIdByVehicleNo(Integer.valueOf(vehicleNo));
        if (userId == null) {
            log.error("不存在的车辆编号或者车辆没有分配，{}", vehicleNo);
        }
        switch (prefix) {
            case RedisKeyPool.VAP_BASE_PREFIX:
                //车辆基本信息
                VehicleLiveInfo vehicleLiveInfo = GmsUtil.getMessage(key, VehicleLiveInfo.class);
                StatusMonitor.delegateStatus(vehicleLiveInfo);
                if (null != userId) {
                    WsUtil.sendMessage(userId.toString(), GmsUtil.toJsonIEnumDesc(vehicleLiveInfo), FunctionEnum.console, Integer.valueOf(vehicleNo));
                }
                WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(vehicleLiveInfo), FunctionEnum.vehicle);
                break;
            case RedisKeyPool.VAP_PATH_PREFIX:
                //交互式路径请求
                String messageId = GmsConstant.DISPATCH + "_" + vehicleNo;
                if (MessageFactory.containMessageEntry(messageId)) {
                    log.debug("交互式路径请求数据解析");
                    Map<String, Object> globalPath = getGlobalPath(key);
                    EventPublisher.publish(new MessageEvent(new Object(), GmsUtil.toJson(globalPath), messageId, EventType.httpRedis));
                }
                //全局路径
                if (WsUtil.isNeed(FunctionEnum.globalPath)) {
                    Map<String, Object> globalPath = getGlobalPath(key);
                    WsUtil.sendMessage(GmsUtil.toJson(globalPath), FunctionEnum.globalPath);
                }
                break;
            case RedisKeyPool.VAP_COLLECTION_PREFIX:
                if (WsUtil.isNeed(FunctionEnum.collectMap, vehicleNo)) {
                    HashMap map = GmsUtil.getMessage(key, HashMap.class);
                    WsUtil.sendMessage(GmsUtil.toJson(map), FunctionEnum.collectMap, Integer.valueOf(vehicleNo));
                }
                break;
            case RedisKeyPool.VAP_TRAIL_PREFIX:
                if (WsUtil.isNeed(FunctionEnum.trail, vehicleNo)) {
                    Map<String, Object> trailPath = getTrailPath(key);
                    WsUtil.sendMessage(GmsUtil.toJson(trailPath), FunctionEnum.trail, Integer.valueOf(vehicleNo));
                }
                break;
            default:
                break;
        }
    }

    private static Map<String, Object> getTrailPath(String key) {
        return getGlobalPath(key);
    }

    /**
     * 获取全局路径
     */
    public static Map<String, Object> getGlobalPath(String key) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String[] strs = RedisService.get(StaticConfig.MONITOR_DB, key).toString().split(",");
            if (strs.length < 4) {
                return resultMap;
            }
            int len = convertToInt(strs[3]);
            int strLen = 4;
            List<Vertex> list = new ArrayList<>(len);
            Vertex vertex;
            for (int i = 0; i < len; i++) {
                vertex = new Vertex();
                vertex.setX(convertToDouble(strs[strLen]));
                vertex.setY(convertToDouble(strs[strLen + 1]));
                vertex.setZ(convertToDouble(strs[strLen + 2]));
                vertex.setType(convertToInt(strs[strLen + 3]));
                vertex.setDirection(convertToFloat(strs[strLen + 4]));
                vertex.setSlope(convertToFloat(strs[strLen + 5]));
                vertex.setCurvature(convertToFloat(strs[strLen + 6]));
                vertex.setLeftDistance(convertToDouble(strs[strLen + 7]));
                vertex.setRightDistance(convertToDouble(strs[strLen + 8]));
                vertex.setMaxSpeed(convertToDouble(strs[strLen + 9]));
                vertex.setSpeed(convertToDouble(strs[strLen + 10]));
                vertex.setS(convertToDouble(strs[strLen + 11]));
                vertex.setReverse(convertToBool(strs[strLen + 12]));
                list.add(vertex);
                strLen += 13;
            }
            resultMap.put("no", convertToInt(strs[0]));
            resultMap.put("vehicleId", convertToInt(strs[1]));
            resultMap.put("status", convertToInt(strs[2]));
            resultMap.put("vertex_num", convertToInt(strs[3]));
            resultMap.put("data", list);
            GmsResponse response = getResponse(convertToInt(strs[1]));
            if (GmsUtil.objNotNull(response)) {
                handEmptyResult(response, list);
                handResult(response, convertToInt(strs[2]));
            } else {
                log.error("全局路径解析未获取到请求实体");
            }
        } catch (Exception e) {
            log.error("全局路径解析失败", e);
        }
        return resultMap;
    }

    private static void handEmptyResult(GmsResponse response, List<Vertex> list) {
        if (!GmsUtil.CollectionNotNull(list)) {
            response.message("路径点集数据为空").badRequest();
        }
    }

    private static void handResult(GmsResponse response, int status) {
        Map<String, String> code = errorCode.getMap();
        String value = String.valueOf(status);
        if (status != 0) {
            response.code(HttpStatus.BAD_REQUEST);
            response.message(code.getOrDefault(value, "交互式路径规划异常"));
        }
    }

    private static GmsResponse getResponse(int vehicleId) {
        MessageEntry entry = MessageFactory.getMessageEntry(GmsConstant.DISPATCH + "_" + vehicleId);
        if (null != entry) {
            return entry.getMessage().getGmsResponse();
        }
        return null;
    }

    private static int convertToInt(String str) {
        return Integer.valueOf(str);
    }

    private static double convertToDouble(String str) {
        return Double.valueOf(str);
    }

    private static Float convertToFloat(String str) {
        return Float.valueOf(str);
    }

    private static Boolean convertToBool(String str) {
        return !str.equals("0");
    }

    @Override
    public void listener(String key) {
        handleVehMessage(key);
    }
}
