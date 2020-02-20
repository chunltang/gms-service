package com.zs.gms.common.message.impl;

import com.zs.gms.common.message.MessageAbstract;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.service.RabbitMqService;
import org.springframework.beans.factory.annotation.Value;


public class MapMessage extends MessageAbstract {

    public MapMessage(String name,String exchangeName){
        super(name,exchangeName);
    }

    /**
     * httpRequest，不要回调
     * */
    public void sendMessageNoID(String routKry, String sendMessage, String resultMessage){
        super.sendMessageNoID(routKry,sendMessage,resultMessage);
    }

    /**
     * httpRequest,需要调用回到接口
     * */
    public void sendMessageWithID(String messageId, String routKry, String sendMessage, String resultMessage){
        super.sendMessageWithID(messageId,routKry,sendMessage,resultMessage);
    }

    /**
     * 传入id,有回调，非http请求
     * */
    public void sendMessageNoResWithID(String messageId,String routKry, String sendMessage){
        super.sendMessageNoResWithID(messageId,routKry,sendMessage);
    }
}
