package com.zs.gms.common.utils;

import com.zs.gms.common.exception.GmsException;

public class Assert {

    /**
     * @param message 异常描述，使用objs替换{}
     * @param arr 需要判空的数组
     * @param objs 需要替换的内容
     * */
    public static void AllNotNull(String message,Object[] arr, Object... objs) {
        if (!GmsUtil.arrayNotNull(arr)) {
            throw new GmsException(GmsUtil.format(message, objs));
        }
    }

    public static void notNull(Object obj, String message, Object... objs) {
        if (!GmsUtil.objNotNull(obj)) {
            throw new GmsException(GmsUtil.format(message, objs));
        }
    }

    public static void hasLength(String str, String message, Object... objs) {
        if (!GmsUtil.StringNotNull(str)) {
            throw new GmsException(GmsUtil.format(message, objs));
        }
    }
}
