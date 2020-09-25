/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.reader;

import com.github.kossy18.service.importer.reader.csv.CsvDocumentReader;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvDocumentReaderTest {
    private static final String CSV_FILE_PATH = "src/test/resources/test.csv";

    // TODO Move to setUp and close the stream

    @Test
    public void readAndVerifyCsvDocument() throws FileNotFoundException {
        DocumentReader reader = new CsvDocumentReader();

        RowSeeker seeker = reader.read(new FileInputStream(CSV_FILE_PATH));
        seeker.next(); // Skip the header row
        List<Cell> cells = seeker.next().getCells();
        seeker.close();

        assertEquals("1", cells.get(0).getValue());
        assertEquals("Book", cells.get(1).getValue());
        assertEquals("5", cells.get(2).getValue());
        assertEquals("5.25", cells.get(3).getValue());
    }

    @Test
    public void readAndCountCsvDocument() throws FileNotFoundException {
        DocumentReader reader = new CsvDocumentReader();
        RowSeeker seeker = reader.read(new FileInputStream(CSV_FILE_PATH));
        assertEquals(2, seeker.count());
    }
}