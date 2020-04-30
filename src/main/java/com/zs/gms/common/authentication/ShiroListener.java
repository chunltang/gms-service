package com.zs.gms.common.authentication;

import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class ShiroListener implements SessionListener {

    private AtomicInteger sessionCount = new AtomicInteger();

    /**
     * 有新会话时触发
     */
    @Override
    public void onStart(Session session) {
        sessionCount.incrementAndGet();
        log.info("有新会话接入,count={}", getSessionCount());
    }

    /**
     * 有会话结束时触发
     */
    @Override
    public void onStop(Session session) {
        sessionCount.decrementAndGet();
        log.info("有会话结束,count={}", getSessionCount());
    }

    /**
     * 会话过期时触发
     */
    @Override
    public void onExpiration(Session session) {
        sessionCount.decrementAndGet();
        log.info("有会话过期,count={}", getSessionCount());
    }

    public Integer getSessionCount() {
        return sessionCount.get();
    }
}
