package com.anderb.statham;

import static com.anderb.statham.JsonType.*;

public class NextValueTypeResolver implements TypeResolver {
    @Override
    public JsonType resolveType(String json, int start) {
        start = JsonParser.skipWhiteSpace(json, start);
        char ch = json.charAt(start);
        if (ch == '"') return STRING;
        if (Character.isDigit(ch)) return NUMBER;
        if (ch == 't' || ch == 'f') return BOOLEAN;
        if (ch == 'n') return NULL;
        if (ch == '{') return OBJECT;
        if (ch == '[') return ARRAY;
        return NO_MORE_ELEMENTS;
    }
}
