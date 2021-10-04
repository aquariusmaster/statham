package com.anderb.statham;

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseHydrator implements Hydrator {

    @SneakyThrows
    private static <T> void setValueToField(T instance, Field field, Object value) {
        field.setAccessible(true);
        value = tryParseToNumber(value, field.getType());
        field.set(instance, value);
    }

    private static Object tryParseToNumber(Object value, Class<?> type) {
        if (!(value instanceof String)) return value;
        String strValue = (String) value;
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(strValue);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(strValue);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(strValue);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(strValue);
        } else if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(strValue);
        } else if (type == short.class || type == Short.class) {
            return Short.parseShort(strValue);
        } else if (type == BigInteger.class) {
            return new BigInteger(strValue);
        } else if (type == BigDecimal.class) {
            return new BigDecimal(strValue);
        }
        return value;
    }

    @SneakyThrows
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> T hydrate(Object jsonObj, Class<T> clazz) {
        if (jsonObj instanceof List) {
            return (T) hydrateList((List) jsonObj, clazz);
        }
        var jsonMap = (Map<String, Object>) jsonObj;
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (var field : clazz.getDeclaredFields()) {
            var value = jsonMap.get(field.getName());
            if (value instanceof Map) {
                value = hydrate(value, field.getType());
            }
            if (value instanceof List) {
                var listType = (ParameterizedType) field.getGenericType();
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
}
