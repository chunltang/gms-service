package com.zs.gms.service.mineralmanager;

import com.zs.gms.entity.mineralmanager.Mineral;

import java.util.List;

public interface MineralService {

    public void addMineral(Mineral mineral);

    /**
     * 判断矿物名称是否添加
     * */
     boolean isMineralExist(String mineralName);

     /**
      * 除mineralId外是否存在
      * */
     boolean isMineralExist(Integer mineralId,String mineralName);

     Mineral getMineral(Integer mineralId);

     List<Mineral> getMineralList();

     void updateMineral(Mineral mineral);

     void deleteMineral(String mineralIds);
}
