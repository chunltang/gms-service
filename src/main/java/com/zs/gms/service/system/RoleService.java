package com.zs.gms.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.entity.system.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<Role> findRolesByUserName(String userName);

    List<Role> getRoleList();


    /**
     * 查询角色是否存在,true为已存在
     */
    boolean roleExistsByRoleSign(String roleSign);

    void addRole(Role role);

    void deleteRole(Integer roleId);

    Role getRole(Integer roleId);

    void updateRole(Role role);

    /**
     * 增加角色权限
     */
    void addRoleMenus(Integer roleId, String menuIds);

    /**
     * 清空角色权限
     */
    void deleteRoleMenus(Integer roleId);

    /**
     * 更新角色权限
     */
    void updateRoleMenus(Integer roleId, String menuIds);

    Role getRoleByRoleName(String roleName);

    List<Role> findRoles();

    Role getRoleIdByRoleSign(String roleSign);

    List<Menu> getMenusByRoleId(Integer roleId);
}
