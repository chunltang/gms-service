package com.zs.gms.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.system.SysLog;
import com.zs.gms.mapper.system.SysLogMapper;
import com.zs.gms.service.system.SysLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    /**
     * 添加日志
     * */
    @Override
    @Transactional
    @Async(value = "gmsAsyncThreadPool")
    public void addSysLog(SysLog log) {
        log.setOperateTime(new Date());
        this.save(log);
    }

    /**
     * 查询日志,根据用户id，开始时间、结束时间查
     * */
    @Override
    @Transactional
    public IPage<SysLog> getSysLogListPage(SysLog sysLog,QueryRequest request){
        Page page=new Page();
        page.setSize(request.getPageSize());
        page.setCurrent(request.getPageNo());
        LambdaQueryWrapper<SysLog> queryWrapper = new LambdaQueryWrapper<SysLog>();
        queryWrapper.eq(Objects.nonNull(sysLog.getUserId()),SysLog::getUserId, sysLog.getUserId());
        queryWrapper.gt(Objects.nonNull(sysLog.getBeginTime()),SysLog::getOperateTime,sysLog.getBeginTime());
        queryWrapper.lt(Objects.nonNull(sysLog.getEndTime()),SysLog::getOperateTime,sysLog.getEndTime());
        queryWrapper.orderByDesc(SysLog::getOperateTime);
        return this.page(page,queryWrapper);
    }
}
