package com.zs.gms.service.mapmanager;

import com.zs.gms.common.authentication.ShiroHelper;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.HttpContextUtil;
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

@Aspect
@Slf4j
@Component
public class MapEditLockAspect {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private ShiroHelper shiroHelper;

    @Around(value = "execution(* com.zs.gms.controller.mapmanager.SAreaController.*(..))")
    public Object before(ProceedingJoinPoint point) throws Throwable {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] params = point.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            if(parameterNames[i].equals("mapId")){
                Integer mapId= GmsUtil.typeTransform(params[i],Integer.class);
                if(GmsUtil.allObjNotNull(user,mapId)){
                    boolean editLock = MapDataUtil.editLock(mapId, user.getUserId().toString());
                    if(!editLock){
                        Object lockUserId = MapDataUtil.getLockUser(mapId);
                        Integer userId = GmsUtil.typeTransform(lockUserId, Integer.class);
                        if(!shiroHelper.isOnline(userId)){//锁定用户不在线,改变锁定用户,为了防止退出时没有释放锁
                            MapDataUtil.releaseLock(mapId,userId.toString());
                            MapDataUtil.editLock(mapId, user.getUserId().toString());
                            break;
                        }
                        User editUser = userService.findUserById(userId);
                        GmsResponse gmsResponse = new GmsResponse().message("用户["+editUser.getUserName()+"]正在编辑地图，当前地图被锁定").badRequest();
                        HttpServletResponse response = HttpContextUtil.getHttpServletResponse();
                        GmsService.callResponse(gmsResponse,response);
                        return null;
                    }
                }
            }
        }
        return point.proceed();
    }
}
