/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.xml;

import com.github.kossy18.service.importer.ImporterException;

public class MappingException extends ImporterException {
    public MappingException(String s) {
        super(s);
    }

    public MappingException(String s, Throwable e) {
        super(s, e);
    }
}
