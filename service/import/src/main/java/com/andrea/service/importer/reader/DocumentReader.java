/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.reader;

import java.io.InputStream;

public interface DocumentReader {

    RowSeeker read(InputStream in);
}
