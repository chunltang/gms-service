package com.zs.gms.common.utils;

import com.zs.gms.entity.system.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class MD5Util {

    /**
     * 加密算法
     * */
    private static final String ALGORITH_NAME="md5";

    /**
     * 加密次数
     * */
    private static final int HASH_ITERATIONS=5;


    public static String encrypt(String userName,String password){
        return new SimpleHash(ALGORITH_NAME,password, ByteSource.Util.bytes(userName+ User.DEAFULT_PASSWORD),HASH_ITERATIONS).toHex();
    }
}
