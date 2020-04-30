package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.vehiclemanager.Excavator;

import java.util.List;

public interface ExcavatorService {

    void addExcavator(Excavator excavator);

    IPage getExcavatorList(Excavator excavator, QueryRequest queryRequest);

    List<Excavator> getExcavators();

    void delExcavator(Integer excavatorId);

    void updateExcavator(Excavator excavator);

    Excavator getExcavatorByNo(Integer excavatorNo);

    boolean isExistNo(Integer excavatorNo);

    boolean isExistId(Integer excavatorId);
}
