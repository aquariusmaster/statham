package com.anderb.statham;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.anderb.statham.JsonType.NO_MORE_ELEMENTS;

@RequiredArgsConstructor
public class Statham {

    private final KeyResolver keyResolver;
    private final CommonValueResolver baseValueResolver;
    private final Hydrator hydrator;

    public Statham() {
        keyResolver = ElementResolvers::keyResolver;
        hydrator = new BaseHydrator();
        baseValueResolver = new CommonValueResolver();
    }

    public <T> T jsonToObj(String json, Class<T> clazz) {
        return hydrator.hydrate(parseToMap(json), clazz);
    }

    public Map<String, Object> parseToMap(String json) {
        Map<String, Object> jsonMap = new HashMap<>();
        int start = 0;
        int end = 0;
        while (start < json.length()) {
            var keyResult = keyResolver.resolveNext(json, start);
            if (keyResult.getElementType() == NO_MORE_ELEMENTS) break;
            String key = keyResult.getValue();
            start = keyResult.getEnd() + 1;

            end = json.indexOf(",", start);
            if (end == -1) {
                end = json.length();
            }
            Object value;
            int valueStart = findValueStart(json, start + 1, end);
            String line = json.substring(start, end);
            if (valueStart == -1) break;
            String key = parseToStringKey(line);
            int startObjIndex = findObjectStart(json, valueStart + 1, end);
            int startArrayIndex = findArrayStart(json, valueStart + 1, end);
            if (startObjIndex != -1 && (startArrayIndex != -1 && startArrayIndex < startObjIndex)) {
                end = json.indexOf('}', startObjIndex) + 1;
                value = parseToMap(json.substring(startObjIndex, end));
            } else if (startArrayIndex != -1) {
                end = json.indexOf(']', startObjIndex) + 1;
                value = parseToList(json.substring(startArrayIndex, end));
            } else {
                value = parseToStringValue(line);
            }
            jsonMap.put(key, value);
            start = end + 1;
        }

        System.out.println(jsonMap);
        return jsonMap;
    }

    public List<Object> parseToList(String json) {
        List<Object> array = new ArrayList<>();
        int start = 1;
        int end = 0;
        while (start < json.length()) {
            end = json.indexOf("    },\n    {", start);
            if (end == -1) {
                end = json.length();
            }
            array.add(parseToMap(json.substring(start, end)));
            start = end + 1;
        }
        System.out.println(array);
        return array;
    }

    private int findValueStart(String json, int start, int end) {
        int fig = json.indexOf(':', start);
        return fig == -1 || fig > end ? -1 : fig + 1;
    }

    private int findObjectStart(String json, int start, int end) {
        int fig = json.indexOf('{', start);
        return fig == -1 || fig > end ? -1 : fig;
    }

    private int findArrayStart(String json, int start, int end) {
        int arrayStartIndex = json.indexOf('[', start);
        return arrayStartIndex == -1 || arrayStartIndex > end ? -1 : arrayStartIndex;
    }

    private static String parseToStringKey(String line) {
        int start = line.indexOf("\"");
        if (start == -1) return null;
        int end = line.indexOf("\":", start);
        if (start + 1 > end) return null;
        return line.substring(start + 1, end);
    }

    private static String parseKey(String json, int l, int r) {
        int start = json.indexOf("\"", l);
        if (start == -1) return null;
        int end = json.indexOf("\":", start + r);
        if (start + 1 > end) return null;
        return json.substring(start + 1, end);
    }

    private static Object parseToStringValue(String line) {
        int start = line.indexOf(": ");
        if (start == -1) return "";
        boolean isStringValue = line.charAt(start + 2) == '"';
        if (isStringValue) {
            int end = line.lastIndexOf("\"");
            if (end == -1) return "";
            return line.substring(start + 3, end);
        }
        int end = -1;
        if (line.charAt(line.length() - 1) == ',') {
            end = line.lastIndexOf(",");
        } else {
            end = line.length();
        }
        if (end == -1 || start + 2 > end) return "";
        return line.substring(start + 2, end);
    }

    public static Statham defaultInstance() {
        var hydrator = new BaseHydrator();
        var valueResolvers = Map.of();
        return new Statham(hydrator, );
    }

}
