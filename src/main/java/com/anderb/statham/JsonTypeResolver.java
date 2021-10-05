package com.anderb.statham;

@FunctionalInterface
public interface JsonTypeResolver {
    JsonType resolveType(String json, int start);
}
