/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.xml;

public class InvalidMappingException extends MappingException {

    public InvalidMappingException(String s) {
        super(s);
    }

    public InvalidMappingException(String s, Throwable e) {
        super(s, e);
    }
}
