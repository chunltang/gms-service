package com.zs.gms.common.entity;

public class GmsConstant {

    public static final String SORT_DESC="desc";
    public static final String SORT_ASC="asc";

    /**
     * 服务描述
     * */
    public static final String DISPATCH="dispatch";
    public static final String MAP="map";
    public static final String VAP="vap";
    public static final String APPROVE="approve";

    public static final int DATA_MAP_INITIAL_CAPACITY=2;

    /**
     * shiro用于rememberMe的加密
     * */
    public static final String GMS_SHIRO_KEY="gms_shiro_key";

    /**
     * MQ等待响应时间
     */
    public final static Long waitTime = 120000L;

    /**
     * 临时文件存放目录
     * */
    public final static String tempDir="/tempDir";
}
