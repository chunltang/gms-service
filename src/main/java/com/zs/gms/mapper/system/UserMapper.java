package com.zs.gms.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.entity.system.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {

    public User findUserByName(String userName);

    public IPage<User> findUserListPage(Page page,User user);

    public List<User> findUserList(@Param("user") User user);

    public List<Map<String,Object>> findUnitUserList();

    public List<User> getUsersByRoleId(Integer roleId);

    public User getUserById(Integer userId);

    public List<User> getUsersByRoleSign(String roleSign);

    public Integer getMaxId();
}
