package com.zs.gms.service.job;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.job.JobLog;

public interface JobLogService extends IService<JobLog> {

    public void saveJobLog(JobLog jobLog);
}
