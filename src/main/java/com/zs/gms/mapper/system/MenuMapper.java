package com.zs.gms.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.gms.entity.system.Menu;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    public List<Menu> findUserPermissions(String userName);
}
