package com.zs.gms.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.system.RoleMenu;

public interface RoleMenuService extends IService<RoleMenu> {

    public void deleteMenusByRoleId(Integer roleId);
}
