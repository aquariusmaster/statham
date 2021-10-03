package com.anderb.statham;

@FunctionalInterface
public interface Hydrator {
    <T> T hydrate(Object jsonObj, Class<T> clazz);
}
