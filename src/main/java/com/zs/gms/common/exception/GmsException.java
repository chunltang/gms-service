package com.zs.gms.common.exception;

public class GmsException extends  Exception {

    private static final long serialVersionUID = -8981002709409574689L;

    public GmsException(String message){
        super(message);
    }

    public GmsException(String message,Throwable e){
        super(message,e);
    }
}
