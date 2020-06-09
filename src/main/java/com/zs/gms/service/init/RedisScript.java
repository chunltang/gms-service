package com.zs.gms.service.init;

import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.monitor.UnitService;
import io.lettuce.core.resource.Delay;
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
            case "0"://修改活动地图后删除调度数据
                UnitService unitService = SpringContextUtil.getBean(UnitService.class);
                unitService.clearUnitSAndVehicles(MapDataUtil.getActiveMap());
                break;
            case "1"://同步车辆数据到redis
                syncRedisData.execute();
                break;
            case "2"://延时定时任务不打印日志
                DelayedService.changePrintFlag(false);
                break;
            case "3"://延时定时任务打印日志
                DelayedService.changePrintFlag(true);
                break;
            case "4"://清除所有车辆缓存数据
                RedisService.deleteLikeKey(StaticConfig.KEEP_DB, "getUserIdByVehicleNo");
                break;
            case "5"://清除模糊键
                Object likeKey = RedisService.get(StaticConfig.MONITOR_DB, key);
                if (GmsUtil.objNotNull(likeKey) && GmsUtil.StringNotNull(String.valueOf(likeKey))) {
                    RedisService.deleteLikeKey(StaticConfig.KEEP_DB, String.valueOf(likeKey));
                }
            case "10"://开始地图采集
                Object obj = RedisService.get(StaticConfig.MONITOR_DB, key);
                if (null != obj) {
                    Integer vehicleId = GmsUtil.typeTransform(obj, Integer.class);
                    StaticConfig.isStartMapCollection = true;
                }
                break;
            case "11"://关闭地图采集
                StaticConfig.isStartMapCollection = false;
                break;
            default:
                log.debug("没有匹配脚本命令");
        }
    }

    public static RedisScript getInstance() {
        return SpringContextUtil.getBean(RedisScript.class);
    }
}
