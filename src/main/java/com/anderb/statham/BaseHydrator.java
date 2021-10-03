package com.anderb.statham;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Map;

public class BaseHydrator implements Hydrator {

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public <T> T hydrate(Map<String, Object> jsonMap, Class<T> clazz) {
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            Object value = jsonMap.get(field.getName());
            if (value instanceof Map) {
                value = hydrate((Map<String, Object>) value, field.getType());
            }
            setValueToField(instance, field, value);
        }
        return instance;
    }

    @SneakyThrows
    private static <T> void setValueToField(T instance, Field field, Object value) {

        field.setAccessible(true);
        if (!(value instanceof String)) {
            field.set(instance, value);
            return;
        }
        String strValue = (String) value;

        System.out.println("Type: " + field.getType());
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
