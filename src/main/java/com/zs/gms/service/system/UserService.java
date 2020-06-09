package com.zs.gms.service.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.system.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    User findByName(String userName);

    Integer getMaxId();

    /**
     * 修改密码重试次数
     */
    void updateRetry(Integer userId, int num);

    /**
     * 账号是否锁定，true锁定
     */
    void updateLock(Integer userId, boolean isLock);

    void updateLastLoginTime(Integer userId, Date lastLoginTime);

    /**
     * 清除锁定
     */
    void clearLock(Integer userId);

    /**
     * 根据角色id获取所有用户
     */
    List<User> getUsersByRoleId(Integer roleId);

    User findUserById(Integer userId);

    List<User> getUsersByRoleSign(String roleSign);

    void addUser(User user);

    IPage<User> findUserListPage(User user, QueryRequest request);

    List<Map<String, Object>> findUnitUserList();

    void register(String userName, String password, String phone);

    void deleteUser(Integer userId);

    void deleteUsers(String[] userIds);

    boolean isExist(String[] userIds);

    void updateUser(User user) throws GmsException;

    /**
     * 修改用户编号
     */
    void updateUserName(Integer userId, String userName);

    /**
     * 修改用户角色
     */
    void updateUserRole(Integer userId, Integer roleId);

    void updatePassword(String userName, String password);
}
