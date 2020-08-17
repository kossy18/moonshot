/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.reader;

import com.andrea.service.importer.ImporterException;

public class DocumentReaderException extends ImporterException {
    public DocumentReaderException(String s, Throwable e) {
        super(s, e);
    }
}
