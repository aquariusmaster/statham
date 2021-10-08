package com.anderb.statham;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;

import static com.anderb.statham.JsonType.*;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

@UtilityClass
public class JsonParser {

    public static Object parse(String json) {
        var type = resolveType(json, 0);
        return type.getParser().parse(json, 0).getValue();
    }

    public static JsonResult parseKey(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        startFrom = json.indexOf('"', startFrom);
        if (startFrom == -1) return JsonResult.empty();
        int end = json.indexOf("\":", startFrom);
        return startFrom < end ? JsonResult.of(json.substring(startFrom + 1, end), STRING, end + 2) : JsonResult.empty();
    }

    public static JsonResult parseToString(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int end = json.indexOf('"', startFrom + 1);
        return startFrom < end ? JsonResult.of(json.substring(startFrom + 1, end), STRING, end) : JsonResult.empty();
    }

    public static JsonResult parseToNumber(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int end = findEnd(json, startFrom);
        return startFrom < end ? JsonResult.of(json.substring(startFrom, end), NUMBER, end) : JsonResult.empty();
    }

    public static JsonResult parseToBoolean(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int end = findEnd(json, startFrom);
        return startFrom < end ? JsonResult.of(parseBoolean(json.substring(startFrom, end)), BOOLEAN, end) : JsonResult.empty();
    }

    public static JsonResult parseToNull(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int end = findEnd(json, startFrom);
        return startFrom < end ? JsonResult.of(null, NULL, end) : JsonResult.empty();
    }

    public static JsonResult parseToObject(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        var innerJson = json.substring(startFrom, findEndingElementIndex(json, '{', '}', startFrom) + 1);
        var jsonMap = new HashMap<String, Object>();
        int initStart = startFrom;
        startFrom = 0;
        while (startFrom < innerJson.length()) {
            var keyResult = parseKey(innerJson, startFrom);
            if (keyResult.getElementType() == EMPTY) break;
            startFrom = keyResult.getEnd();

            var type = resolveType(innerJson, startFrom);
            if (type == EMPTY) break;

            var valueResult = type.getParser().parse(innerJson, startFrom);
            startFrom = valueResult.getEnd() + 1;

            jsonMap.put((String) keyResult.getValue(), valueResult.getValue());
        }
        return JsonResult.of(jsonMap, OBJECT, initStart + innerJson.length());
    }

    public static JsonResult parseToList(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        var innerJson = json.substring(startFrom, findEndingElementIndex(json, '[', ']', startFrom) + 1);
        var array = new ArrayList<>();
        int initStart = startFrom;
        startFrom = 1;
        while (startFrom < innerJson.length()) {
            var type = resolveType(innerJson, startFrom);
            var valRes = type.getParser().parse(innerJson, startFrom);
            if (valRes.getElementType() == EMPTY) break;
            array.add(valRes.getValue());
            startFrom = valRes.getEnd() + 1;
        }
        return JsonResult.of(array, ARRAY, initStart + innerJson.length());
    }

    public static JsonType resolveType(String json, int start) {
        start = JsonParser.skipWhiteSpaces(json, start);
        char ch = json.charAt(start);
        if (ch == '"') return STRING;
        if (isDigit(ch)) return NUMBER;
        if (ch == 't' || ch == 'f') return BOOLEAN;
        if (ch == 'n') return NULL;
        if (ch == '{') return OBJECT;
        if (ch == '[') return ARRAY;
        return EMPTY;
    }

    public static int findEndingElementIndex(String json, char startChar, char endChar, int startFrom) {
        while (startFrom < json.length()) {
            int end = json.indexOf(endChar, startFrom);
            if (end == -1) break;
            int betweenCount = countBetween(json, startFrom, end, startChar);
            if (betweenCount == 0) return end;
            startFrom = skip(json, end, betweenCount, endChar);
            if (startFrom == -1)
                throw new IllegalStateException("No ending element for starting symbol '" + startChar + "'");
            startFrom++;
        }
        return startFrom - 1;
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

    private static int skip(String json, int start, int count, char ch) {
        while (count-- > 0) {
            start = json.indexOf(ch, start + 1);
        }
        return start;
    }

    private static int findEnd(String json, int startFrom) {
        int end = json.indexOf(',', startFrom + 1);
        if (end != -1) return end;
        end = findMinEnd(
                json.indexOf(' ', startFrom + 1),
                json.indexOf('\n', startFrom + 1),
                json.indexOf('}', startFrom + 1)
        );
        if (end == -1) throw new IllegalStateException("No valid json end for '" + startFrom + "' position");
        return end;
    }

    private static int findMinEnd(int... values) {
        if (values.length == 1) return values[0];
        int min = -1;
        for (int value : values) {
            if (value != -1 && min == -1) {
                min = value;
                continue;
            }
            if (value != -1 && value < min) min = value;
        }
        return min;
    }

}
