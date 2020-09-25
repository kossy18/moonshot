/*
 * Copyright (c) 2020. Inyiama Kossy
 */

package com.github.kossy18.service.importer.reader;

import java.util.List;

public class Row {

    private final int index;

    private final List<Cell> cells;

    public Row(int index, List<Cell> cells) {
        this.index = index;
        this.cells = cells;
    }

    public int getIndex() {
        return index;
    }

    public List<Cell> getCells() {
        return cells;
    }
}
