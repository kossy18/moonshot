/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.reader;

import com.andrea.service.importer.reader.csv.CsvDocumentReader;

public class DefaultDocumentReaderFactory implements DocumentReaderFactory {

    @Override
    public DocumentReader createReader(ReaderType type) {
        if (type == ReaderType.CSV) {
            return new CsvDocumentReader();
        }
        throw new IllegalArgumentException("ReaderType: " + type + " is not supported");
    }
}
