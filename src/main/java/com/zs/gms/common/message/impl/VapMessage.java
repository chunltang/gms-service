package com.zs.gms.common.message.impl;

import com.zs.gms.common.message.MessageAbstract;

public class VapMessage extends MessageAbstract {

    public VapMessage(String name,String exchangeName){
        super(name,exchangeName);
    }

    public void sendMessageNoResNoID(String routKry, String sendMessage){
        super.sendMessageNoResNoID(routKry,sendMessage);
    }
}
