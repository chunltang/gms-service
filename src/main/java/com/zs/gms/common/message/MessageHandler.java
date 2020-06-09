package com.zs.gms.common.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.MessageEvent;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.properties.ErrorCode;
import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 监听事件处理
 */
@Component
@Slf4j
public class MessageHandler {

    private final static String RESPONSE_STATUS_FIELD = "status";

    private final static String RESPONSE_MESSAGE_FIELD = "message";

    private final static String RESPONSE_DATA_FIELD = "data";

    private final static String SUCCESS_DESC = "成功";

    private final static String FAIL_DESC = "失败";

    @Autowired
    private ErrorCode errorCode;

    /**
     * 有数据返回的情况下使用
     */
    @EventListener
    @Async(value = "gmsAsyncThreadPool")
    public void handleMqResult(MessageEvent event) throws GmsException {
        String messageId = event.getMessageId();
        String message = event.getMessage();
        EventType eventType = event.getEventType();
        MessageEntry entry = MessageFactory.getMessageEntry(messageId);
        if (entry == null) {
            return;
        }
        log.debug("执行发布事件eventType={},routeKey={},messageId={}",eventType.name(),entry.getRouteKey(),entry.getMessageId());
        try {
            switch (eventType) {
                case httpMq:
                    handleHttpResponse(message, entry);
                    break;
                case notHttpMq:
                    entry.setHttp(false);
                    handleResult(message, entry);
                    break;
                case httpRedis:
                    handleRedisToHttp(message, entry);
                    break;
            }
        } finally {
            entry.eventNotify(eventType);
        }
    }

    private void handleRedisToHttp(String message, MessageEntry entry) {
        GmsResponse gmsResponse = entry.getMessage().getGmsResponse();
        gmsResponse.data(message);
    }

    private void handleHttpResponse(String message, MessageEntry entry) {//处理前端响应
        HttpServletResponse response = entry.getMessage().getHttpResponse();
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
        GmsResponse gmsResponse = entry.getMessage().getGmsResponse();
        String resultInfo = (String) gmsResponse.getMessage();
        if (!StringUtils.isEmpty(message)) {
            JSONObject jsonObject = JSONObject.parseObject(message);
            if (jsonObject.containsKey(RESPONSE_STATUS_FIELD)) {
                String status = jsonObject.getString(RESPONSE_STATUS_FIELD);
                jsonObject.remove(RESPONSE_STATUS_FIELD);
                MessageResult mr = entry.getHandleResult();
                handleResultData(jsonObject,entry);
                switch (status) {
                    case "0"://成功
                        if (mr.equals(MessageResult.RESPONSE_EXPIRE)) {
                            entry.setHandleResult(MessageResult.AFTER_SUCCESS);
                        } else {
                            entry.setHandleResult(MessageResult.SUCCESS);
                        }
                        //描述为空，则设置为业务层的输入
                        if (!GmsUtil.StringNotNull(gmsResponse.getMessage())) {
                            gmsResponse.message(resultInfo);
                        }
                        break;
                    default:
                        gmsResponse.badRequest();
                        //调度状态码处理
                        if (entry.getMessageId().startsWith(GmsConstant.DISPATCH)) {
                            dispatchErrorHandler(gmsResponse, status);
                        }
                        //描述为空，则设置为业务层的输入
                        if (!GmsUtil.StringNotNull(gmsResponse.getMessage())) {
                            gmsResponse.message(resultInfo.replaceAll(SUCCESS_DESC, FAIL_DESC));
                        }
                        //设置失败状态
                        if (mr.equals(MessageResult.RESPONSE_EXPIRE)) {
                            entry.setHandleResult(MessageResult.AFTER_FAIL);
                        } else {
                            entry.setHandleResult(MessageResult.FAIL);
                        }
                        break;
                }
            } else {
                entry.setHandleResult(MessageResult.NO_STATUS);
                log.debug("没有处理结果标志位status,messageId={}", entry.getMessageId());
            }
        }
    }

    /**
     * 处理返回数据
     * */
    private void handleResultData(JSONObject jsonObject, MessageEntry entry) {
        if (!jsonObject.isEmpty() && jsonObject.containsKey(RESPONSE_MESSAGE_FIELD)) {//有返回数据
            String returnData = jsonObject.getString(RESPONSE_MESSAGE_FIELD);
            GmsResponse gmsResponse = entry.getMessage().getGmsResponse();
            if (!StringUtils.isEmpty(returnData) && JSON.isValidObject(returnData)) {
                try {
                    JSONObject json = JSONObject.parseObject(returnData);
                    if (json.containsKey(RESPONSE_DATA_FIELD)) {
                        JSONObject jsonObj = json.getJSONObject(RESPONSE_DATA_FIELD);
                        entry.setReturnData(jsonObj.toJSONString());
                        gmsResponse.data(jsonObj);
                    }
                } catch (Exception e) {
                    log.error("非JSON格式数据");
                    gmsResponse.message(returnData);
                }
            } else {
                gmsResponse.message(returnData);
            }
        }
    }

    private void handleResult(String message, MessageEntry entry) {//处理非前端请求结果
        JSONObject jsonObject = JSONObject.parseObject(message);
        if (!jsonObject.isEmpty() && jsonObject.containsKey(RESPONSE_MESSAGE_FIELD)) {//有返回数据
            String returnData = jsonObject.getString(RESPONSE_MESSAGE_FIELD);
            if (!StringUtils.isEmpty(returnData)) {
                try {
                    JSONObject json = JSONObject.parseObject(returnData);
                    if (json.containsKey(RESPONSE_DATA_FIELD)) {
                        Object data = json.get(RESPONSE_DATA_FIELD);
                        entry.setReturnData(JSON.toJSONString(data));
                    }
                } catch (Exception e) {
                    log.error("非JSON格式数据");
                }
            }
        }
        if (jsonObject.containsKey(RESPONSE_STATUS_FIELD)) {
            String status = jsonObject.getString(RESPONSE_STATUS_FIELD);
            switch (status) {
                case "0":
                    entry.setHandleResult(MessageResult.SUCCESS);
                    break;
                default:
                    entry.setHandleResult(MessageResult.FAIL);
                    break;
            }

        } else {
            entry.setHandleResult(MessageResult.NO_STATUS);
            log.debug("没有处理结果标志位status,messageId={}", entry.getMessageId());
        }
    }

    /**
     * 处理异常返回
     *
     * @param status 错误码
     */
    private void dispatchErrorHandler(GmsResponse response, String status) {
        Map<String, String> dispatch = errorCode.getDispatch();
        if (dispatch.containsKey(status)) {
            response.message(dispatch.get(status));
        }
    }
}
