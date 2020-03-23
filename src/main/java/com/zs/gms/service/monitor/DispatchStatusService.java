package com.zs.gms.service.monitor;

import com.zs.gms.entity.monitor.DispatchStatus;

public interface DispatchStatusService {

    void addDispatchStatus(DispatchStatus dispatchStatus);

    DispatchStatus getBaseInfo(Integer vehicleId);
}
