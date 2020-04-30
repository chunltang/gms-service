package com.zs.gms.common.utils;

public class ThreadLocalUtil {

    private static final ThreadLocal<Object> inLocal = new InheritableThreadLocal<>();
    private static final ThreadLocal<Object> local = new ThreadLocal<>();

    public static void setInLocal(Object obj) {
        inLocal.set(obj);
    }

    public static Object getInLocal() {
        return inLocal.get();
    }

    public static void removeInLocal() {
        inLocal.remove();
    }

    public static void set(Object obj) {
        local.set(obj);
    }

    public static Object get() {
        return local.get();
    }

    public static void remove() {
        local.remove();
    }
}
