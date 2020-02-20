package com.zs.gms.common.service.websocket;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.service.WebSocketConfig;
import com.zs.gms.common.service.websocket.impl.HandleCenter;
import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

//注意：采用前后端分离后，websocket连接需要带token信息，在拦截器中进行处理
@ServerEndpoint(value = "/webSocket", configurator = WebSocketConfig.class)
@Component
@Slf4j
public class WebSocketService {

    private Session session;

    private String key;

    /**
     * 连接成功调用
     */
    @OnOpen
    public void onOpen(Session session) {
        Object key = session.getUserProperties().get("key");
        if (ObjectUtils.isEmpty(key)) {
            try {
                synchronized (session){
                    session.getBasicRemote().sendText("连接失败，当前用户未登陆!");
                    session.close();
                }
            } catch (IOException e) {
                log.error("websocket连接失败，当前用户未登陆!", e);
            }
            return;
        }
        this.session=session;
        this.key=key.toString();
        HandleCenter.getInstance().newSession(key.toString(),session);
    }


    /**
     * 接收消息,订阅功能点或取消订阅
     */
    @OnMessage
    public void onMessage(String message, Session session) {//{funcName:"globalPath",type:"remove/add"}
        HashMap params = GmsUtil.toObj(message, HashMap.class);
        if(params==null){
            log.error("websocket监听消息失败,message={}",message);
            return;
        }
        HandleCenter.getInstance().handleRequest(params,session);
    }

    /**
     * 关闭连接
     */
    @OnClose
    public void onClose() {
        closeSession();
    }

    /**
     * 异常处理
     */
    @OnError
    public void onError(Session session, Throwable error) throws GmsException {
        log.error("webSocket异常:{}",error.toString());
    }

    private void closeSession() {
        HandleCenter.getInstance().removeSession(session);
    }
}
