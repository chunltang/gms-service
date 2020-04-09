package com.zs.gms.common.authentication;

import com.mchange.v2.ser.SerializableUtils;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.DateUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.User;
import javafx.collections.transformation.SortedList;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Slf4j
public class ShiroHelper extends ShiroRealm {

    @Autowired
    @Lazy
    private SessionDAO sessionDAO;

    /**
     * 获取当前用户的角色和权限集合
     */
    public AuthorizationInfo getCurrentUserAuthorizationInfo() {
        return super.doGetAuthorizationInfo(null);
    }

    /**
     * 获取所有在线用户
     */
    public List<User> getLoginUsers() {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        List<User> users = new ArrayList<>();
        for (Session session : sessions) {
            PrincipalCollection principalCollection = (PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (null != principalCollection) {
                User user = (User) principalCollection.getPrimaryPrincipal();
                users.add(user);
            }
        }
        return users;
    }

    /**
     * 根据用户id判断用户是否在线
     */
    public boolean isOnline(Integer userId) {
        List<User> loginUsers = getLoginUsers();
        for (User user : loginUsers) {
            if (user.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有同名用户,并按登录时间排序
     */
    public List<User> getSessions(String userName) {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        List<User> users=new ArrayList<>();
        for (Session session : sessions) {
            PrincipalCollection principalCollection = (PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (null != principalCollection) {
                User user = (User) principalCollection.getPrimaryPrincipal();
                if (null != user && user.getUserName().equals(userName)) {
                    user.setSessionId(String.valueOf(session.getId()));
                    user.setLastLoginTime(session.getStartTimestamp());
                    users.add(user);
                }
            }
        }
        users.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if (!GmsUtil.allObjNotNull(o1.getLastLoginTime(), o1.getLastLoginTime())) {
                    return 0;
                }
                return o1.getLastLoginTime().getTime() > o2.getLastLoginTime().getTime() ? 1 : -1;
            }
        });
        return users;
    }

    public Collection<User> getActiveSessions() throws UnsupportedEncodingException {
        Set<User> users = new HashSet();
        Collection<String> keys = RedisService.getLikeKey(StaticConfig.KEEP_DB, "shiro-session" + "*");
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Object obj = RedisService.get(StaticConfig.KEEP_DB, key);
                System.out.println(obj);
                User user=new User();
                user.setUserId(1);
                user.setUserName("t");
                user.setLastLoginTime(new Date());
                byte[] bytes = GmsUtil.serializer(user);
                RedisTemplate<String, Object> template = RedisService.getTemplate(0);
                template.setValueSerializer(new JdkSerializationRedisSerializer());
                template.opsForValue().set("test",bytes);//二进制数据使用JdkSerializationRedisSerializer
                Object test = template.opsForValue().get("test");
                User o =(User)GmsUtil.deserializer((byte[])test);
                users.add(o);
            }
        }
        return users;
    }
}
