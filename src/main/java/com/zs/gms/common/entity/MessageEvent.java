package com.zs.gms.common.entity;

import com.zs.gms.common.message.EventType;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class MessageEvent extends ApplicationEvent {

    private String messageId;

    private String message;

    private EventType eventType;


    public MessageEvent(Object object,String message,String messageId,EventType eventType){
        super(object);
        this.message=message;
        this.messageId=messageId;
        this.eventType=eventType;
    }
}
