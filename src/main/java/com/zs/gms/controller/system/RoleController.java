package com.zs.gms.controller.system;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.entity.system.Role;
import com.zs.gms.service.system.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
@Api(tags = {"用户管理"}, description = "User Controller")
@Validated
public class RoleController {

    @Autowired
    @Lazy
    private RoleService roleService;

    @Log(value = "新增角色")
    @PostMapping
    @ApiOperation(value = "新增角色", httpMethod = "POST")
    public GmsResponse addRole(@Valid @MultiRequestBody Role role) throws GmsException {
        try {
            roleService.addRole(role);
            return new GmsResponse().success();
        } catch (Exception e) {
            String message = "新增角色失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log(value = "获取用户角色列表")
    @GetMapping
    @ApiOperation(value = "获取用户角色列表", httpMethod = "GET")
    public GmsResponse getRoleList() throws GmsException {
        try {
            List<Role> list = roleService.getRoleList();
            return new GmsResponse().message("获取用户角色列表成功").data(list).success();
        } catch (Exception e) {
            String message = "获取用户角色列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }

    }

    @Log(value = "删除角色")
    @DeleteMapping(value = "/{roleId}")
    @ApiOperation(value = "删除角色", httpMethod = "DELETE")
    public GmsResponse deleteRole(@PathVariable Integer roleId) throws GmsException {
        try {
            if (roleId == null)
                throw new GmsException("角色ID为空");
            roleService.deleteRole(roleId);
            return new GmsResponse().success();
        } catch (Exception e) {
            String message = "删除角色失败," + e.getMessage();
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log(value = "修改角色")
    @PutMapping
    @ApiOperation(value = "修改角色", httpMethod = "PUT")
    public GmsResponse updateRole(@MultiRequestBody Role role) throws GmsException {
        try {
            if (role.getRoleId() == null)
                throw new GmsException("角色ID为空");
            roleService.updateRole(role);
            return new GmsResponse().success();
        } catch (Exception e) {
            String message = "修改角色失败," + e.getMessage();
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log(value = "更新角色权限")
    @PutMapping("/{roleId}/menus/{menuIds}")
    @ApiOperation(value = "更新角色权限", httpMethod = "PUT")
    public GmsResponse updateRoleMenus(@PathVariable Integer roleId, @PathVariable String menuIds) throws GmsException {
        try {
            if (!ObjectUtils.allNotNull(roleId, menuIds))
                throw new GmsException("参数异常");
            roleService.updateRoleMenus(roleId, menuIds);
            return new GmsResponse().message("更新角色权限成功").success();
        } catch (Exception e) {
            String message = "更新角色权限失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log(value = "清空角色权限")
    @DeleteMapping("/{roleId}/menus")
    @ApiOperation(value = "更新角色权限", httpMethod = "DELETE")
    public GmsResponse deleteRoleMenus(@PathVariable Integer roleId) throws GmsException {
        try {
            roleService.deleteRoleMenus(roleId);
            return new GmsResponse().message("清空角色权限成功").success();
        } catch (Exception e) {
            String message = "清空角色权限失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log(value = "获取角色权限集合")
    @GetMapping("/{roleId}/menus")
    @ApiOperation(value = "获取角色权限集合", httpMethod = "GET")
    public GmsResponse getMenusByRoleId(@PathVariable Integer roleId) throws GmsException {
        try {
            List<Menu> menus = roleService.getMenusByRoleId(roleId);
            return new GmsResponse().message("获取角色权限集合成功").data(menus).success();
        } catch (Exception e) {
            String message = "获取角色权限集合失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}
