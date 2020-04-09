package com.zs.gms.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StatisticsMapper extends Mapper {

    List<Map<String,Object>> statisticsByUser(@Param("userId") Integer userId,@Param("format") String format);

    List<Map<String, Object>> statisticsByVehicle(@Param("vehicleId")Integer vehicleId,@Param("format") String format);

    List<Map<String, Object>> statisticsByExcavator(@Param("excavatorId")Integer excavatorId,@Param("format") String format);

    List<Map<String, Object>> statisticsByUnload(@Param("unloadId")Integer unloadId,@Param("format") String format);

    List<Map<String, Object>> statisticsByWarn(@Param("vehicleId")Integer vehicleId,@Param("format") String format);
}
