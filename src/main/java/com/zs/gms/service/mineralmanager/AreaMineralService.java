package com.zs.gms.service.mineralmanager;

import com.zs.gms.entity.mineralmanager.AreaMineral;

import java.util.List;

public interface AreaMineralService {

    public void addAreaMineral(AreaMineral areaMineral);

    public AreaMineral getAreaMineral(Integer areaId);

    public void deleteAreaMineral(Integer id);

    public List<AreaMineral> getUnAreaIds(Integer mineralId);
}
