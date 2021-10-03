package com.anderb.statham.resolvers;

import com.anderb.statham.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.anderb.statham.JsonType.*;
import static java.lang.Character.isWhitespace;

public class JsonParser {
    private static final KeyResolver keyResolver = JsonParser::keyResolver;
    private static final ValueTypeResolver valueTypeResolver = new NextValueTypeResolver();
    private static final CommonValueResolver valueResolver = new CommonValueResolver();

    public static Object parse(String json) {
        JsonType jsonType = valueTypeResolver.resolveType(json, 0);
        return valueResolver.resolve(jsonType, json, 0).getValue();
    }

    public static ResolveResult keyResolver(String json, int start) {
        int start1 = json.indexOf("\"", start);
        if (start1 == -1) return ResolveResult.noElement();
        int end = json.indexOf("\":", start1);
        if (start1 + 1 > end) return ResolveResult.noElement();
        return ResolveResult.of(json.substring(start1 + 1, end), ELEMENT, end);
    }

    public static ResolveResult parseElementToString(String json, int start) {
        start = skipWhiteSpace(json, start);
        boolean isStringValue = json.charAt(start) == '"';
        if (isStringValue) {
            int end = json.indexOf('"', start + 1);
            if (end == -1) return ResolveResult.noElement();
            return ResolveResult.of(json.substring(start + 1, end), ELEMENT, end);
        }
        int end = json.indexOf(",", start + 1);
        if (end == -1) return ResolveResult.noElement();
        String value = json.substring(start, end);
        if (value.equals("null")) return ResolveResult.of(null, ELEMENT, end);
        return ResolveResult.of(value, ELEMENT, end);
    }

    public static ResolveResult parseToObject(String json, int start) {
        Map<String, Object> jsonMap = new HashMap<>();
        start = skipToNext(json, start, OBJECT.getStartSymbol());
        json = json.substring(start, getTypeEnd(json, OBJECT, start));
        int initStart = start;
        start = 1;
        while (start < json.length()) {
            var keyResult = keyResolver.resolve(json, start + 1);
            if (keyResult.getElementType() == NO_MORE_ELEMENTS) break;
            start = keyResult.getEnd() + 2;

            JsonType jsonType = valueTypeResolver.resolveType(json, start);
            if (jsonType == NO_MORE_ELEMENTS) break;

            ResolveResult valueResult = valueResolver.resolve(jsonType, json, start);
            start = valueResult.getEnd() + 1;

            jsonMap.put((String) keyResult.getValue(), valueResult.getValue());
        }

        System.out.println(jsonMap);
        return ResolveResult.of(jsonMap, OBJECT, initStart + start);
    }

    public static ResolveResult parseToList(String json, int start) {
        List<Object> array = new ArrayList<>();
        start = skipToNext(json, start, ARRAY.getStartSymbol());
        json = json.substring(start, getTypeEnd(json, ARRAY, start));
        while (start < json.length()) {
            JsonType jsonType = valueTypeResolver.resolveType(json, start + 1);
            var valRes = valueResolver.resolve(jsonType, json, start + 1);
            array.add(valRes.getValue());
            start = valRes.getEnd() + 1;
        }
        System.out.println(array);
        return ResolveResult.of(array, ARRAY, start);
    }

    private static int skipWhiteSpace(String json, int start) {
        while (start < json.length() && isWhitespace(json.charAt(start))) start++;
        return start;
    }

    private static int skipToNext(String json, int start, char ch) {
        while (start < json.length() && json.charAt(start) != ch) start++;
        return start;
    }

    private static int getTypeEnd(String json, JsonType type, int start) {
        int end = -1;
        while (start < json.length()) {
            end = json.indexOf(type.getEndSymbol(), start + 1);
            if (end == -1) throw new IllegalStateException("No end for " + type.getStartSymbol()
                    + ", element position " + start);
            int innerElementStartPosition = json.indexOf(type.getStartSymbol(), start + 1);
            if (innerElementStartPosition == -1 || innerElementStartPosition > end) break;
            start = innerElementStartPosition + 1;
        }
        return end + 1;
    }
}
