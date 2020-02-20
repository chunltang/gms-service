package com.zs.gms.common.filter;


import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 调度模块过滤，只有存在活动地图的情况下才能执行操作
 * */
@Slf4j
@WebFilter(filterName = "dispatchFilter",urlPatterns ={"/dispatchTasks/*"})
public class DispatchFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Integer mapId = GmsUtil.getActiveMap();
        if(mapId==null){
            log.debug("当前不存在活动地图，不能执行调度相关操作");
            HttpServletResponse res = (HttpServletResponse) response;
            GmsResponse gmsResponse = new GmsResponse().message("当前不存在活动地图，不能执行调度相关操作").badRequest();
            res.setHeader("Content-Type", "application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print(GmsUtil.toJson(gmsResponse));
            writer.flush();
            writer.close();
            return;
        }
        chain.doFilter(request,response);
    }
}
