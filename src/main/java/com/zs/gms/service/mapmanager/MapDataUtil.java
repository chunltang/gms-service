package com.zs.gms.service.mapmanager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.nio.sctp.HandlerResult;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.RedisKey;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageInterface;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.service.websocket.impl.HandleCenter;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.Point;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.service.monitor.schdeule.LivePosition;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MapDataUtil {

    /**
     * 获取半静态层数据
     * */
    public static List<SemiStatic> getSemiStaticData(Integer mapId){
        Object obj = RedisService.get(GmsConstant.KEEP_DB, RedisKey.SEMI_STATIC_DATA + mapId);
        List<SemiStatic> semiStatics = new ArrayList<>();
        if(null!=obj){
            JSONArray jsonObject = JSON.parseArray(obj.toString());
            if(null!=jsonObject){
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
     * */
    public static List<SemiStatic> getAreaInfos(Integer mapId, AreaTypeEnum areaType){
        List<SemiStatic> semiStatics = getSemiStaticData(mapId);
        if(null==areaType){
            return semiStatics;
        }
        List<SemiStatic> result=new ArrayList<>();
        for (SemiStatic semiStatic : semiStatics) {
            if(null!=areaType && areaType.equals(semiStatic.getAreaType())){
                result.add(semiStatic);
            }
        }
        return result;
    }

    /**
     * 判断区域类型是否存在
     * */
    public static boolean isAreaExist(Integer mapId,Integer id,AreaTypeEnum areaType){
        List<SemiStatic> areaInfos = getAreaInfos(mapId, areaType);
        for (SemiStatic areaInfo : areaInfos) {
            if(areaInfo.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断位置在地图的那个区域
     * */
    public static void getCoordinateArea(LivePosition.Position position){
        Map<String,Object> params=new HashMap<>();
        params.put("mapId",position.getMapId());
        params.put("x",position.getPoint().getX());
        params.put("y",position.getPoint().getY());
        params.put("z",0);
        MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
        entry.setHttp(false);
        entry.setAfterHandle(()->{
            if(MessageResult.SUCCESS.equals(entry.getHandleResult())){
                String returnData = entry.getReturnData();
                String key="areaId";
                if(GmsUtil.StringNotNull(returnData)){
                    Map map = GmsUtil.toObj(returnData,HashMap.class);
                    if(null!=map && map.containsKey(key)){
                        Object value = map.get(key);
                        Integer areaId = GmsUtil.typeTransform(value, Integer.class);
                        position.setLastArea(areaId);
                        log.debug("车{}所在地图区域:{}",position.getVehicleId(),value);
                        if(WsUtil.isNeed(FunctionEnum.excavator,value)){
                            WsUtil.sendMessage(GmsUtil.toJson(position),FunctionEnum.excavator,areaId);
                        }
                    }
                }
            }
        });
        MessageFactory.getMapMessage().sendMessageNoResWithID(entry.getMessageId(),"getCoordinateArea",GmsUtil.toJson(params));
    }
}
