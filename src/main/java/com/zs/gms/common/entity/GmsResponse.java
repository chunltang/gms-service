package com.zs.gms.common.entity;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

public class GmsResponse extends HashMap<String, Object> {

    private static final long serialVersionUID = 5836236931810090550L;

    public GmsResponse code(HttpStatus status) {
        this.put("code", status.value());
        return this;
    }

    public GmsResponse message(String message) {
        this.put("message", message);
        return this;
    }

    public String getMessage() {
        Object message = this.get("message");
        return null == message ? "" : message.toString();
    }


    public GmsResponse data(Object data) {
        this.put("data", data);
        return this;
    }

    public GmsResponse success() {
        this.code(HttpStatus.OK);
        if (!this.containsKey("message")) {
            this.message("操作成功");
        }
        return this;
    }

    public GmsResponse unauth() {
        this.code(HttpStatus.UNAUTHORIZED);
        return this;
    }

    public GmsResponse badRequest() {
        this.code(HttpStatus.BAD_REQUEST);
        return this;
    }

    public GmsResponse fail() {
        this.code(HttpStatus.INTERNAL_SERVER_ERROR);
        return this;
    }

    public GmsResponse put(String key, Object object) {
        super.put(key, object);
        return this;
    }

}
