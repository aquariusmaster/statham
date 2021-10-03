package com.anderb.statham;

@FunctionalInterface
public interface ElementResolver<E extends Enum<E>> {
    ResolveResult resolveNext(String json, int start);
}
