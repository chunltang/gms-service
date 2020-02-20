package com.zs.gms.service.monitor.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.utils.SortUtil;
import com.zs.gms.entity.monitor.VehicleLive;
import com.zs.gms.mapper.monitor.VehicleLiveMapper;
import com.zs.gms.service.monitor.VehicleLiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED,readOnly = true,rollbackFor = Exception.class)
public class VehicleLiveServiceImpl extends ServiceImpl<VehicleLiveMapper, VehicleLive> implements VehicleLiveService  {

    @Override
    @Transactional
    public void addVehicleLive(VehicleLive vehicleLive) {
        this.save(vehicleLive);
    }

    @Override
    @Transactional
    public IPage<VehicleLive> getVehicleLiveListPage(VehicleLive vehicleLive, QueryRequest request) {
        Page page=new Page();
        SortUtil.handlePageSort(request,page, GmsConstant.SORT_DESC,"ADDTIME");
        QueryWrapper queryWrapper = combinationQuery(vehicleLive);
        if(!ObjectUtils.isEmpty(vehicleLive.getBeginTime())){
            queryWrapper.gt("ADDTIME",vehicleLive.getBeginTime());
        }
        if(!ObjectUtils.isEmpty(vehicleLive.getEndTime())){
            queryWrapper.lt("ADDTIME",vehicleLive.getEndTime());
        }
        IPage iPage = this.baseMapper.selectMapsPage(page, queryWrapper);
        List<Map<String,Object>> records = iPage.getRecords();
        for (Map<String, Object> objectMap : records) {
            //需要解析数据
            //liveData.parseData(objectMap);
        }
        return iPage;
    }

    /**
     * 组合等于查询
     * */
    private QueryWrapper combinationQuery(VehicleLive vehicleLive){
        QueryWrapper queryWrapper=new QueryWrapper();
        Field[] fields = vehicleLive.getClass().getDeclaredFields();
        for (Field field : fields) {
            TableField annotation = field.getAnnotation(TableField.class);
            if(null==annotation||!annotation.exist()){
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(vehicleLive);
                if(null== value){
                    continue;
                }
                queryWrapper.eq(field.getName().toUpperCase(),value);
            } catch (IllegalAccessException e) {
                log.error("字段反射异常",e);
                continue;
            }
        }
        return queryWrapper;
    }
}
