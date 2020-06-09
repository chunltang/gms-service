package com.zs.gms.mapper.vehiclemanager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.entity.vehiclemanager.Excavator;

import java.util.List;

public interface ExcavatorMapper extends BaseMapper<Excavator> {

    IPage<Excavator> findExcavatorListPage(Page page,Excavator excavator);

    List<Excavator> findExcavatorList();
}
