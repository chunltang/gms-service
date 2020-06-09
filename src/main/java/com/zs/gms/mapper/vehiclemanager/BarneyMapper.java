package com.zs.gms.mapper.vehiclemanager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.entity.vehiclemanager.Barney;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BarneyMapper extends BaseMapper<Barney> {

    /**
     * 分页查询
     */
    IPage<Barney> findVehicleListPage(Page page, Barney barney);

    List<Barney> findVehicleList(@Param("barney") Barney barney);

    Integer findUserIdByVehicleNo(@Param("vehicleNo") Integer vehicleNo);

    List<Map<String, Object>> getBarneyBaseInfos();
}
