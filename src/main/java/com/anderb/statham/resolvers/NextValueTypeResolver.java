package com.anderb.statham.resolvers;

import com.anderb.statham.JsonType;
import com.anderb.statham.ValueTypeResolver;

import static com.anderb.statham.JsonType.*;
import static java.lang.Character.isWhitespace;

public class NextValueTypeResolver implements ValueTypeResolver {
    @Override
    public JsonType resolveType(String json, int start) {
        while (start < json.length()) {
            char ch = json.charAt(start);
            if (!isWhitespace(ch)) {
                return resolveType(ch);
            }
            start++;
        }
        return NO_MORE_ELEMENTS;
    }

    private JsonType resolveType(char ch) {
        for (var type : JsonType.valuesWithStartDefined()) {
            if (type.getStartSymbol() == ch) return type;
        }
        return ELEMENT;
    }
}
