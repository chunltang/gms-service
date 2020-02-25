package com.zs.gms.controller.system;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.authentication.ShiroHelper;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.MD5Util;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.system.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Api(tags = {"用户管理"}, description = "User Controller")
@Validated
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private ShiroHelper shiroHelper;

    @Log("用户登录")
    @PostMapping(value = "/login")
    @ApiOperation(value = "用户登录", httpMethod = "POST")
    public GmsResponse login(@MultiRequestBody("userName") String userName, @MultiRequestBody("password") String password, Boolean rememberMe) throws GmsException {
        if (StringUtils.isAnyBlank(userName, password))
            throw new GmsException("用户名或密码为空");
        try {
            password = MD5Util.encrypt(userName, password);
            UsernamePasswordToken token = new UsernamePasswordToken(userName, password, false);
            super.login(token);
            return new GmsResponse().data(getUserInfo()).message("用户登录成功").success();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GmsException(e.getMessage());
        }
    }

    @Log("用户是否已登录")
    @GetMapping(value = "/isLogin")
    @ApiOperation(value = "用户是否已登录", httpMethod = "GET")
    public GmsResponse isLogin() throws GmsException {
        try {
            if (super.subIsLogin()){
                return new GmsResponse().data(getUserInfo()).message("用户已登录").success();
            }
            return new GmsResponse().unauth().message("用户未登录");
        } catch (Exception e) {
            String message="判断用户是否登录失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    public Map getUserInfo() {
        AuthorizationInfo info = shiroHelper.getCurrentUserAuthorizationInfo();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("token", super.getSession().getId());
        dataMap.put("permissions", info.getStringPermissions());
        User user = userService.findByName(super.getCurrentUser().getUserName());
        user.setPassword("***");
        dataMap.put("user", user);
        dataMap.put("roles", info.getRoles());
        return dataMap;
    }

    @Log("用户注册")
    @RequestMapping(value = "/regist")
    @ApiOperation(value = "用户注册", httpMethod = "POST")
    public GmsResponse regist(@MultiRequestBody("userName") String userName,
                              @MultiRequestBody("password") String password) throws GmsException {
        if (StringUtils.isAnyBlank(userName, password)) {
            throw new GmsException("用户名或密码为空");
        }
        User user = userService.findByName(userName);
        if (null != user) {
            throw new GmsException("用户名已存在");
        }
        try {
            this.userService.regist(userName, password);
            return new GmsResponse().message("用户注册成功").success();
        } catch (Exception e) {
            String message = "用户注册失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("用户登出")
    @RequestMapping(value = "/logout")
    @ApiOperation(value = "用户登出", httpMethod = "POST")
    public GmsResponse userLogout() throws GmsException {
        try {
            super.logout();
            return new GmsResponse().message("用户登出成功").success();
        } catch (Exception e) {
            String message = "用户登出失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("未登录")
    @RequestMapping(value = "/unauth")
    @ApiOperation(value = "未登录", httpMethod = "POST")
    @ResponseBody
    public GmsResponse unauth() {
        return new GmsResponse().unauth().message("未登录");
    }
}
