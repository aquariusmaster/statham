package com.anderb.statham;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;

import static com.anderb.statham.JsonType.*;
import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;
import static java.util.Collections.emptyList;

@UtilityClass
public class JsonParser {

    public static Object parse(String json) {
        var type = resolveType(json, 0);
        return type != null ? type.getParser().parse(json, 0).getValue() : null;
    }

    public static JsonResult parseKey(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        startFrom = json.indexOf('"', startFrom);
        if (startFrom == -1) return JsonResult.empty();
        int end = json.indexOf('\"', startFrom + 1);
        int delimiterIndex = json.indexOf(':', end);
        return startFrom < end ? JsonResult.of(json.substring(startFrom + 1, end), STRING, delimiterIndex + 1) : JsonResult.empty();
    }

    public static JsonResult parseToString(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int stringEnd = json.indexOf('"', startFrom + 1);
        return JsonResult.of(json.substring(startFrom + 1, stringEnd), STRING, json.indexOf(',', stringEnd));
    }

    public static JsonResult parseToNumber(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        int numberEnd = findValueEnd(json, startFrom);
        return JsonResult.of(json.substring(startFrom, numberEnd), NUMBER, json.indexOf(',', numberEnd));
    }

    public static JsonResult parseToBoolean(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        boolean value = json.charAt(startFrom) == 't';
        return JsonResult.of(value, BOOLEAN, json.indexOf(',', startFrom + 4));
    }

    public static JsonResult parseToNull(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        return JsonResult.of(null, NULL, json.indexOf(',', startFrom + 4));
    }

    public static JsonResult parseToObject(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        json = json.substring(startFrom, findEndingElementIndex(json, '{', '}', startFrom) + 1);
        var jsonMap = new HashMap<String, Object>();
        int initStart = startFrom;
        startFrom = 0;
        while (startFrom < json.length()) {
            var keyResult = parseKey(json, startFrom);
            if (keyResult.getElementType() == null) break;
            startFrom = keyResult.getEnd();

            var type = resolveType(json, startFrom);
            if (type == null) break;

            var valueResult = type.getParser().parse(json, startFrom);
            jsonMap.put((String) keyResult.getValue(), valueResult.getValue());
            if (valueResult.getEnd() == -1) break;
            startFrom = valueResult.getEnd() + 1;
        }
        return JsonResult.of(jsonMap, OBJECT, initStart + json.length());
    }

    public static JsonResult parseToList(String json, int startFrom) {
        startFrom = skipWhiteSpaces(json, startFrom);
        json = json.substring(startFrom, findEndingElementIndex(json, '[', ']', startFrom) + 1);
        int initStart = startFrom;
        startFrom = 1;
        var array = new ArrayList<>();
        while (startFrom < json.length()) {
            var type = resolveType(json, startFrom);
            if (type == null) break;
            var valRes = type.getParser().parse(json, startFrom);
            if (valRes.getElementType() == null) break;
            array.add(valRes.getValue());
            if (valRes.getEnd() == -1) break;
            startFrom = valRes.getEnd() + 1;
        }
        return JsonResult.of(array, ARRAY, initStart + json.length());
    }

    public static JsonType resolveType(String json, int start) {
        start = skipWhiteSpaces(json, start);
        char ch = json.charAt(start);
        if (ch == '"') return STRING;
        if (isDigit(ch)) return NUMBER;
        if (ch == 't' || ch == 'f') return BOOLEAN;
        if (ch == 'n') return NULL;
        if (ch == '{') return OBJECT;
        if (ch == '[') return ARRAY;
        return null;
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

    private static int findValueEnd(String json, int startFrom) {
        int end = findMinValueEnd(json, startFrom, ',', ' ', '\n', '}', ']');
        if (end == -1) throw new IllegalStateException("No valid json end for '" + startFrom + "' position");
        return end;
    }

    private static int findMinValueEnd(String json, int startFrom, char... chars) {
        if (chars.length == 1) return json.indexOf(chars[0], startFrom);
        while (++startFrom < json.length()) {
            char cur = json.charAt(startFrom);
            for (char value : chars) {
                if (cur == value) return startFrom;
            }
        }
        return -1;
    }

}
