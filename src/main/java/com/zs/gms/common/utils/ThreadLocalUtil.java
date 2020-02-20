package com.zs.gms.common.utils;

public class ThreadLocalUtil {

    private static final ThreadLocal<Integer> inLocal = new InheritableThreadLocal<>();
    private static final ThreadLocal<Integer> local = new ThreadLocal<>();

    public static void setInLocal(Integer integer) {
        inLocal.set(integer);
    }

    public static Integer getInLocal() {
        return inLocal.get();
    }

    public static void removeInLocal() {
        inLocal.remove();
    }

    public static void set(Integer integer) {
        local.set(integer);
    }

    public static Integer get() {
        return local.get();
    }

    public static void remove() {
        local.remove();
    }
}
