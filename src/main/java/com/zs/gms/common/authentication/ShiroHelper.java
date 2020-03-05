package com.zs.gms.common.authentication;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.entity.system.User;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class ShiroHelper extends ShiroRealm {

    @Autowired
    @Lazy
    private SessionDAO sessionDAO;
    /**
     * 获取当前用户的角色和权限集合
     * */
    public  AuthorizationInfo getCurrentUserAuthorizationInfo(){
         return super.doGetAuthorizationInfo(null);
    }

    /**
     * 获取所有在线用户
     * */
    public List<User> getLoginUsers(){
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        List<User> users=new ArrayList<>();
        for (Session session : sessions) {
            PrincipalCollection principalCollection = (PrincipalCollection)session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            User user = (User) principalCollection.getPrimaryPrincipal();
            users.add(user);
        }
        return users;
    }

    /**
     * 根据用户id判断用户是否在线
     * */
    public boolean isOnline(Integer userId){
        List<User> loginUsers = getLoginUsers();
        for (User user : loginUsers) {
            if(user.getUserId().equals(userId)){
                return true;
            }
        }
        return false;
    }
}
