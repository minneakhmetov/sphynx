package com.razzzil.sphynx.commons.socket;

import com.fasterxml.jackson.databind.JavaType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerInvocation {
    private Method method;
    private Object bean;

    public HandlerInvocation(Method method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

    public Object invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(bean, args);
    }

    public String getCanonicalClass(){
        return method.getDeclaringClass().getCanonicalName();
    }

    public Class<?> getParameterClass(){
        return method.getParameterTypes()[0];
    }

}
