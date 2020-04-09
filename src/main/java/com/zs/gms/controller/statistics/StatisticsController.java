package com.zs.gms.controller.statistics;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.entity.Export;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.ExportUtil;
import com.zs.gms.enums.statistics.DateFormatEnum;
import com.zs.gms.service.statistics.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/statistics")
@Api(tags ="数据统计",description = "Statistics Controller")
@Slf4j
public class StatisticsController{

    @Autowired
    private StatisticsService statisticsService;

    @Log("导出车辆数据")
    @GetMapping(value = "/vehicles")
    @ApiOperation(value = "导出车辆数据",httpMethod = "GET")
    public void switchToAutoModel(HttpServletResponse response,Integer num) throws GmsException {
        try{
            Export export=new Export();
            export.setFileName("bigtest");
            export.setType(Export.Type.xlsx);
            export.setSheetName("车辆数据");
            Map<String,String> headers=new HashMap<>();
            headers.put("vehicleNo","车辆编号");
            headers.put("vehicleId","车辆ID");
            export.setHeaders(headers);
            List<Map<String,Object>> dataMap=new ArrayList<>();
            for (int i = 0; i < num; i++) {
                Map<String,Object> dataObj=new HashMap<>();
                dataObj.put("vehicleNo","No."+i);
                dataObj.put("vehicleId",i);
                dataMap.add(dataObj);
            }
            export.setExportData(dataMap);
            ExportUtil.export(response,export);
        }catch (Exception e){
            String message="导出车辆数据失败";
            throw new GmsException(message);
        }
    }

    @Log("统计调度员工作情况")
    @GetMapping(value = "/users")
    @ApiOperation(value = "统计调度员工作情况",httpMethod = "GET")
    public GmsResponse statisticsByUser(Integer userId, String format) throws GmsException{
        try {
            List<Map<String, Object>> maps = statisticsService.statisticsByUser(userId, DateFormatEnum.getInstance(format));
            return new GmsResponse().data(maps).message("统计数据获取成功").success();
        }catch (Exception e){
            String message="统计数据失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("统计矿车工作情况")
    @GetMapping(value = "/barneys")
    @ApiOperation(value = "导出车辆数据",httpMethod = "GET")
    public GmsResponse statisticsByVehicle(Integer vehicleId, String format) throws GmsException{
        try {
            List<Map<String, Object>> maps = statisticsService.statisticsByVehicle(vehicleId, DateFormatEnum.getInstance(format));
            return new GmsResponse().data(maps).message("统计数据获取成功").success();
        }catch (Exception e){
            String message="统计数据失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("统计卸载区工作情况")
    @GetMapping(value = "/unloads")
    @ApiOperation(value = "导出车辆数据",httpMethod = "GET")
    public GmsResponse statisticsByUnload(Integer unloadId,String format) throws GmsException{
        try {
            List<Map<String, Object>> maps = statisticsService.statisticsByUnload(unloadId, DateFormatEnum.getInstance(format));
            return new GmsResponse().data(maps).message("统计数据获取成功").success();
        }catch (Exception e){
            String message="统计数据失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("统计挖掘机工作情况")
    @GetMapping(value = "/excavators")
    @ApiOperation(value = "导出车辆数据",httpMethod = "GET")
    public GmsResponse statisticsByExcavator(Integer excavatorId,String format) throws GmsException{
        try {
            List<Map<String, Object>> maps = statisticsService.statisticsByExcavator(excavatorId, DateFormatEnum.getInstance(format));
            return new GmsResponse().data(maps).message("统计数据获取成功").success();
        }catch (Exception e){
            String message="统计数据失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("自动矿车告警信息统计")
    @GetMapping(value = "/warnings")
    @ApiOperation(value = "自动矿车告警信息统计",httpMethod = "GET")
    public GmsResponse statisticsByFault(Integer vehicleId,String format) throws GmsException{
        try {
            List<Map<String, Object>> maps = statisticsService.statisticsByWarn(vehicleId, DateFormatEnum.getInstance(format));
            return new GmsResponse().data(maps).message("统计数据获取成功").success();
        }catch (Exception e){
            String message="统计数据失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
