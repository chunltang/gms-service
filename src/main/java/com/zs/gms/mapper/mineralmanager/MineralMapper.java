package com.zs.gms.mapper.mineralmanager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.entity.mineralmanager.Mineral;

public interface MineralMapper extends BaseMapper<Mineral> {

    IPage<Mineral> getMineralListPage(Page<Mineral> page);
}
