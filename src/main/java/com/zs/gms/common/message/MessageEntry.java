package com.zs.gms.common.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zs.gms.common.entity.Message;
import com.zs.gms.common.interfaces.ResponseCallBack;
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
    private Message message;     //保存前端HttpServletResponse和数据
    private List<ResponseCallBack> Handles = new ArrayList<>();
    private String returnData;   //返回数据
    private boolean isHttp = true; //是否是http请求
    private final Set<EventType> eventTypes=new HashSet<>();//外部触发程序的类型
    private int collect=1; //几个外部触发程序
    private  boolean isEnd=false; //任务是否已结束
    private long bTime; //任务开始时间
    private String routeKey;

    public void setAfterHandle(ResponseCallBack callBack) {
        Handles.add(callBack);
    }

    /**
     * 最后处理http响应
     * */
    private void execLastHttpHandle() {
        writeResult(this);
    }

    /**
     * 事件完成通知
     * */
    public void eventNotify(EventType eventType){
        eventTypes.add(eventType);
        synchronized (eventTypes){
            if(null!=handleResult &&
                    !handleResult.equals(MessageResult.SUCCESS)){
                exec();//执行失败
                return;
            }
            if(!isEnd && eventTypes.size()==collect){
                exec();
            }
        }
    }

    /**
     * 执行最后的处理
     * */
    public void exec() {
        if(callback()){
            if (isHttp) {
                execLastHttpHandle();
            }
        }
    }

    boolean callback(){
        synchronized (messageId){
            if(!isEnd) {
                isEnd = true;
                for (ResponseCallBack handle : Handles) {
                    handle.afterHandler();
                }
                return true;
            }
            return false;
        }
    }

    private void writeResult(MessageEntry entry) {
        ObjectMapper mapper = new ObjectMapper();
        HttpServletResponse response = entry.getMessage().getHttpResponse();
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