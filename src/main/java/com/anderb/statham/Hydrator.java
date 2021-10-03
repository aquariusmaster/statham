package com.anderb.statham;

import java.util.Map;

@FunctionalInterface
public interface Hydrator {
    <T> T hydrate(Map<String, Object> jsonMap, Class<T> clazz);
}
