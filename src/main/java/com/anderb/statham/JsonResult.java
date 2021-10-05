package com.anderb.statham;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.anderb.statham.JsonType.EMPTY;

@RequiredArgsConstructor
@Getter
public class JsonResult {
    private final Object value;
    private final JsonType elementType;
    private final int end;

    public static JsonResult of(Object value, JsonType elementType, int end) {
        return new JsonResult(value, elementType, end);
    }

    public static JsonResult empty() {
        return new JsonResult(null, EMPTY, -1);
    }
}