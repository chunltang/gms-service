package com.zs.gms.service.client.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.entity.client.Excavator;
import com.zs.gms.mapper.client.ExcavatorMapper;
import com.zs.gms.service.client.ExcavatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class ExcavatorServiceImpl extends ServiceImpl<ExcavatorMapper, Excavator> implements ExcavatorService {

    @Override
    @Transactional
    public void addExcavator(Excavator excavator) {
        excavator.setCreateTime(new Date());
        this.save(excavator);
    }

    @Override
    @Transactional
    public void delExcavator(Integer excavatorId) {
        this.removeById(excavatorId);
    }

    @Override
    @Transactional
    public void updateExcavator(Excavator excavator) {
        updateById(excavator);
    }

    @Override
    @Transactional
    public Excavator getExcavatorByNo(Integer excavatorNo) {
        LambdaQueryWrapper<Excavator> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Excavator::getExcavatorNo,excavatorNo);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean isExistNo(Integer excavatorNo) {
        return null!=getExcavatorByNo(excavatorNo);
    }

    @Override
    public boolean isExistId(Integer excavatorId) {
        Excavator excavator = this.baseMapper.selectById(excavatorId);
        return excavator!=null;
    }
}
