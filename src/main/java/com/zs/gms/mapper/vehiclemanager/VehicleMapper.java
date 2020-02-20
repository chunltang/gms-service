package com.zs.gms.mapper.vehiclemanager;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.gms.entity.vehiclemanager.Vehicle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VehicleMapper extends BaseMapper<Vehicle> {

    /**
     * 分页查询
     * */
    public IPage<Vehicle> findVehicleListPage(Page page, Vehicle vehicle);

    public List<Vehicle> findVehicleList(@Param("vehicle") Vehicle vehicle);

    public Integer findUserIdByVehicleNo(@Param("vehicleNo") Integer vehicleNo);
}
