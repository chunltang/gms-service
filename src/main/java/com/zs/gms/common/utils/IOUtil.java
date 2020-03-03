package com.zs.gms.common.utils;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.entity.vehiclemanager.FileInfo;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public class IOUtil {

    /**
     * 下载zip文件
     *
     * @param fileName 下载文件名，不带后缀
     */
    public static void loadZip(HttpServletResponse response, String fileName, List<File> files) {
        String dir = System.getProperty("user.dir");
        String tempDir = dir + File.separator + GmsConstant.TEMP_DIR;
        File temp = new File(tempDir);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        File target = new File(tempDir +File.separator+ fileName + ".zip");
        try (FileOutputStream os = new FileOutputStream(target);
             ZipOutputStream zos = new ZipOutputStream(os, Charset.forName("UTF-8"))) {
            for (File file : files) {
                String name = file.getName();
                ZipEntry zipEntry = new ZipEntry(name);
                zos.putNextEntry(zipEntry);
                FileInputStream fis = new FileInputStream(file);
                ioCopy(fis, zos);
                fis.close();
            }
        } catch (Exception e) {
            log.error("文件压缩异常", e);
        }
        writeToResponse(response, target);
        target.delete();
    }

    /**
     * @param file    为要压缩的文件或目录
     * @param zos     为压缩后的zip文件
     * @param dirPath 为文件压缩前去掉前置目录字符串
     */
    public static void fileCompress(File file, ZipOutputStream zos, String dirPath) throws Exception {
        File[] files = file.listFiles();
        ZipEntry zipEntry;
        FileInputStream fileInputStream;
        for (File f : files) {
            if (f.isDirectory()) {
                fileCompress(f, zos, dirPath);
                continue;
            }
            zipEntry = new ZipEntry(f.getPath().replace(dirPath + File.separator, ""));
            zos.putNextEntry(zipEntry);
            fileInputStream = new FileInputStream(f);
            IOUtil.ioCopy(fileInputStream, zos);
            fileInputStream.close();
        }
    }

    /**
     * @param files 指定文件对象压缩
     */
    public static void fileCompress(List<File> files, ZipOutputStream zos, String dirPath) throws Exception {
        ZipEntry zipEntry;
        FileInputStream fileInputStream;
        for (File f : files) {
            zipEntry = new ZipEntry(f.getPath().replace(dirPath + File.separator, ""));
            zos.putNextEntry(zipEntry);
            fileInputStream = new FileInputStream(f);
            IOUtil.ioCopy(fileInputStream, zos);
            fileInputStream.close();
        }
    }

    /**
     * 查找所有文件对象
     */
    public static void listFiles(File file, List<File> files, String... excludeDir) {
        if (file.isDirectory()) {
            if (Arrays.asList(excludeDir).contains(file.getName())) {
                return;
            }
            File[] fs = file.listFiles();
            for (File f : fs) {
                listFiles(f, files, excludeDir);
            }
            return;
        }
        files.add(file);
    }

    /**
     * 删除目录
     * */
    public static void delDir(File file){
        List<File> files=new ArrayList<>();
        listFiles(file,files);
        for (File f : files) {
            f.delete();
        }
        file.delete();
    }

    /**
     * zip解压
     *
     * @param path 解压路径，以斜杠结尾
     */
    public static void unZip(InputStream is, String path) {
        createDir(path,true);
        ZipEntry entry;
        FileOutputStream fos = null;
        ZipInputStream zis = new ZipInputStream(is, Charset.forName("GBK"));
        try {
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                File target = new File(path+ name);
                if (entry.isDirectory()) {
                    if (!target.exists()) {
                        target.mkdirs();
                    }
                    continue;
                }
                fos = new FileOutputStream(target);
                ioCopy(zis, fos);
                fos.close();
            }
            zis.close();
            is.close();
        } catch (IOException e) {
            log.error("zip文件解压异常", e);
        }
    }

    public static void deCompression(InputStream is, String path, FileInfo.CompressedFileType type){
          switch (type){
              case RAR:
                  unRar(is,path);
                  break;
              case ZIP:
                  unZip(is,path);
                  break;
                  default:
                      log.debug("解压类型不存在");
          }
    }

    /**
     * 5.0之后的版本不能解压
     * @param path 解压目录，以斜杠结尾
     * */
    public static void unRar(InputStream is, String path) {
        Archive archive = null;
        try {
            archive = new Archive(is);
            FileHeader fileHeader = archive.nextFileHeader();
            while (fileHeader != null) {
                if (fileHeader.isDirectory()) {
                    fileHeader = archive.nextFileHeader();
                    continue;
                }
                File file = createMultiFile(path + fileHeader.getFileNameString());
                FileOutputStream os = new FileOutputStream(file);
                archive.extractFile(fileHeader, os);
                os.close();
                fileHeader = archive.nextFileHeader();
            }
            archive.close();
        } catch (Exception e) {
            log.error("解压rar文件异常",e);
        }
    }

    /**
     * 创建目录
     *
     * @param delFlag 存在是否删除，true未删除重建
     */
    public static void createDir(String dirPath, boolean delFlag) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        } else if (delFlag) {
            file.delete();
            file.mkdirs();
        }
    }

    /**
     * 创建文件
     */
    public static File createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("创建文件失败", e);
            }
        }
        return file;
    }

    /**
     * 创建多级文件
     * @param filePath 最后以文件名结尾
     */
    public static File createMultiFile(String filePath) {
        File file = new File(filePath);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        return createFile(filePath);
    }

    /**
     * 将压缩文件写入响应对象
     */
    public static void writeToResponse(HttpServletResponse response, File target) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + target.getName());
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(target);
            ioCopy(fileInputStream, outputStream);
            fileInputStream.close();
            response.flushBuffer();
        } catch (IOException e) {
            log.error("流数据拷贝异常", e);
        }
    }


    /**
     * 流数据拷贝
     */
    public static void ioCopy(InputStream in, OutputStream os) {
        int len = 0;
        byte[] bytes = new byte[1024];
        try {
            while ((len = in.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
        } catch (Exception e) {
            log.error("流数据拷贝异常", e);
        }
    }


    /**
     * 文件拷贝
     *
     * @param file 源文件
     * @param path 路径,加file的名字获得复制后的文件
     */
    public static void copy(File file, String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = path + File.separator + file.getName();
        try (FileOutputStream target = new FileOutputStream(new File(name));
             FileInputStream source = new FileInputStream(file)) {
            FileChannel inChannel = source.getChannel();
            FileChannel outChannel = target.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            log.error("文件复制异常", e);
        }
    }
}
