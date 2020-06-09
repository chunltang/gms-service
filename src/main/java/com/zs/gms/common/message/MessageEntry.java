package com.zs.gms.common.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zs.gms.common.entity.Message;
import com.zs.gms.common.entity.MessageEvent;
import com.zs.gms.common.interfaces.ResponseCallBack;
import com.zs.gms.common.utils.GmsUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Data
@Slf4j
public class MessageEntry {
    private  String messageId;    //消息id
    private MessageResult handleResult; //处理结果
    @JsonIgnore
    private Message message;     //保存前端HttpServletResponse和数据
    @JsonIgnore
    private List<ResponseCallBack> handles = new ArrayList<>();
    @JsonIgnore
    private String returnData;   //返回数据
    private boolean isHttp = true; //是否是http请求
    private final Set<EventType> eventTypes=new HashSet<>();//外部触发程序的类型
    private int collect=1; //几个外部触发程序
    private  boolean isEnd=false; //任务是否已结束
    private long bTime; //任务开始时间
    private String routeKey;
    private Map<String,Object> params=new HashMap<>(2);//实体携带的数据
    public void setAfterHandle(ResponseCallBack callBack) {
        handles.add(callBack);
    }

    /**
     * 最后处理http响应
     * */
    private void execLastHttpHandle() {
        //log.debug("最后处理http响应,routeKey={},messageId={}",routeKey,messageId);
        writeResult(this);
    }

    /**
     * 事件完成通知
     * */
    public void eventNotify(EventType eventType){
        eventTypes.add(eventType);
        synchronized (this){
            if(!isEnd && eventTypes.size()==collect){
                //log.debug("实体事件完成通知,routeKey={},messageId={}",routeKey,messageId);
                exec();
            }
        }
    }

    /**
     * 执行最后的处理
     * */
    public void exec() {
        if(callback()){
            //log.debug("实体事件执行完成后置处理,routeKey={},messageId={},entry={}",routeKey,messageId, GmsUtil.toJson(this));
            if (isHttp) {
                execLastHttpHandle();
            }
        }
    }

    boolean callback(){
        synchronized (this){
            if(!isEnd) {
                isEnd = true;
                for (ResponseCallBack handle : handles) {
                    handle.afterHandler();
                }
                return true;
            }
            return false;
        }
    }

    private void writeResult(MessageEntry entry) {
        ObjectMapper mapper = new ObjectMapper();
        Message message = entry.getMessage();
        if(null!=message){
            HttpServletResponse response =message.getHttpResponse();
            String valueAsString = null;
            try(PrintWriter writer = response.getWriter()) {
                valueAsString = mapper.writeValueAsString(entry.getMessage().getGmsResponse());
                writer.print(valueAsString);
            } catch (IOException e) {
                log.error("发送数据失败", e);
            } finally {
                synchronized (response) {
                    response.notifyAll();
                }
            }
        }
    }

    public static Build build(String prefix){
        return new Build(MessageFactory.createMessageEntry(prefix));
    }

    @Data
    public static class Build{

        private MessageEntry entry;

        private Build(MessageEntry entry){
            this.entry=entry;
        }

        public Build setHttp(boolean isHttp){
            entry.setHttp(isHttp);
            return this;
        }

        public Build setMessage(Message message){
            entry.setMessage(message);
            return this;
        }

        public Build setMessageId(String messageId){
            entry.setMessageId(messageId);
            return this;
        }

        public Build setHandles(ResponseCallBack callBack){
            entry.setAfterHandle(callBack);
            return this;
        }

        public Build setRouteKey(String routeKey){
            entry.setRouteKey(routeKey);
            return this;
        }

        public Build setCollect(int collect){
            entry.setCollect(collect);
            return this;
        }
    }
}