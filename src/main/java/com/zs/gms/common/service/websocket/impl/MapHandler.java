package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MapHandler extends AbstractFunctionHandler {

    public Map<Session, Set<Integer>> sessionMap = new ConcurrentHashMap();

    @Override
    public Set<Session> getSession(Integer vehicleId) {
        Set<Session> sessions = new HashSet<>();
        Set<Session> keySet = sessionMap.keySet();
        for (Session session : keySet) {
            if(sessionMap.get(session).contains(vehicleId)){
                sessions.add(session);
            }
        }
        return sessions;
    }

    @Override
    public boolean hasSession(Session session) {
        return sessionMap.containsKey(session);
    }

    @Override
    public boolean isNeed(Object ...params) {
        return GmsUtil.arrayNotNull(params)?hasVehicle(GmsUtil.typeTransform(params[0],Integer.class)):!sessionMap.isEmpty();
    }

    public boolean hasVehicle(Integer vehicleId){
        for (Set<Integer> value : sessionMap.values()) {
            if(value.contains(vehicleId)){
                return true;
            }
        }
        return false;
    }
}
