package com.zs.gms.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.entity.system.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    public User findUserByName(String userName);

    public IPage<User> findUserListPage(Page page,User user);

    public List<User> getUsersByRoleId(Integer roleId);

    public User getUserById(Integer userId);

    public List<User> getUsersByRoleSign(String roleSign);
}
