package com.zs.gms.controller.system;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.service.system.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/menus")
@Api(tags = {"用户管理"},description = "User Controller")
public class MenuController {

    @Autowired
    @Lazy
    private MenuService menuService;

    @Log(value = "获取权限菜单列表")
    @GetMapping
    @ApiOperation(value = "获取权限菜单列表",httpMethod ="GET")
    public GmsResponse getMenuList() throws GmsException {
        try {
            List<Menu> list = menuService.list();
            return new GmsResponse().message("获取权限菜单列表成功").data(list).success();
        }catch (Exception e){
            String message="获取权限菜单列表失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log(value = "新增权限菜单")
    @PostMapping
    @ApiOperation(value = "新增权限菜单",httpMethod ="POST")
    public GmsResponse addMenu(@Valid @MultiRequestBody Menu menu) throws GmsException {
        try{
            if(menuService.isExists(menu.getPowerLabel())){
                return new GmsResponse().badRequest().message("权限已存在");
            }
            menuService.addMenu(menu);
            return new GmsResponse().message("新增权限成功").success();
        }catch (Exception e){
            String message="新增权限菜单失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log(value = "删除权限菜单")
    @DeleteMapping(value = "/{menuId}")
    @ApiOperation(value = "删除权限菜单",httpMethod ="DELETE")
    public GmsResponse deleteMenu(@PathVariable Integer menuId) throws GmsException {
        try{
            if(menuId==null)
                throw new GmsException("菜单ID为空");
            menuService.deleteMenu(menuId);
            return new GmsResponse().success();
        }catch (Exception e){
            String message="删除权限菜单失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log(value = "修改权限菜单")
    @PutMapping
    @ApiOperation(value = "修改权限菜单",httpMethod ="PUT")
    public GmsResponse updateMenu(@MultiRequestBody Menu menu) throws GmsException {
        try{
            if(!GmsUtil.objNotNull(menu.getMenuId()))
                throw new GmsException("权限ID为空");
            if(menuService.isExists(menu.getMenuId(),menu.getPowerLabel())){
                return new GmsResponse().badRequest().message("权限名称已存在");
            }
            menuService.updateMenu(menu);
            return new GmsResponse().message("修改权限菜单成功").success();
        }catch (Exception e){
            String message="修改权限菜单失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

}
