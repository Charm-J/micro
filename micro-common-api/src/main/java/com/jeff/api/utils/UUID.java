package com.jeff.api.utils;

public class UUID {
    public static String gen() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
