/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.converters;

import com.andrea.service.importer.reader.Cell;
import com.andrea.service.importer.reader.DefaultCell;
import com.andrea.service.importer.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ConverterTest {

    @Test
    public void convertStringToDate() {
        Converter dateConverter = new DateConverter();

        Cell cell = new DefaultCell(1, "Column_1", "22/03/2019");
        Date expectedResult = (Date) dateConverter.convert(cell, "dd/MM/yyyy");
        Assert.assertEquals(expectedResult, TestUtils.getDate(22, 3, 2019));
    }

    @Test
    public void convertStringToInteger() {
        Converter numberConverter = new NumberConverter();

        Cell cell = new DefaultCell(1, "Column_1", "9123");
        int expectedResult = (int) numberConverter.convert(cell, NumberConverter.NumberType.INTEGER.name());
        Assert.assertEquals(expectedResult, 9123);
    }

    @Test
    public void convertStringToDouble() {
        Converter numberConverter = new NumberConverter();

        Cell cell = new DefaultCell(1, "Column_1", "167.123456789");
        double expectedResult = (double) numberConverter.convert(cell, NumberConverter.NumberType.DOUBLE.name());
        Assert.assertEquals(expectedResult, 167.123456789, 0.0);
    }
}
