package com.zs.gms.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.system.UserRole;
import com.zs.gms.mapper.system.UserRoleMapper;
import com.zs.gms.service.system.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    @Transactional
    public boolean userExistsByRoleSign(String roleSign) {
        Integer count = this.baseMapper.queryCountByRoleSign(roleSign);
        return count>0?true:false;
    }

    @Override
    @Transactional
    public void removeUserRole(Integer userId) {
        this.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
    }
}
