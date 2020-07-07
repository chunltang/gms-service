package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.common.configure.EventPublisher;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.MessageEvent;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.message.EventType;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.properties.ErrorCode;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.nettyclient.WsUtil;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.mapmanager.point.AnglePoint;
import com.zs.gms.entity.monitor.*;
import com.zs.gms.entity.vehiclemanager.BarneyType;
import com.zs.gms.service.init.SyncRedisData;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.monitor.UnitVehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * 实时数据处理
 */
@Slf4j
public class LiveVapHandle implements RedisListener {

    private static ListOperations<String, Object> listOperations;

    private static UnitVehicleService unitVehicleService;

    private static ErrorCode errorCode;

    private static LiveVapHandle instance = new LiveVapHandle();

    static {
        unitVehicleService = SpringContextUtil.getBean(UnitVehicleService.class);
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
        Unit unit = unitVehicleService.getUnitByVehicleId(Integer.valueOf(vehicleNo));
        Integer userId = null;
        if (unit == null) {
            log.debug("车辆没有分配调度单元，{}", vehicleNo);
        } else {
            userId = unit.getUserId();
        }
        switch (prefix) {
            case RedisKeyPool.VAP_BASE_PREFIX:
                //车辆基本信息
                VehicleLiveInfo info = GmsService.getMessage(key, VehicleLiveInfo.class);
                if (null == info) {
                    log.error("车辆基本信息获取转换失败!");
                    return;
                }
                addUserInfo(info);
                calculatePosition(info);
                StatusMonitor.delegateStatus(info);
                if (null != userId) {
                    //控制台
                    WsUtil.sendMessage(userId.toString(), GmsUtil.toJsonIEnumDesc(info), FunctionEnum.console, Integer.valueOf(vehicleNo));
                }
                //车辆基本信息
                WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(info), FunctionEnum.vehicle);
                //地图采集
                MapDataUtil.mapCollection(info, info.getVehicleId());
                break;
            case RedisKeyPool.VAP_PATH_PREFIX:
                //交互式路径请求
                GlobalPath globalPath = getGlobalPath(key, false);
                WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(globalPath), FunctionEnum.globalPath);
                break;
            case RedisKeyPool.VAP_COLLECTION_PREFIX:
                info = GmsService.getMessage(key, VehicleLiveInfo.class);
                MapDataUtil.mapCollection(info, info.getVehicleId());
                break;
            case RedisKeyPool.VAP_TRAIL_PREFIX:
                if (WsUtil.isNeed(FunctionEnum.trail, vehicleNo)) {
                    GlobalPath trailPath = getTrailPath(key);
                    WsUtil.sendMessage(GmsUtil.toJson(trailPath), FunctionEnum.trail, Integer.valueOf(vehicleNo));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 添加调度员信息
     */
    private static void addUserInfo(VehicleLiveInfo vehicleLiveInfo) {
        Integer vehicleId = vehicleLiveInfo.getVehicleId();
        UnitVehicleService vehicleService = SpringContextUtil.getBean(UnitVehicleService.class);
        Unit unit = vehicleService.getUnitByVehicleId(vehicleId);
        if (null != unit) {
            vehicleLiveInfo.setUserId(unit.getUserId());
            vehicleLiveInfo.setUserName(unit.getName());
        }
    }

    private static GlobalPath getTrailPath(String key) {
        return getGlobalPath(key, true);
    }

    /**
     * 计算图片显示中心
     */
    private static void calculatePosition(VehicleLiveInfo vehicleLiveInfo) {
        Integer vehicleId = vehicleLiveInfo.getVehicleId();
        Object obj = getVehicleBaseInfo(vehicleId);
        boolean flag = false;
        Monitor monitor = vehicleLiveInfo.getMonitor();
        double xWorld = monitor.getXworld();
        double yWorld = monitor.getYworld();
        if (null != obj) {
            BarneyType barneyType = GmsUtil.toObj(obj, BarneyType.class);
            if (barneyType != null) {
                Double vehicleWidth = barneyType.getVehicleWidth();
                Double vehicleLength = barneyType.getVehicleLenght();
                if (null == vehicleWidth || vehicleWidth < 4 || vehicleWidth > 10) {
                    vehicleWidth = 2d;
                }
                if (null == vehicleLength || vehicleLength < 6 || vehicleLength > 15) {
                    vehicleLength = 4d;
                }
                monitor.setW(vehicleWidth);
                monitor.setL(vehicleLength);
                Double vehicleTailAxle = barneyType.getVehicleTailAxle();
                if (vehicleLength > 0 && vehicleTailAxle != null && vehicleTailAxle > 0 && vehicleLength / 2 > vehicleTailAxle) {
                    double yawAngle = monitor.getYawAngle();
                    double s = vehicleLength / 2 - vehicleTailAxle;
                    double x = xWorld + s * Math.cos(yawAngle);
                    double y = yWorld + s * Math.sin(yawAngle);
                    monitor.setX(GmsUtil.getDoubleScale(x,2));
                    monitor.setY(GmsUtil.getDoubleScale(y,2));
                    flag = true;
                }
            }
        } else {
            log.error("没有缓存对应的车辆基本信息数据!,{}", vehicleId);
        }
        if (!flag) {
            monitor.setX(xWorld);
            monitor.setY(yWorld);
        }
    }

    /**
     * 获取缓存的车辆基本信息
     */
    private static Object getVehicleBaseInfo(Integer vehicleId) {
        Object obj = RedisService.hashOperations(StaticConfig.KEEP_DB).get(RedisKeyPool.VEH_BASE_INFO, vehicleId.toString());
        if (null == obj) {
            SyncRedisData syncRedisData = SpringContextUtil.getBean(SyncRedisData.class);
            syncRedisData.syncBarneyBaseInfos();
        }
        return obj;
    }

    /**
     * 获取全局路径
     *
     * @param flag true为前端获取,false为系统推送
     */
    public static GlobalPath getGlobalPath(String key, boolean flag) {
        GlobalPath path = new GlobalPath();
        try {
            Object obj = RedisService.get(StaticConfig.MONITOR_DB, key);
            if (null == obj) {
                if (!flag)
                    log.error("获取路径轨迹数据失败!");
                return path;
            }
            String[] strs = obj.toString().split(",");
            if (strs.length < 4) {
                return path;
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
            path.setNo(convertToInt(strs[0]));
            path.setVehicleId(convertToInt(strs[1]));
            path.setStatus(convertToInt(strs[2]));
            path.setVertexNum(convertToInt(strs[3]));
            path.setData(list);
            if (!flag) {
                redisResponseEntry(convertToInt(strs[1]), path);
            }
        } catch (Exception e) {
            log.error("全局路径解析失败", e);
        }
        return path;
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

    /**
     * 根据路径最总点匹配
     */
    private static void redisResponseEntry(int vehicleId, GlobalPath path) {
        Set<MessageEntry> entries = MessageFactory.getMessageEntryByParams("vehicleId", "points");
        log.debug("交互式路径请求数据解析,找到{}个请求实体",entries.size());
        if (entries.size() > 0) {
            List<Vertex> list = path.getData();
            if (list.size() > 0) {
                Vertex vertex = list.get(list.size() - 1);
                double x = vertex.getX();
                double y = vertex.getY();
                for (MessageEntry entry : entries) {
                    Map<String, Object> params = entry.getParams();
                    Integer id = GmsUtil.typeTransform(params.get("vehicleId"), Integer.class);
                    AnglePoint[] points = (AnglePoint[]) params.get("points");
                    AnglePoint point = points[points.length - 1];
                    //(Math.abs(point.getX() - x) < 1 || Math.abs(point.getY() - y) < 1)
                    if (vehicleId == id) {
                        redisPublisher(path, entry);
                    }else{
                        log.error("redis发布未能执行");
                    }
                }
            } else {
                for (MessageEntry entry : entries) {
                    Map<String, Object> params = entry.getParams();
                    Integer id = GmsUtil.typeTransform(params.get("vehicleId"), Integer.class);
                    if (vehicleId == id) {
                        redisPublisher(path, entry);
                    }else{
                        log.error("redis发布未能执行");
                    }
                }
            }
        }
    }

    private static void redisPublisher(GlobalPath path, MessageEntry entry) {
        log.debug("交互式路径:redis数据发布!,messageId={},routeKey={}",entry.getMessageId(),entry.getRouteKey());
        path.setMessageId(entry.getMessageId());
        GmsResponse response = entry.getMessage().getGmsResponse();
        handEmptyResult(response, path.getData());
        handResult(response, path.getStatus());
        EventPublisher.publish(new MessageEvent(new Object(), GmsUtil.toJson(path), entry.getMessageId(), EventType.httpRedis));
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
