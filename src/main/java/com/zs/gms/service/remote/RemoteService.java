package com.zs.gms.service.remote;

import com.zs.gms.entity.system.User;

public interface RemoteService {

    /**
     * 获取进入控制台权限
     * */
    boolean remoteAccess(Integer vehicleId,User user);
}
