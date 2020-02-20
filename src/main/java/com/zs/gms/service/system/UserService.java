package com.zs.gms.service.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.system.User;

import java.util.List;

public interface UserService extends IService<User> {

    public User findByName(String userName);

    /**
     * 根据角色id获取所有用户
     * */
    public List<User> getUsersByRoleId(Integer roleId);

    public User findUserById(Integer userId);

    public List<User> getUsersByRoleSign(String roleSign);

    public void addUser(User user);

    public IPage<User> findUserListPage(User user, QueryRequest request);

    public void regist(String userName, String password);

    public void deleteUser(Integer userId);

    public void deleteUsers(String [] userIds);

    public boolean isExist(String [] userIds);

    public void updateUser(User user) throws GmsException;

    /**
     * 修改用户角色
     * */
    public void updateUserRole(Integer userId,String roleIds);

    public void updatePassword(String userName,String password);
}
