package com.anderb.statham.resolvers;

import com.anderb.statham.JsonType;
import com.anderb.statham.KeyResolver;
import com.anderb.statham.ResolveResult;
import com.anderb.statham.ValueTypeResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.anderb.statham.JsonType.*;
import static java.lang.Character.isWhitespace;

public class JsonParser {
    private static final KeyResolver keyResolver = JsonParser::keyResolver;
    private static final ValueTypeResolver valueTypeResolver = new NextValueTypeResolver();
    private static final CommonValueResolver valueResolver = new CommonValueResolver();

    public static Object parse(String json) {
        var type = valueTypeResolver.resolveType(json, 0);
        return valueResolver.resolve(type, json, 0).getValue();
    }

    public static ResolveResult keyResolver(String json, int start) {
        int innerStart = json.indexOf("\"", start);
        if (innerStart == -1) return ResolveResult.noElement();
        int end = json.indexOf("\":", innerStart);
        if (innerStart + 1 > end) return ResolveResult.noElement();
        return ResolveResult.of(json.substring(innerStart + 1, end), ELEMENT, end);
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
        var value = json.substring(start, end);
        if (value.equals("null")) return ResolveResult.of(null, ELEMENT, end);
        return ResolveResult.of(value, ELEMENT, end);
    }

    public static ResolveResult parseToObject(String innerJson, int start) {
        var jsonMap = new HashMap<String, Object>();
        start = skipToNext(innerJson, start, OBJECT.getStartSymbol());
        innerJson = innerJson.substring(start, findEndElementIndex(innerJson, OBJECT.getStartSymbol(), OBJECT.getEndSymbol(), start) + 1);
        int initStart = start;
        start = 1;
        while (start < innerJson.length()) {
            var keyResult = keyResolver.resolve(innerJson, start + 1);
            if (keyResult.getElementType() == NO_MORE_ELEMENTS) break;
            start = keyResult.getEnd() + 2;

            var type = valueTypeResolver.resolveType(innerJson, start);
            if (type == NO_MORE_ELEMENTS) break;

            var valueResult = valueResolver.resolve(type, innerJson, start);
            start = valueResult.getEnd() + 1;

            jsonMap.put((String) keyResult.getValue(), valueResult.getValue());
        }
        return ResolveResult.of(jsonMap, OBJECT, initStart + innerJson.length());
    }

    public static ResolveResult parseToList(String json, int start) {
        List<Object> array = new ArrayList<>();
        start = skipToNext(json, start, ARRAY.getStartSymbol());
        json = json.substring(start, findEndElementIndex(json, ARRAY.getStartSymbol(), ARRAY.getEndSymbol(), start) + 1);
        int initStart = start;
        start = 1;
        while (start < json.length()) {
            JsonType jsonType = valueTypeResolver.resolveType(json, start + 1);
            var valRes = valueResolver.resolve(jsonType, json, start + 1);
            if (valRes.getElementType() == NO_MORE_ELEMENTS) break;
            array.add(valRes.getValue());
            start = valRes.getEnd() + 1;
        }
        return ResolveResult.of(array, ARRAY, initStart + json.length());
    }

    public static int findEndElementIndex(String json, char startSym, char endSym, int start) {
        int midCount = 0;
        while (start < json.length()) {
            int end = json.indexOf(endSym, start + 1);
            if (end == -1) break;
            int betweenCount = countBetween(json, start, end, startSym);
            if (betweenCount == 0 && midCount == 0) return end;
            start = end + 1;
            midCount += betweenCount - 1;
        }
        throw new IllegalStateException("No ending element for starting symbol '" + startSym + "'");
    }

    private static int skipWhiteSpace(String json, int start) {
        while (start < json.length() && isWhitespace(json.charAt(start))) start++;
        return start;
    }

    private static int skipToNext(String json, int start, char ch) {
        while (start < json.length() && json.charAt(start) != ch) start++;
        return start;
    }

    private static int countBetween(String json, int start, int end, char sym) {
        int count = 0;
        for (int i = start + 1; i < end; i++) {
            if (json.charAt(i) == sym) count++;
        }
        return count;
    }
}
