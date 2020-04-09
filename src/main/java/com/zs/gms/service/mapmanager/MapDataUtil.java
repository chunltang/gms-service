package com.zs.gms.service.mapmanager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.company.Interval;
import com.zs.gms.common.annotation.RedisLock;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.mapmanager.MapInfo;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.service.monitor.schdeule.LivePosition;
import com.zs.gms.service.system.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class MapDataUtil {

    @Interval(interval = 10000,
            before = "com.zs.gms.common.service.GmsService.preIntervalHandler",
            isReturn = true)
    public static void syncMap() {
        MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
        entry.setAfterHandle(() -> {
            if (MessageResult.SUCCESS.equals(entry.getHandleResult())) {
                String returnData = entry.getReturnData();
                List<MapInfo> mapInfos = GmsUtil.toCollectObj(returnData, ArrayList.class,MapInfo.class);
                MapInfoService bean = SpringContextUtil.getBean(MapInfoService.class);
                Set<Integer> mapIds = mapInfos.stream().map(MapInfo::getMapId).collect(Collectors.toSet());
                if (GmsUtil.CollectionNotNull(mapInfos)) {
                    for (MapInfo mapInfo : mapInfos) {
                        Integer id = mapInfo.getMapId();
                        if (null != id) {
                            MapInfo info = bean.getMapInfo(id);
                            if (null == info) {//新增数据
                                setDefaultValue(mapInfo);
                                bean.addMapInfo(mapInfo);
                            } else {
                                bean.updateMapInfo(mapInfo);
                            }
                        }
                    }
                }
                //删除业务层存在而地图不存在的id
                QueryRequest queryRequest = new QueryRequest();
                queryRequest.setPageSize(1000);
                IPage<MapInfo> listPage = bean.getMapInfoListPage(queryRequest);
                List<MapInfo> records = listPage.getRecords();
                for (MapInfo mapInfo : records) {
                    Integer mapId = mapInfo.getMapId();
                    MapInfo.Status status = mapInfo.getStatus();
                    if(!mapIds.contains(mapId)){
                        if(status.equals(MapInfo.Status.USING)){
                            log.error("业务层信息和地图服务信息不同步，业务层活动地图在地图模块没有相关数据");
                            continue;
                        }
                        bean.deleteMapInfo(mapId);
                    }
                }
            }
        });
        MessageFactory.getMapMessage().sendMessageNoResWithID(entry.getMessageId(), "getAllBasicMapInfo", "");
    }

    /**
     * 获取活动地图
     */
    public static Integer getActiveMap() {
        Object obj = RedisService.getTemplate(StaticConfig.KEEP_DB).opsForValue().get(RedisKeyPool.ACTIVITY_MAP);
        if (obj != null) {
            String value = String.valueOf(obj);
            if (StringUtils.isNotEmpty(value)) {
                return Integer.valueOf(value);
            }
        }
        return null;
    }

    /**
     * 设置默认创建信息
     */
    private static void setDefaultValue(MapInfo mapInfo) {
        UserService bean = SpringContextUtil.getBean(UserService.class);
        List<User> sign = bean.getUsersByRoleSign(Role.RoleSign.ADMIN_ROLE.getValue());
        User user = sign.get(0);
        mapInfo.setUserId(user.getUserId());
        mapInfo.setUserName(user.getUserName());
    }


    /**
     * 地图编辑加锁,默认30分钟后释放,false表示不是自己的锁
     */
    @RedisLock(key = "mapServerLock")
    public static boolean editLock(Integer mapId, String userId) {
        synchronized (RedisKeyPool.MAP_EDIT_LOCK + mapId) {
            if (!getLockStatus(mapId, userId)) {
                log.debug("添加地图编辑锁");
                return RedisService.set(StaticConfig.KEEP_DB, RedisKeyPool.MAP_EDIT_LOCK + mapId, userId, 30l, TimeUnit.MINUTES);
            }
        }
        return false;
    }

    /**
     * 释放锁
     */
    public static void releaseLock(Integer mapId, String userId) {
        synchronized (RedisKeyPool.MAP_EDIT_LOCK) {
            if (!getLockStatus(mapId, userId)) {
                log.debug("释放地图编辑锁");
                RedisService.deleteKey(StaticConfig.KEEP_DB, RedisKeyPool.MAP_EDIT_LOCK + mapId);
            }
        }
    }

    /**
     * 获取锁状态,true为其他用户已加锁
     */
    private static boolean getLockStatus(Integer mapId, String userId) {
        Object editUser = getLockUser(mapId);
        if (null == editUser || editUser.toString().equals(userId)) {
            return false;
        }
        return true;
    }

    /**
     * 获取当前编辑用户
     */
    public static Object getLockUser(Integer mapId) {
        return RedisService.get(StaticConfig.KEEP_DB, RedisKeyPool.MAP_EDIT_LOCK + mapId);
    }

    /**
     * 获取半静态层数据
     */
    public static List<SemiStatic> getSemiStaticData(Integer mapId) {
        Object obj = RedisService.get(StaticConfig.KEEP_DB, RedisKeyPool.SEMI_STATIC_DATA + mapId);
        List<SemiStatic> semiStatics = new ArrayList<>();
        if (null != obj) {
            JSONArray jsonObject = JSON.parseArray(obj.toString());
            if (null != jsonObject) {
                for (Object o : jsonObject) {
                    SemiStatic semiStatic = GmsUtil.toObjIEnum(o, SemiStatic.class);
                    semiStatics.add(semiStatic);
                }
            }
        }
        return semiStatics;
    }

    /**
     * 根据区域类型筛选数据
     */
    public static List<SemiStatic> getAreaInfos(Integer mapId, AreaTypeEnum areaType) {
        List<SemiStatic> semiStatics = getSemiStaticData(mapId);
        if (null == areaType) {
            return semiStatics;
        }
        List<SemiStatic> result = new ArrayList<>();
        for (SemiStatic semiStatic : semiStatics) {
            if (null != areaType && areaType.equals(semiStatic.getAreaType())) {
                result.add(semiStatic);
            }
        }
        return result;
    }

    /**
     * 根据区域id获取区域信息
     */
    public static SemiStatic getAreaInfo(Integer mapId, Integer areaId) {
        List<SemiStatic> semiStatics = getSemiStaticData(mapId);
        if (null == areaId) {
            return null;
        }
        for (SemiStatic semiStatic : semiStatics) {
            if (semiStatic.getId().equals(areaId)) {
                return semiStatic;
            }
        }
        return null;
    }

    /**
     * 判断区域类型是否存在
     */
    public static boolean isAreaExist(Integer mapId, Integer id, AreaTypeEnum areaType) {
        List<SemiStatic> areaInfos = getAreaInfos(mapId, areaType);
        for (SemiStatic areaInfo : areaInfos) {
            if (areaInfo.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断位置在地图的那个区域
     */

    public static void getCoordinateArea(LivePosition.Position position) {
        Map<String, Object> params = new HashMap<>();
        params.put("mapId", position.getMapId());
        params.put("x", position.getPoint().getX());
        params.put("y", position.getPoint().getY());
        params.put("z", 0);
        MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
        entry.setHttp(false);
        entry.setAfterHandle(() -> {
            if (MessageResult.SUCCESS.equals(entry.getHandleResult())) {
                String returnData = entry.getReturnData();
                String key = "areaId";
                if (GmsUtil.StringNotNull(returnData)) {
                    Map map = GmsUtil.toObj(returnData, HashMap.class);
                    if (null != map && map.containsKey(key)) {
                        Object value = map.get(key);
                        Integer areaId = GmsUtil.typeTransform(value, Integer.class);
                        Integer lastArea = position.getLastArea();
                        position.setLastArea(areaId);
                        SemiStatic areaInfo = LivePosition.getAreaInfo(position);
                        if (position.isLoadArea() && null != areaInfo && !AreaTypeEnum.LOAD_AREA.equals(areaInfo.getAreaType())) {
                            //表示出了装载区,推送最后一条数据
                            position.setLoadArea(false);
                            WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(position), FunctionEnum.excavator, lastArea);//拿之前的id
                            return;
                        }
                        if (LivePosition.isAreaType(position, AreaTypeEnum.LOAD_AREA)) {
                            position.setLoadArea(true);
                            if (WsUtil.isNeed(FunctionEnum.excavator, value)) {
                                WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(position), FunctionEnum.excavator, areaId);
                            }
                        }
                        if (null != areaInfo && areaInfo.getAreaType() != null) {
                            log.debug("车{}所在地图区域:{} {}", position.getVehicleId(), value, areaInfo.getAreaType().getDesc());
                        }
                    }
                }
            }
        });
        MessageFactory.getMapMessage().sendMessageNoResWithID(entry.getMessageId(), "getCoordinateArea", GmsUtil.toJson(params));
    }
}
