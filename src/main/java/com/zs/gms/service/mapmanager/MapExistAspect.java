package com.zs.gms.service.mapmanager;

import com.zs.gms.common.authentication.ShiroHelper;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.HttpContextUtil;
import com.zs.gms.entity.mapmanager.MapInfo;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.system.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Aspect
@Slf4j
@Component
public class MapExistAspect {

    @Autowired
    @Lazy
    private MapInfoService mapInfoService;

    @Around(value = "execution(* com.zs.gms.controller.mapmanager.GAreaController.*(..))")
    public Object before(ProceedingJoinPoint point) throws Throwable {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] params = point.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            if(parameterNames[i].equals("mapId")){
                Integer mapId= GmsUtil.typeTransform(params[i],Integer.class);
                MapInfo mapInfo = mapInfoService.getMapInfo(mapId);
                if(null==mapInfo){
                    throw new GmsException("地图不存在!");
                }
            }
        }
        return point.proceed();
    }
}
