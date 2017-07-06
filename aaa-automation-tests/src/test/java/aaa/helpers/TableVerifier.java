/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.exigen.ipb.etcsa.utils.Dollar;

import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

public abstract class TableVerifier {

    protected Map<String, String> values = new HashMap<>();

    public final VerifyRows rows = new VerifyRows();

    protected abstract Table getTable();

    protected abstract String getTableName();

    public TableVerifier verify(int rowNumber) {
        for (Entry<String, String> entry : values.entrySet()) {
            getTable().getRow(rowNumber).getCell(entry.getKey()).verify
                    .value(String.format("Table '%s', Row '%s', Column '%s'", getTableName(), rowNumber, entry.getKey()), entry.getValue());
        }
        return this;
    }

    public TableVerifier verifyDollar(int rowNumber, String column, Dollar value, double precision) {
        Dollar actualDollar = new Dollar(getTable().getRow(rowNumber).getCell(column).getValue());
        actualDollar.verify.equals(value, precision);
        return this;
    }

    public TableVerifier verify(int rowNumberStart, int rowNumberEnd) {
        for (int i = rowNumberStart; i <= rowNumberEnd; i++) {
            verify(i);
        }
        return this;
    }

    public TableVerifier setValue(String columnName, String expectedValue) {
        values.put(columnName, expectedValue);
        return this;
    }

    public class VerifyRows {
        protected List<String> excludeColumn = new ArrayList<>();

        public VerifyRows excludeColumn(String columnName) {
            excludeColumn.add(columnName);
            return this;
        }

        public void verify(Row actualRow, Row expectedRow) {
            for (int i = 1; i <= getTable().getColumnsCount(); i++) {
                if (!excludeColumn.contains(getTable().getHeader().getCell(i).getValue())) {
                    actualRow.getCell(i).verify.value(String.format("Table '%s', Column '%s'", getTableName(), getTable().getHeader().getCell(i).getValue()),
                            expectedRow.getCell(i).getValue());
                }
            }
        }
    }
}
