package com.zs.gms.controller.gpsmanager;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.gpsmanager.Gps;
import com.zs.gms.service.gpsmanager.GpsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/gpses")
@Slf4j
@Api(tags = {"GPS管理"},description = "gps Controller")
@Validated
public class GpsController extends BaseController {

    @Autowired
    private GpsService gpsService;

    @Log("添加GPS")
    @PostMapping
    @ApiOperation(value = "添加GPS",httpMethod = "POST")
    public GmsResponse addGps(@MultiRequestBody("gps")@Valid Gps gps) throws GmsException {
        boolean existNo = gpsService.isExistNo(gps.getGpsNo());
        if(existNo){
            return new GmsResponse().message("该编号已添加").badRequest();
        }
        try {
            gps.setUserId(super.getCurrentUser().getUserId());
            gpsService.addGps(gps);
            return new GmsResponse().message("添加GPS成功").success();
        }catch (Exception e){
            String message="添加GPS失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("修改GPS参数")
    @PutMapping
    @ApiOperation(value = "修改GPS参数",httpMethod = "PUT")
    public GmsResponse updateGps(@MultiRequestBody("gps") Gps gps) throws GmsException {
        Integer gpsId = gps.getGpsId();
        if(null==gpsId){
            throw new GmsException("参数异常，id不能为空");
        }
        boolean existNo = gpsService.isExistNo(gps.getGpsNo());
        if(existNo){
            return new GmsResponse().message("该编号已存在").badRequest();
        }
        try {
            gpsService.updateGps(gps);
            return new GmsResponse().message("修改GPS参数成功").success();
        }catch (Exception e){
            String message="修改GPS参数失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("删除GPS")
    @DeleteMapping("/{gpsId}")
    @ApiOperation(value = "删除GPS",httpMethod = "DELETE")
    public GmsResponse delGps(@PathVariable("gpsId") Integer gpsId) throws GmsException {
        boolean existId = gpsService.isExistId(gpsId);
        if(existId){
            return new GmsResponse().message("该id记录不存在").badRequest();
        }
        try {
            gpsService.delGps(gpsId);
            return new GmsResponse().message("删除GPS成功").success();
        }catch (Exception e){
            String message="删除GPS失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
