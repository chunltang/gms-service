package com.zs.gms.service.init;

import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.nettyclient.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class HeartBeatCheck {

    private final static long INTERVAL = 35 * 1000;

    private static String dispatch_pre_Time;

    private static String map_pre_Time;

    public static long redis_monitor__last_time = System.currentTimeMillis();//重置监听时间

    private static Map<CheckStatusEnum, Long> statusMap = new HashMap<>();

    private boolean flag = false;//判断一轮检查是否结束

    static {
        long curLongTime = getCurLongTime() - 20 * 1000;
        for (CheckStatusEnum anEnum : CheckStatusEnum.values()) {
            statusMap.put(anEnum, curLongTime);
        }
    }

    @Autowired
    @Lazy
    private DataSource mysqlFactory;

    @Autowired
    @Lazy
    private ConnectionFactory rabbitMqFactory;

    public void run() {
        check();
        heartBeat();
    }

    /**
     * 执行数据初始化检测
     */
    private void check() {
        this.flag = false;
        checkRedis();
        checkRabbitMq();
        checkMysql();
        checkDispatch();
        checkMap();
        this.flag = true;
    }

    /**
     * 检测心跳,30秒，延时2秒检测线程是否阻塞
     */
    private void heartBeat() {
        DelayedService.Task task = DelayedService.buildTask()
                .withAtOnce(true)
                .withNum(-1)
                .withDesc("业务层心跳检测")
                .withTask(() -> {
                    Thread thread = Thread.currentThread();
                    DelayedService.addTask(() -> {
                        try {
                            HeartBeatCheck.checkResult();
                            if (!getFlag()) {//2秒后执行检查未完成，表示出现异常，则打断线程运行
                                while (!thread.isInterrupted()) {
                                    thread.interrupt();
                                }
                            }
                        } catch (Exception e) {
                            log.error("检测心跳失败");
                        }
                    }, 2000).withDesc("推送心跳结果");

                    try {
                        check();
                    } catch (Exception e) {
                        log.error("检测心跳异常");
                    }
                });
        DelayedService.addTask(task, HeartBeatCheck.INTERVAL-5);
    }

    private static long getCurLongTime() {
        return System.currentTimeMillis();
    }

    private boolean getFlag() {
        return flag;
    }

    private static void checkResult() {
        Map<String, Boolean> errorMessage = getErrorMessage();
        String toJson = GmsUtil.toJson(errorMessage);
        if(errorMessage.values().contains(false)){
            log.error("心跳检测异常结果:{}", toJson);
        }
        WsUtil.sendMessage(toJson, FunctionEnum.checkServer);
    }

    private static void updateStatus(CheckStatusEnum statusEnum, boolean checkTime) {
        if (checkTime) {
            statusMap.put(statusEnum, getCurLongTime());
        }
    }

    /**
     * true对应服务断开连接
     */
    public static boolean getServiceStatus(CheckStatusEnum statusEnum) {
        Long aLong = statusMap.getOrDefault(statusEnum, 0L);
        return getCurLongTime() - aLong > INTERVAL;
    }

    private static Map<String, Boolean> getErrorMessage() {
        Set<Map.Entry<CheckStatusEnum, Long>> entries = statusMap.entrySet();
        Map<String, Boolean> result = new HashMap<>();
        for (Map.Entry<CheckStatusEnum, Long> entry : entries) {
            Long value = entry.getValue();
            boolean flag = true;
            if (getCurLongTime() - value > INTERVAL) {
                flag = false;
            }
            result.put(entry.getKey().getErrorDesc(), flag);
        }
        return result;
    }


    /**
     * mysql检测
     */
    private void checkMysql() {
        boolean result;
        try {
            Connection connection = mysqlFactory.getConnection();
            result = !connection.isClosed();
            connection.close();
        } catch (SQLException e) {
            result = false;
        }
        updateStatus(CheckStatusEnum.MYSQL_SERVICE_STATUS, result);
    }

    /**
     * 调度检测
     */
    private void checkDispatch() {
        boolean result;
        try {
            Object dispatchValue = RedisService.get(StaticConfig.KEEP_DB, RedisKeyPool.DISPATCH_SERVER_HEARTBEAT);
            if (null == dispatchValue || dispatchValue.toString().equals(dispatch_pre_Time)) {
                result = false;
            } else {
                dispatch_pre_Time = dispatchValue.toString();
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        updateStatus(CheckStatusEnum.DISPATCH_SERVICE_STATUS, result);
    }

    /**
     * 地图检测
     */
    private void checkMap() {
        boolean result;
        try {
            Object mapValue = RedisService.get(StaticConfig.KEEP_DB, RedisKeyPool.MAP_SERVER_HEARTBEAT);
            if (null == mapValue || mapValue.toString().equals(map_pre_Time)) {
                result = false;
            } else {
                map_pre_Time = mapValue.toString();
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        updateStatus(CheckStatusEnum.MAP_SERVICE_STATUS, result);
    }

    /**
     * redis监听程序检测
     */
    private void checkRedis() {
        boolean result = true;
        try {
            long timeMillis = getCurLongTime();
            if (timeMillis - redis_monitor__last_time > INTERVAL) {//重启redis监听服务，毫秒
                log.info("重启redis监听服务");
                RedisService.addConfig();
                RedisMessageListenerContainer bean = SpringContextUtil.getBean(RedisMessageListenerContainer.class);
                bean.stop();
                bean.start();
            }
            RedisService.set(StaticConfig.MONITOR_DB, RedisKeyPool.REDIS_MONITOR, String.valueOf(timeMillis));
        } catch (Exception e) {
            log.debug("redis监听程序检测异常");
            result = false;
        }
        updateStatus(CheckStatusEnum.REDIS_SERVICE_STATUS, result);
    }

    /**
     * mq检测
     */
    private void checkRabbitMq() {
        boolean result;
        try {
            org.springframework.amqp.rabbit.connection.Connection connection = rabbitMqFactory.createConnection();
            result = connection.isOpen();
        } catch (Exception e) {
            result = false;
        }
        updateStatus(CheckStatusEnum.MQ_SERVICE_STATUS, result);
    }

    public enum CheckStatusEnum {

        DISPATCH_SERVICE_STATUS("dispatch_service_status", "调度服务"),
        MAP_SERVICE_STATUS("map_service_status", "地图服务"),
        REDIS_SERVICE_STATUS("redis_service_status", "redis服务"),
        MQ_SERVICE_STATUS("mq_service_status", "rabbitmq服务"),
        MYSQL_SERVICE_STATUS("mysql_service_status", "mysql服务");

        private String key;

        private String errorDesc;

        CheckStatusEnum(String key, String errorDesc) {
            this.key = key;
            this.errorDesc = errorDesc;
        }

        public String getKey() {
            return this.key;
        }

        public String getErrorDesc() {
            return this.errorDesc;
        }
    }

}
