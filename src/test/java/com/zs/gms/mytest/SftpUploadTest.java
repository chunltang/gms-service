package com.zs.gms.mytest;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class SftpUploadTest {
    //账号
    private static String user = "root";

    //主机ip
    private static String host = "192.168.2.114";//100
    //private static String host = "192.168.2.114";

    //密码
    private static String password = "gitlab";

    private static Channel channel = null;
    private static Session sshSession = null;

    //端口
    private static int port = 22;

    public static void main(String[] args) {
        uploadFile("D:\\idea\\gms-service\\target\\gms-0.0.1-SNAPSHOT.jar");
        System.out.println("-----------gms-0.0.1-SNAPSHOT.jar上传成功-------------");
       List<String> cmds=new ArrayList<>();
        cmds.add("cd /usr/dockersoft");
        //cmds.add("sh docker.sh ycc  gms-1.0.2 gms-1.0.2 8080 server4");
        cmds.add("sh docker.sh dac `docker ps | grep gms-1.0.2 |awk '{print $1}'`  `docker images | grep gms-1.0.2 |awk '{print $3}'` gms-1.0.2 gms-1.0.2 8080 server4");
        //cmds.add("sh docker.sh dac `docker ps | grep gms-1.0.1 |awk '{print $1}'`  `docker images | grep gms-1.0.1 |awk '{print $3}'` gms-1.0.1 gms-1.0.1 9999 server3");
        //cmds.add("sh docker.sh dac `docker ps | grep gms-1.0.0 |awk '{print $1}'`  `docker images | grep gms-1.0.0 |awk '{print $3}'` gms-1.0.0 gms-1.0.0 8080 server2");

        cmds.add("docker ps");
        execCmds(cmds);
    }

    public  static List<String> listFileNames( String dir) {
        List<String> list = new ArrayList<String>();
        try {
            ChannelSftp channel = getChannel("sftp",ChannelSftp.class);
            channel.connect();
            Vector ls = channel.ls(dir);
            for (Object l : ls) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) l;
                System.out.println(entry.getFilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    /**
     * 上传文件
     * */
    public  static void uploadFile( String fileName){
        ChannelSftp channel = getChannel("sftp",ChannelSftp.class);
        FileInputStream is=null;
        try {
            File file=new File(fileName);
            is = new FileInputStream(file);
            channel.connect();
            channel.put(is,"/usr/dockersoft/gms-0.0.1-SNAPSHOT.jar");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行linux命令,单条
     * */
    public static void execCmd(String cmd){
        ChannelExec channel = getChannel("exec",ChannelExec.class);
        System.out.println("执行命令:"+cmd);
        InputStream in=null;
       try {
           channel.setCommand(cmd);
           channel.setErrStream(System.err);
           in = channel.getInputStream();
           channel.connect();
           BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
           String buf = null;
           while ((buf = reader.readLine()) != null){
               System.out.println(buf);
           }
           reader.close();
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           close();
           if(in!=null){
               try {
                   in.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
    }

    /**
     * 执行多条命令
     * */
    public static void execCmds(List<String> cmds){
        ChannelShell channel = getChannel("shell",ChannelShell.class);
        System.out.println("执行命令:"+cmds.toString());
        InputStream in=null;
        OutputStream os=null;
        try {
            in = channel.getInputStream();
            channel.setPty(true);
            channel.connect();
            os = channel.getOutputStream();
            //写入该流的数据  都将发送到远程端,使用PrintWriter 就是为了使用println 这个方法,好处就是不需要每次手动给字符加\n
            PrintWriter printWriter = new PrintWriter(os);
            for (String cmd : cmds) {
                printWriter.println(cmd);
            }
            printWriter.println("exit");//为了结束本次交互
            printWriter.flush();//把缓冲区的数据强行输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            String buf = null;
            while ((buf = reader.readLine()) != null){
                System.out.println(buf);
            }
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close();
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T extends Channel> T getChannel(String type,Class clazz){
        Channel channel=null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(user, host, port);
            sshSession = jsch.getSession(user, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            System.out.println("连接成功!");
            channel = sshSession.openChannel(type);
        }catch (Exception e){
            System.out.println("连接失败");
        }
        return (T)clazz.cast(channel);
    }

    private static void close() {
        if (sshSession != null) {
            if (sshSession.isConnected()) {
                sshSession.disconnect();
            }
        }
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }
}

