package com.anderb.statham;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;

import static com.anderb.statham.ResolveResult.noElement;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Getter
@RequiredArgsConstructor
public enum JsonType {
    STRING(JsonParser::parseToString),
    NUMBER(JsonParser::parseToNumber),
    BOOLEAN(JsonParser::parseToBoolean),
    NULL(JsonParser::parseToNull),
    OBJECT(JsonParser::parseToObject),
    ARRAY(JsonParser::parseToList),
    NO_MORE_ELEMENTS((json, start) -> noElement());

    private final ElementResolver resolver;

    private final static Map<JsonType, ElementResolver> resolvers =
            Arrays.stream(values()).collect(toMap(identity(), JsonType::getResolver));

    public static ResolveResult resolve(JsonType type, String json, int start) {
        return resolvers.get(type).resolve(json, start);
    }
}
