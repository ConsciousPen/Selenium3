/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingInstallmentsScheduleVerifier extends TableVerifier {

    public BillingInstallmentsScheduleVerifier setBilledStatus(String value) {
        setValue("Billed Status", value);
        return this;
    }

    public BillingInstallmentsScheduleVerifier setBilledAmount(Dollar value) {
        setValue("Billed Amount", value.toString());
        return this;
    }

    public BillingInstallmentsScheduleVerifier setBilledAmountZero() {
        setValue("Billed Amount", "");
        return this;
    }

    public BillingInstallmentsScheduleVerifier setScheduleDueAmount(Dollar value) {
        setValue("Schedule Due Amount", value.toString());
        return this;
    }

    public BillingInstallmentsScheduleVerifier setScheduleDueAmountZero() {
        setValue("Schedule Due Amount", BillingHelper.DZERO.toString());
        return this;
    }

    @Override
    protected Table getTable() {
        return BillingSummaryPage.tableInstallmentSchedule;
    }

    @Override
    protected String getTableName() {
        return "Installment Schedule";
    }
}
