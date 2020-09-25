/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.converters;

import com.github.kossy18.service.importer.ImporterException;

public class ConverterException extends ImporterException {

    public ConverterException(String s, Throwable e) {
        super(s, e);
    }
}
