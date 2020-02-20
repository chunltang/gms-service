package com.zs.gms.service.messagebox;

import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.messagebox.ApproveType;

import java.util.List;
import java.util.Map;

public interface ApproveService {

     Integer addApprove(Approve approve);

     /**
      * @param params  请求参数
      * @param userIds  审批人
      * @param user     提交人
      * @param approveType    提交类型
      * @param rule  审批规则
      * */
    Integer createApprove(Map<String,Object> params, String userIds, User user, ApproveType approveType, boolean rule);

    /**
     * 删除提交
     * */
     boolean updateStatus(Integer approveId,Approve.Status status);

     Approve updateProcess(Integer approveId,String userId,Approve.Status status,String reason);

     void sendApproveResult(Approve approve);

    /**
     * 获取指定人员没有审批的消息
     * */
     List<Approve> getApproveRemaining(String userId);

    /**
     * 获取没有标记的消息
     * */
     List<Approve> getApproveNoMark(String userId);

    /**
     * 审批结果标记为已读
     * */
     void updateApproveResult(Integer approveId);

    /**
     * 添加异常信息
     * */
     void addError(Integer approveId,String error);
}
