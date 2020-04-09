package com.zs.gms.common.utils;

import com.rabbitmq.client.Channel;
import com.zs.gms.common.configure.EventPublisher;
import com.zs.gms.common.entity.*;
import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.message.EventType;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.service.init.HeartBeatCheck;
import com.zs.gms.service.init.RedisScript;
import com.zs.gms.service.monitor.schdeule.DispatchHandle;
import com.zs.gms.service.monitor.schdeule.LiveVapHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;

@Slf4j
public class MessageUtil {

    /*********************************************MQ*************************************************/

    public static void handleMqMessage(org.springframework.amqp.core.Message message, Channel channel) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();//根据路由键调不同函数处理方法
        String msg = new String(message.getBody());
        String messageId = message.getMessageProperties().getMessageId();
        if(!GmsUtil.StringNotNull(messageId)){
            log.error("mq接收消息id为空");
            return;
        }
        log.debug("MQ接收消息，key={}，message={},messageId={}", routingKey, msg.length()>1000?msg.substring(0,1000):msg, messageId);
        MessageEntry entry = MessageFactory.getMessageEntry(messageId);
        if(entry!=null){
            if(entry.getMessage()==null){
                EventPublisher.publish(new MessageEvent(new Object(), msg, messageId, EventType.notHttpMq));
            }else{
                EventPublisher.publish(new MessageEvent(new Object(), msg, messageId, EventType.httpMq));
            }
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (IOException e) {
                log.error("mq消息确认异常", e);
            }
        }
    }

    /********************************************redis**************************************************/

    public static void handeListenerResult(Message message) {
        HeartBeatCheck.redis_monitor__last_time =System.currentTimeMillis();
        RedisListener listener=null;
        String key = message.toString();
        if(key.startsWith(RedisKeyPool.VAP_PREFIX))
        {   //车辆数据
            listener= LiveVapHandle.getInstance();
        }
        else if(key.startsWith(RedisKeyPool.DISPATCH_PREFIX))
        {   //调度服务
            listener= DispatchHandle.getInstance();
        }
        else if(key.startsWith(RedisKeyPool.REDIS_SCRIPT_PREFIX))
        {   //脚本
            listener= RedisScript.getInstance();
        }

        if(listener!=null)
        {
            listener.listener(key);
        }
    }
}
