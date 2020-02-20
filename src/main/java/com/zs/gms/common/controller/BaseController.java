package com.zs.gms.common.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.entity.system.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.HashMap;
import java.util.Map;


public class BaseController {

    protected Subject getSubject(){
        return SecurityUtils.getSubject();
    }

    protected User getCurrentUser(){
        return (User) SecurityUtils.getSubject().getPrincipal();
    }

    protected Session getSession(){
        return SecurityUtils.getSubject().getSession();
    }

    protected void login(AuthenticationToken token){
        SecurityUtils.getSubject().login(token);
    }

    protected void logout(){
        Subject subject = getSubject();
        if(subject.isAuthenticated()){
            subject.logout();
        }
    }

    protected boolean subIsLogin(){
        Subject subject = getSubject();
        if(subject.isAuthenticated()){
            return true;
        }
        return false;
    }

    protected Map<String,Object>  getDataTable(IPage<?> pageinfo){
        return getDataTable(pageinfo, GmsConstant.DATA_MAP_INITIAL_CAPACITY);
    }

    /**
     * 返回的分页数据
     * */
    protected Map<String,Object>  getDataTable(IPage<?> pageinfo,int defaultMapInitCapacity){
        Map<String,Object> dataMap=new HashMap<>(defaultMapInitCapacity);
        dataMap.put("rows",pageinfo.getRecords());
        dataMap.put("total",pageinfo.getTotal());
       return dataMap;
    }
}
