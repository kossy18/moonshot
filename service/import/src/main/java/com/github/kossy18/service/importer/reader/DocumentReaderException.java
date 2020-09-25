/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.reader;

import com.github.kossy18.service.importer.ImporterException;

public class DocumentReaderException extends ImporterException {
    public DocumentReaderException(String s, Throwable e) {
        super(s, e);
    }
}
