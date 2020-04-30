package com.zs.gms.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.system.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

     List<Menu> findUserPermissions(String userName);

     void addMenu(Menu menu);

     void deleteMenu(Integer menuId);

     void updateMenu(Menu menu);

    /**
     * 判断权限是否存在
     * */
     boolean isExists(String name);

     boolean isExists(Integer menuId,String name);
}
