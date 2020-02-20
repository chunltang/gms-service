package com.zs.gms.common.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ScriptUtil {

    private static String DEFAULT_CHART = "UTF-8";

    private static  String host;

    private static String userName;

    private static String password;


    //执行项目所在容器命令
    public static String execLocalCmd(String cmd){
        String osName = System.getProperty("os.name");
        if(osName.startsWith("Win")){
            log.debug("{} 不能执行命令: {}",osName,cmd);
            return "";
        }
        BufferedReader reader=null;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",cmd});
            boolean flag = process.waitFor(10, TimeUnit.SECONDS);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder sb=new StringBuilder();
            while((line=reader.readLine())!=null){
                sb.append(line).append("\n");
            }
            log.debug("shell命令执行结果:{},{}",flag,sb);
            return sb.toString();
        } catch (Exception e) {
            log.error("shell命令执行异常",e);
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("流关闭异常",e);
                }
            }
        }
        return "";
    }

    public static List<String> execCmd(String cmd) {
        List<String> reList=new ArrayList<>();
        try{
            Connection conn = getConnection();
            if (conn != null) {
                Session session = conn.openSession();// 打开一个会话
                log.debug("执行命令:{}",cmd);
                long btime = System.currentTimeMillis();
                session.execCommand(cmd);// 执行命令
                reList = processStdout(session.getStdout(), DEFAULT_CHART);
                long etime = System.currentTimeMillis();
                if(reList.size()>0){
                    log.debug(">>>时间：{}，执行结果：success,line:{}",etime-btime,reList.size());
                }else{
                    log.debug(">>>时间：{}，执行结果：无输出",etime-btime);
                }
                conn.close();
                session.close();
            }
        } catch (IOException e) {
            log.debug("执行命令失败,链接conn:{},执行的命令：{}" ,cmd , e);
            e.printStackTrace();
        }
        return reList;
    }

    private static Connection getConnection(){
        Connection conn=null;
        try {
            conn=new Connection(host);
            conn.connect();
            boolean flag = conn.authenticateWithPassword(userName, password);
            if(flag){
                log.debug("登录成功");
                return conn;
            }
        }catch (Exception e){
           log.error("登录失败",e);
           conn.close();
        }
        return null;
    }

    private static List<String> processStdout(InputStream in, String charset) {
        InputStream stdout = new StreamGobbler(in);
        List<String> reList=new ArrayList<>();
        BufferedReader br=null;
        try {
            br = new BufferedReader(new InputStreamReader(stdout));
            String line;
            while ((line = br.readLine()) != null) {
                if(!StringUtils.isBlank(line)){
                    reList.add(line);
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.error("解析脚本出错：",e);
        } catch (IOException e) {
            log.error("解析脚本出错：",e);
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


    @Value("${gms.remote.host}")
    public void setHost(String host){
        this.host=host;
    }

    @Value("${gms.remote.username}")
    public void setUserName(String userName){
        this.userName=userName;
    }

    @Value("${gms.remote.password}")
    public void setPassword(String password){
        this.password=password;
    }
}
