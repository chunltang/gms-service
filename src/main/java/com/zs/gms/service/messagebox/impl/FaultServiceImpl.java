package com.zs.gms.service.messagebox.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.messagebox.Fault;
import com.zs.gms.mapper.messagebox.FaultMapper;
import com.zs.gms.service.messagebox.FaultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class FaultServiceImpl extends ServiceImpl<FaultMapper, Fault> implements FaultService {

    @Override
    @Transactional
    public void addFault(Fault fault) {
        fault.setCreateTime(new Date());
        this.save(fault);
    }

    @Override
    @Transactional
    public void updateFaultById(Fault fault) {
        if(fault.getFaultId()==null){
            return;
        }
        LambdaQueryWrapper<Fault> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Fault::getFaultDesc,"");
        this.updateById(fault);
    }

    @Override
    @Transactional
    public IPage<Fault> getFaultListPage(QueryRequest request) {
        Page<Fault> page=new Page();
        SortUtil.handlePageSort(request,page, GmsConstant.SORT_DESC,"FAULTID");
        return this.page(page);
    }

    @Override
    @Transactional
    public void updateFaultStatus(Integer faultId,Fault.Status status) {
        Fault fault=new Fault();
        fault.setFaultId(faultId);
        fault.setStatus(status);
        if(status.equals(Fault.Status.PROCESSED)){//添加处理结束时间
            fault.setHandleTime(new Date());
        }
        this.updateById(fault);
    }
}
