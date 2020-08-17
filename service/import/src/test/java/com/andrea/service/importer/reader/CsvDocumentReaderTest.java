/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.reader;

import com.andrea.service.importer.reader.csv.CsvDocumentReader;
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

        assertEquals(cells.get(0).getValue(), "1");
        assertEquals(cells.get(1).getValue(), "Book");
        assertEquals(cells.get(2).getValue(), "5");
        assertEquals(cells.get(3).getValue(), "5.25");
    }

    @Test
    public void readAndCountCsvDocument() throws FileNotFoundException {
        DocumentReader reader = new CsvDocumentReader();
        RowSeeker seeker = reader.read(new FileInputStream(CSV_FILE_PATH));
        assertEquals(seeker.count(), 3);
    }
}