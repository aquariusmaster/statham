package com.anderb.statham;

public interface ValueTypeResolver {
    JsonType resolveType(String json, int start);
}
