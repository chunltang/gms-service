package com.zs.gms.service.job.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.job.Job;
import com.zs.gms.mapper.job.JobMapper;
import com.zs.gms.service.job.JobService;
import com.zs.gms.service.job.util.ScheduleUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

    @Autowired
    private Scheduler scheduler;


    /**
     * 项目启动时，初始化定时器
     */
    //@PostConstruct
    @Transactional
    public void init() {
        List<Job> scheduleJobList = this.list();
        // 如果不存在，则创建
        scheduleJobList.forEach(scheduleJob -> {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        });
    }
}
