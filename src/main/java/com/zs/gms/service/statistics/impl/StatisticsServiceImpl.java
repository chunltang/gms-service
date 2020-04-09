package com.zs.gms.service.statistics.impl;

import com.zs.gms.common.properties.VehicleCode;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.enums.statistics.DateFormatEnum;
import com.zs.gms.mapper.statistics.StatisticsMapper;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.statistics.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    @Lazy
    private VehicleCode vehicleCode;

    @Autowired
    @Lazy
    private StatisticsMapper statisticsMapper;

    @Override
    public List<Map<String, Object>> statisticsByUser(Integer userId, DateFormatEnum format) {
        if (!GmsUtil.objNotNull(format)) {
            return new ArrayList<>();
        }
        return this.statisticsMapper.statisticsByUser(userId, format.getFormat());
    }

    @Override
    public List<Map<String, Object>> statisticsByVehicle(Integer vehicleId, DateFormatEnum format) {
        if (!GmsUtil.objNotNull(format)) {
            return new ArrayList<>();
        }
        return this.statisticsMapper.statisticsByVehicle(vehicleId, format.getFormat());
    }

    @Override
    public List<Map<String, Object>> statisticsByExcavator(Integer excavatorId, DateFormatEnum format) {
        if (!GmsUtil.objNotNull(format)) {
            return new ArrayList<>();
        }
        return this.statisticsMapper.statisticsByExcavator(excavatorId, format.getFormat());
    }

    @Override
    public List<Map<String, Object>> statisticsByUnload(Integer unloadId, DateFormatEnum format) {
        if (!GmsUtil.objNotNull(format)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> maps = this.statisticsMapper.statisticsByUnload(unloadId, format.getFormat());
        for (Map<String, Object> map : maps) {
            List<SemiStatic> semiStatics = MapDataUtil.getAreaInfos(GmsUtil.typeTransform(map.get("mapId"), Integer.class), AreaTypeEnum.UNLOAD_MINERAL_AREA);
            String unloadName = "";
            for (SemiStatic aStatic : semiStatics) {
                if (aStatic.getId().equals(map.get("unloadId"))) {
                    unloadName = aStatic.getName();
                    break;
                }
            }
            map.put("unloadName", unloadName);
        }
        return maps;
    }

    @Override
    @Transactional
    public List<Map<String, Object>> statisticsByWarn(Integer vehicleId, DateFormatEnum format) {
        if (!GmsUtil.objNotNull(format)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> maps = this.statisticsMapper.statisticsByWarn(vehicleId, format.getFormat());
        Map<Integer, VehicleCode.Code> codeMap = this.vehicleCode.getCode();
        Map<Integer, Map<Integer, String>> warnDescMap = this.vehicleCode.getWarnDesc();
        for (Map<String, Object> map : maps) {
            Object partNo = map.getOrDefault("partNo", "");
            Object warnCode = map.getOrDefault("warnCode", "");
            VehicleCode.Code code = codeMap.getOrDefault(GmsUtil.typeTransform(partNo, Integer.class), null);
            if (null != code) {
                Integer type = code.getWarnDescCode();
                map.put("partName", code.getPartName());
                map.put("level", code.getLevel());
                Map<Integer, String> warnDescOrDefault = warnDescMap.getOrDefault(type, null);
                if (null != warnDescOrDefault) {
                    map.put("warnDesc", warnDescOrDefault.getOrDefault(warnCode, ""));
                } else {
                    map.put("warnDesc", "");
                }
            } else {
                log.error("车辆异常码没有配置:" + partNo);
            }
        }
        return maps;
    }
}
