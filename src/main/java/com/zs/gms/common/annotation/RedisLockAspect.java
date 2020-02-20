package com.zs.gms.common.annotation;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class RedisLockAspect {

    @Pointcut(value = "@annotation(com.zs.gms.common.annotation.RedisLock)")
    public void point(){

    }

    @Around(value = "point() && @annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint,RedisLock redisLock) throws Throwable {
        boolean flag = RedisService.lockService(redisLock.key(), GmsConstant.SERVICE_NAME, redisLock.seconds());
        if(flag){
            return joinPoint.proceed();
        }
        return null;
    }
}
