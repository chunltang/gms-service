package com.zs.gms.common.configure;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

//@Configuration
@Slf4j
public class ScheduleConfig {

    @Autowired
    private  DynamicRoutingDataSource dataSource;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(){
        Properties prop=new Properties();
        try {
            prop.load(this.getClass().getResourceAsStream("/quartz_bak.properties"));
        } catch (IOException e) {
            log.error("quartz.properties文件不存在",e);
        }
        SchedulerFactoryBean factoryBean=new SchedulerFactoryBean();
        DataSource quartz = dataSource.getDataSource("quartz");
        factoryBean.setQuartzProperties(prop);
        factoryBean.setDataSource(quartz);
        factoryBean.setAutoStartup(true);
        factoryBean.setApplicationContextSchedulerContextKey("applicationContextKey");
        factoryBean.setStartupDelay(1);
        factoryBean.setOverwriteExistingJobs(true);
        return factoryBean;
    }
}
