package com.zs.gms.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.gms.entity.system.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole> {

    public Integer queryCountByRoleSign(String roleSign);
}
