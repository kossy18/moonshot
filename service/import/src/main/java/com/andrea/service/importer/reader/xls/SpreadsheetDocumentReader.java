/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.andrea.service.importer.reader.xls;

import com.andrea.service.importer.reader.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpreadsheetDocumentReader implements DocumentReader {

    @Override
    public RowSeeker read(InputStream in) {
        return new SpreadsheetRowSeeker(in);
    }

    private static class SpreadsheetRowSeeker implements RowSeeker {
        private List<Cell> cellHeaders;
        private InputStream is;

        private XSSFSheet sheet;
        private XSSFWorkbook workbook;
        private Iterator<org.apache.poi.ss.usermodel.Row> rowIterator;

        private SpreadsheetRowSeeker(InputStream is) {
            this.is = is;
            try {
                workbook = new XSSFWorkbook(is);
                sheet = workbook.getSheetAt(0);
                rowIterator = sheet.iterator();
            } catch (IOException e) {
                throw new DocumentReaderException("An error occurred while trying to read the xls file", e);
            }
        }

        @Override
        public Row next() {
            if (rowIterator.hasNext()) {
                List<Cell> cells = new ArrayList<>();

                org.apache.poi.ss.usermodel.Row row = rowIterator.next();
                for (org.apache.poi.ss.usermodel.Cell cell : row) {
                    if (row.getRowNum() == 0) {
                        cells.add(new DefaultCell(cell.getColumnIndex(), cell.toString(), cell.toString()));
                    } else {
                        cells.add(new DefaultCell(cell.getColumnIndex(), cellHeaders.get(cell.getColumnIndex()).getValue(), cell.toString()));
                    }
                }
                if (row.getRowNum() == 0) {
                    cellHeaders = cells;
                }
                return new Row(row.getRowNum(), cells);
            }
            return null;
        }

        @Override
        public int count() {
            return sheet.getPhysicalNumberOfRows();
        }

        @Override
        public void close() {
            try {
                workbook.close();
            } catch (IOException ignored) {
                // Ignore
            }
            try {
                is.close();
            } catch (IOException ignored) {
                // Ignore
            }
        }
    }
}
