package com.zs.gms.service.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.system.User;

import java.util.Date;
import java.util.List;

 public interface UserService extends IService<User> {

     User findByName(String userName);

     void updateLastLoginTime(Integer userId, Date lastLoginTime);

    /**
     * 根据角色id获取所有用户
     * */
     List<User> getUsersByRoleId(Integer roleId);

     User findUserById(Integer userId);

     List<User> getUsersByRoleSign(String roleSign);

     void addUser(User user);

     IPage<User> findUserListPage(User user, QueryRequest request);

     void register(String userName, String password,String phone);

     void deleteUser(Integer userId);

     void deleteUsers(String [] userIds);

     boolean isExist(String [] userIds);

     void updateUser(User user) throws GmsException;

    /**
     * 修改用户角色
     * */
     void updateUserRole(Integer userId,String roleIds);

     void updatePassword(String userName,String password);
}
