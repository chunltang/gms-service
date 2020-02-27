package com.zs.gms.service.gpsmanager;

import com.zs.gms.entity.gpsmanager.Gps;

public interface GpsService {

    void addGps(Gps gps);

    void delGps(Integer gpsId);

    boolean isExistNo(Integer gpsNo);

    boolean isExistId(Integer gpsId);

    /**
     * 根据id修改
     * */
    void updateGps(Gps gps);
}
