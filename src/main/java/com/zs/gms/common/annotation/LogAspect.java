package com.zs.gms.common.annotation;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.properties.GmsProperties;
import com.zs.gms.common.utils.HttpContextUtil;
import com.zs.gms.common.utils.IPUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.system.SysLog;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.system.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@Aspect
@DependsOn(value = {"gmsProperties","springContextUtil"})
public class LogAspect {

    @Autowired
    @Lazy
    private SysLogService sysLogService;

    private static GmsProperties gmsProperties= (GmsProperties)SpringContextUtil.getBean("gmsProperties");

    @Pointcut(value = "@annotation(com.zs.gms.common.annotation.Log)")
    public void pointCut(){

    }

    @Around(value = "pointCut() && @annotation(annLog)")
    public Object around(ProceedingJoinPoint point, Log annLog) throws Throwable {
        Object result;
        long beginTime = System.currentTimeMillis();
        result=point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        User user = (User) SecurityUtils.getSubject().getPrincipal();

        //保存操作日志
        if(null!=user){
            if(gmsProperties.isOpenAopLog()){
                HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
                String ipAddr = IPUtil.getIpAddr(request);
                SysLog sysLog=new SysLog();
                sysLog.setUserId(user.getUserId());
                sysLog.setUserName(user.getUserName());
                sysLog.setElapsedTime(time);
                sysLog.setIp(ipAddr);
                String value=annLog.value();
                String append = annLog.append();
                if(!StringUtils.isEmpty(append)){//指定参数保存
                    MethodSignature methodSignature = (MethodSignature) point.getSignature();
                    String[] parameterNames = methodSignature.getParameterNames();
                    Object[] params = point.getArgs();
                    int index = ArrayUtils.indexOf(parameterNames, append);
                    if(index!=-1){//获取value参数值
                        value+=":"+params[index];
                    }
                }
                if(result instanceof GmsResponse){//保存返回结果
                    GmsResponse response = (GmsResponse) result;
                    sysLog.setCode(String.valueOf(response.getOrDefault("code","")));
                    sysLog.setResultDesc(String.valueOf(response.getOrDefault("message","")));
                }
                sysLog.setOperateDesc(value);
                if(time> GmsConstant.waitTime){
                    sysLog.setResultDesc("远程接口调用超时");
                }
                sysLogService.addSysLog(sysLog);
                log.info("操作用户: {},操作动作: {},操作耗时：{}",user.getUserName(),value,time);
            }
        }
        return result;
    }

    @AfterThrowing(pointcut = "pointCut() && @annotation(annLog)",throwing = "e")
    public void catchException(JoinPoint point,Log annLog,Exception e){
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        //保存操作日志
        if(null!=user) {
            if (gmsProperties.isOpenAopLog()) {
                HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
                String ipAddr = IPUtil.getIpAddr(request);
                SysLog sysLog=new SysLog();
                sysLog.setUserId(user.getUserId());
                sysLog.setUserName(user.getUserName());
                sysLog.setElapsedTime(0L);
                sysLog.setIp(ipAddr);
                sysLog.setCode("400");
                sysLog.setOperateDesc(annLog.value());
                sysLog.setResultDesc(e.getMessage().length()>500?e.getMessage().substring(0,500):e.getMessage());
                sysLogService.addSysLog(sysLog);
            }
        }

    }
}
