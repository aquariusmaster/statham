package com.anderb.statham;

import com.anderb.statham.resolvers.JsonParser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Statham {

    private final Hydrator hydrator;

    public Statham() {
        hydrator = new BaseHydrator();
    }

    public <T> T jsonToObj(String json, Class<T> clazz) {
        return hydrator.hydrate(JsonParser.parse(json), clazz);
    }

}
