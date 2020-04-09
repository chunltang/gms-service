package com.zs.gms.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.system.UserRole;

public interface UserRoleService extends IService<UserRole> {

     boolean userExistsByRoleSign(String roleSign);

     void removeUserRole(Integer userId);

     void addUserRole(Integer userId,Integer roleId);
}
