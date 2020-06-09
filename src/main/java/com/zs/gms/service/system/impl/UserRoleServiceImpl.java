package com.zs.gms.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.utils.Assert;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.entity.system.UserRole;
import com.zs.gms.mapper.system.UserRoleMapper;
import com.zs.gms.service.system.RoleService;
import com.zs.gms.service.system.UserRoleService;
import com.zs.gms.service.system.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Override
    @Transactional
    public boolean userExistsByRoleSign(String roleSign) {
        Integer count = this.baseMapper.queryCountByRoleSign(roleSign);
        return count > 0;
    }

    @Override
    @Transactional
    public void removeUserRole(Integer userId) {
        this.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
    }

    @Override
    @Transactional
    public void addUserRole(Integer userId, Integer roleId) {
        Assert.AllNotNull("字段不能为空",new Object[]{userId,roleId});
        User user = userService.findUserById(userId);
        Role role = roleService.getRole(roleId);
        String sign = Role.getEnum(role.getRoleSign()).getSign();
        userService.updateUserName(userId,sign+user.getUserName().substring(1));
        removeUserRole(userId);
        UserRole userRole=new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);
        this.save(userRole);
    }
}
