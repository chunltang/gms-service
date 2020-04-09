package com.zs.gms.controller.mineralmanager;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mineralmanager.AreaMineral;
import com.zs.gms.entity.mineralmanager.Mineral;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.mineralmanager.AreaMineralService;
import com.zs.gms.service.mineralmanager.MineralService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Api(tags = {"矿物管理"})
@Validated
@RequestMapping(value = "/minerals")
public class MineralController extends BaseController {

    @Autowired
    @Lazy
    private MineralService mineralService;

    @Autowired
    private AreaMineralService areaMineralService;

    @Log("新增矿物")
    @PostMapping
    @ApiOperation(value = "新增矿物",httpMethod = "POST")
    public GmsResponse addMineral(@Valid @MultiRequestBody Mineral mineral) throws GmsException {
        boolean exist = mineralService.isMineralExist(mineral.getMineralName());
        if(exist){
            return new GmsResponse().message("矿物名称已存在").badRequest();
        }
        try {
            mineralService.addMineral(mineral);
            return new GmsResponse().message("新增矿物成功").success();
        }catch (Exception e){
            String message="新增矿物失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("获取矿物列表")
    @GetMapping
    @ApiOperation(value = "获取矿物列表",httpMethod = "GET")
    public GmsResponse getMineralList() throws GmsException {
        try {
            List<Mineral> minerals = mineralService.getMineralList();
            return new GmsResponse().data(minerals).message("获取矿物列表成功").success();
        }catch (Exception e){
            String message="获取矿物列表失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("修改矿物信息")
    @PutMapping
    @ApiOperation(value = "修改矿物信息",httpMethod = "PUT")
    public GmsResponse updateMineral(@MultiRequestBody Mineral mineral) throws GmsException {
        if(null==mineral.getMineralId())
            throw  new GmsException("矿物id为空");
        try {
            boolean exist = mineralService.isMineralExist(mineral.getMineralId(),mineral.getMineralName());
            if(exist){
                return new GmsResponse().message("矿物名称已存在").badRequest();
            }
            mineralService.updateMineral(mineral);
            return new GmsResponse().message("修改矿物信息成功").success();
        }catch (Exception e){
            String message="修改矿物信息失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("删除矿物")
    @DeleteMapping(value = "/{mineralIds}")
    @ApiOperation(value = "删除矿物",httpMethod = "DELETE")
    public GmsResponse deleteMineral(@PathVariable String mineralIds) throws GmsException {
        if(StringUtils.isEmpty(mineralIds))
            throw  new GmsException("矿物id为空");
        try {
            mineralService.deleteMineral(mineralIds);
            return new GmsResponse().message("删除矿物成功").success();
        }catch (Exception e){
            String message="删除矿物失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }

    @Log("新增卸矿区和矿种对应关系")
    @PostMapping(value = "/areaMinerals")
    @ApiOperation(value = "新增卸矿区和矿种对应关系",httpMethod = "POST")
    public GmsResponse addAreaMineral(@MultiRequestBody(required = false,value = "mapId",parseAllFields = false) Integer mapId,
                                      @MultiRequestBody(value = "mineralId")Integer mineralId,
                                      @MultiRequestBody(value = "unloadAreaId")Integer unloadAreaId) throws GmsException {
        if(!GmsUtil.allObjNotNull(mineralId,unloadAreaId)){
            throw new GmsException("参数不能为空");
        }
        Mineral mineral = mineralService.getMineral(mineralId);
        if(null==mineral){
            throw new GmsException("该矿种类型不存在");
        }
        if(null==mapId){
            mapId = MapDataUtil.getActiveMap();
        }
        if(null==mapId){
            throw new GmsException("当前地图不存在，不能新增");
        }
        boolean areaExist = MapDataUtil.isAreaExist(mapId, unloadAreaId, AreaTypeEnum.UNLOAD_MINERAL_AREA);
        if(!areaExist){
            throw new GmsException("当前地图不存在该卸矿区，不能新增");
        }
        try {
            AreaMineral areaMineral = new AreaMineral();
            areaMineral.setUserId(super.getCurrentUser().getUserId());
            areaMineral.setMineralId(mineralId);
            areaMineral.setAreaId(unloadAreaId);
            areaMineralService.addAreaMineral(areaMineral);
            return new GmsResponse().message("新增卸矿区和矿种对应关系成功").success();
        }catch (Exception e){
            String message="新增卸矿区和矿种对应关系失败";
            log.error(message,e);
            throw new GmsException(message);
        }
    }
}
