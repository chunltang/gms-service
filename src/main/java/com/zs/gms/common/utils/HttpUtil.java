package com.zs.gms.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class HttpUtil {

    @Autowired
    private RestTemplate autoRestTemplate;

    private static RestTemplate restTemplate;

    @PostConstruct
    public void init(){
        restTemplate=autoRestTemplate;
    }

    public  static JSONObject restRequest(String url, HttpMethod method, String data){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(data,httpHeaders);
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, method, httpEntity, JSONObject.class);
        return responseEntity.getBody();
    }

    public static String sendPostJson(String url,String jsonStr){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        StringEntity entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
        entity.setContentEncoding("utf-8");
        HttpPost post = new HttpPost(url);
        post.setEntity(entity);
        CloseableHttpResponse response=null;
        try {
            response = httpClient.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode()== HttpStatus.SC_OK){
                return EntityUtils.toString(response.getEntity(),"utf-8");
            }
        } catch (IOException e) {
            log.error("http执行POST请求失败",e);
        }finally {
            if(response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static String sendGetData(String url){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-type", "application/json");

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
               return  EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            log.error("http执行GET请求失败",e);
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String sendPostDataByMap(String url, Map<String, String> map){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePairs = new ArrayList();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        CloseableHttpResponse response=null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
             response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void main(String[] args) {
        Map<String,String> params=new HashMap<>();
        params.put("userName","admin");
        params.put("password","123456");
        //HttpUtil.sendPostJson("http://192.168.2.100:8080/login", JSON.toJSONString(params));
        //HttpUtil.sendPostJson("http://192.168.2.100:8080/login", JSON.toJSONString(params));
        //HttpUtil.sendPostDataByMap("http://192.168.2.100:8080/login", params);
        HttpUtil.restRequest("http://192.168.2.100:8080/login", HttpMethod.POST, JSON.toJSONString(params));

    }
}
