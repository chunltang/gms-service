package com.zs.gms.service.statistics;

import com.zs.gms.enums.statistics.DateFormatEnum;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    List<Map<String,Object>> statisticsByUser(Integer userId, DateFormatEnum format);

    List<Map<String,Object>> statisticsByVehicle(Integer vehicleId, DateFormatEnum format);

    List<Map<String, Object>> statisticsByExcavator(Integer excavatorId, DateFormatEnum format);

    List<Map<String, Object>> statisticsByUnload(Integer unloadId, DateFormatEnum format);

    List<Map<String, Object>> statisticsByWarn(Integer vehicleId, DateFormatEnum format);
}
