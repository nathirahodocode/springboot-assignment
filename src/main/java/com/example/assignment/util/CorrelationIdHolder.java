package com.example.assignment.util;

public final class CorrelationIdHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    private CorrelationIdHolder() {}

    public static void set(String id) { CONTEXT.set(id); }
    public static String get() { return CONTEXT.get(); }
    public static void clear() { CONTEXT.remove(); }
}