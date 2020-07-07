package com.zs.gms.controller.client;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.mineralmanager.AreaMineral;
import com.zs.gms.entity.mineralmanager.Mineral;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.enums.messagebox.ApproveType;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.messagebox.ApproveService;
import com.zs.gms.service.mineralmanager.AreaMineralService;
import com.zs.gms.service.mineralmanager.MineralService;
import com.zs.gms.service.monitor.UnitService;
import com.zs.gms.service.system.UserService;
import com.zs.gms.service.vehiclemanager.UserExcavatorLoadAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/clients/excavator")
@Slf4j
@Api(tags = {"客户端管理"}, description = "client Controller")
public class ExcavatorsController extends BaseController {

    @Autowired
    @Lazy
    private UnitService unitService;

    @Autowired
    private MineralService mineralService;

    @Autowired
    private ApproveService approveService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserExcavatorLoadAreaService bindService;

    @Autowired
    private AreaMineralService areaMineralService;

    @Log("发送进车信号")
    @PutMapping("/signals/into")
    @ApiOperation(value = "发送进车信号", httpMethod = "PUT")
    public void intoSignal(@MultiRequestBody("taskSpotId") Integer taskSpotId) throws GmsException {
        Integer unitId = getUnitId(taskSpotId);
        if (!GmsUtil.allObjNotNull(unitId, taskSpotId)) {
            throw new GmsException("参数异常,装载区没有分配调度单元");
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("unitId", unitId);
            params.put("taskSpotId", taskSpotId);
            MessageFactory.getDispatchMessage().sendMessageNoID("LoadAreaEntry", GmsUtil.toJson(params), "进车信号发送成功");
        } catch (Exception e) {
            String message = "进车信号发送失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("发送出车信号")
    @PutMapping("/signals/out")
    @ApiOperation(value = "发送出车信号", httpMethod = "PUT")
    public void outSignal(@MultiRequestBody("taskSpotId") Integer taskSpotId) throws GmsException {
        Integer unitId = getUnitId(taskSpotId);
        if (!GmsUtil.allObjNotNull(unitId, taskSpotId)) {
            throw new GmsException("参数异常,装载区没有分配调度单元");
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("unitId", unitId);
            params.put("taskSpotId", taskSpotId);
            MessageFactory.getDispatchMessage().sendMessageNoID("LoadAreaWorkDone", GmsUtil.toJson(params), "出车信号发送成功");
        } catch (Exception e) {
            String message = "发送出车信号失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("任务点开装")
    @PutMapping("/signals/exec")
    @ApiOperation(value = "任务点开始工作", httpMethod = "PUT")
    public void execSignal(@MultiRequestBody("taskSpotId") Integer taskSpotId) throws GmsException {
        Integer unitId = getUnitId(taskSpotId);
        if (!GmsUtil.allObjNotNull(unitId, taskSpotId)) {
            throw new GmsException("参数异常,装载区没有分配调度单元");
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("unitId", unitId);
            params.put("taskSpotId", taskSpotId);
            MessageFactory.getDispatchMessage().sendMessageNoID("LoadAreaWorkBegin", GmsUtil.toJson(params), "任务点执行工作成功");
        } catch (Exception e) {
            String message = "任务点执行工作失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("取消进车信号")
    @PutMapping("/signals/cancel")
    @ApiOperation(value = "取消进车信号", httpMethod = "PUT")
    public void cancelSignal(@MultiRequestBody("taskSpotId") Integer taskSpotId) throws GmsException {
        Integer unitId = getUnitId(taskSpotId);
        if (!GmsUtil.allObjNotNull(unitId, taskSpotId)) {
            throw new GmsException("参数异常,装载区没有分配调度单元");
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("unitId", unitId);
            params.put("taskSpotId", taskSpotId);
            MessageFactory.getDispatchMessage().sendMessageNoID("LoadAreaEntryCancel", GmsUtil.toJson(params), "取消进车信号成功");
        } catch (Exception e) {
            String message = "取消进车信号失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("任务点开工")
    @PutMapping(value = "/taskSpots/statuses/runStatus")
    @ApiOperation(value = "任务点开工", httpMethod = "PUT")
    public void runTaskSpot(@MultiRequestBody("taskSpotId") Integer taskSpotId) throws GmsException {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("unitId", getUnitId(taskSpotId));
        paramMap.put("taskSpotId", taskSpotId);
        try {
            MessageFactory.getDispatchMessage().sendMessageNoID("TaskSpotStart", JSONObject.toJSONString(paramMap), "任务点开工成功");
        } catch (Exception e) {
            String message = "任务点开工失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("任务点停工")
    @PutMapping(value = "/taskSpots/statuses/stopStatus")
    @ApiOperation(value = "任务点停工", httpMethod = "PUT")
    public void stopTaskSpot(@MultiRequestBody("taskSpotId") Integer taskSpotId) throws GmsException {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("unitId", getUnitId(taskSpotId));
        paramMap.put("taskSpotId", taskSpotId);
        try {
            MessageFactory.getDispatchMessage().sendMessageNoID("TaskSpotStop", JSONObject.toJSONString(paramMap), "任务点停工成功");
        } catch (Exception e) {
            String message = "任务点停工失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("申请更换矿种")
    @PutMapping(value = "/minerals")
    @ApiOperation(value = "更换矿种", httpMethod = "PUT")
    public GmsResponse ChangeLoadType(@MultiRequestBody("mineralId") Integer mineralId,
                                      @MultiRequestBody("unLoadAreaId") Integer unLoadAreaId
                                      //unLoadAreaId为调度需要参数
    ) throws GmsException {
        try {
            Integer mapId = MapDataUtil.getActiveMap();
            if (null == mapId) {
                return new GmsResponse().message("活动地图不存在!").badRequest();
            }
            if (!ObjectUtils.allNotNull(mineralId, unLoadAreaId)) {
                return new GmsResponse().message("参数不能为空!").badRequest();
            }
            //获取挖掘机所在装载区的id;
            User user = super.getCurrentUser();
            UserExcavatorLoadArea bind = bindService.getBindByUser(user.getUserId());
            if (null == bind) {
                return new GmsResponse().message("该用户没有绑定挖掘机!").badRequest();
            }

            Unit unit = unitService.getUnitByLoadId(mapId, bind.getLoadAreaId());
            if (null == unit) {
                throw new GmsException("该装载区不在调度单元中!");
            }
            if (!Role.RoleSign.EXCAVATORPERSON_ROLE.getValue().equals(user.getRoleSign())) {
                throw new GmsException("非挖掘机操作员，不能提交申请");
            }
            Mineral newMineral = mineralService.getMineral(mineralId);
            if (null == newMineral) {
                throw new GmsException("矿物种类不存在");
            }
            //卸载区校验
            boolean exist = MapDataUtil.isAreaExist(mapId, unLoadAreaId, AreaTypeEnum.UNLOAD_MINERAL_AREA);
            if (!exist) {
                return new GmsResponse().message("活动地图不存在该卸载区!").badRequest();
            }
            AreaMineral areaMineral = areaMineralService.getAreaMineral(bind.getLoadAreaId());
            Mineral oldMineral = new Mineral();
            if (null != areaMineral) {
                oldMineral.setMineralId(areaMineral.getMineralId());
                oldMineral.setMineralName(areaMineral.getMineralName());
            }
            approveService.delApproveByKey(bind.getLoadAreaId(), "loaderAreaId", ApproveType.MINERALCHANGE);//重复提交删除前面的
            boolean result = addApprove(user, bind.getExcavatorId(), oldMineral, newMineral, unit.getUnitId(), bind.getLoadAreaId(), unLoadAreaId);
            if (result) {
                return new GmsResponse().message("更换矿种申请提交成功").success();
            } else {
                return new GmsResponse().message("更换矿种申请提交失败").badRequest();
            }
        } catch (Exception e) {
            String message = "更换矿种申请提交失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }


    private boolean addApprove(User user, Integer excavatorNo, Mineral oldMineral, Mineral newMineral, Integer unitId, Integer loadAreaId, Integer unLoadAreaId) {
        List<User> users = userService.getUsersByRoleSign(Role.RoleSign.CHIEFDESPATCHER_ROLE.getValue());
        if (CollectionUtils.isNotEmpty(users)) {
            HashSet<String> userIds = new HashSet<>();
            for (User u : users) {
                Integer id = u.getUserId();
                userIds.add(String.valueOf(id));
            }
            String join = String.join(StringPool.COMMA, userIds.toArray(new String[0]));
            Map<String, Object> params = new HashMap<>();
            params.put("unitId", unitId);
            params.put("mapId", MapDataUtil.getActiveMap());
            params.put("loaderAreaId", loadAreaId);
            params.put("unLoaderAreaId", unLoadAreaId);
            params.put("excavatorNo", excavatorNo);
            params.put("oldMineralId", oldMineral.getMineralId());
            params.put("oldMineralName", oldMineral.getMineralName());
            params.put("newMineralId", newMineral.getMineralId());
            params.put("newMineralName", newMineral.getMineralName());
            Integer id = approveService.createApprove(params, join, user, ApproveType.MINERALCHANGE, false);
            return null != id;
        }
        return false;
    }

    /**
     * 根据任务点id获取调度单元
     */
    private Integer getUnitId(Integer taskSpotId) {
        Integer activeMap = MapDataUtil.getActiveMap();
        if (null == activeMap) {
            return null;
        }
        List<SemiStatic> semiStatics = MapDataUtil.getSemiStaticData(activeMap);
        for (SemiStatic semiStatic : semiStatics) {
            if (AreaTypeEnum.LOAD_AREA.equals(semiStatic.getAreaType())) {
                SemiStatic.TaskSpot[] taskSpots = semiStatic.getTaskSpots();
                for (SemiStatic.TaskSpot taskSpot : taskSpots) {
                    //获取装载区id
                    if (taskSpot.getId().equals(taskSpotId)) {
                        //获取调度单元id
                        List<Unit> units = unitService.getUnitListByLoadId(semiStatic.getId());
                        if (GmsUtil.CollectionNotNull(units)) {
                            return units.get(0).getUnitId();
                        }
                    }
                }
            }
        }
        return null;
    }
}
