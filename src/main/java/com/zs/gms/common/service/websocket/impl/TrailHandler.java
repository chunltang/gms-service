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
import java.util.concurrent.CopyOnWriteArraySet;

@WsFunction(key = FunctionEnum.trail)
@Component
@Slf4j
public class TrailHandler extends MapHandler {

    @Override
    public void addFunction(Map<String, Object> params) {
        if(GmsUtil.mapContains(params,SESSION_FIELD,VEHICLE_FIELD)){
            Session session = (Session) params.get(SESSION_FIELD);
            Integer integer = Integer.valueOf(params.get(VEHICLE_FIELD).toString());
            sessionMap.computeIfAbsent(session, s -> new HashSet<>()).add(integer);
        }
    }

    @Override
    public void removeFunction(Session session) {
        if (sessionMap.containsKey(session)) {
            sessionMap.remove(session);
        }
    }

    @Override
    public void sendMessage(Session session, String message) {
        synchronized (session) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                } else {
                    sessionMap.remove(session);
                }
            } catch (IOException e) {
                log.error("ws-trail发送数据失败", e);
            }
        }
    }


    
}
