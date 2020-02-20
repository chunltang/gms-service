package com.zs.gms.mqtest;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

@MyColor
public class AnnotationTest {

    public static void main(String[] args) {
        Class<AnnotationTest> aClass = AnnotationTest.class;
        Annotation[] annotations = aClass.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> type = annotation.annotationType();
            System.out.println(type.getName());
            AnnotatedType superclass = type.getAnnotatedSuperclass();
            System.out.println(superclass.getType().getTypeName());
        }

        AnnotatedType[] types = aClass.getAnnotatedInterfaces();
        for (AnnotatedType type : types) {
            System.out.println(type.getType().getTypeName());
        }

    }
}
