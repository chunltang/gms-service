package com.zs.gms.service.messagebox;

import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.messagebox.ApproveType;
import org.springframework.transaction.annotation.Transactional;

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

    Approve getApprove(Integer approveId);

    void sendMessage(Approve approve);

    /**
     * 删除提交
     * */
     boolean deleteApprove(Integer approveId, Approve.Status status);

     Approve updateProcess(Integer approveId,String userId,Approve.Status status,String reason);

     void sendApproveResult(Approve approve);

     /**
      * 同一人、同类型审批，先提交的将被删除
      * */
     void deleteOtherApprove();

     /**
      * 等待中的审批可以取消
      * */
     void cancel(Approve approve);

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

     /**
      * 查询对应类型的审批提交记录,是等待中的审批
      * */
     List<Approve> getApproveListByType(ApproveType approveType);

     /**
      * @param val 要匹配的参数
      * @param key 要匹配的key
      * @param approveType 要匹配的类型
      * */
     void delApproveByKey(Object val,String key,ApproveType approveType);

     void delAllApproveByKey(ApproveType approveType);

     /**
      * 是否存在对应参数和类型的审批,true为存在
      * */
     boolean isExistApproveByKey(Object val,String key,ApproveType approveType);
}
