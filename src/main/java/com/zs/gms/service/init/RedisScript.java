package com.zs.gms.service.init;

import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisScript implements RedisListener {

    @Autowired
    @Lazy
    private SyncRedisData syncRedisData;

    @Override
    public void listener(String key) {
        String s = GmsUtil.subLastStr(key, "_");
        switch (s) {
            case "1"://同步车辆数据到redis
                syncRedisData.execute();
                break;
            default:
                log.debug("没有匹配脚本命令");
        }
    }

    public static RedisScript getInstance() {
        return Instance.redisScript;
    }

    private static class Instance {
        private final static RedisScript redisScript = new RedisScript();
    }
}
