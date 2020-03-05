package com.zs.gms.common.annotation;

import com.zs.gms.common.interfaces.MarkInterface;
import org.springframework.core.io.support.PropertySourceFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mark {

    String value() default "";

    Class<? extends MarkInterface> markImpl();

}
