package com.zs.gms.mytest;

import com.zs.gms.common.utils.IOUtil;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.zip.ZipInputStream;

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

    public static void main(String[] args) {

        String property = System.getProperty("user.dir");
        System.out.println(property);
    }

}
