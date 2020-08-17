/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.converters;

import com.andrea.service.importer.ImporterException;

import java.text.ParseException;

public class ConverterException extends ImporterException {

    public ConverterException(String s, Throwable e) {
        super(s, e);
    }
}
