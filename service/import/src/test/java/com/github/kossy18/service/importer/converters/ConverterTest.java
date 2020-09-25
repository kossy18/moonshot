/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.converters;

import com.github.kossy18.service.importer.reader.Cell;
import com.github.kossy18.service.importer.reader.DefaultCell;
import com.github.kossy18.service.importer.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ConverterTest {

    @Test
    public void convertStringToDate() {
        CellConverter dateConverter = new DateConverter();

        Cell cell = new DefaultCell(1, "Column_1", "22/03/2019");
        Date expectedResult = (Date) dateConverter.convert("dd/MM/yyyy", cell);
        Assert.assertEquals(expectedResult, TestUtils.getDate(22, 3, 2019));
    }

    @Test
    public void convertStringToInteger() {
        CellConverter numberConverter = new NumberConverter();

        Cell cell = new DefaultCell(1, "Column_1", "9123");
        int expectedResult = (int) numberConverter.convert(NumberConverter.NumberType.INTEGER.name(), cell);
        Assert.assertEquals(9123, expectedResult);
    }

    @Test
    public void convertStringToDouble() {
        CellConverter numberConverter = new NumberConverter();

        Cell cell = new DefaultCell(1, "Column_1", "167.123456789");
        double expectedResult = (double) numberConverter.convert(NumberConverter.NumberType.DOUBLE.name(), cell);
        Assert.assertEquals(167.123456789, expectedResult, 0.0);
    }
}
