/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.util;

public final class AssertUtils {

    public static void notEmpty(String s) {
        if (StringUtils.isEmpty(s)) {
            throw new IllegalArgumentException("String supplied must not be empty");
        }
    }

    public static void notNull(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Argument supplied must not be null");
        }
    }
}
