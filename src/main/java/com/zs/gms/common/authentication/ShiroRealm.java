package com.zs.gms.common.authentication;

import com.zs.gms.entity.system.Menu;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.system.MenuService;
import com.zs.gms.service.system.RoleService;
import com.zs.gms.service.system.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private MenuService menuService;


    /**
     * 授权模块，获取角色和权限
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String userName = user.getUserName();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        /**
         * 获取用户角色集
         * */
        List<Role> roles = this.roleService.findRolesByUserName(userName);
        Set<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toSet());
        authorizationInfo.setRoles(roleNames);

        /**
         *获取用户权限集
         * */
        List<Menu> menus = this.menuService.findUserPermissions(userName);
        Set<String> menuNames = menus.stream().map(Menu::getPowerName).collect(Collectors.toSet());
        authorizationInfo.setStringPermissions(menuNames);
        log.info("当前用户>>>:{},角色:{},权限:{}",user.getUserName(),roleNames.toString(),menuNames.toString());
        return authorizationInfo;
    }

    /**
     * 认证模块，身份校验及对应异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        User user = this.userService.findByName(userName);
        if (null == user) {
            throw new UnknownAccountException("账号未注册!");
        }

        if (!StringUtils.equals(password, user.getPassword())) {
            throw new IncorrectCredentialsException("账号或密码不存在!");
        }

        return new SimpleAuthenticationInfo(user, password, getName());
    }

    /**
     * 清除当前用户权限缓存
     */
    public void clearCache() {
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }
}
