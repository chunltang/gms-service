package com.zs.gms.mytest;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShellTest {

    private static String DEFAULT_CHART = "UTF-8";

    public static void main(String[] args) {
        execCmd("ls -l /root/path");
    }

    public static List<String> execCmd(String cmd) {
        List<String> reList=new ArrayList<>();
        try{
            Connection conn = getConnection();
            if (conn != null) {
                Session session = conn.openSession();// 打开一个会话
                System.out.println("执行命令:"+cmd);
                session.execCommand(cmd,DEFAULT_CHART);// 执行命令
                reList = processStdout(session.getStdout());
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            System.out.println("执行命令失败");
            e.printStackTrace();
        }
        return reList;
    }

    private static Connection getConnection(){
        Connection conn=null;
        try {
            conn=new Connection("192.168.2.100");
            conn.connect();
            boolean flag = conn.authenticateWithPassword("root", "gitlab");
            if(flag){
                System.out.println("登录成功");
                return conn;
            }
        }catch (Exception e){
            System.out.println("登录失败");
            conn.close();
        }
        return null;
    }

    private static List<String> processStdout(InputStream in) {
        InputStream stdout = new StreamGobbler(in);
        List<String> reList=new ArrayList<>();
        BufferedReader br=null;
        try {
            br = new BufferedReader(new InputStreamReader(stdout,DEFAULT_CHART));
            String line;
            StringBuilder sb=new StringBuilder();
            while ((line = br.readLine()) != null) {
                if(!StringUtils.isBlank(line)){
                    reList.add(line);
                    sb.append(line).append("\n");
                }
            }
            System.out.println(sb.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return reList;
    }
}
