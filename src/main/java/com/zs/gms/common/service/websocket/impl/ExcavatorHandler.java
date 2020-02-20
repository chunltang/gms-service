package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.ScheduleService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.service.monitor.schdeule.StatusMonitor;
import com.zs.gms.service.monitor.schdeule.VehicleDispatchStatusHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@WsFunction(key = FunctionEnum.excavator)
@Component
@Slf4j
public class ExcavatorHandler extends SetHandler {

    private Map<Integer,Session> areaSessionMap=new ConcurrentHashMap<>();
    @Override
    public void sendMessage(Session session, String message) {
        synchronized (session) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                } else {
                    sessions.remove(session);
                }
            } catch (IOException e) {
                log.error("ws-excavator发送数据失败", e);
            }
        }
    }

    @Override
    public Set<Session> getSession(Integer areaId) {
        Set<Session> sessions = new HashSet<>();
        if(areaSessionMap.containsKey(areaId)){
            sessions.add(areaSessionMap.get(areaId));
        }
        return sessions;
    }

    @Override
    public boolean isNeed(Object ...params) {
        if(null!=params && params.length>0){
            Integer areaId=GmsUtil.typeTransform(params[0],Integer.class);
            return !sessions.isEmpty() && areaSessionMap.keySet().contains(areaId);
        }
        return !sessions.isEmpty();
    }

    /**
     * 获取挖掘机所在区域
     * */
    public void afterAdd(Session session){
        Object key = session.getUserProperties().get("key");
        //获取user管理的电铲，并获取电铲管理的区域
        Integer excavatorId=1100;
        //获取电铲所在装载区
        Integer areaId=1001;
        areaSessionMap.put(areaId,session);
    }
}
