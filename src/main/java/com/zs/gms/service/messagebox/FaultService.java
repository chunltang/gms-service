package com.zs.gms.service.messagebox;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.messagebox.Fault;

public interface FaultService {

    /**
     * 添加故障信息
     * */
    public void addFault(Fault fault);

    /**
     * 根据id修改
     * */
    public void updateFaultById(Fault fault);

    /**
     * 获取分页结果
     * */
    public IPage<Fault> getFaultListPage(QueryRequest request);

    /**
     * 修改故障处理进度
     * */
    public void updateFaultStatus(Integer faultId,Fault.Status status);
}
