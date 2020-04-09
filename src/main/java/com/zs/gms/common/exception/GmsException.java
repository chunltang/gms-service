package com.zs.gms.common.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GmsException extends  RuntimeException {

    private static final long serialVersionUID = -8981002709409574689L;

    public GmsException(String message){
        super(message);
    }

    public GmsException(String message,Throwable e){
        super(message,e);
    }
}
