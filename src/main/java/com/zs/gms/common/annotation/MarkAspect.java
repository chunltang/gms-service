package com.zs.gms.common.annotation;

import com.zs.gms.common.interfaces.MarkInterface;
import com.zs.gms.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class MarkAspect {

    @Pointcut(value = "@annotation(com.zs.gms.common.annotation.Mark)")
    public void pointCut(){

    }

    @After(value = "pointCut() && @annotation(mark)")
    public void after(JoinPoint point, Mark mark) {
       log.debug("触发mark:{}",mark.value());
        MarkInterface bean = SpringContextUtil.getBean(mark.markImpl());
        bean.execute();
    }
}
