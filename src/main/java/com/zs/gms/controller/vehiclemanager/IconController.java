package com.zs.gms.controller.vehiclemanager;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.IOUtil;
import com.zs.gms.entity.system.User;
import com.zs.gms.entity.vehiclemanager.FileInfo;
import com.zs.gms.entity.vehiclemanager.IconLib;
import com.zs.gms.service.vehiclemanager.IconLibService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/vehicleIconLibs")
@Api(tags = {"车辆管理"}, description = "vehicle Controller")
public class IconController extends BaseController {

    @Autowired
    private IconLibService iconLibService;

    @Log("图标库上传")
    @PostMapping
    @ApiOperation(value = "图标库上传", httpMethod = "POST")
    public GmsResponse iconUpload(@MultiRequestBody("file") MultipartFile file,
                                  @RequestParam("name") String name,
                                  @RequestParam(value = "remark", required = false) String remark) throws GmsException {
        boolean existName = iconLibService.isExistName(name);
        if(existName){
            return new GmsResponse().message("库名称已存在").badRequest();
        }
        try {
            String fileName = file.getOriginalFilename();
            String last = GmsUtil.subLastStr(fileName, ".");
            String preName = fileName.substring(0, fileName.lastIndexOf("."));
            FileInfo.CompressedFileType type = FileInfo.CompressedFileType.getType(last);
            if (null == type) {
                return new GmsResponse().message("不支持" + last + "类型的文件上传").badRequest();
            }
            String dir = System.getProperty("user.dir");
            String relativePath = GmsConstant.ICON_LIB + File.separator + name;
            String iconDir = dir + relativePath;
            File iconFile = new File(iconDir);
            if (!iconFile.exists()) {
                iconFile.mkdirs();
            }
            IconLib iconLib = new IconLib();
            boolean isContainFile = unZip(file, dir + relativePath,iconLib);
            if(!isContainFile){
                return new GmsResponse().message("上传目录为空文件夹,上传图标库失败").badRequest();
            }

            User user = super.getCurrentUser();
            iconLib.setUserId(user.getUserId());
            iconLib.setUserName(user.getUserName());
            iconLib.setPath(GmsUtil.replaceAll(relativePath + File.separator + preName, "\\\\", "\\/"));
            iconLib.setName(name);
            iconLib.setRemark(remark);
            iconLibService.addIconLib(iconLib);
            return new GmsResponse().message("图标库上传成功").success();
        } catch (Exception e) {
            String message = "图标库上传失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("获取图标库列表")
    @GetMapping
    @ApiOperation(value = "获取图标库列表", httpMethod = "GET")
    public GmsResponse getIconList() throws GmsException {
        try {
            List<IconLib> libs = iconLibService.getLibs();
            return new GmsResponse().data(libs).message("获取图标库列表成功").success();
        } catch (Exception e) {
            String message = "获取图标库列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @SuppressWarnings("Duplicates")
    @Log("删除图标库")
    @DeleteMapping("/{libId}")
    @ApiOperation(value = "删除图标库", httpMethod = "DELETE")
    public GmsResponse delIconLib(@PathVariable("libId") Integer libId) throws GmsException {
        boolean exist = iconLibService.isExist(libId);
        if (!exist) {
            return new GmsResponse().message("该图标库id不存在").badRequest();
        }
        try {
            iconLibService.delById(libId);
            return new GmsResponse().message("删除图标库成功").success();
        } catch (Exception e) {
            String message = "删除图标库失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @SuppressWarnings("Duplicates")
    @Log("获取图标库文件列表")
    @GetMapping("/{libId}")
    @ApiOperation(value = "获取图标库文件列表", httpMethod = "GET")
    public GmsResponse getIconList(@PathVariable("libId") Integer libId) throws GmsException {
        boolean idExist = iconLibService.isExist(libId);
        if (!idExist) {
            return new GmsResponse().message("该图标库id不存在").badRequest();
        }
        try {
            IconLib lib = iconLibService.getLib(libId);
            List<File> files = fileList(lib.getPath());
            FileInfo fileInfo;
            List<FileInfo> infos = new ArrayList<>();
            for (File file : files) {
                fileInfo = new FileInfo();
                String path = file.getPath();
                path=GmsUtil.replaceAll(path, "\\\\", "\\/");
                fileInfo.setFileName(file.getName());
                fileInfo.setFilePath(path.substring(path.indexOf(GmsConstant.ICON_LIB)));
                fileInfo.setFileSize(file.length());
                infos.add(fileInfo);
            }
            return new GmsResponse().data(infos).message("获取图标库文件列表成功").success();
        } catch (Exception e) {
            String message = "获取图标库文件列表失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("下载图标库")
    @GetMapping("/libLoad/{libId}")
    @ApiOperation(value = "下载图标库", httpMethod = "GET")
    public void loadIconLib(HttpServletResponse response, @PathVariable("libId") Integer libId) throws GmsException {
        IconLib iconLib = iconLibService.getLib(libId);
        if (null == iconLib) {
            throw new GmsException("该图标库id不存在");
        }
        try {
            List<File> files = fileList(iconLib.getPath());
            IOUtil.loadZip(response,iconLib.getName(),files);
        } catch (Exception e) {
            String message = "下载图标库失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    @Log("替换图标库")
    @PutMapping("/{libId}")
    @ApiOperation(value = "替换图标库", httpMethod = "PUT")
    public GmsResponse replaceIconLib(@MultiRequestBody("file") MultipartFile file,@PathVariable("libId") Integer libId) throws GmsException {
        IconLib iconLib = iconLibService.getLib(libId);
        if (null == iconLib) {
            throw new GmsException("该图标库id不存在");
        }
        try {
            String dir = System.getProperty("user.dir");
            String oldPath=dir+iconLib.getPath();
            File dirFile = new File(oldPath);
            IOUtil.delDir(dirFile);
            String relativePath = dir+GmsConstant.ICON_LIB + File.separator + iconLib.getName();
            unZip(file,relativePath,iconLib);
            return new GmsResponse().message("替换图标库成功").success();
        } catch (Exception e) {
            String message = "替换图标库失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }

    private List<File> fileList(String path){
        String dir = System.getProperty("user.dir");
        List<File> files = new ArrayList<>();
        IOUtil.listFiles(new File(dir + path), files);
        return files;
    }

    private boolean unZip(MultipartFile file,String unZipPath,IconLib lib) throws IOException {
        //将压缩文件输出到临时目录
        String dir = System.getProperty("user.dir");
        InputStream is = file.getInputStream();
        File zipFile = IOUtil.createMultiFile(dir + GmsConstant.TEMP_DIR + File.separator + file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(zipFile);
        IOUtil.ioCopy(is, fos);
        fos.close();
        is.close();
        //解压
        FileInputStream fis = new FileInputStream(zipFile);
        IOUtil.unZip(fis, unZipPath + File.separator);
        List<File> files=new ArrayList<>();
        IOUtil.listFiles(new File(unZipPath),files);
        boolean isContainFile=false;
        if(GmsUtil.CollectionNotNull(files)){
            isContainFile=true;
            String path = files.get(0).getPath();
            path=GmsUtil.replaceAll(path, "\\\\", "\\/");
            lib.setFirstIconPath(path.substring(path.indexOf(GmsConstant.ICON_LIB)));
        }
        zipFile.delete();
        return isContainFile;
    }
}
