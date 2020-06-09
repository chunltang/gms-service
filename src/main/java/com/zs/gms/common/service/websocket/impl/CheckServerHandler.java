package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;

@WsFunction(key = FunctionEnum.checkServer)
@Component
@Slf4j
public class CheckServerHandler extends SetHandler {

    @Override
    public void sendMessage(Session session, String message) {
        sendMessage(message);
    }

    public void sendMessage(String message) {
        for (Session session : sessions) {
            super.sendMessage(session, message);
        }
    }
}
