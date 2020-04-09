package com.zs.gms.controller.messagebox;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.messagebox.ApproveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/approves")
@Slf4j
@Api(tags = "消息盒子", description = "Approve Controller")
public class ApproveController extends BaseController {

    @Autowired
    @Lazy
    private ApproveService approveService;

    @Log("更新审批结果")
    @PutMapping("/{approveId}")
    @ApiOperation(value = "更新审批结果", httpMethod = "PUT")
    public GmsResponse updateProcess(@PathVariable Integer approveId,
                                     @MultiRequestBody("userId") String userId,
                                     @MultiRequestBody("status") Approve.Status status,
                                     @MultiRequestBody(value = "reason", parseAllFields = false, required = false) String reason) throws GmsException {

        if (!ObjectUtils.allNotNull(userId, status)) {
            throw new GmsException("参数异常");
        }
        try {
            User user = super.getCurrentUser();
            //提交人删除审批
            if (status.equals(Approve.Status.DELETE)) {
                if (String.valueOf(user.getUserId()).equals(userId)) {
                    approveService.deleteApprove(approveId, status);
                    return new GmsResponse().message("删除审批成功").success();
                }
                return new GmsResponse().message("当前不是审批提交人，删除审批失败").badRequest();
            } else {
                Approve approve = approveService.updateProcess(approveId, userId, status, reason);
                if (approve != null) {
                    approveService.sendApproveResult(approve);
                    return new GmsResponse().message("更新审批结果成功").success();
                } else {
                    return new GmsResponse().message("更新审批结果失败").badRequest();
                }
            }

        } catch (Exception e) {
            String message = "更新审批结果失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("审批结果标记为已读")
    @PutMapping("/{approveId}/mark")
    @ApiOperation(value = "审批结果标记为已读", httpMethod = "PUT")
    public GmsResponse updateProcess(@PathVariable Integer approveId) throws GmsException {
        try {
            approveService.updateApproveResult(approveId);
            return new GmsResponse().message("审批结果标记为已读成功").success();
        } catch (Exception e) {
            String message = "审批结果标记为已读失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取所有未处理审批")
    @GetMapping
    @ApiOperation(value = "获取所有未处理审批", httpMethod = "GET")
    public GmsResponse getProcess() throws GmsException {
        try {
            User user = super.getCurrentUser();
            List<Approve> remaining = approveService.getApproveRemaining(String.valueOf(user.getUserId()));
            List<Approve> noMark = approveService.getApproveNoMark(String.valueOf(user.getUserId()));
            ArrayList<Approve> approves = new ArrayList<>();
            approves.addAll(remaining);
            approves.addAll(noMark);
            return new GmsResponse().data(approves).message("获取所有未处理审批成功").success();
        } catch (Exception e) {
            String message = "获取所有未处理审批失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}
