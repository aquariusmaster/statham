package com.anderb.statham;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.anderb.statham.JsonResult.empty;

@Getter
@RequiredArgsConstructor
public enum JsonType {
    STRING(JsonParser::parseToString),
    NUMBER(JsonParser::parseToNumber),
    BOOLEAN(JsonParser::parseToBoolean),
    NULL(JsonParser::parseToNull),
    OBJECT(JsonParser::parseToObject),
    ARRAY(JsonParser::parseToList),
    EMPTY((json, start) -> empty());

    private final ElementParser parser;
}
