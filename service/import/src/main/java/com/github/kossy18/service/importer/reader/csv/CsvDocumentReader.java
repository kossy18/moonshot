/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.reader.csv;

import com.github.kossy18.service.importer.util.FileUtils;
import com.github.kossy18.service.importer.reader.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvDocumentReader implements DocumentReader {

    @Override
    public RowSeeker read(InputStream is) {
        return new CsvRowSeeker(is);
    }

    private static class CsvRowSeeker implements RowSeeker {
        private int rows = 0;
        private int linesCount = -1;

        private List<Cell> cellHeaders;
        private CSVReader csvReader;

        private InputStream is;
        private InputStreamReader reader;

        private CsvRowSeeker(InputStream is) {
            this.is = is;
            reader = new InputStreamReader(is);
            csvReader = new CSVReader(reader);
        }

        @Override
        public Row next() {
            try {
                String[] values = csvReader.readNext();
                if (values != null) {
                    linesCount++;
                    List<Cell> cells = new ArrayList<>(values.length);
                    for (int i = 0, j = values.length; i < j; i++) {
                        String s = values[i].trim();
                        if (linesCount == 0) {
                            cells.add(new DefaultCell(i, s, s));
                        } else {
                            cells.add(new DefaultCell(i, cellHeaders.get(i).getValue(), s));
                        }
                    }
                    if (linesCount == 0) {
                        cellHeaders = cells;
                    }
                    return new Row(linesCount, cells);
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
                is.close();
            } catch (IOException e) {
                // Ignore
            }
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
