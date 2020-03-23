package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class SetHandler extends AbstractFunctionHandler {

    public CopyOnWriteArraySet<Session> sessions=new CopyOnWriteArraySet<>();

    @Override
    public void addFunction(Map<String, Object> params) {
        if(GmsUtil.mapContains(params,SESSION_FIELD)){
            Session session = (Session) params.get(SESSION_FIELD);
            sessions.add(session);
            afterAdd(session);
        }
    }

    @Override
    public void removeFunction(Session session) {
        if (sessions.contains(session)) {
            sessions.remove(session);
        }
    }

    @Override
    public boolean hasSession(Session session) {
        return sessions.contains(session);
    }

    @Override
    public boolean isNeed(Object ...params) {
        return !sessions.isEmpty();
    }
}
