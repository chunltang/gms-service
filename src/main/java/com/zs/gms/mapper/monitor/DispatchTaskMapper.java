package com.zs.gms.mapper.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.gms.entity.monitor.DispatchTask;

public interface DispatchTaskMapper extends BaseMapper<DispatchTask> {

    DispatchTask getUnitByVehicleId(Integer vehicleId);
}
