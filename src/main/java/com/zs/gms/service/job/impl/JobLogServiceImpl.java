package com.zs.gms.service.job.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.job.JobLog;
import com.zs.gms.mapper.job.JobLogMapper;
import com.zs.gms.service.job.JobLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLog> implements JobLogService {

    @Override
    @Transactional
    public void saveJobLog(JobLog jobLog){
        this.save(jobLog);
    }
}
