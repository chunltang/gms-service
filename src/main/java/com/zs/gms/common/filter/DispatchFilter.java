package com.zs.gms.common.filter;


import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.service.init.HeartBeatCheck;
import com.zs.gms.service.init.ScheduleTask;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 调度模块过滤，只有存在活动地图的情况下才能执行操作
 * */
@Slf4j
@WebFilter(filterName = "dispatchFilter",urlPatterns ={"/dispatchTasks/*"})
public class DispatchFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        if(HeartBeatCheck.getServiceStatus(HeartBeatCheck.CheckStatusEnum.DISPATCH_SERVICE_STATUS)){
            GmsResponse gmsResponse = new GmsResponse().message("当前调度服务断开连接，不能执行调度相关操作").badRequest();
            GmsUtil.callResponse(gmsResponse,res);
            return;
        }
        Integer mapId = GmsUtil.getActiveMap();
        if(mapId==null){
            log.debug("当前不存在活动地图，不能执行调度相关操作");
            GmsResponse gmsResponse = new GmsResponse().message("当前不存在活动地图，不能执行调度相关操作").badRequest();
            GmsUtil.callResponse(gmsResponse,res);
            return;
        }
        chain.doFilter(request,response);
    }
}
