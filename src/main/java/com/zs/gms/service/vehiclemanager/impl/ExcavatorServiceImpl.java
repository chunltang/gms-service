package com.zs.gms.service.vehiclemanager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Ordering;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.entity.WhetherEnum;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.vehiclemanager.Excavator;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.mapper.vehiclemanager.ExcavatorMapper;
import com.zs.gms.service.vehiclemanager.ExcavatorService;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.vehiclemanager.ExcavatorTypeService;
import com.zs.gms.service.vehiclemanager.UserExcavatorLoadAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class ExcavatorServiceImpl extends ServiceImpl<ExcavatorMapper, Excavator> implements ExcavatorService {

    @Autowired
    private UserExcavatorLoadAreaService bindService;

    @Autowired
    private ExcavatorTypeService excavatorTypeService;

    @Override
    @Transactional
    public void addExcavator(Excavator excavator) {
        excavator.setCreateTime(new Date());
        excavator.setVehicleStatus(WhetherEnum.NO);
        this.save(excavator);
        updateExcavatorTypeActive();
    }

    @Override
    @Transactional
    public void updateExcavatorTypeActive(){
        List<Excavator> barneys = this.baseMapper.findExcavatorList();
        Set<Integer> collect = barneys.stream().map(Excavator::getExcavatorTypeId).collect(Collectors.toSet());
        excavatorTypeService.updateActive(collect);
    }

    @Override
    @Transactional
    public void updateExcavatorActive() {
        updateNoStatus();
        Collection<Integer> excavatorNos = bindService.getAllExcavatorNos();
        LambdaUpdateWrapper<Excavator> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Excavator::getVehicleStatus,WhetherEnum.YES);
        updateWrapper.in(Excavator::getExcavatorNo,excavatorNos);
        this.update(updateWrapper);
    }

    /**
     * 修改所有挖掘机状态为未激活
     * */
    @Transactional
    public void updateNoStatus(){
        LambdaUpdateWrapper<Excavator> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Excavator::getVehicleStatus, WhetherEnum.NO);
        this.update(updateWrapper);
    }

    @Override
    @Transactional
    public IPage getExcavatorList(Excavator excavator, QueryRequest queryRequest) {
        Map<String, Boolean> sortFiledMap=new HashMap<>();
        sortFiledMap.put("vehicleStatus",false);
        sortFiledMap.put("vap",false);
        IPage<Excavator> listPage = this.baseMapper.findExcavatorListPage(SortUtil.getPage(queryRequest,sortFiledMap), excavator);
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
        updateExcavatorTypeActive();
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

    @Override
    public Excavator getExcavatorById(Integer excavatorId) {
        return this.getById(excavatorId);
    }
}
