package com.zs.gms.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.gms.entity.system.UserRole;

public interface UserRoleService extends IService<UserRole> {

    public boolean userExistsByRoleSign(String roleSign);

    public void removeUserRole(Integer userId);
}
