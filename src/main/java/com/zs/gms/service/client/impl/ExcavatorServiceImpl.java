package com.zs.gms.service.client.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.vehiclemanager.Excavator;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.mapper.vehiclemanager.ExcavatorMapper;
import com.zs.gms.service.client.ExcavatorService;
import com.zs.gms.service.mapmanager.MapDataUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public IPage getExcavatorList(Excavator excavator, QueryRequest queryRequest) {
        Page page=new Page();
        SortUtil.handlePageSort(queryRequest,page, GmsConstant.SORT_DESC,"EXCAVATORID");
        IPage<Excavator> listPage = this.baseMapper.findExcavatorListPage(page, excavator);
        List<Excavator> records = listPage.getRecords();
        listPage.setRecords(records.stream().map(e->{
            List<SemiStatic> areaInfos = MapDataUtil.getAreaInfos(e.getMapId(), AreaTypeEnum.LOAD_AREA);
            for (SemiStatic info : areaInfos) {
                if(info.getId().equals(e.getLoadId())){
                    e.setLoadName(info.getName());
                    break;
                }
            }
            return e;
        }).collect(Collectors.toList()));
        return listPage;
    }

    @Override
    @Transactional
    public List<Excavator> getExcavators() {
        return this.list();
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
