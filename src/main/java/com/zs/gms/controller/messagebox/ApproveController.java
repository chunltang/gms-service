package com.zs.gms.controller.messagebox;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.utils.HttpContextUtil;
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

import javax.servlet.http.HttpServletResponse;
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
    public void updateProcess(@PathVariable Integer approveId,
                              @MultiRequestBody("userId") String userId,
                              @MultiRequestBody("status") Approve.Status status,
                              @MultiRequestBody(value = "reason", parseAllFields = false, required = false)String reason,
                              HttpServletResponse response) throws GmsException {

        if (!ObjectUtils.allNotNull(userId, status)) {
            throw new GmsException("参数异常");
        }
        try {
            Approve app = approveService.getApprove(approveId);
            if(null==app){
                GmsService.callResponse(new GmsResponse().message("审批不存在或已被撤销!").badRequest(), response);
                 return;
            }
            User user = super.getCurrentUser();
            //提交人删除审批
            if (status.equals(Approve.Status.DELETE)) {
                if (String.valueOf(user.getUserId()).equals(userId)) {
                    approveService.deleteApprove(approveId, status);
                    app.setStatus(Approve.Status.DELETE);
                    approveService.sendMessage(app);//发送取消审批
                    GmsService.callResponse(new GmsResponse().message("取消审批成功").success(), response);
                    return;
                }
                GmsService.callResponse(new GmsResponse().message("当前不是审批提交人，取消审批失败").badRequest(), response);
            } else {
                Approve approve = approveService.updateProcess(approveId, userId, status, reason);
                if (approve != null) {
                    approveService.sendApproveResult(approve);
                    GmsService.callResponse(new GmsResponse().message("更新审批成功").success(), response);
                } else {
                    GmsService.callResponse(new GmsResponse().message("更新审批失败").badRequest(), response);
                }
            }
        } catch (Exception e) {
            String message = "更新审批失败";
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
