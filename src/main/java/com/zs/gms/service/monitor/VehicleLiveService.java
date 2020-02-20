package com.zs.gms.service.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.monitor.VehicleLive;

public interface VehicleLiveService  {

    public void addVehicleLive(VehicleLive vehicleLive);

    public IPage<VehicleLive> getVehicleLiveListPage(VehicleLive vehicleLive, QueryRequest request);
}
