package com.zs.gms.controller.mapmanager;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.Export;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.QueryRequest;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.DateUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.MapFile;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.mineralmanager.AreaMineral;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.enums.mapmanager.MapFileTypeEnum;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.mapmanager.MapInfoService;
import com.zs.gms.enums.monitor.TaskTypeEnum;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
@Api(tags = {"地图管理"}, description = "Map Controller")
@RequestMapping("/maps")
@Validated
public class OtherController extends BaseController {

    @Autowired
    @Lazy
    private MapInfoService mapInfoService;

    @Autowired
    private AreaMineralService areaMineralService;


    @Log("获取地图列表")
    @GetMapping
    @ApiOperation(value = "获取地图列表", httpMethod = "GET")
    public GmsResponse getMapVersionList(@MultiRequestBody QueryRequest request) throws GmsException {
        try {
            Map<String, Object> dataTable = getDataTable(mapInfoService.getMapInfoListPage(request));
            return new GmsResponse().data(dataTable).message("获取地图列表成功").success();
        } catch (Exception e) {
            String message = "获取地图列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取地图文件列表")
    @GetMapping("/files")
    @ApiOperation(value = "获取地图文件列表", httpMethod = "GET")
    public GmsResponse getMapFileList() throws GmsException {
        try {
            List<MapFile> mapFiles=new ArrayList<>();
            String filePath = new File("../mapfile").getCanonicalPath();
            File file = new File(filePath);
            if(!file.exists()||!file.isDirectory()){
                file.mkdirs();
            }
            List<File> files=new ArrayList<>();
            listFiles(file,files);
            MapFile mapFile;
            for (File mf : files) {
                String name = mf.getName();
                long mtime = mf.lastModified();
                mapFile=new MapFile();
                mapFile.setSize(mf.length());
                mapFile.setFileType(mf.toPath().getParent().getFileName().toString());
                mapFile.setLastUpdateTime(DateUtil.formatLongTime(mtime));
                String[] split = StringUtils.split(name, StringPool.DOT);
                String fileName=name;
                String rex="[0-9]{13}";
                if(split.length>2&&split[2].matches(rex)){
                    fileName=name.replaceAll("\\.[0-9]{13}","");
                    Pattern pattern = Pattern.compile(rex);
                    Matcher matcher = pattern.matcher(name);
                    matcher.find();
                    Long value = Long.valueOf(matcher.group());
                    mapFile.setId(value);
                    mapFile.setCreatTime(DateUtil.formatLongTime(value));
                }else{
                    String all = mf.getCanonicalPath().replaceAll(mf.getName(), "");
                    log.debug("重命名文件:{}",(all+name+"."+mtime));
                    mf.renameTo(new File(all+name+"."+mtime));//加id重命名文件
                    mapFile.setId(mtime);
                    mapFile.setCreatTime(DateUtil.formatLongTime(mtime));
                }
                mapFile.setName(fileName);
                mapFiles.add(mapFile);
            }

            return new GmsResponse().data(mapFiles).message("获取地图文件列表成功").success();
        } catch (Exception e) {
            String message = "获取地图文件列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    /**
     * 递归获取所有文件
     * */
    public void listFiles(File file,List<File> files){
        File[] listFiles = file.listFiles();
        for (File listFile : listFiles) {
            if (listFile.isDirectory()){
                listFiles(listFile,files);
            }else if(listFile.getName().contains("txt")){
                files.add(listFile);
            }
        }
    }



    @Log("地图文件导入")
    @PostMapping(value = "/mapImport/{id}")
    @ApiOperation(value = "地图文件导入", httpMethod = "POST")
    public GmsResponse importMap(@PathVariable Long id,
                                 @NotNull(message = "文件类型不能为空") @MultiRequestBody("fileType") String fileType) throws GmsException {
        try {
            List<Double[]> list = new ArrayList<>();
            String filePath = new File("../mapfile"+File.separator+fileType).getCanonicalPath();
            File file = new File(filePath);
            File[] files = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.contains(id.toString())) {
                        return true;
                    }
                    return false;
                }
            });
            if(files.length>0){
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[0])));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split("\\s+");
                    if (split.length >= 3) {
                        Double[] point = translate2Array(Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]));
                        list.add(point);
                    }else if(split.length == 2){
                        Double[] point = translate2Array(Double.valueOf(split[0]), Double.valueOf(split[1]), 0d);
                        list.add(point);
                    }
                }
            }
            return new GmsResponse().data(list).message("地图文件导入成功").success();
        } catch (Exception e) {
            String message = "地图文件导入失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    /**
     * 文件上传
     * */
    @Log("上传地图文件")
    @PostMapping(value = "/updateMapFile")
    @ApiOperation(value = "上传地图文件", httpMethod = "POST")
    public GmsResponse updateMapFile(@NotNull @MultiRequestBody("file") MultipartFile file,@NotNull @MultiRequestBody("fileType") MapFileTypeEnum fileType) throws GmsException {
        try {
            String path = new File("../mapfile").getCanonicalPath();
            File mapFileDir = new File(path + File.separator + fileType.toString());
            if(!mapFileDir.exists()||!mapFileDir.isDirectory()){
                mapFileDir.mkdirs();
            }
            String name = file.getName();
            String fileName = file.getOriginalFilename();
            File mapFile = new File(mapFileDir.getCanonicalPath() + File.separator + fileName);
            FileOutputStream os = new FileOutputStream(mapFile);
            os.write(file.getBytes());
            os.flush();
            os.close();
            return new GmsResponse().message("上传地图文件成功").success();
        } catch (Exception e) {
            String message = "上传地图文件失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    /**
     * 坐标转换
     */
    private Double[] translate2Array(Double x, Double y, Double z) {
        Double[] point= new Double[3];
        x = (x / 20037508.34f) * 180;
        y = (y / 20037508.34f) * 180;
        double value =(180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2));
        point[0]=x;
        point[1]=value;
        point[2]=z;
        return point;
    }

    @Log("获取活动地图区域信息")
    @GetMapping(value = "/activeMap/areas")
    @ApiOperation(value = "获取活动地图区域信息", httpMethod = "GET")
    public GmsResponse getAreaListByType(@MultiRequestBody(value = "type",required = false,parseAllFields = false) AreaTypeEnum areaType) throws GmsException {
        try {
            Integer activeMap = GmsUtil.getActiveMap();
            if(null==activeMap){
                return new GmsResponse().message("当前无活动地图").badRequest();
            }
            List<SemiStatic> areaInfos = MapDataUtil.getAreaInfos(activeMap, areaType);
            return new GmsResponse().data(areaInfos).message("获得区域列表成功").success();
        } catch (Exception e) {
            String message = "获得区域列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("根据矿物类型获取卸载区列表")
    @GetMapping(value = "/{mapId}/areas/unloads/{mineralId}")
    @ApiOperation(value = "根据矿物类型获取卸载区列表", httpMethod = "GET")
    public GmsResponse getUnLoadAreaList(@PathVariable Integer mapId,@PathVariable Integer mineralId) throws GmsException {
        try {
            //up 需要配置矿种和卸载区的关系
            List<AreaMineral> unAreaIds = areaMineralService.getUnAreaIds(mineralId);
            HashSet<Integer> sets = new HashSet<>();
            for (AreaMineral areaMineral : unAreaIds) {
                sets.add(areaMineral.getAreaId());
            }
            List<SemiStatic> areaInfos = MapDataUtil.getAreaInfos(mapId, AreaTypeEnum.UNLOAD_MINERAL_AREA);
            List<SemiStatic> result = new ArrayList<>();
            for (SemiStatic areaInfo : areaInfos) {
                Integer id = areaInfo.getId();
                if(sets.contains(id)){
                    result.add(areaInfo);
                }
            }
            return new GmsResponse().data(result).message("获取卸载区列表成功").success();
        } catch (Exception e) {
            String message = "获取卸载区列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }
}
