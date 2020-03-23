package com.zs.gms.service.init;

import com.zs.gms.common.entity.RedisKey;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.properties.GmsProperties;
import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.DateUtil;
import com.zs.gms.common.utils.IOUtil;
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
import java.util.ArrayList;
import java.util.List;
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
            RedisService.addConfig();
            DelayedService.addTask(this::dispatchInit, 3000).withDesc("调度初始化");
            DelayedService.addTask(this::codeCompress, 5000).withDesc("redis监控配置");
        } catch (Exception e) {
            log.error("redis start fail!", e);
            context.close();
        }
        if (context.isActive()) {
            systemInit.init();
            InetAddress address = InetAddress.getLocalHost();
            String url = String.format("http://%s:%s", address.getHostAddress(), port);
            String loginUrl = gmsProperties.getShiro().getLoginUrl();
            if (StringUtils.isNotBlank(loginUrl))
                url += loginUrl;
            log.info("系统启动完毕,地址:{}", url);
        }
    }

    /**
     * 初始化调度
     */
    public void dispatchInit() {
        RedisService.set(StaticConfig.MONITOR_DB, RedisKey.DISPATCH_SERVER_INIT, DateUtil.formatLongTime(System.currentTimeMillis()));
    }

    /**
     * 启动完成，执行代码压缩
     */
    public void codeCompress() {
        String system = System.getProperty("os.name");
        if(!system.startsWith("Win")){
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