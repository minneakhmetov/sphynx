package com.razzzil.sphynx.commons.util;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class Condition<T> {

    private Boolean condition;
    private T trueReturn;
    private T falseReturn;

    private Condition(Boolean condition) {
        this.condition = condition;
    }

    public static <T> Condition<T> of(Boolean condition) {
        return new Condition<>(condition);
    }

    public static <T> Condition<T> isNull(Object object) {
        return new Condition<>(Objects.isNull(object));
    }

    public static <T> Condition<T> nonNull(Object object) {
        return new Condition<>(Objects.nonNull(object));
    }

    public <V extends Throwable> Condition<T> ifTrueThrow(Supplier<? extends V> exceptionSupplier) throws V {
        if (condition) {
            throw exceptionSupplier.get();
        }
        return this;
    }

    public <V extends Throwable> Condition<T> ifFalseThrow(Supplier<? extends V> exceptionSupplier) throws V {
        if (!condition) {
            throw exceptionSupplier.get();
        }
        return this;
    }

    public Condition<T> ifTrue(T object) {
        trueReturn = object;
        return this;
    }

    public Condition<T> ifFalse(T object) {
        falseReturn = object;
        return this;
    }

    public Condition<T> ifTrueExecute(Callable<?> callable) throws Exception {
        if (condition){
            callable.call();
        }
        return this;
    }

    public Condition<T> ifFalseExecute(Callable<?> callable) throws Exception {
        if (!condition){
            callable.call();
        }
        return this;
    }

    public T returnObject(){
        return condition ? trueReturn : falseReturn;
    }

    public Boolean returnCondition(){
        return condition;
    }


}
