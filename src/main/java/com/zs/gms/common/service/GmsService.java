package com.zs.gms.common.service;

import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.interfaces.MarkInterface;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class GmsService {

    /**
     * 前置处理，判断当前用户调用的方法是否被锁定
     */
    public static boolean preIntervalHandler(long interval, String methodName, Object... params) {
        String key_suffix = "none";
        if (params.length > 0) {
            key_suffix = String.valueOf(params[0]);
        } else {
            User user = getCurrentUser();
            if (user != null) {
                key_suffix = String.valueOf(user.getUserId());
            }
        }
        String key = RedisKeyPool.METHOD_INVOKE_INTERVAL_PREFIX + methodName + "_" + key_suffix;
        boolean intervalLock = RedisService.intervalLock(key, "interval", interval);
        return intervalLock;
    }


    /**
     * 后置处理
     */
    public static void afterIntervalHandler() {

    }

    /**
     * 获取当前登录用户
     */
    public static User getCurrentUser() {
        try {
            return (User) SecurityUtils.getSubject().getPrincipal();
        } catch (Exception e) {
            log.debug("获取当前登录用户异常");
        }
        return null;
    }

    /**
     * 返回前端数据
     */
    public static void callResponse(GmsResponse gmsResponse, HttpServletResponse response) {
        if(!response.isCommitted()){
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            PrintWriter writer = null;
            try {
                writer = response.getWriter();
                writer.print(GmsUtil.toJson(gmsResponse));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取监听库中的消息，带有枚举转换
     */
    public static <T> T getMessage(String key, Class<T> clazz) {
        Object json = RedisService.get(StaticConfig.MONITOR_DB, key);
        if (ObjectUtils.isEmpty(json))
            return null;
        return GmsUtil.toObjIEnum(json, clazz);
    }
}
