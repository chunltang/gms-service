package com.zs.gms.common.utils;

import com.zs.gms.common.entity.GmsConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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
        String tempDir = dir + File.separator + GmsConstant.tempDir;
        File temp = new File(tempDir);
        if (!temp.exists()) {
            temp.mkdirs();
        }
        File target = new File(tempDir + File.separator + fileName + ".zip");
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
    }

    /**
     * zip解压
     * @param path 解压路径
     */
    public static void unZip(InputStream is, String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        ZipEntry entry;
        FileOutputStream fos=null;
        ZipInputStream zis=new ZipInputStream(is,Charset.forName("GBK"));
        try {
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                File target = new File(path + File.separator + name);
                if(entry.isDirectory()){
                     if(!target.exists()){
                         target.mkdirs();
                     }
                     continue;
                }
                fos = new FileOutputStream(target);
                ioCopy(zis,fos);
                fos.close();
            }
            zis.close();
            is.close();
        } catch (IOException e) {
           log.error("zip文件解压异常",e);
        }
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
            outputStream.flush();
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
