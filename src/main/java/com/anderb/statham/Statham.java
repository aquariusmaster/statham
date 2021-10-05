package com.anderb.statham;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class Statham {

    private final Hydrator hydrator;

    public Statham() {
        hydrator = new BaseHydrator();
    }

    public <T> T jsonToObj(String json, Class<T> clazz) {
        return hydrator.hydrate(JsonParser.parse(json), clazz);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> jsonToMap(String json) {
        Object result = JsonParser.parse(json);
        if (result instanceof Map) return (Map<String, Object>) result;
        return Map.of("result", result);
    }

}
