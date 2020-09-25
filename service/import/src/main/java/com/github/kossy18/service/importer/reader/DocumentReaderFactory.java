/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.reader;

public interface DocumentReaderFactory {

    DocumentReader createReader(ReaderType type);
}
