package com.zs.gms.service.vehiclemanager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.vehiclemanager.Barney;
import com.zs.gms.entity.vehiclemanager.IconLib;
import io.swagger.models.auth.In;

import java.util.List;


public interface IconLibService extends IService<IconLib> {

    void addIconLib(IconLib lib);

    List<IconLib> getLibs();

    void delById(Integer id);

    boolean isExist(Integer id);

    boolean isExistName(String name);

    IconLib getLib(Integer id);
}
