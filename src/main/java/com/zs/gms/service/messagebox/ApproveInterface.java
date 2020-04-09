package com.zs.gms.service.messagebox;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.messagebox.Approve;

public interface ApproveInterface {

    boolean updateStatus(Approve approve);

    /**
     * 取消审批
     */
    default void cancel(Approve approve){};
}
