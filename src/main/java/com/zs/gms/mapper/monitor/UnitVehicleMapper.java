package com.zs.gms.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.monitor.UnitVehicle;

import java.util.List;
import java.util.Map;

public interface UnitVehicleMapper extends BaseMapper<UnitVehicle> {

    Unit getUnitByVehicleId(Integer vehicleId);

    List<Map<String, Integer>> getAllVehicles();
}
