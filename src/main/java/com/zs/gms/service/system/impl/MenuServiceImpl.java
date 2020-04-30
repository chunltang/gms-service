package com.zs.gms.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.system.Menu;
import com.zs.gms.mapper.system.MenuMapper;
import com.zs.gms.service.system.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    @Transactional
    public List<Menu> findUserPermissions(String userName) {
        return this.baseMapper.findUserPermissions(userName);
    }

    @Override
    @Transactional
    public void addMenu(Menu menu) {
        this.baseMapper.insert(menu);
    }

    @Override
    @Transactional
    public void deleteMenu(Integer menuId) {
        this.removeById(menuId);
    }

    @Override
    @Transactional
    public void updateMenu(Menu menu) {
        this.updateById(menu);
    }

    @Override
    @Transactional
    public boolean isExists(String name) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getPowerLabel,name);
        int count = this.count(queryWrapper);
        return count > 0;
    }

    @Override
    public boolean isExists(Integer menuId, String name) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Menu::getMenuId,menuId);
        queryWrapper.eq(Menu::getPowerLabel,name);
        int count = this.count(queryWrapper);
        return count > 0;
    }
}
