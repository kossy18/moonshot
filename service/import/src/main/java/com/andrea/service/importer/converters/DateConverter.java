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
 * @see Converter
 */
public class DateConverter implements Converter {

    @Override
    public Object convert(Cell cell, String dateFormat) {
        try {
            return new SimpleDateFormat(dateFormat).parse(cell.getValue());
        } catch (ParseException e) {
            throw new ConverterException("Could not parse date " + cell.getValue() + " with format " + dateFormat, e);
        }
    }
}
