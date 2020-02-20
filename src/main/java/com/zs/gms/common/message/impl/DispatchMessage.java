package com.zs.gms.common.message.impl;

import com.zs.gms.common.message.MessageAbstract;

public class DispatchMessage extends MessageAbstract {

    public DispatchMessage(String name,String exchangeName){
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
     * 非http，有回调
     * */
    public void sendMessageNoResWithID(String messageId, String routKry, String sendMessage){
        super.sendMessageNoResWithID(messageId,routKry,sendMessage);
    }

    /**
     * 只发不收，没回调
     * */
    public void sendMessageNoResNoID(String routKry, String sendMessage){
        super.sendMessageNoResNoID(routKry,sendMessage);
    }
}
