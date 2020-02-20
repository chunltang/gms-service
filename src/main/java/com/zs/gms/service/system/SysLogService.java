package com.zs.gms.service.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.system.SysLog;

public interface SysLogService {

    public void addSysLog(SysLog log);

    public IPage<SysLog> getSysLogListPage(SysLog sysLog, QueryRequest request);
}
