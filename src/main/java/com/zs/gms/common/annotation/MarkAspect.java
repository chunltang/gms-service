package com.zs.gms.common.annotation;

import com.zs.gms.common.interfaces.MarkInterface;
import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.service.MapThreadLocal;
import com.zs.gms.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 触发标记
 */
@Slf4j
@Component
@Aspect
public class MarkAspect {

    /**
     * 在一个处理中，同一个mark只会触发一次
     */
    private MapThreadLocal<Class<? extends MarkInterface>,Boolean> mapThreadLocal = new MapThreadLocal();


    @Pointcut(value = "@annotation(com.zs.gms.common.annotation.Mark)")
    public void pointCut() {

    }

    @After(value = "pointCut() && @annotation(mark)")
    public void after(JoinPoint point, Mark mark) {
        if(mapThreadLocal.getValue(mark.markImpl(),false)){
            return;
        }
        mapThreadLocal.setValue(mark.markImpl(),true);
        log.debug("触发mark:{},impl:{}", mark.value(),mark.markImpl().getSimpleName());
        MarkInterface bean = SpringContextUtil.getBean(mark.markImpl());
        DelayedService.addTask(bean::execute,1000).withDesc("mark任务执行");
    }
}
