package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@WsFunction(key = FunctionEnum.globalPath)
@Component
@Slf4j
public class GlobalPathHandler extends SetHandler {

    @Override
    public void sendMessage(Session session, String message) {
        if (null != session) {
            super.sendMessage(session,message);
        } else {
            for (Session s : sessions) {
                if(!isRole(s,Role.RoleSign.CHIEFDESPATCHER_ROLE,Role.RoleSign.DESPATCHER_ROLE)){
                    log.error("非调度长或调度员会话，关闭连接！");
                    removeFunction(s);
                    return;
                }
                super.sendMessage(s,message);
            }
        }
    }
}

