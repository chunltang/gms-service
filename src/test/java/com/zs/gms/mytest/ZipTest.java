package com.zs.gms.mytest;

import com.zs.gms.common.utils.IOUtil;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipTest {

    /*public static void main(String[] args) {
        List<File> list = new ArrayList<>();
        list.add(new File("D:\\testZip.txt"));
        list.add(new File("D:\\testZip1.txt"));
        IOUtil.loadZip(null,"test",list);
    }*/

   /* public static void main(String[] args) {
        IOUtil.copy(new File("D:\\testZip.txt"),"D:\\copy");
    }*/


    /*public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        try {
            FileInputStream zipInputStream = new FileInputStream(new File("D:\\idea\\gms-service\\tempDir\\test.zip"));
            IOUtil.unZip(zipInputStream,"D:\\test");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }*/

    /*public static void main(String[] args) throws Exception {
        String property = System.getProperty("user.dir");
        System.out.println(property);
        File file = new File(property + "/log");
        FileOutputStream outputStream = new FileOutputStream(new File(property + "/log.zip"));
        ZipOutputStream zos = new ZipOutputStream(outputStream, Charset.forName("utf-8"));
        IOUtil.fileCompress(file,zos,property);
        zos.close();
    }*/

    /*public static void main(String[] args) throws Exception{
        String property = System.getProperty("user.dir");
        File sqlFile = new File(property + "/sql");
        File srcFile = new File(property + "/src");
        List<File> files = new ArrayList<>();
        IOUtil.listFiles(sqlFile,files);
        IOUtil.listFiles(srcFile,files);
        FileOutputStream outputStream = new FileOutputStream(new File(property + "/gms-service.zip"));
        ZipOutputStream zos = new ZipOutputStream(outputStream, Charset.forName("utf-8"));
        IOUtil.fileCompress(files,zos,property);
        zos.close();
        outputStream.close();
    }*/

    public static void main(String[] args) throws Exception{
        String property = System.getProperty("user.dir");
        File dir = new File(property);
        List<File> files = new ArrayList<>();
        IOUtil.listFiles(dir,files,"target","log");
        FileOutputStream outputStream = new FileOutputStream(new File(dir.getParentFile().getPath() + "/gms-service.zip"));
        ZipOutputStream zos = new ZipOutputStream(outputStream, Charset.forName("utf-8"));
        IOUtil.fileCompress(files,zos,property);
        zos.close();
        outputStream.close();
    }
}
