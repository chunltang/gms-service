package com.zs.gms.service.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.monitor.Unit;

import java.util.List;

public interface UnitService {

    void addUnit(Unit unit);

    /**
     * 判断用户是否已分配调度单元
     * */
    boolean isExistUser(Integer userId);

    /**
     * 根据调度员id获取调度单元
     * */
    List<Unit> getUnitListByUserId(Integer userId);

    /**
     * 判断名字是否存在
     * */
    boolean isExistName(String unitName);

    boolean isExistName(String unitName,Integer unitId);

    /**
     * 判断id是否存在
     * */
    boolean isExistId(Integer unitId);

    IPage<Unit> getUnitList(QueryRequest queryRequest);

    void updateUnit(Unit unit);

    void deleteUnit(Integer unitId);

    void updateStatus(Integer unitId,Unit.Status status);
}
