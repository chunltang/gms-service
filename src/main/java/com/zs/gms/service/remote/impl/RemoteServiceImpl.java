package com.zs.gms.service.remote.impl;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.nettyclient.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.messagebox.ApproveType;
import com.zs.gms.service.messagebox.ApproveInterface;
import com.zs.gms.service.messagebox.ApproveService;
import com.zs.gms.service.remote.RemoteService;
import com.zs.gms.service.system.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RemoteServiceImpl implements RemoteService, ApproveInterface {

    @Autowired
    private ApproveService approveService;

    @Autowired
    private UserService userService;

    @Override
    public boolean updateStatus(Approve approve) {
        Integer userId = approve.getSubmitUserId();
        WsUtil.sendMessage(String.valueOf(userId), GmsUtil.toJsonIEnumDesc(approve), FunctionEnum.approve);
        String userIds = approve.getApproveUserIds();
        String[] ids = userIds.split(StringPool.COMMA);
        for (String id : ids) {
            WsUtil.sendMessage(id, GmsUtil.toJsonIEnumDesc(approve), FunctionEnum.approve);
        }
        return true;
    }

    @Override
    public boolean remoteAccess(Integer vehicleId,User user) {
        List<User> users = userService.getUsersByRoleSign(Role.RoleSign.CHIEFDESPATCHER_ROLE.getValue());
        if(CollectionUtils.isEmpty(users)){
            log.debug("申请进入控制台，审批人为空");
            return false;
        }
        String[] userIds = users.stream().map(u -> {
            return String.valueOf(u.getUserId());
        }).toArray(String[]::new);
        Map<String,Object> params=new HashMap<>();
        params.put("vehicleId",vehicleId);
        Integer approve = approveService.createApprove(params, String.join(StringPool.COMMA,userIds), user, ApproveType.REMOTEACCESS, false);
        if(null!=approve){
            return true;
        }
        return false;
    }
}
