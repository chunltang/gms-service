package com.zs.gms.controller.system;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.entity.system.SysLog;
import com.zs.gms.enums.system.LogLevelEnum;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.service.system.SysLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = {"用户管理"},description = "User Controller")
@RestController
@RequestMapping(value = "/logs")
@Slf4j
public class LogController extends BaseController {

    @Autowired
    @Lazy
    private SysLogService sysLogService;

    /**
     * 设置日志级别
     * */
    @Log("设置日志级别")
    @ApiOperation(value = "设置日志级别",httpMethod = "PUT")
    @PutMapping
    public GmsResponse updateLogLevel(@MultiRequestBody("configuredLevel") LogLevelEnum configuredLevel,@MultiRequestBody("packageName") String packageName) throws GmsException{
        if(null==configuredLevel||LogLevelEnum.getAllEnumDesc().contains(configuredLevel.getDesc())){
            throw new GmsException("日志级别为空或异常");
        }

        Logger logger = (Logger) (Logger) log;
        if(StringUtils.isEmpty(packageName)){
            List<Logger> loggers = logger.getLoggerContext().getLoggerList();
            for (Logger er : loggers) {
                er.setLevel(Level.toLevel(configuredLevel.getDesc()));
            }
        }else{
            logger.getLoggerContext().getLogger(packageName).setLevel(Level.toLevel(configuredLevel.getDesc()));
        }
        log.info("包名：{}，设置日志级别为：{}",packageName,configuredLevel.getDesc());
        return new GmsResponse().success();
    }

    @Log("查询用户日志")
    @GetMapping
    @ApiOperation(value = "查询用户日志",httpMethod = "GET")
    public GmsResponse getSysLogList(SysLog sysLog, QueryRequest request) throws GmsException {
        try {
            Map<String, Object> dataTable = this.getDataTable(sysLogService.getSysLogListPage(sysLog, request));
            return new GmsResponse().data(dataTable).message("查询用户日志").success();
        }catch (Exception e){
            String message="查询用户日志失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }
}
