package com.zs.gms.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.system.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

    public List<Menu> findUserPermissions(String userName);

    public void addMenu(Menu menu);

    public void deleteMenu(long menuId);

    public void updateMenu(Menu menu);

    /**
     * 判断权限是否存在
     * */
    public boolean isExists(String name);
}
