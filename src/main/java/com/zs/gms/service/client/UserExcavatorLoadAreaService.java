package com.zs.gms.service.client;

import com.zs.gms.entity.client.UserExcavatorLoadArea;

public interface UserExcavatorLoadAreaService {

    boolean isExistUser(Integer userId);

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
