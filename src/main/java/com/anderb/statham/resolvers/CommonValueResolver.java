package com.anderb.statham.resolvers;

import com.anderb.statham.JsonType;
import com.anderb.statham.ResolveResult;
import com.anderb.statham.ValueResolver;

import java.util.Map;

import static com.anderb.statham.ResolveResult.noElement;

public class CommonValueResolver {
    private final static Map<JsonType, ValueResolver> resolvers =
            Map.of(
                    JsonType.ELEMENT, JsonParser::parseElementToString,
                    JsonType.OBJECT, JsonParser::parseToObject,
                    JsonType.ARRAY, JsonParser::parseToList,
                    JsonType.NO_MORE_ELEMENTS, (json, start) -> noElement()
            );

    public ResolveResult resolve(JsonType type, String json, int start) {
        return resolvers.get(type).resolve(json, start);
    }

}
