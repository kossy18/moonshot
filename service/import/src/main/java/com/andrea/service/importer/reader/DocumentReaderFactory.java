/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.reader;

public interface DocumentReaderFactory {

    DocumentReader createReader(ReaderType type);
}
