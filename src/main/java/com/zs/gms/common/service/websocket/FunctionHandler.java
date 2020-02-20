package com.zs.gms.common.service.websocket;

import com.alibaba.fastjson.JSONObject;

import javax.websocket.Session;
import java.util.Map;
import java.util.Set;

public interface FunctionHandler {

    public void addFunction(Map<String,Object> params);

    public void removeFunction(Session session);

    public void sendMessage(Session session,String message);

    public boolean hasSession(Session session);

    /**
     * 根据车id获取session
     * */
    public Set<Session> getSession(Integer vehicleId);

    public void removeSession(Session session);

    public boolean isNeed(Object ...params);
}
