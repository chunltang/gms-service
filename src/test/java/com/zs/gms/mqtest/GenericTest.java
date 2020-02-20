package com.zs.gms.mqtest;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;

public class GenericTest {

    public static void main(String[] args) {
        TestGenericType2 type2 = new TestGenericType2();
        System.out.println(type2.tClass);
    }
}

abstract class GenericType2<T> {

    protected Class<T> tClass;

    public GenericType2() {

        Class<? extends GenericType2> aClass = this.getClass();

        Type superclass = aClass.getGenericSuperclass();

        if(superclass instanceof ParameterizedType){
            Type[] typeArguments = ((ParameterizedType) superclass).getActualTypeArguments();
            tClass=(Class<T>) typeArguments[0];
        }

    }
}

class TestGenericType2 extends GenericType2<String>{

}

class TestGenericType3 extends GenericType2<Integer>{

}
