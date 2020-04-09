package com.zs.gms.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.entity.system.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

     List<Role> findRolesByUserName(String userName);

     List<Role> findRoles();

     List<Menu> getMenusByRoleId(Integer roleId);
}
