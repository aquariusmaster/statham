package com.anderb.statham;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.anderb.statham.JsonType.NO_MORE_ELEMENTS;
import static com.anderb.statham.JsonType.NULL;

@RequiredArgsConstructor
@Getter
public class ResolveResult<T extends Enum<JsonType>> {
    private final String value;
    private final JsonType elementType;
    private final int end;

    public static ResolveResult of(String value, JsonType elementType, int end) {
        return new ResolveResult(value, elementType, end);
    }

    public static ResolveResult nul(String json) {
        return new ResolveResult(null, NULL, -1);
    }

    public static ResolveResult noElement(String json) {
        return new ResolveResult(null, NO_MORE_ELEMENTS, json.length());
    }
}
