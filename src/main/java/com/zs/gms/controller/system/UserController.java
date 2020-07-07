package com.zs.gms.controller.system;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.authentication.ShiroHelper;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.MD5Util;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.system.MenuService;
import com.zs.gms.service.system.RoleService;
import com.zs.gms.service.system.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@Api(tags = {"用户管理"},description = "User Controller")
@Validated
public class UserController extends BaseController {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private ShiroHelper shiroHelper;

    @Autowired
    @Lazy
    private RoleService roleService;

    @Autowired
    @Lazy
    private MenuService menuService;

    @Log("新增用户")
    @PostMapping
    @ApiOperation(value = "新增用户",httpMethod = "POST")
    public GmsResponse addUser(@Valid @MultiRequestBody User user) throws GmsException {
        try{
            Role role = roleService.getRole(user.getRoleId());
            if(null==role){
                return new GmsResponse().message("角色不存在").badRequest();
            }
            String roleSignDesc = role.getRoleSign();
            Role.RoleSign roleSign = Role.getEnum(roleSignDesc);
            /*if(Role.RoleSign.CHIEFDESPATCHER_ROLE.equals(roleSign)||
                    Role.RoleSign.MANAGER_ROLE.equals(roleSign)||
                    Role.RoleSign.MAPMAKER_ROLE.equals(roleSign)){
                List<User> users = userService.getUsersByRoleSign(roleSignDesc);
                if(users.size()>0){
                    return new GmsResponse().message("选定角色只能存在一个账号!").badRequest();
                }
            }*/

            /*User byName = userService.findByName(user.getUserName());
            if(null!=byName){
                return new GmsResponse().message("该用户已注册").badRequest();
            }*/
            Integer maxId = userService.getMaxId()+1;
            user.setUserId(maxId);
            String str = String.format("%03d", maxId);
            user.setUserName(roleSign.getSign()+str);
            userService.addUser(user);
            return new GmsResponse().message("新增用户成功").success();
        }catch (Exception e){
            String message="新增用户失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }


    @Log("获取用户列表")
    @GetMapping
    @ApiOperation(value = "获取用户列表",httpMethod ="GET")
    public GmsResponse getUserList( User user,QueryRequest request) throws GmsException {
        try {
            Map<String, Object> dataTable = getDataTable(userService.findUserListPage(user, request));
            return new GmsResponse().message("获取用户列表成功").data(dataTable).success();
        }catch (Exception e){
            String message="获取用户列表失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("获取用户角色权限信息")
    @GetMapping("/{userName}")
    @ApiOperation(value = "获取用户角色权限信息",httpMethod ="GET")
    public GmsResponse getUserInfo(@PathVariable String userName) throws GmsException {
        try {
            List<Role> roles = this.roleService.findRolesByUserName(userName);
            List<Menu> menus = this.menuService.findUserPermissions(userName);
            Map<String,Object> dataMap=new HashMap<>();
            dataMap.put("roles", roles);
            dataMap.put("permissions", menus);
            return new GmsResponse().message("获取用户角色权限信息成功").data(dataMap).success();
        }catch (Exception e){
            String message="获取用户角色权限信息失败";
            log.error(message,e);
            throw new GmsException(message);
        }

    }


    @Log("获取当前用户角色权限信息")
    @GetMapping("/curUser")
    @ApiOperation(value = "获取当前用户角色权限信息",httpMethod ="GET")
    public GmsResponse getCurrentUserInfo() throws GmsException {
        try {
            User user = userService.findByName(super.getCurrentUser().getUserName());
            AuthorizationInfo info = shiroHelper.getCurrentUserAuthorizationInfo();
            Map<String,Object> dataMap=new HashMap<>();
            dataMap.put("roles", info.getRoles());
            dataMap.put("permissions", info.getStringPermissions());
            dataMap.put("user", user);
            return new GmsResponse().message("获取当前用户角色权限信息成功").data(dataMap).success();
        }catch (Exception e){
            String message="获取当前用户角色权限信息失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }


    @Log(value = "批量删除用户")
    @DeleteMapping(value = "/{userIds}")
    @ApiOperation(value = "批量删除用户",httpMethod = "DELETE")
    public GmsResponse deleteUsers(@PathVariable String userIds) throws GmsException {
        try {
            if(StringUtils.isBlank(userIds))
                throw new GmsException("用户IDs为空");
            String[] ids = userIds.split(StringPool.COMMA);
            boolean exist = userService.isExist(ids);
            if(!exist){
                return new GmsResponse().message("用户不存在").badRequest();
            }
            userService.deleteUsers(ids);
            return new GmsResponse().message("删除成功").success();
        }catch (Exception e){
            String message="删除失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "修改用户")
    @PutMapping
    @ApiOperation(value = "修改用户",httpMethod = "PUT")
    public GmsResponse updateUser(@MultiRequestBody User user) throws GmsException {
        try {
            if(user.getUserId()==null)
                throw new GmsException("用户ID为空");
            userService.updateUser(user);
            return new GmsResponse().message("修改用户成功").success();
        }catch (Exception e){
            String message="修改用户失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "修改用户角色")
    @PutMapping("/{userId}/{roleId}")
    @ApiOperation(value = "修改用户角色",httpMethod = "PUT")
    public GmsResponse updateUserRole(@PathVariable Integer userId,@PathVariable Integer roleId) throws GmsException {
        try {
            Role role = roleService.getRole(roleId);
            User user = userService.findUserById(userId);
            if(Role.RoleSign.ADMIN_ROLE.getValue().equals(user.getRoleSign()) || Role.RoleSign.ADMIN_ROLE.getValue().equals(role.getRoleSign())){
                return new GmsResponse().message("系统管理员角色禁止修改").badRequest();
            }
            if(!ObjectUtils.allNotNull(userId,roleId))
                throw new GmsException("参数异常");
            userService.updateUserRole(userId,roleId);
            return new GmsResponse().message("修改用户成功").success();
        }catch (Exception e){
            String message="修改用户失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "修改用户密码")
    @PutMapping("/{userName}")
    @ApiOperation(value = "修改用户密码",httpMethod = "PUT")
    public GmsResponse updatePassword(@PathVariable String userName,
                                      @MultiRequestBody("oldPwd") String oldPwd,
                                      @MultiRequestBody("newPwd") String newPwd) throws GmsException {

        if(StringUtils.isAnyEmpty(userName,oldPwd,newPwd))
            throw new GmsException("参数异常");
        User userById = userService.findByName(userName);
        if(null==userById)
            throw new GmsException("用户不存在");
        String encrypt = MD5Util.encrypt(userName, oldPwd);
        if(!encrypt.equals(userById.getPassword()))
            throw new GmsException("密码错误");
        encrypt = MD5Util.encrypt(userName, newPwd);
        if(encrypt.equals(userById.getPassword()))
            throw new GmsException("新密码和旧密码相同");
        try {
            userService.updatePassword(userName,newPwd);
            return new GmsResponse().message("修改用户密码成功").success();
        }catch (Exception e){
            String message="修改用户密码失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log(value = "根据角色获取用户列表")
    @GetMapping("/{roleId}/s")
    @ApiOperation(value = "根据角色获取用户列表",httpMethod = "GET")
    public GmsResponse getUsersByRoleIds(@PathVariable Integer roleId) throws GmsException {
        try {
            List<User> users = userService.getUsersByRoleId(roleId);
            return new GmsResponse().data(users).message("获取用户列表成功").success();
        }catch (Exception e){
            String message="根据角色获取用户列表失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("获取在线用户，调度员是否分配作业")
    @GetMapping(value = "/online")
    @ApiOperation(value = "获取在线用户", httpMethod = "GET")
    public GmsResponse getOnlineUsers() throws GmsException {
        try {
            List<Map<String,Object>> userList = userService.findUnitUserList();
            Set<Integer> onLines = shiroHelper.getOnLines();
            List<Map<String,Object>> collect = userList.stream().filter(obj -> {
                Integer userId = GmsUtil.typeTransform(obj.get("userId"), Integer.class);
                if (onLines.contains(userId)) {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            return new GmsResponse().data(collect).message("获取在线用户成功").success();
        } catch (Exception e) {
            String message = "获取在线用户失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log(value = "清除密码锁定")
    @PutMapping("/lock")
    @ApiOperation(value = "清除密码锁定",httpMethod = "PUT")
    public GmsResponse clearUserLock( Integer userId) throws GmsException {
        try {
            User user = userService.findUserById(userId);
            if(null==user){
                return new GmsResponse().message("用户不存在!").badRequest();
            }
            userService.clearLock(userId);
            return new GmsResponse().message("清除密码锁定成功").success();
        }catch (Exception e){
            String message="清除密码锁定失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
