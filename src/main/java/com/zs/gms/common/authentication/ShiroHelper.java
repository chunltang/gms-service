package com.zs.gms.common.authentication;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ShiroHelper extends ShiroRealm {

    /**
     * 获取当前用户的角色和权限集合
     * */
    public  AuthorizationInfo getCurrentUserAuthorizationInfo(){
         return super.doGetAuthorizationInfo(null);
    }
}
