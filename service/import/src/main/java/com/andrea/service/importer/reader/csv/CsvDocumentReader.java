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
import java.util.List;

public class CsvDocumentReader implements DocumentReader {

    @Override
    public RowSeeker read(InputStream is) {
        return new CsvRowSeeker(is);
    }

    private static class CsvRowSeeker implements RowSeeker {

        private int linesCount = -1;

        private int rows = 0;

        private Cell[] cellHeaders;

        private InputStream is;

        private InputStreamReader reader;

        CsvRowSeeker(InputStream is) {
            this.is = is;
            reader = new InputStreamReader(is);
        }

        @Override
        public Row next() {
            try {
                List<String> values = CsvHelper.parseLine(reader);
                if (!values.isEmpty()) {
                    Cell[] cells = new Cell[values.size()];
                    linesCount++;
                    if (linesCount == 0) {
                        for (int i = 0, j = values.size(); i < j; i++) {
                            String s = values.get(i).trim();
                            cells[i] = new DefaultCell(i, s, s);
                        }
                        cellHeaders = cells;
                    } else {
                        for (int i = 0, j = values.size(); i < j; i++) {
                            String s = values.get(i).trim();
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
        }
    }
}
