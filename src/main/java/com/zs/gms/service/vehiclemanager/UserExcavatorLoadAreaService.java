package com.zs.gms.service.vehiclemanager;

import com.zs.gms.entity.client.UserExcavatorLoadArea;

import java.util.Collection;

public interface UserExcavatorLoadAreaService {

    boolean isExistUser(Integer userId);

    /**
     * 获取所有已绑定挖掘机
     * */
    Collection<Integer> getAllExcavatorNos();

    void bindExcavator(UserExcavatorLoadArea bind);

    UserExcavatorLoadArea getBindByUser(Integer userId);

    UserExcavatorLoadArea getBindByLoad(Integer loadId);

    /**
     * 修改电铲所在区域
     * */
    void updateLoadArea(Integer excavatorId,Integer mapId,Integer loadArea);

    /**
     * 修改电铲操作员
     * */
    void updateUser(Integer excavatorId,Integer userId);
}
