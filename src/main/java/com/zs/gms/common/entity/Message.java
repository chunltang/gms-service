package com.zs.gms.common.entity;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@Data
public class Message  implements Serializable {
    private static final long serialVersionUID = 301504008790134381L;

    /**
     * 要返回数据的http响应对象
     * */
    private  HttpServletResponse httpResponse;

    /**
     * httpResponse要返回的数据对象
     * */
    private GmsResponse gmsResponse;

    public Message(HttpServletResponse httpResponse,GmsResponse gmsResponse){
        this.httpResponse=httpResponse;
        this.gmsResponse=gmsResponse;
    }



}
