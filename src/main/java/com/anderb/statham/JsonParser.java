package com.anderb.statham;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;

import static com.anderb.statham.JsonType.*;
import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;

@UtilityClass
public class JsonParser {

    public static Object parse(String json) {
        var type = resolveType(json, 0, json.length() - 1);
        return type != null ? type.getParser().parse(json, 0, json.length() - 1).getValue() : null;
    }

    public static JsonResult parseKey(String json, int startFrom, int endTo) {
        startFrom = skipWhiteSpaces(json, startFrom, endTo);
        startFrom = json.indexOf('"', startFrom);
        if (startFrom == -1 || startFrom > endTo) return JsonResult.empty();
        int end = json.indexOf('"', startFrom + 1);
        if (end > endTo) return JsonResult.empty();
        int delimiterIndex = json.indexOf(':', end);
        return startFrom < end
                ? JsonResult.of(json.substring(startFrom + 1, end), STRING, delimiterIndex < endTo ? delimiterIndex + 1 : -1)
                : JsonResult.empty();
    }

    public static JsonResult parseToString(String json, int startFrom, int endTo) {
        startFrom = skipWhiteSpaces(json, startFrom, endTo);
        int stringEnd = findNextValidIndexOf(json, startFrom + 1, endTo, '"');
        return JsonResult.of(json.substring(startFrom + 1, stringEnd), STRING, findNextEnd(json, stringEnd, endTo));
    }

    public static JsonResult parseToNumber(String json, int startFrom, int endTo) {
        startFrom = skipWhiteSpaces(json, startFrom, endTo);
        int numberEnd = findValueEnd(json, startFrom, endTo);
        return JsonResult.of(json.substring(startFrom, numberEnd), NUMBER, findNextEnd(json, numberEnd, endTo));
    }

    public static JsonResult parseToBoolean(String json, int startFrom, int endTo) {
        startFrom = skipWhiteSpaces(json, startFrom, endTo);
        boolean value = json.charAt(startFrom) == 't';
        return JsonResult.of(value, BOOLEAN, findNextEnd(json, startFrom + 4, endTo));
    }

    public static JsonResult parseToNull(String json, int startFrom, int endTo) {
        startFrom = skipWhiteSpaces(json, startFrom, endTo);
        return JsonResult.of(null, NULL, findNextEnd(json, startFrom + 4, endTo));
    }

    public static JsonResult parseToObject(String json, int startFrom, int endTo) {
        startFrom = skipWhiteSpaces(json, startFrom, endTo);
        endTo = findEndingElementIndex(json, startFrom, endTo, '{', '}') + 1;
        var jsonMap = new HashMap<String, Object>();
        while (startFrom <= endTo) {
            var keyResult = parseKey(json, startFrom, endTo);
            if (keyResult.getElementType() == null) break;
            startFrom = keyResult.getEnd();

            var type = resolveType(json, startFrom, endTo);
            if (type == null) break;

            var valueResult = type.getParser().parse(json, startFrom, endTo);
            jsonMap.put((String) keyResult.getValue(), valueResult.getValue());
            if (valueResult.getEnd() == -1) break;
            startFrom = valueResult.getEnd();
        }
        return JsonResult.of(jsonMap, OBJECT, endTo);
    }

    public static JsonResult parseToList(String json, int startFrom, int endTo) {
        startFrom = skipWhiteSpaces(json, startFrom, endTo);
        endTo = findEndingElementIndex(json, startFrom, endTo, '[', ']') + 1;
        var array = new ArrayList<>();
        while (startFrom <= endTo) {
            var type = resolveType(json, startFrom + 1, endTo);
            if (type == null) break;
            var valRes = type.getParser().parse(json, startFrom + 1, endTo);
            if (valRes.getElementType() == null) break;
            array.add(valRes.getValue());
            if (valRes.getEnd() == -1) break;
            startFrom = valRes.getEnd();
        }
        return JsonResult.of(array, ARRAY, endTo);
    }

    public static JsonType resolveType(String json, int start, int endTo) {
        start = skipWhiteSpaces(json, start, endTo);
        char ch = json.charAt(start);
        if (ch == '"') return STRING;
        if (isDigit(ch)) return NUMBER;
        if (ch == 't' || ch == 'f') return BOOLEAN;
        if (ch == 'n') return NULL;
        if (ch == '{') return OBJECT;
        if (ch == '[') return ARRAY;
        return null;
    }

    public static int findEndingElementIndex(String json, int startFrom, int endTo, char startChar, char endChar) {
        while (startFrom <= endTo) {
            int end = json.indexOf(endChar, startFrom);
            if (end == -1 || end > endTo) break;
            int betweenCount = countBetween(json, startFrom, end, startChar);
            if (betweenCount == 0) return end;
            startFrom = skip(json, end, betweenCount, endChar);
            if (startFrom == -1)
                throw new IllegalStateException("No ending element for starting symbol '" + startChar + "'");
            startFrom++;
        }
        return startFrom - 1;
    }

    public static int skipWhiteSpaces(String json, int startFrom, int endTo) {
        if (startFrom == 0 && endTo == 0) return 0;
        while (startFrom <= endTo && isWhitespace(json.charAt(startFrom))) startFrom++;
        return startFrom;
    }

    public static int countBetween(String json, int start, int end, char ch) {
        int count = 0;
        while (++start <= end) if (json.charAt(start) == ch) count++;
        return count;
    }

    private static int skip(String json, int start, int count, char ch) {
        while (count-- > 0) {
            start = json.indexOf(ch, start + 1);
        }
        return start;
    }

    private static int findValueEnd(String json, int startFrom, int endTo) {
        int end = findMinValueEnd(json, startFrom, endTo, ',', ' ', '\n', '}', ']');
        if (end == -1) throw new IllegalStateException("No valid json end for '" + startFrom + "' position");
        return end;
    }

    private static int findMinValueEnd(String json, int startFrom, int endTo, char... chars) {
        if (chars.length == 1) return json.indexOf(chars[0], startFrom);
        while (++startFrom <= endTo) {
            char cur = json.charAt(startFrom);
            for (char value : chars) {
                if (cur == value) return startFrom;
            }
        }
        return -1;
    }

    private static int findNextEnd(String json, int startFrom, int endTo) {
        int end = json.indexOf(',', startFrom);
        return end == -1 || end >= endTo ? -1 : end;
    }

    private static int findNextValidIndexOf(String json, int startFrom, int endTo, char ch) {
        if (startFrom > endTo) return -1;
        int end = json.indexOf(ch, startFrom);
        if (end == -1) return -1;
        if (end < endTo && json.charAt(end - 1) == '\\') return findNextValidIndexOf(json, end + 1, endTo, ch);
        return end >= endTo ? -1 : end;
    }
}
