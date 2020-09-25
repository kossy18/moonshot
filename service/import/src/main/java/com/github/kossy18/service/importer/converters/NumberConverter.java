/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.converters;

import com.github.kossy18.service.importer.reader.Cell;
import com.github.kossy18.service.importer.util.StringUtils;

public class NumberConverter implements CellConverter {

    @Override
    public Object convert(String numberType, Cell... cells) {
        if (StringUtils.isEmpty(numberType)) {
            return 0;
        }

        Cell cell = cells[0];

        try {
            NumberType type = NumberType.valueOf(numberType.toUpperCase());

            switch (type) {
                case INTEGER:
                    return Double.valueOf(cell.getValue()).intValue();
                case FLOAT:
                    return Double.valueOf(cell.getValue()).floatValue();
                case DOUBLE:
                    return Double.valueOf(cell.getValue());
                case LONG:
                    return Double.valueOf(cell.getValue()).longValue();
                case BYTE:
                    return Double.valueOf(cell.getValue()).byteValue();
                case SHORT:
                    return Double.valueOf(cell.getValue()).shortValue();
            }
        } catch (NumberFormatException e) {
            throw new ConverterException("Could not convert '" + cell.getValue() + "' to the type: " + numberType, e);
        } catch (IllegalArgumentException e) {
            throw new ConverterException("Could not convert to '" + numberType + "'", e);
        }
        return 0;
    }

    public enum NumberType {
        INTEGER, FLOAT, DOUBLE, LONG, BYTE, SHORT
    }
}
