package com.anderb.statham;

@FunctionalInterface
public interface TypeResolver {
    JsonType resolveType(String json, int start);
}
