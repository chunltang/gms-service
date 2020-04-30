package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.vehiclemanager.Excavator;
import com.zs.gms.entity.vehiclemanager.ExcavatorType;

import java.util.List;

public interface ExcavatorTypeService {

    void addExcavatorType(ExcavatorType excavatorType);

    IPage<ExcavatorType> getExcavatorTypeListPage(QueryRequest queryRequest);

    void deleteExcavatorType(Integer excavatorTypeId);

    void updateExcavatorType(ExcavatorType excavatorType);

    boolean isExistName(String excavatorTypeName);

    boolean isExistName(Integer excavatorTypeId,String excavatorTypeName);

    boolean isExistTypeId(Integer excavatorTypeId);
}
