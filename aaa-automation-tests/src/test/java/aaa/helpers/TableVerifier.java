/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.exigen.ipb.etcsa.utils.Dollar;

import toolkit.webdriver.controls.composite.table.Table;

public abstract class TableVerifier {

    protected Map<String, String> values = new HashMap<>();

    protected abstract Table getTable();

    protected abstract String getTableName();

    public TableVerifier verifyPresent() {
        return verifyPresent(true);
    }

    public TableVerifier verifyPresent(boolean expectedValue) {
        getTable().getRow(values).verify.present(expectedValue);
        return this;
    }

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
}
