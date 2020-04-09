package com.zs.gms.common.message;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.Message;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.message.impl.DispatchMessage;
import com.zs.gms.common.message.impl.MapMessage;
import com.zs.gms.common.message.impl.VapMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MessageFactory {



    private static Map<String,MessageEntry> messageEntryMap = new ConcurrentHashMap<>();

    /**
     * 获取消息实体
     * */
    public static MessageEntry getMessageEntry(String messageId){
        if(messageEntryMap.containsKey(messageId)){
            return messageEntryMap.get(messageId);
        }
        return null;
    }

    /**
     * 根据HttpServletResponse获取实体
     * */
    public static MessageEntry getMessageEntry(HttpServletResponse response){
        if(response==null){
            return null;
        }
        for (MessageEntry entry : messageEntryMap.values()) {
            Message message = entry.getMessage();
            if(null!=message){
                HttpServletResponse httpResponse = message.getHttpResponse();
                if(response.equals(httpResponse)){
                    return entry;
                }
            }
        }
        return null;
    }

    /**
     * 删除消息实体
     * */
    private static void delMessageEntry(String messageId){
        messageEntryMap.remove(messageId);
    }

    /**
     * 是否包含消息实体
     * */
    public static boolean containMessageEntry(String messageId){
        return messageEntryMap.containsKey(messageId);
    }


    /**
     * 生成消息实体
     * */
    public static  MessageEntry createMessageEntry(String prefix){
        MessageEntry entry=new MessageEntry();
        String id = messageIdGenerator(prefix);
        entry.setMessageId(id);
        entry.setBTime(System.currentTimeMillis());
        messageEntryMap.put(id,entry);
        return entry;
    }

    /**
     * 生成消息id
     */
    public static String messageIdGenerator(String prefix) {
        messageId.incrementAndGet();
        return prefix + "_" + messageId.get();
    }

    /**
     * 传入id生成实体
     * */
    public static MessageEntry getMessageEntryById(String id){
        MessageEntry entry=new MessageEntry();
        entry.setMessageId(id);
        entry.setBTime(System.currentTimeMillis());
        messageEntryMap.put(id,entry);
        return entry;
    }

    public static MessageEntry getApproveEntry(Integer id){
        return getMessageEntryById(GmsConstant.APPROVE+"_"+id);
    }

    /**
     * 检查数据是否已过期
     * */
    public static void checkOver(){
        List<MessageEntry> entries = new ArrayList<>(messageEntryMap.values());
        for (MessageEntry entry : entries) {
            long bTime = entry.getBTime();
            if(System.currentTimeMillis()-bTime> GmsConstant.WAIT_TIME){
                entry.exec();
                delMessageEntry(entry.getMessageId());
            }
        }
    }

    /**
     * 消息id
     */
    private static AtomicLong messageId = new AtomicLong(100000L);

    public static MessageInterface getMapMessage(){
        return new MapMessage(GmsConstant.MAP, StaticConfig.bMRequest);
    }

    public static MessageInterface getDispatchMessage(){
        return new DispatchMessage(GmsConstant.DISPATCH,StaticConfig.bDRequest);
    }

    public static MessageInterface getVapMessage(){
        return new VapMessage(GmsConstant.VAP,StaticConfig.bVRequest);
    }

}
