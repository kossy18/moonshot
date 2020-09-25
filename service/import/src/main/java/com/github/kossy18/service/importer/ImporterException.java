/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer;

public class ImporterException extends RuntimeException {

    public ImporterException(String s) {
        super(s);
    }

    public ImporterException(String s, Throwable e) {
        super(s, e);
    }
}
