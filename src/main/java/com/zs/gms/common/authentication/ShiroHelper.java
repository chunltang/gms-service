package com.zs.gms.common.authentication;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ShiroHelper extends ShiroRealm {

    @Autowired
    @Lazy
    private SessionDAO sessionDAO;


    public static void put(User user) {
        if (null != user) {
            HashOperations<String, String, Object> hashOperations = RedisService.hashOperations(StaticConfig.KEEP_DB);
            hashOperations.put(RedisKeyPool.ACTIVATE_USERS, user.getUserId().toString(), GmsUtil.toJson(user));
        }
    }

    public static void remove(Integer userId) {
        HashOperations<String, String, Object> hashOperations = RedisService.hashOperations(StaticConfig.KEEP_DB);
        hashOperations.delete(RedisKeyPool.ACTIVATE_USERS, userId.toString());
    }

    public static Map<String, Object> getActivateUsers() {
        HashOperations<String, String, Object> hashOperations = RedisService.hashOperations(StaticConfig.KEEP_DB);
        return hashOperations.entries(RedisKeyPool.ACTIVATE_USERS);
    }

    /**
     * 判断是否有对应角色登录
     */
    public static boolean roleIsLogin(User user) {
        Map<String, Object> users = getActivateUsers();
        if (null != user && null != users) {
            for (Object value : users.values()) {
                User activeUser = GmsUtil.toObj(value.toString(), User.class);
                if (null != activeUser) {
                    if (!activeUser.getUserId().equals(user.getUserId()) && user.getRoleId().equals(activeUser.getRoleId())) {
                        if (isActivate(activeUser.getUserId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断用户是否在线
     */
    public static boolean isActivate(Integer userId) {
        Map<String, Object> users = getActivateUsers();
        if (null != users) {
            if (!users.keySet().contains(userId.toString())) {
                return false;
            }
            User user = GmsUtil.toObj(users.get(userId.toString()), User.class);
            if (null != user) {
                boolean existsKey = RedisService.existsKey(StaticConfig.CACHE_DB, user.getSessionId());
                if (existsKey) {
                    return true;
                } else {
                    remove(userId);
                    return false;
                }
            }
        }
        return false;
    }

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
        List<String> sessionIds = new ArrayList<>(sessions.size() / 2);
        for (Session session : sessions) {
            Object attribute = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (null != attribute) {
                PrincipalCollection principalCollection = (PrincipalCollection) attribute;
                User user = (User) principalCollection.getPrimaryPrincipal();
                users.add(user);
            }else{
                sessionIds.add(GmsConstant.SHIRO_SESSION_PREFIX+session.getId());
            }
        }
        clearNullitySession(sessionIds);
        return users;
    }

    /**
     * 清理所有无效session
     * */
    private void clearNullitySession(Collection<String> collection) {
        RedisService.deleteKey(StaticConfig.CACHE_DB, collection);
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

    public Set<Integer> getOnLines() {
        List<User> loginUsers = getLoginUsers();
        return loginUsers.stream().map(User::getUserId).collect(Collectors.toSet());
    }


    /**
     * 清除用户session
     */
    public void clearSession(Integer userId) {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        if (sessions.size() > 0) {
            for (Session session : sessions) {
                // 5. 清除当前用户以前登录时保存的session会话
                PrincipalCollection principalCollection = (PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                if (null != principalCollection) {
                    User user = (User) principalCollection.getPrimaryPrincipal();
                    if (userId.equals(user.getUserId())) {
                        sessionDAO.delete(session);
                        return;
                    }
                }
            }
        }
    }

    public void clearSession(String userName) {
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        if (sessions.size() > 0) {
            for (Session session : sessions) {
                // 5. 清除当前用户以前登录时保存的session会话
                PrincipalCollection principalCollection = (PrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                if (null != principalCollection) {
                    User user = (User) principalCollection.getPrimaryPrincipal();
                    if (userName.equals(user.getUserName())) {
                        sessionDAO.delete(session);
                        return;
                    }
                }
            }
        }
    }


    /**
     * 获取所有同名用户,并按登录时间排序
     */
    /*public List<User> getSessions(String userName) {
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
    }*/
}
