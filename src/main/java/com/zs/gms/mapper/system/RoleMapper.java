package com.zs.gms.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.entity.system.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

    public List<Role> findRolesByUserName(String userName);

    public List<Role> findRoles();

    public List<Menu> getMenusByRoleId(Integer roleId);
}
