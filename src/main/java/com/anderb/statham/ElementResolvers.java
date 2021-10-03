package com.anderb.statham;

public class ElementResolvers {

    public static ResolveResult keyResolver(String json, int start) {
        int start1 = json.indexOf("\"", start);
        if (start1 == -1) return ResolveResult.noElement(json);
        int end = json.indexOf("\":", start1);
        if (start1 + 1 > end) return ResolveResult.noElement(json);
        return ResolveResult.of(json.substring(start1 + 1, end), JsonType.ELEMENT, end);
    }
}
