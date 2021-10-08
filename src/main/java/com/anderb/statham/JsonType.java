package com.anderb.statham;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JsonType {
    STRING(JsonParser::parseToString),
    NUMBER(JsonParser::parseToNumber),
    BOOLEAN(JsonParser::parseToBoolean),
    NULL(JsonParser::parseToNull),
    OBJECT(JsonParser::parseToObject),
    ARRAY(JsonParser::parseToList);

    private final JsonValueParser parser;
}
