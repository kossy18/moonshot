/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.reader;

import com.github.kossy18.service.importer.reader.spreadsheet.SpreadsheetDocumentReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class SpreadsheetDocumentReaderTest {
    private static final String XLS_FILE_PATH = "src/test/resources/test2.xls";
    private static final String XLSX_FILE_PATH = "src/test/resources/test.xlsx";

    @Test
    public void readAndVerifySpreadsheetDocument() throws IOException {
        DocumentReader reader = new SpreadsheetDocumentReader();

        RowSeeker seeker = reader.read(new FileInputStream(XLS_FILE_PATH));
        seeker.next(); // Skip the header row
        List<Cell> cells = seeker.next().getCells();
        seeker.close();

        assertEquals("1.0", cells.get(0).getValue());
        assertEquals("Book", cells.get(1).getValue());
        assertEquals("5.0", cells.get(2).getValue());
        assertEquals("5.25", cells.get(3).getValue());
    }

    @Test
    public void readAndVerifySpreadsheetDocument_2() throws IOException {
        DocumentReader reader = new SpreadsheetDocumentReader();

        RowSeeker seeker = reader.read(new FileInputStream(XLSX_FILE_PATH));
        seeker.next(); // Skip the header row
        List<Cell> cells = seeker.next().getCells();
        seeker.close();

        assertEquals("1.0", cells.get(0).getValue());
        assertEquals("Book", cells.get(1).getValue());
        assertEquals("5.0", cells.get(2).getValue());
        assertEquals("5.25", cells.get(3).getValue());
    }

    /*@Test
    public void readAndVerifyDifferentSpreadsheetIndex() throws IOException {
        DocumentReader reader = new SpreadsheetDocumentReader();

        RowSeeker seeker = reader.read(new FileInputStream(XLSX_FILE_PATH));
        seeker.next();
        seeker.next();
        List<Cell> cells = seeker.next().getCells();
        seeker.close();

        // We match with doubles instead of integers due to the conversion by the poi library

        assertEquals("2.0", cells.get(0).getValue());
        assertEquals("Pen", cells.get(1).getValue());
        assertEquals("3.0", cells.get(2).getValue());
        assertEquals("1.52", cells.get(3).getValue());
    }*/

    @Test
    public void readAndCountSpreadsheetDocument() throws IOException {
        DocumentReader reader = new SpreadsheetDocumentReader();
        RowSeeker seeker = reader.read(new FileInputStream(XLSX_FILE_PATH));
        assertEquals(3, seeker.count());
    }
}