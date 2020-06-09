package com.zs.gms.service.mineralmanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.mineralmanager.Mineral;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.entity.vehiclemanager.BarneyType;
import com.zs.gms.mapper.mineralmanager.MineralMapper;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.mineralmanager.MineralService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
        mineral.setActivate(WhetherEnum.NO);
        this.save(mineral);
    }

    @Override
    @Transactional
    public void updateActive(Collection<Integer> mineralIds) {
        LambdaUpdateWrapper<Mineral> updateWrapper1 = new LambdaUpdateWrapper<>();
        updateWrapper1.set(Mineral::getActivate, WhetherEnum.NO);
        this.update(updateWrapper1);
        if(GmsUtil.CollectionNotNull(mineralIds)){
            LambdaUpdateWrapper<Mineral> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Mineral::getActivate, WhetherEnum.YES);
            updateWrapper.in(Mineral::getMineralId,mineralIds);
            this.update(updateWrapper);
        }
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
    public IPage<Mineral> getMineralList(QueryRequest queryRequest) {
        Page<Mineral> page=new Page<>();
        SortUtil.handlePageSort(queryRequest,page, GmsConstant.SORT_DESC,"mineralId");
        IPage<Mineral> listPage = this.baseMapper.getMineralListPage(page);
        List<Mineral> records = listPage.getRecords();
        for (Mineral record : records) {
            if(GmsUtil.allObjNotNull(record.getMapId(),record.getLoadId())){
                SemiStatic areaInfo = MapDataUtil.getAreaInfo(record.getMapId(), record.getLoadId());
                record.setLoadIdName(areaInfo.getName());
            }
            if(GmsUtil.allObjNotNull(record.getMapId(),record.getUnLoadId())){
                SemiStatic areaInfo = MapDataUtil.getAreaInfo(record.getMapId(), record.getUnLoadId());
                record.setUnlLoadIdName(areaInfo.getName());
            }
        }
        return listPage;
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
