/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.xml;

import com.andrea.service.importer.ImporterException;

public class MappingException extends ImporterException {
    public MappingException(String s) {
        super(s);
    }

    public MappingException(String s, Throwable e) {
        super(s, e);
    }
}
