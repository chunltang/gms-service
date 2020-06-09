package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.entity.vehiclemanager.Excavator;

import java.util.List;

public interface ExcavatorService {

    void addExcavator(Excavator excavator);

    void updateExcavatorTypeActive();

    /**
     * 改变挖掘机的激活状态
     * */
    void updateExcavatorActive();

    IPage getExcavatorList(Excavator excavator, QueryRequest queryRequest);

    List<Excavator> getExcavators();

    void delExcavator(Integer excavatorId);

    void updateExcavator(Excavator excavator);

    Excavator getExcavatorByNo(Integer excavatorNo);

    boolean isExistNo(Integer excavatorNo);

    boolean isExistId(Integer excavatorId);

    Excavator getExcavatorById(Integer excavatorId);
}
