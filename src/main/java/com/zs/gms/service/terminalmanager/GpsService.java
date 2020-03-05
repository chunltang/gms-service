package com.zs.gms.service.terminalmanager;

import com.zs.gms.entity.terminalmanager.Gps;

import java.util.List;

public interface GpsService {

    void addGps(Gps gps);

    void delGps(Integer gpsId);

    boolean isExistNo(Integer gpsNo);

    boolean isExistId(Integer gpsId);

    List<Gps> getAllGps();

    /**
     * 根据id修改
     * */
    void updateGps(Gps gps);
}
