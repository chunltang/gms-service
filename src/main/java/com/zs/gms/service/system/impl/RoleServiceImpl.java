package com.zs.gms.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.RoleMenu;
import com.zs.gms.mapper.system.RoleMapper;
import com.zs.gms.service.system.RoleMenuService;
import com.zs.gms.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    @Transactional
    public List<Role> findRolesByUserName(String userName) {
        return this.baseMapper.findRolesByUserName(userName);
    }

    @Override
    @Transactional
    public List<Role> getRoleList() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Role::getRoleSign,Role.RoleSign.ADMIN_ROLE.getValue());
        return this.list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean roleExistsByRoleSign(String roleSign) {
        LambdaQueryWrapper<Role> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleSign,roleSign);
        int count = this.count(queryWrapper);
        return count>0?true:false;
    }

    @Override
    @Transactional
    public void addRole(Role role) {
        this.baseMapper.insert(role);
    }

    @Override
    @Transactional
    public void deleteRole(Integer roleId) {
        this.removeById(roleId);
    }

    @Override
    @Transactional
    public Role getRole(Integer roleId) {
       return this.getById(roleId);
    }

    @Override
    @Transactional
    public void updateRole(Role role) {
        this.updateById(role);
    }

    @Override
    @Transactional
    public void addRoleMenus(Integer roleId, String menuIds) {
        batchSaveUserRole(roleId,menuIds.split(StringPool.COMMA));

    }

    @Override
    @Transactional
    public void deleteRoleMenus(Integer roleId) {
        roleMenuService.deleteMenusByRoleId(roleId);
    }

    @Override
    @Transactional
    public void updateRoleMenus(Integer roleId, String menuIds) {
        roleMenuService.deleteMenusByRoleId(roleId);
        if(StringUtils.isNotEmpty(menuIds)){
            addRoleMenus(roleId,menuIds);
        }
    }

    @Override
    @Transactional
    public Role getRoleByRoleName(String roleName) {
        QueryWrapper<Role> queryWrapper=new QueryWrapper<>();
        queryWrapper.lambda().eq(Role::getRoleName,roleName);
        Role role = this.getOne(queryWrapper);
        return role;
    }

    /**
     * 查询所有角色，包含角色权限
     * */
    @Override
    @Transactional
    public List<Role> findRoles() {
        return this.baseMapper.findRoles();
    }

    @Override
    @Transactional
    public Role getRoleIdByRoleSign(String roleSign) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getRoleSign,roleSign);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional
    public List<Menu> getMenusByRoleId(Integer roleId) {
        return this.baseMapper.getMenusByRoleId(roleId);
    }

    /**
     * 批量写入角色菜单关系
     * */
    @Transactional
    public void batchSaveUserRole(Integer roleId,String[] menuIds){
        List<RoleMenu> roleMenus=new ArrayList<>();
        Arrays.stream(menuIds).forEach(id->{
            RoleMenu roleMenu=new RoleMenu();
            roleMenu.setMenuId(Integer.valueOf(id));
            roleMenu.setRoleId(roleId);
            roleMenus.add(roleMenu);
        });
        roleMenuService.saveBatch(roleMenus);
    }
}
