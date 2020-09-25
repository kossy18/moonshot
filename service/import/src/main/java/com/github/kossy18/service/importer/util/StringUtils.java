/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.util;

public class StringUtils {

    private StringUtils() {
        // No implementation
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
