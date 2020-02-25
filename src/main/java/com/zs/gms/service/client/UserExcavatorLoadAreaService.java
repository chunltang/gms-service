package com.zs.gms.service.client;

import com.zs.gms.entity.client.UserExcavatorLoadArea;

public interface UserExcavatorLoadAreaService {

    void bindExcavator(UserExcavatorLoadArea bind);

    /**
     * 修改电铲所在区域
     * */
    void updateLoadArea(Integer excavatorId,Integer mapId,Integer loadArea);

    /**
     * 修改电铲操作员
     * */
    void updateUser(Integer excavatorId,Integer userId);
}
