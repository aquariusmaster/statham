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
        json = json.substring(start, findEndElementIndex(json, OBJECT.getStartSymbol(), OBJECT.getEndSymbol(), start) + 1);
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

//        System.out.println("new Obj: " + jsonMap);
        return ResolveResult.of(jsonMap, OBJECT, initStart + json.length());
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
//        System.out.println("new list: " + array);
        return ResolveResult.of(array, ARRAY, initStart + json.length());
    }

    private static int skipWhiteSpace(String json, int start) {
        while (start < json.length() && isWhitespace(json.charAt(start))) start++;
        return start;
    }

    private static int skipToNext(String json, int start, char ch) {
        while (start < json.length() && json.charAt(start) != ch) start++;
        return start;
    }

    public static int findEndElementIndex(String json, char startSym, char endSym, int start) {
        int midCount = 0;
        while (start < json.length()) {
            int end = json.indexOf(endSym, start + 1);
            if (end == -1) break;
            int betweenCount = countBetween(json, start, end, startSym);
            if (betweenCount == 0 && midCount == 0) return end;
            start = end + 1;
            midCount+=betweenCount - 1;
        }
        throw new IllegalStateException("No ending element for starting symbol '" + startSym + "'");
    }

    private static int countBetween(String json, int start, int end, char sym) {
        int count = 0;
        for (int i = start + 1; i < end; i++) {
            if (json.charAt(i) == sym) {
                count++;
            }
        }
        return count;
    }
}
