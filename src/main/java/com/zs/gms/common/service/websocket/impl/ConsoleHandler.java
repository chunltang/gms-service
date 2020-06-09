package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WsFunction(key = FunctionEnum.console)
@Component
@Slf4j
public class ConsoleHandler extends MapHandler {

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
        super.sendMessage(session,message);
    }
    
}
