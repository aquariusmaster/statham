package com.anderb.statham;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum JsonType {
    ELEMENT, //string, boolean, number, character or null
    OBJECT('{', '}'),
    ARRAY('[', ']'),
    NO_MORE_ELEMENTS;

    private final char startSymbol;
    private final char endSymbol;

    JsonType() {
        this.startSymbol = (char) -1;
        this.endSymbol = (char) -1;
    }

    private static final List<JsonType> listWithStartSymbol = Arrays.stream(values())
            .filter(type -> type.getStartSymbol() != (char) -1)
            .collect(Collectors.toList());

    public static List<JsonType> valuesWithStartDefined() {
        return listWithStartSymbol;
    }

}
