///*
// * Copyright (c) 2020. Inyiama Kossy
// */
//
//package com.andrea.service.importer.document;
//
//import org.junit.Test;
//
//import java.util.List;
//
//public class SpreadsheetDocumentReaderTest {
//    private static final String XLSX_FILE_PATH = "src/test/resources/test.xlsx";
//
//    @Test
//    public void readAndVerifySpreadsheetDocument() {
//        DocumentReader reader = new SpreadsheetDocumentReader();
//
//        RowSeeker seeker = reader.read(XLSX_FILE_PATH, 0);
//        seeker.next(); // Skip the header row
//        List<Cell> cells = seeker.next().getCells();
//        seeker.close();
//
//        assertEquals(cells.get(0).getValue(), "1.0");
//        assertEquals(cells.get(1).getValue(), "Book");
//        assertEquals(cells.get(2).getValue(), "5.0");
//    }
//
//    @Test
//    public void readAndVerifyDifferentSpreadsheetIndex() {
//        DocumentReader reader = new SpreadsheetDocumentReader();
//
//        RowSeeker seeker = reader.read(XLSX_FILE_PATH, 1);
//        seeker.next();
//        seeker.next();
//        List<Cell> cells = seeker.next().getCells();
//        seeker.close();
//
//        // We match with doubles instead of integers due to the conversion by the poi library
//
//        assertEquals(cells.get(0).getValue(), "2.0");
//        assertEquals(cells.get(1).getValue(), "Pen");
//        assertEquals(cells.get(2).getValue(), "3.0");
//        assertEquals(cells.get(3).getValue(), "1.52");
//    }
//
//    @Test
//    public void readAndCountSpreadsheetDocument() {
//        DocumentReader reader = new SpreadsheetDocumentReader();
//        RowSeeker seeker = reader.read(XLSX_FILE_PATH, 0);
//        assertEquals(seeker.count(), 3);
//    }
//}