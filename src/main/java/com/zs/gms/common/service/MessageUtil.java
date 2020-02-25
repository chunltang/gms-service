package com.zs.gms.common.service;

import com.rabbitmq.client.Channel;
import com.zs.gms.common.configure.EventPublisher;
import com.zs.gms.common.entity.MessageEvent;
import com.zs.gms.common.entity.RedisKey;
import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.message.EventType;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.service.init.DispatchInit;
import com.zs.gms.service.monitor.schdeule.LiveStateHandle;
import com.zs.gms.service.monitor.schdeule.LiveVapHandle;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class MessageUtil {

    /**
     * 重置监听时间
     * */
    public static long lastTime=System.currentTimeMillis();

    /*********************************************MQ*************************************************/

    public static void handleMqMessage(org.springframework.amqp.core.Message message, Channel channel) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();//根据路由键调不同函数处理方法
        String msg = new String(message.getBody());
        String messageId = message.getMessageProperties().getMessageId();
        log.debug("MQ接收消息，key={}，messgae={},messageId={}", routingKey, msg.length()>1000?msg.substring(0,1000):msg, messageId);
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
        lastTime=System.currentTimeMillis();
        RedisListener listener=null;
        String key = message.toString();
        if(key.startsWith(RedisKey.VAP_PREFIX))
        {   //车辆数据
            listener= LiveVapHandle.getInstance();
        }
        else if(key.startsWith(RedisKey.TASK_PREFIX))
        {
            listener=LiveStateHandle.getInstance();
        }
        else if(key.equals(RedisKey.DISPATCH_SERVER_INIT))
        {   //调度初始化
            listener= SpringContextUtil.getBean(DispatchInit.class);
        }

        if(listener!=null)
        {
            listener.listener(key);
        }
    }
}
