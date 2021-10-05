package com.anderb.statham;

@FunctionalInterface
public interface ElementParser {
    JsonResult parse(String json, int startFrom);
}
