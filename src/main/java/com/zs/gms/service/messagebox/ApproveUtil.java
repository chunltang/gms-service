package com.zs.gms.service.messagebox;

import com.zs.gms.common.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApproveUtil {

    public static void addError(Integer approveId,String errorMessage){
        ApproveService approveService = SpringContextUtil.getBean(ApproveService.class);
        approveService.addError(approveId,errorMessage);
    }
}
