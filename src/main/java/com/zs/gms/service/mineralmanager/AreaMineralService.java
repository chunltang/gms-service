package com.zs.gms.service.mineralmanager;

import com.zs.gms.entity.mineralmanager.AreaMineral;

import java.util.List;

public interface AreaMineralService {

     void addAreaMineral(AreaMineral areaMineral);

     AreaMineral getAreaMineral(Integer areaId);

     void deleteAreaMineralById(Integer id);

     void deleteAreaMineral(Integer mineralId);

     List<AreaMineral> getAreaIds(Integer mineralId);
}
