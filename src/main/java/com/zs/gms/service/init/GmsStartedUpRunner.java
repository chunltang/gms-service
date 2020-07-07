package com.zs.gms.service.init;

import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.properties.GmsProperties;
import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.*;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.vehiclemanager.VehicleMaintainTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

@Component
@Slf4j
public class GmsStartedUpRunner implements ApplicationRunner {

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private GmsProperties gmsProperties;

    @Value("${server.port:8080}")
    private String port;

    @Value("${gms.server.name}")
    private String serverName;

    @Autowired
    private SystemInit systemInit;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //测试redis是否连接成功
        try {
            if(checkServiceUsed()){
                context.close();
                System.exit(0);
                return;
            }
        } catch (Exception e) {
            log.error("redis start fail!", e);
            context.close();
        }
        if (context.isActive()) {
            InetAddress address = InetAddress.getLocalHost();
            String url = GmsUtil.format("{}->http://{}:{}",serverName,address.getHostAddress(),port);
            String loginUrl = gmsProperties.getShiro().getLoginUrl();
            if (StringUtils.isNotBlank(loginUrl))
                url += loginUrl;
            log.info("系统启动完毕,地址:{}", url);
        }
        run();
    }

    /**
     * 延时初始化调用
     * */
    private void run() {
        RedisService.addConfig();
        DelayedService.addTask(this.systemInit::init, 500).withDesc("系统初始化");
        DelayedService.addTask(this::checkService, 1000).withDesc("执行心跳检测");
        DelayedService.addTask(this::loadMaintainTask, 1000).withDesc("加载维护任务");
        //DelayedService.addTask(this::checkPing, 3000).withDesc("判断服务ip是可用");
        DelayedService.addTask(this::serviceDiscover, 5000).withDesc("服务心跳").withNum(-1);
        DelayedService.addTask(this::dispatchInit, 3000).withDesc("调度初始化");
        DelayedService.addTask(MapDataUtil::syncMap, 3000).withDesc("同步地图基础信息");
        DelayedService.addTask(this::initDelayTask, 4000).withDesc("初始化延时任务");
        DelayedService.addTask(this::codeCompress, 5000).withDesc("执行代码压缩");
        DelayedService.addTask(MessageFactory::checkOver, 30000).withDesc("检测消息id过期!").withNum(-1);
    }

    /**
     * 初始化缓存中的延时任务
     * */
    private void initDelayTask(){
        Collection<String> keys = RedisService.getLikeKey(StaticConfig.KEEP_DB, RedisKeyPool.DELAY_TASK_PREFIX);
        if (GmsUtil.CollectionNotNull(keys)) {
            for (String key : keys) {
                Object json = RedisService.get(StaticConfig.KEEP_DB, key);
                DelayedService.Task task = GmsUtil.toObj(json, DelayedService.Task.class);
                if(null!=task&&task.getNum()==0){
                    RedisService.deleteKey(StaticConfig.KEEP_DB, RedisKeyPool.DELAY_TASK_PREFIX+task.getTaskId());
                    continue;
                }
                DelayedService.addInitializedTask(task,false);
            }
        }
    }

    /**
     * 加载维护任务
     * */
    public void loadMaintainTask() {
        VehicleMaintainTaskService maintainTaskService = SpringContextUtil.getBean(VehicleMaintainTaskService.class);
        maintainTaskService.loadMaintainTasks();
    }

    /**
     * 执行心跳检测
     */
    public void checkService() {
        HeartBeatCheck beatCheck = SpringContextUtil.getBean(HeartBeatCheck.class);
        beatCheck.run();
    }

    /**
     * 初始化调度
     */
    public void dispatchInit() {
        RedisService.set(StaticConfig.MONITOR_DB, RedisKeyPool.DISPATCH_SERVER_INIT, DateUtil.formatLongToString(System.currentTimeMillis()));
    }

    /**
     * 服务心跳
     * */
    public void serviceDiscover(){
        String key = RedisKeyPool.SERVICE_DISCOVER_PREFIX + serverName;
        RedisService.set(StaticConfig.KEEP_DB,key,String.valueOf(GmsUtil.getCurTime()),6000L, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查服务是否被占用
     * */
    private boolean checkServiceUsed(){
        String key = RedisKeyPool.SERVICE_DISCOVER_PREFIX + serverName;
        if(RedisService.existsKey(StaticConfig.KEEP_DB,key)){
            log.error("[{}]服务名称被占用!",serverName);
            return true;
        }
        return false;
    }

    /**
     * ping所有网路服务
     * */
    private void checkPing(){
        String property = System.getProperty("os.name");
        if(!property.startsWith("Win")){
            Map<String, String> services = gmsProperties.getServices();
            DelayedService.addTask(()->HeartBeatCheck.checkPing(services,1), 3000).withDesc("判断服务ip是可用").withNum(-1);

        }
    }

    /**
     * 启动完成，执行代码压缩
     */
    public void codeCompress() {
        String system = System.getProperty("os.name");
        if (!system.startsWith("Win")) {
            return;
        }
        String property = System.getProperty("user.dir");
        File dir = new File(property);
        List<File> files = new ArrayList<>();
        IOUtil.listFiles(dir, files, "target", "log");
        try (FileOutputStream fos = new FileOutputStream(new File(dir.getParentFile().getPath() + "/gms-service.zip"));
             ZipOutputStream zos = new ZipOutputStream(fos, Charset.forName("utf-8"))) {
            IOUtil.fileCompress(files, zos, property);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}