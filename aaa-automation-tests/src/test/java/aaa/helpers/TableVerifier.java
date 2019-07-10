/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import com.exigen.ipb.eisa.utils.Dollar;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.table.Table;

public abstract class TableVerifier {

    protected Map<String, String> values = new HashMap<>();
    protected ETCSCoreSoftAssertions softly;

    public TableVerifier() {}

    public TableVerifier(ETCSCoreSoftAssertions softly) {
        this.softly = softly;
    }

    protected abstract Table getTable();

    protected abstract String getTableName();

    public TableVerifier verifyPresent() {
        return verifyPresent(true);
    }

    public TableVerifier verifyPresent(boolean expectedValue) {
        String message;
        if (expectedValue) {
            message = String.format("Row with values %s in table '%s' is present.", values, getTableName());
        } else {
            message = String.format("Row with values %s in table '%s' is absent.", values, getTableName());
        }
        if (softly != null) {
            softly.assertThat(getTable().getRow(values).isPresent()).as(message).isEqualTo(expectedValue);
        } else {
            assertThat(getTable().getRow(values).isPresent()).as(message).isEqualTo(expectedValue);
        }
        return this;
    }

    public TableVerifier verifyCount(int count) {
        if (softly != null) {
            softly.assertThat(getTable()).hasMatchingRows(count, values);
        } else {
            assertThat(getTable()).hasMatchingRows(count, values);
        }
        return this;
    }

    public TableVerifier verify(int rowNumber) {
        if (softly != null) {
            values.forEach((key, value) -> softly.assertThat(getTable().getRow(rowNumber))
                    .as("Table '%s', Row '%s', Column '%s'", getTableName(), rowNumber, key).hasCellWithValue(key, value));
        } else {
            values.forEach((key, value) -> assertThat(getTable().getRow(rowNumber))
                    .as("Table '%s', Row '%s', Column '%s'", getTableName(), rowNumber, key).hasCellWithValue(key, value));
        }
        return this;
    }

    public TableVerifier verifyDollar(int rowNumber, String column, Dollar value, double precision) {
        Dollar actualDollar = new Dollar(getTable().getRow(rowNumber).getCell(column).getValue());
        actualDollar.verify.isEqual(value, precision);
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
