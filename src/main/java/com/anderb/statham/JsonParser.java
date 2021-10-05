package com.anderb.statham;

import java.util.ArrayList;
import java.util.HashMap;

import static com.anderb.statham.JsonType.*;
import static java.lang.Character.isWhitespace;

public class JsonParser {
    private static final ElementParser keyParser = JsonParser::parseKey;
    private static final JsonTypeResolver typeResolver = new NextValueTypeResolver();

    public static Object parse(String json) {
        var type = typeResolver.resolveType(json, 0);
        return type.getParser().parse(json, 0).getValue();
    }

    public static JsonResult parseKey(String json, int startFrom) {
        int innerStart = json.indexOf('"', startFrom);
        if (innerStart == -1) return JsonResult.empty();
        int end = json.indexOf("\":", innerStart);
        if (innerStart + 1 > end) return JsonResult.empty();
        return JsonResult.of(json.substring(innerStart + 1, end), STRING, end);
    }

    public static JsonResult parseToString(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int end = json.indexOf('"', startFrom + 1);
        if (end == -1) return JsonResult.empty();
        return JsonResult.of(json.substring(startFrom + 1, end), STRING, end);
    }

    public static JsonResult parseToNumber(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int end = json.indexOf(',', startFrom + 1);
        if (end == -1) return JsonResult.empty();
        var value = json.substring(startFrom, end);
        return JsonResult.of(value, NUMBER, end);
    }

    public static JsonResult parseToBoolean(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int end = json.indexOf(',', startFrom + 1);
        if (end == -1) return JsonResult.empty();
        return JsonResult.of(Boolean.parseBoolean(json.substring(startFrom, end)), BOOLEAN, end);
    }

    public static JsonResult parseToNull(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int end = json.indexOf(',', startFrom + 1);
        if (end == -1) return JsonResult.empty();
        return JsonResult.of(null, NULL, end);
    }

    public static JsonResult parseToObject(String innerJson, int startFrom) {
        startFrom = skipWhiteSpaces(innerJson, startFrom);
        innerJson = innerJson.substring(startFrom, findEndingElementIndex(innerJson, '{', '}', startFrom) + 1);
        var jsonMap = new HashMap<String, Object>();
        int initStart = startFrom;
        startFrom = 1;
        while (startFrom < innerJson.length()) {
            var keyResult = keyParser.parse(innerJson, startFrom + 1);
            if (keyResult.getElementType() == EMPTY) break;
            startFrom = keyResult.getEnd() + 2;

            var type = typeResolver.resolveType(innerJson, startFrom);
            if (type == EMPTY) break;

            var valueResult = type.getParser().parse(innerJson, startFrom);
            startFrom = valueResult.getEnd() + 1;

            jsonMap.put((String) keyResult.getValue(), valueResult.getValue());
        }
        return JsonResult.of(jsonMap, OBJECT, initStart + innerJson.length());
    }

    public static JsonResult parseToList(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        json = json.substring(startFrom, findEndingElementIndex(json, '[', ']', startFrom) + 1);
        var array = new ArrayList<>();
        int initStart = startFrom;
        startFrom = 1;
        while (startFrom < json.length()) {
            var type = typeResolver.resolveType(json, startFrom + 1);
            var valRes = type.getParser().parse(json, startFrom + 1);
            if (valRes.getElementType() == EMPTY) break;
            array.add(valRes.getValue());
            startFrom = valRes.getEnd() + 1;
        }
        return JsonResult.of(array, ARRAY, initStart + json.length());
    }

    public static int findEndingElementIndex(String json, char startChar, char endChar, int startFrom) {
        int midCount = 0;
        while (startFrom < json.length()) {
            int end = json.indexOf(endChar, startFrom + 1);
            if (end == -1) break;
            int betweenCount = countBetween(json, startFrom, end, startChar);
            if (betweenCount == 0 && midCount == 0) return end;
            startFrom = end + 1;
            midCount += betweenCount - 1;
        }
        throw new IllegalStateException("No ending element for starting symbol '" + startChar + "'");
    }

    public static int skipWhiteSpaces(String json, int startFrom) {
        while (startFrom < json.length() && isWhitespace(json.charAt(startFrom))) startFrom++;
        return startFrom;
    }

    public static int countBetween(String json, int start, int end, char ch) {
        int count = 0;
        for (int i = start + 1; i < end; i++) {
            if (json.charAt(i) == ch) count++;
        }
        return count;
    }

}
