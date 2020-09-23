/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.reader.csv;

import com.andrea.service.importer.reader.*;
import com.andrea.service.importer.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvDocumentReader implements DocumentReader {

    @Override
    public RowSeeker read(InputStream is) {
        return new CsvRowSeeker(is);
    }

    private static class CsvRowSeeker implements RowSeeker {

        private int rows = 0;
        private int linesCount = -1;

        private Cell[] cellHeaders;
        private CSVReader csvReader;

        private InputStream is;
        private InputStreamReader reader;

        CsvRowSeeker(InputStream is) {
            this.is = is;
            reader = new InputStreamReader(is);
            csvReader = new CSVReader(reader);
        }

        @Override
        public Row next() {
            try {
                String[] values = csvReader.readNext();
                if (values != null) {
                    Cell[] cells = new Cell[values.length];
                    linesCount++;
                    if (linesCount == 0) {
                        for (int i = 0, j = values.length; i < j; i++) {
                            String s = values[i].trim();
                            cells[i] = new DefaultCell(i, s, s);
                        }
                        cellHeaders = cells;
                    } else {
                        for (int i = 0, j = values.length; i < j; i++) {
                            String s = values[i].trim();
                            cells[i] = new DefaultCell(i, cellHeaders[i].getValue(), s);
                        }
                    }
                    return new Row(linesCount, new ArrayList<>(Arrays.asList(cells)));
                }
            } catch (Exception e) {
                throw new DocumentReaderException("An error occurred while trying to read the csv file", e);
            }
            return null;
        }

        @Override
        public int count() {
            if (rows == 0) {
                try {
                    rows = FileUtils.countLines(is);
                } catch (IOException e) {
                    rows = -1;
                }
            }
            return rows;
        }

        @Override
        public void close() {
            try {
                reader.close();
            } catch (IOException e) {
                // Ignore
            }
            try {
                csvReader.close();
            } catch (IOException ignore) {
                // Ignore
            }
        }
    }
}
