package com.anderb.statham;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.anderb.statham.JsonType.NO_MORE_ELEMENTS;

@RequiredArgsConstructor
@Getter
public class ResolveResult {
    private final Object value;
    private final JsonType elementType;
    private final int end;

    public static ResolveResult of(Object value, JsonType elementType, int end) {
        return new ResolveResult(value, elementType, end);
    }

    public static ResolveResult noElement() {
        return new ResolveResult(null, NO_MORE_ELEMENTS, -1);
    }
}
