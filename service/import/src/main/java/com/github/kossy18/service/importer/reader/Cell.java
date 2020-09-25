/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.reader;

public interface Cell {

    int getColumnIndex();

    String getColumnName();

    String getValue();
}
