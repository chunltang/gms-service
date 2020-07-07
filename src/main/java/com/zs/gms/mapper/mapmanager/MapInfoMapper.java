package com.zs.gms.mapper.mapmanager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.entity.mapmanager.MapInfo;

import java.util.List;
import java.util.Map;

public interface MapInfoMapper extends BaseMapper<MapInfo> {

    List<Map<String,Object>> getMapInfoList();

    String getExcludeSql();
}
