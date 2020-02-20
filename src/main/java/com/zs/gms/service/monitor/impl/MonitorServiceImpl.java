package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.monitor.Monitor;
import com.zs.gms.mapper.monitor.MonitorMapper;
import com.zs.gms.service.monitor.MonitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class MonitorServiceImpl extends ServiceImpl<MonitorMapper,Monitor> implements MonitorService {

    @Override
    @Transactional
    public void addMonitor(Monitor monitor) {
       this.save(monitor);
    }
}
