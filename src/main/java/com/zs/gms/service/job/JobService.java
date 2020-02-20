package com.zs.gms.service.job;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.job.Job;

public interface JobService extends IService<Job> {

    public void init();
}
