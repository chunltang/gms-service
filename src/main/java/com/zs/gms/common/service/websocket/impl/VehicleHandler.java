package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.Obstacle;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@WsFunction(key = FunctionEnum.vehicle)
@Component
@Slf4j
public class VehicleHandler extends SetHandler {

    @Override
    public void sendMessage(Session session, String message) {
        if (null != session) {
            super.sendMessage(session, message);
        } else {
            for (Session s : sessions) {
                if(s.isOpen()){
                    if (!isRole(s, Role.RoleSign.CHIEFDESPATCHER_ROLE, Role.RoleSign.DESPATCHER_ROLE, Role.RoleSign.EXCAVATORPERSON_ROLE)) {
                        log.error("非调度长/调度员/挖掘机操作员会话，关闭连接！");
                        removeFunction(s);
                        return;
                    }
                    super.sendMessage(s, message);
                }else{
                    removeFunction(s);
                }
            }
        }
    }

    @Override
    public void afterAdd(Session session) {
        HashMap<String, Object> params = new HashMap();
        params.put(FUNCTION_FIELD, FunctionEnum.obstacle.name());
        params.put(TYPE_FIELD, ADD_FUNCTION);
        HandleCenter.getInstance().handleRequest(params, session);
    }

    @Override
    public void removeFunction(Session session) {
        if (sessions.contains(session)) {
            sessions.remove(session);
        }
        HashMap<String, Object> params = new HashMap();
        params.put(FUNCTION_FIELD, FunctionEnum.obstacle.name());
        params.put(TYPE_FIELD, REMOVE_FUNCTION);
        HandleCenter.getInstance().handleRequest(params, session);
    }


}
