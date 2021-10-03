package com.anderb.statham;

@FunctionalInterface
public interface ElementResolver {
    ResolveResult resolve(String json, int start);
}
