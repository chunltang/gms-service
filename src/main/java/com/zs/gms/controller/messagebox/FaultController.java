package com.zs.gms.controller.messagebox;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.messagebox.Fault;
import com.zs.gms.enums.messagebox.HandleStatus;
import com.zs.gms.service.messagebox.FaultService;
import com.zs.gms.entity.system.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/faults")
@Slf4j
@Api(tags = "消息盒子",description = "Fault Controller")
public class FaultController extends BaseController {

    @Autowired
    @Lazy
    private FaultService faultService;

    @Log("告警列表数据查询")
    @GetMapping
    @ApiOperation(value = "告警列表数据查询",httpMethod = "GET")
    public GmsResponse getFaultList(QueryRequest request) throws GmsException {
        try {
            Map<String, Object> dataTable = this.getDataTable(faultService.getFaultListPage(request));
            return new GmsResponse().data(dataTable).message("告警列表数据查询成功").success();
        }catch (Exception e){
            String message="告警列表数据查询失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("设置故障处理人员")
    @PutMapping("/{faultId}")
    @ApiOperation(value = "设置故障处理人员",httpMethod = "PUT")
    public GmsResponse setFaultUser(@PathVariable Integer faultId) throws GmsException {
        try {
            User currentUser = this.getCurrentUser();
            if(currentUser==null){
                throw new GmsException("当前用户未登录");
            }
            Fault fault=new Fault();
            fault.setFaultId(faultId);
            fault.setHandleName(currentUser.getUserName());
            faultService.updateFaultById(fault);
            return new GmsResponse().message("设置故障处理人员成功").success();
        }catch (Exception e){
            String message="设置故障处理人员失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("修改故障处理进度")
    @PutMapping("/statuses")
    @ApiOperation(value = "修改故障处理进度",httpMethod = "PUT")
    public GmsResponse updateFaultStatus(@MultiRequestBody("faultId") Integer faultId,
                                         @MultiRequestBody("status") HandleStatus status) throws GmsException {
        try {
            faultService.updateFaultStatus(faultId,status);
            return new GmsResponse().message("修改故障处理进度成功").success();
        }catch (Exception e){
            String message="修改故障处理进度失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }
}
