package com.zs.gms.common.service;

import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;


@Configuration
public class WebSocketConfig extends ServerEndpointConfig.Configurator {

    @Bean
    @ConditionalOnExpression(value = "${gms.test}")
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * ws握手时执行，因为ws连接成功后不能走shiro拿到用户信息，需要在握手成功前加入用户信息
     * */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response){
        User user = GmsUtil.getCurrentUser();
        sec.getUserProperties().put("key",user==null?"":user.getUserId());
        super.modifyHandshake(sec,request,response);
    }
}