package com.anderb.statham;

@FunctionalInterface
public interface JsonValueParser {
    JsonResult parse(String json, int startFrom);
}
