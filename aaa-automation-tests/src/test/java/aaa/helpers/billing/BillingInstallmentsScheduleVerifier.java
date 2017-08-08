/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable;
import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

import java.time.LocalDateTime;
import java.util.Map;

public class BillingInstallmentsScheduleVerifier extends TableVerifier {

    @Override
    protected Table getTable() {
        return BillingSummaryPage.tableInstallmentSchedule;
    }

    @Override
    protected String getTableName() {
        return "Installment Schedule";
    }

    public BillingInstallmentsScheduleVerifier setBilledStatus(String value) {
        setValue(BillingInstallmentScheduleTable.BILLED_STATUS, value);
        return this;
    }

    public BillingInstallmentsScheduleVerifier setDescription(String value) {
        setValue(BillingInstallmentScheduleTable.DESCRIPTION, value);
        return this;
    }

    public BillingInstallmentsScheduleVerifier setInstallmentDueDate(LocalDateTime value) {
        setValue(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE, value.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public BillingInstallmentsScheduleVerifier setBilledAmount(Dollar value) {
        setValue(BillingInstallmentScheduleTable.BILLED_AMOUNT, value.toString());
        return this;
    }

    public BillingInstallmentsScheduleVerifier setBilledAmountZero() {
        setValue(BillingInstallmentScheduleTable.BILLED_AMOUNT, "");
        return this;
    }

    public BillingInstallmentsScheduleVerifier setScheduleDueAmount(Dollar value) {
        setValue(BillingInstallmentScheduleTable.SCHEDULE_DUE_AMOUNT, value.toString());
        return this;
    }

    public BillingInstallmentsScheduleVerifier setScheduleDueAmountZero() {
        setValue(BillingInstallmentScheduleTable.SCHEDULE_DUE_AMOUNT, BillingHelper.DZERO.toString());
        return this;
    }

    public void verifyRowWithInstallmentDate(LocalDateTime date) {
        Row row = getTable().getRow(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String message = String.format("Table '%s', Installment Due Date '%s', Column '%s'", getTableName(), date.format(DateTimeUtils.MM_DD_YYYY), entry.getKey());
            row.getCell(entry.getKey()).verify.value(message, entry.getValue());
        }
    }
}
