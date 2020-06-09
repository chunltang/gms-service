package com.zs.gms.controller.mapmanager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mineralmanager.Mineral;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.messagebox.ApproveType;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.messagebox.ApproveService;
import com.zs.gms.service.system.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/maps")
@Validated
@Slf4j
@Api(tags = {"地图管理"}, description = "Map Controller")
public class DAreaController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApproveService approveService;

    @Log("删除区域")
    @DeleteMapping(value = "/{mapId}/areas/{areaId}")
    @ApiOperation(value = "删除区域", httpMethod = "DELETE")
    public void deleteArea(@PathVariable("mapId") Integer mapId, @PathVariable("areaId") Integer areaId) throws GmsException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("mapId", mapId);
            params.put("areaId", areaId);
            String jsonStr = JSONObject.toJSON(params).toString();
            MessageFactory.getMapMessage().sendMessageNoID("deleteArea", jsonStr, "删除区域成功");
        } catch (Exception e) {
            String message = "删除区域失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("删除区域元素")
    @DeleteMapping(value = "/{mapId}/areas/{areaId}/{elementId}")
    @ApiOperation(value = "删除区域元素", httpMethod = "DELETE")
    public void deleteElement(@PathVariable("mapId") Integer mapId,
                              @PathVariable("areaId") Integer areaId,
                              @PathVariable("elementId") Integer elementId) throws GmsException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("mapId", mapId);
            params.put("areaId", areaId);
            params.put("elementId", elementId);
            String jsonStr = JSONObject.toJSON(params).toString();
            MessageFactory.getMapMessage().sendMessageNoID("deleteElement", jsonStr, "删除区域元素成功");
        } catch (Exception e) {
            String message = "删除区域元素失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    //tcl 05-09 新增接口
    @Log("删除障碍物")
    @DeleteMapping(value = "/obstacles/{id}")
    @ApiOperation(value = "删除障碍物", httpMethod = "DELETE")
    public GmsResponse removeObstacle(@PathVariable("id") Integer id,String name) throws GmsException {
        try {
            User user = super.getCurrentUser();
            boolean approve = addApprove(user, MapDataUtil.getActiveMap(), id,name);
            if(approve){
                return new GmsResponse().message("删除障碍物提交成功").success();
            }else{
                return new GmsResponse().message("删除障碍物提交失败").badRequest();
            }
        } catch (Exception e) {
            String message = "删除障碍物失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    private boolean addApprove(User user,Integer mapId, Integer obstacleId,String name) {
        List<User> users = userService.getUsersByRoleSign(Role.RoleSign.CHIEFDESPATCHER_ROLE.getValue());
        if (CollectionUtils.isNotEmpty(users)) {
            HashSet<String> userIds = new HashSet<>();
            for (User u : users) {
                Integer id = u.getUserId();
                userIds.add(String.valueOf(id));
            }
            Map<String, Object> params = new HashMap<>();
            String join = String.join(StringPool.COMMA, userIds.toArray(new String[0]));
            params.put("obstacleId", obstacleId);
            params.put("mapId", mapId);
            params.put("name", name);
            Integer id = approveService.createApprove(params, join, user, ApproveType.OBSTACLEDELETE, false);
            return null != id;
        }
        return false;
    }
}
