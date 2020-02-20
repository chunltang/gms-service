package com.zs.gms.controller.mapmanager;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/maps")
@Validated
@Slf4j
@Api(tags = {"地图管理"},description = "Map Controller")
public class DAreaController {

    @Log("删除区域")
    @DeleteMapping(value = "/{mapId}/areas/{areaId}")
    @ApiOperation(value = "删除区域",httpMethod = "DELETE")
    public void deleteArea(@PathVariable("mapId") Integer mapId, @PathVariable("areaId") Integer areaId) throws GmsException {
        try {
            Map<String,Object> params=new HashMap<>();
            params.put("mapId",mapId);
            params.put("areaId",areaId);
            String jsonStr = JSONObject.toJSON(params).toString();
            MessageFactory.getMapMessage().sendMessageNoID("deleteArea",jsonStr,"删除区域成功");
        }catch (Exception e){
            String message="删除区域失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }

    @Log("删除区域元素")
    @DeleteMapping(value = "/{mapId}/areas/{areaId}/{elementId}")
    @ApiOperation(value = "删除区域元素",httpMethod = "DELETE")
    public void deleteElement(@PathVariable("mapId") Integer mapId,
                              @PathVariable("areaId") Integer areaId,
                              @PathVariable("elementId") Integer elementId) throws GmsException {
        try {
            Map<String,Object> params=new HashMap<>();
            params.put("mapId",mapId);
            params.put("areaId",areaId);
            params.put("elementId",elementId);
            String jsonStr = JSONObject.toJSON(params).toString();
            MessageFactory.getMapMessage().sendMessageNoID("deleteElement",jsonStr,"删除区域元素成功");
        }catch (Exception e){
            String message="删除区域元素失败";
            log.error(message,e);
            throw  new GmsException(message);
        }
    }
}
