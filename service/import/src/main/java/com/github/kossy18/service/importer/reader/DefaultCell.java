/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.reader;

/**
 * A simple implementation of the <tt>Cell</tt> interface
 *
 * @see Cell
 */
public final class DefaultCell implements Cell {

    private final int columnIndex;
    private final String columnName;
    private final String value;

    /**
     * Used to create a new <tt>Cell</tt>
     *
     * @param columnIndex the column index of the cell
     * @param columnName the column name of the cell
     * @param value the <tt>String</tt> value of the cell
     */
    public DefaultCell(int columnIndex, String columnName, String value) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
        this.value = value;
    }

    @Override
    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{" +
                "columnIndex=" + columnIndex +
                ", columnName='" + columnName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
