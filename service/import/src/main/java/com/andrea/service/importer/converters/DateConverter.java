/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.converters;

import com.andrea.service.importer.reader.Cell;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Converts strings to a {@link java.util.Date Date} object
 *
 * @author Inyiama Kossy
 * @see CellConverter
 */
public class DateConverter implements CellConverter {

    @Override
    public Object convert(String dateFormat, Cell... cell) {
        try {
            return new SimpleDateFormat(dateFormat).parse(cell[0].getValue());
        } catch (ParseException e) {
            throw new ConverterException("Could not parse date " + cell[0].getValue() + " with format " + dateFormat, e);
        }
    }
}
