package com.zs.gms.service.messagebox;

import com.zs.gms.common.service.nettyclient.WsUtil;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.messagebox.Approve;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApproveUtil {

    public static void addError(Integer approveId,String errorMessage){
        ApproveService approveService = SpringContextUtil.getBean(ApproveService.class);
        approveService.addError(approveId,errorMessage);
        Approve approve = approveService.getApprove(approveId);
        WsUtil.sendMessage(String.valueOf(approve.getSubmitUserId()), GmsUtil.toJsonIEnumDesc(approve), FunctionEnum.approve);
    }
}
