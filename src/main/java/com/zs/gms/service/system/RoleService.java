package com.zs.gms.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.entity.system.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

    public List<Role> findRolesByUserName(String userName);

    /**
     * 查询角色是否存在,true为已存在
     * */
    public boolean roleExistsByRoleSign(String roleSign);

    public void addRole(Role role);

    public void deleteRole(Integer roleId);

    public Role getRole(Integer roleId);

    public void updateRole(Role role);

    /**
     * 增加角色权限
     * */
    public void addRoleMenus(Integer roleId,String menuIds);

    /**
     * 清空角色权限
     * */
    public void deleteRoleMenus(Integer roleId);

    /**
     * 更新角色权限
     * */
    public void updateRoleMenus(Integer roleId,String menuIds);

    public Role getRoleByRoleName(String roleName);

    public List<Role> findRoles();

    public Role getRoleIdByRoleSign(String roleSign);

    public List<Menu> getMenusByRoleId(Integer roleId);
}
