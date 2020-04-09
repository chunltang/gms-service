package com.zs.gms.service.mineralmanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.mineralmanager.Mineral;
import com.zs.gms.mapper.mineralmanager.MineralMapper;
import com.zs.gms.service.mineralmanager.MineralService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(propagation = Propagation.SUPPORTS,readOnly = true,rollbackFor = Exception.class)
public class MineralServiceImpl extends ServiceImpl<MineralMapper, Mineral> implements MineralService  {

    /**
     * 添加矿种
     * */
    @Override
    @Transactional
    public void addMineral(Mineral mineral) {
        mineral.setAddTime(new Date());
        this.save(mineral);
    }

    @Override
    @Transactional
    public boolean isMineralExist(String mineralName) {
        LambdaQueryWrapper<Mineral> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Mineral::getMineralName,mineralName);
        int count = this.count(queryWrapper);
        return count > 0;
    }

    @Override
    public boolean isMineralExist(Integer mineralId, String mineralName) {
        LambdaQueryWrapper<Mineral> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Mineral::getMineralId,mineralId);
        queryWrapper.eq(Mineral::getMineralName,mineralName);
        int count = this.count(queryWrapper);
        return count > 0;
    }

    @Override
    public Mineral getMineral(Integer mineralId) {
        LambdaQueryWrapper<Mineral> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Mineral::getMineralId,mineralId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 获取矿种列表
     * */
    @Override
    @Transactional
    public List<Mineral> getMineralList() {
        List<Mineral> minerals = this.list(new LambdaQueryWrapper<Mineral>().orderByDesc(Mineral::getMineralId));
        return minerals;
    }

    /**
     * 更新矿种
     * */
    @Override
    @Transactional
    public void updateMineral(Mineral mineral) {
         this.updateById(mineral);
    }

    /**
     * 删除矿种
     * */
    @Override
    @Transactional
    public void deleteMineral(String mineralIds) {
        String[] ids = mineralIds.split(StringPool.COMMA);
        this.remove(new LambdaQueryWrapper<Mineral>().in(Mineral::getMineralId,ids));
    }
}
