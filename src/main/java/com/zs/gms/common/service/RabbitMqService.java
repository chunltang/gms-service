package com.zs.gms.common.service;


import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.properties.GmsProperties;
import com.zs.gms.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
@DependsOn(value = {"springContextUtil","rabbitTemplate"})
public class RabbitMqService implements InitializingBean{

    @Autowired
    private RabbitAdmin admin;

    @Autowired
    private GmsProperties gmsProperties;

    private  static RabbitTemplate template=(RabbitTemplate)SpringContextUtil.getBean("rabbitTemplate");

    private static String serverName;

    @Override
    public void afterPropertiesSet() throws Exception {
        createQueues();
        createExChanges();
        createBinds();
    }

    public void createQueues(){
        Map<String, String> queuesMap = gmsProperties.getQueues();
        if(MapUtils.isNotEmpty(queuesMap)){
            for (String queue : queuesMap.values()) {
                if(queue.startsWith("Busi")){
                    admin.declareQueue(new Queue(queue,true));
                }else{
                    admin.declareQueue(new Queue(queue+"_"+serverName,true));
                }
            }
        }
    }

    public void createExChanges(){
        Map<String, String> exchangesMap = gmsProperties.getExchanges();
        if(MapUtils.isNotEmpty(exchangesMap)){
            for (String exchange : exchangesMap.values()) {
                admin.declareExchange(new TopicExchange(exchange,true,false));
            }
        }
    }

    public void createBinds(){
        Map<String, List<String>> binds = gmsProperties.getBinds();
        if(null!=binds && binds.size()>0){
            Set<Map.Entry<String, List<String>>> entries = binds.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                String routKey=value.get(1);
                if(!routKey.startsWith("#")){
                    routKey=routKey+"."+serverName;
                    key=key+"_"+serverName;
                }

                admin.declareBinding(new Binding(key, Binding.DestinationType.QUEUE,value.get(0),routKey,null));
            }
        }
    }

    /**
     * 监听queueListener队列，AmqpHeaders获取属性，通过MessagePostProcessor来添加属性
     * 配置SimpleMessageListenerContainer或使用注解 @RabbitListener
     * 注解 @SendTo 可以设置返回一个消息
     * */
    /*@RabbitListener(queues = {"queueListener"},containerFactory = "containerFactory")
    public void onMessage(@Payload String body,@Header(AmqpHeaders.RECEIVED_ROUTING_KEY)String routeKey) {
        log.info("消费者消息：{},routeKey={}", body,routeKey);
    }*/

    /**
     * 发送消息
     * */
    public  static void sendMessage(String exchange,String routeKey,String message,String messageId) {
          if(StringUtils.isAnyBlank(exchange,routeKey)){
            log.info("exchange,routeKey isAnyBlank");
            return;

        }
        try {
            JSONObject object = JSONObject.parseObject(message);
            template.convertAndSend(exchange,routeKey+"."+serverName,object,msg -> {
                   msg.getMessageProperties().setMessageId(messageId);
                   msg.getMessageProperties().setExpiration("5000");
                   return msg;
            },new CorrelationData(messageId));//CorrelationData在消息发送异常时能取到发送消息

            if(!StringUtils.isEmpty(messageId)&&MessageFactory.containMessageEntry(messageId)){
                MessageEntry entry = MessageFactory.getMessageEntry(messageId);
                entry.setHandleResult(MessageResult.SENDING);
                entry.setRouteKey(routeKey);
            }

            if(message.length()<1000){
                log.debug("send message>>>{},{},{}",routeKey,messageId,message);
            }else{
                log.debug("send message>>>{},{},{}",routeKey,messageId,message.substring(0,1000));
            }
        }catch (Exception e){
            log.error("mq发送数据异常",e);
            return;
        }
    }

    @Value("${gms.server.name}")
    public void setServerName(String name) {
        serverName = name;
    }
}
