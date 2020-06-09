package com.zs.gms.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.entity.monitor.Unit;

public interface UnitMapper extends BaseMapper<Unit> {

      IPage<Unit> getUnitListPage(Page page,Integer mapId);
}
