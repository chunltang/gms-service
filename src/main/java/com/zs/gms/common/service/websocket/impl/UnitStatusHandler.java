package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;

@WsFunction(key = FunctionEnum.unitStatus)
@Component
@Slf4j
public class UnitStatusHandler extends SetHandler {

    @Override
    public void sendMessage(Session sNull, String message) {
        String result = getResult(message,FunctionEnum.unitStatus.name());
        for (Session session : sessions) {
            synchronized (session) {
                try {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(result);
                    } else {
                        sessions.remove(session);
                    }
                } catch (IOException e) {
                    log.error("ws-unitStatus发送数据失败", e);
                }
            }
        }
    }
}
