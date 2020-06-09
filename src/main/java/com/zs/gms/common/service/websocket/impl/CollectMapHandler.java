package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

@WsFunction(key = FunctionEnum.collectMap)
@Component
@Slf4j
public class CollectMapHandler extends MapHandler {

    @Override
    public void addFunction(Map<String, Object> params) {
        if (GmsUtil.mapContains(params, SESSION_FIELD, VEHICLE_FIELD)) {
            Session session = (Session) params.get(SESSION_FIELD);
            Integer integer = Integer.valueOf(params.get(VEHICLE_FIELD).toString());
            sessionMap.computeIfAbsent(session, s -> new HashSet<>()).add(integer);
            //调度开启采集
        }
    }

    @Override
    public void removeFunction(Session session) {
        if (sessionMap.containsKey(session)) {
            sessionMap.remove(session);
            //调度关闭
        }
    }

    @Override
    public void sendMessage(Session session, String message) {
        if (null != session) {
            super.sendMessage(session,message);
        } else {
            for (Session s : sessionMap.keySet()) {
                super.sendMessage(s,message);
            }
        }
    }

}
