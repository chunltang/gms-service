package com.zs.gms.common.configure;

import com.rabbitmq.client.Channel;
import com.zs.gms.common.properties.GmsProperties;
import com.zs.gms.common.service.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Configuration
@Slf4j
public class RabbitMqConfig {

    @Autowired
    private GmsProperties gmsProperties;

    @Value("${gms.server.name}")
    private  String serverName;

    @Bean
    @Primary
    public ConnectionFactory createFactory(RabbitProperties properties){
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setVirtualHost(properties.getVirtualHost());
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());
        factory.setHost(properties.getHost());
        factory.setPort(properties.getPort());
        factory.setPublisherConfirms(true);
        factory.setPublisherReturns(true);
        return factory;
    }

    @Bean
    @Primary
    public RabbitAdmin createRabbitAdmin(ConnectionFactory factory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(factory);
        rabbitAdmin.setAutoStartup(true);//程序启动的时候自动创建这些队列等
        return rabbitAdmin;
    }


    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new ConfirmCallBackListener());
        rabbitTemplate.setReturnCallback(new ReturnCallBackListener());
        return rabbitTemplate;
    }

    @Bean(name = "containerFactory")
    @Primary
    public SimpleRabbitListenerContainerFactory createContainerFacotry(ConnectionFactory factory){
        SimpleRabbitListenerContainerFactory containerFactory=new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(factory);
        containerFactory.setMessageConverter(new Jackson2JsonMessageConverter());
        return containerFactory;
    }

    /**
     * 消息监听容器,可以设置多个消费者参数
     * */
    @Bean
    @Primary
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory factory){
        Map<String, String> monitorQueuesMap = gmsProperties.getMonitorQueues();
        String[] monitorQueues = monitorQueuesMap.values().toArray(new String[monitorQueuesMap.size()]);
        String[] queues=Arrays.stream(monitorQueues).map(qs -> {
            return qs + "_" + serverName;
        }).toArray(String[]::new);
        SimpleMessageListenerContainer messageContainer=new SimpleMessageListenerContainer(factory);
        messageContainer.setQueueNames(queues);
        messageContainer.setConcurrentConsumers(1);
        messageContainer.setMaxConcurrentConsumers(10);
        messageContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        messageContainer.setExposeListenerChannel(true);
        messageContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {
            @Override
            public String createConsumerTag(String queue) {
                return queue+ UUID.randomUUID().toString();
            }
        });
        messageContainer.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                MessageUtil.handleMqMessage(message,channel);
            }
        });
        return messageContainer;
    }

    public class ConfirmCallBackListener implements RabbitTemplate.ConfirmCallback{
        /**
         * 消息确认
         * */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            Message message = correlationData.getReturnedMessage();
            if(ack&&null!=message){
                log.error("----------mq消息失败退回，队列不存在------------");
                String s = new String(message.getBody());
                log.error("回退消息:{}",s);
            }else if(ack){
                log.debug("----------mq消息发送成功------------");
            }else if(!ack){
                log.error("----------mq消息发送失败------------");
            }
        }
    }

    public class ReturnCallBackListener implements RabbitTemplate.ReturnCallback{

        /**
         * 消息失败退回
         * */
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            log.error("-----------mq消息被拒收-----------");
        }
    }
}
