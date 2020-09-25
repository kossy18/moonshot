/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.converters;

import com.github.kossy18.service.importer.reader.Cell;

public interface CellConverter {

    Object convert(String extras, Cell... cells);
}
