package com.zs.gms.common.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 对请求做封装
 * */
@Slf4j
public class RequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    private Map<String,String[]> params=new HashMap<>();

    public RequestWrapper(HttpServletRequest request) throws IOException{
        super(request);
        String bodyStr=getBodyString(request);
        body=bodyStr.getBytes(Charset.defaultCharset());
        params.putAll(request.getParameterMap());
        /*if(isJson(request)&&!StringUtils.isEmpty(bodyStr)){
            JSONObject jsonObject = JSONObject.parseObject(bodyStr);
            addParameter(jsonObject,null);
        }*/
    }


    ////////////////////////////////////////使流可以多次使用///////////////////////////////////////////
    public String getBodyString(ServletRequest request) {
        try {
            return inputStreamToString(request.getInputStream());
        } catch (IOException e) {
            log.error("从request获取输入流失败",e);
            throw new RuntimeException(e);
        }
    }

    public String getBodyString() {
        InputStream inputStream = new ByteArrayInputStream(body);
        return inputStreamToString(inputStream);
    }

    public String inputStreamToString(InputStream inputStream) {
        StringBuilder buffer = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            log.error("实数流获取失败", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("流关闭失败", e);
                }
            }
        }
        return buffer.toString();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }

    ///////////////////////////////////将Body的JOSN数据转为request的请求参数///////////////////////////////////////
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector(params.keySet()).elements();
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values;
    }

    /**
     * 添加参数
     * */
    public void addParameter(JSONObject json,String pname){
        Set<Map.Entry<String, Object>> entries = json.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            String name = entry.getKey();
            if(!StringUtils.isEmpty(pname)){
                name=pname+"."+name;
            }
            if(value instanceof JSONObject){
                addParameter((JSONObject) value,name);
            }else if(value instanceof JSONArray){
                addParameter((JSONArray)value,name);
            }else{
                String[] param={String.valueOf(value)};
                params.put(name,param);
            }
        }
    }

    /**
     * 处理数组
     * */
    public void addParameter(JSONArray json,String pname){
        StringBuilder sb;
        String name;
        Object obj;
        for (int i = 0; i < json.size(); i++) {
            sb=new StringBuilder();
            sb.append("[").append(i).append("]");
            name=pname+sb.toString();
            obj = json.get(i);
            if(obj instanceof JSONObject){
                addParameter((JSONObject)obj,name);
            }else if(obj instanceof JSONArray){
                addParameter((JSONArray)obj,name);
            }
            else{
                String[] param={String.valueOf(obj)};
                params.put(name,param);
            }
        }
    }



    /**
     * 判断本次请求是否为json
     * */
    public boolean isJson(HttpServletRequest request){
        if(request.getContentType()!=null){
            return request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)||
                    request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE);
        }
        return false;
    }
}
