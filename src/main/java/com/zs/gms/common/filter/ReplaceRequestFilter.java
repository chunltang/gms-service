package com.zs.gms.common.filter;

import com.zs.gms.common.annotation.MarkAspect;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class ReplaceRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        ServletRequest requestWrapper = new RequestWrapper(httpServletRequest);
        if(log.isDebugEnabled()){
            String path = httpServletRequest.getRequestURI();
            String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
            String method = httpServletRequest.getMethod();
            RequestWrapper wrapper = (RequestWrapper) requestWrapper;
            String params = wrapper.getBodyString();
            log.debug("请求method:{},url:{},params:{}",method,basePath,params.length()>1000?params.substring(0,1000):params);
        }
        chain.doFilter(requestWrapper,response);
    }
}
