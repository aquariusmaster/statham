package com.anderb.statham;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseHydrator implements Hydrator {

    @SneakyThrows
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> T hydrate(Object jsonObj, Class<T> clazz) {
        if (jsonObj instanceof List) {
            return (T) hydrateList((List) jsonObj, clazz);
        }
        Map<String, Object> jsonMap = (Map<String, Object>) jsonObj;
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            Object value = jsonMap.get(field.getName());
            if (value instanceof Map) {
                value = hydrate(value, field.getType());
            }
            if (value instanceof List) {
                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                value = hydrateList((List) value, (Class<?>) listType.getActualTypeArguments()[0]);
            }
            setValueToField(instance, field, value);
        }
        return instance;
    }

    private <T> List<T> hydrateList(List<T> value, Class<T> clazz) {
        return value.stream()
                .map(obj -> hydrate(obj, clazz))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private static <T> void setValueToField(T instance, Field field, Object value) {

        field.setAccessible(true);
        if (!(value instanceof String)) {
            field.set(instance, value);
            return;
        }
        String strValue = (String) value;
        if (field.getType().equals(String.class)) {
            field.set(instance, strValue);
        } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            field.set(instance, Boolean.parseBoolean(strValue));
        } else if (field.getType() == int.class || field.getType() == Integer.class) {
            field.set(instance, Integer.parseInt(strValue));
        } else if (field.getType() == long.class || field.getType() == Long.class) {
            field.set(instance, Long.parseLong(strValue));
        } else if (field.getType() == double.class || field.getType() == Double.class) {
            field.set(instance, Double.parseDouble(strValue));
        } else {
            System.out.println("Type is unsupported, ignoring");
        }
    }
}
