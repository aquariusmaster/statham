package com.anderb.statham;

@FunctionalInterface
public interface ValueTypeResolver {
    JsonType resolveType(String json, int start);
}
