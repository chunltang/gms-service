package com.zs.gms.mytest;

import java.io.*;
import java.util.Random;

public class FileTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("D:\\py\\test.txt");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        StringBuilder sb;
        Random random = new Random();
        for (int i = 10; i < 10000; i++) {
            sb = new StringBuilder();
            int nextInt = random.nextInt(10);
            append(sb,nextInt+i);
            append(sb,nextInt+i+1);
            append(sb,nextInt+i+2);
            append(sb,nextInt+i+3);
            append(sb,nextInt+i+4);
            writer.write(sb.toString());
            Thread.sleep(1000);
            writer.newLine();
            writer.flush();
        }
    }

    private static void append(StringBuilder sb,int i){
        sb.append(i).append(" ");
    }
}
