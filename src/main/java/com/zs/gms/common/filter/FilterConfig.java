package com.zs.gms.common.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean filterRegistrationBean(){//注册过滤器
        FilterRegistrationBean registrationBean=new FilterRegistrationBean();
        registrationBean.setFilter(replaceRequestFilter());
        registrationBean.setName("paramFilter");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
    public ReplaceRequestFilter replaceRequestFilter(){
        return new ReplaceRequestFilter();
    }
}


