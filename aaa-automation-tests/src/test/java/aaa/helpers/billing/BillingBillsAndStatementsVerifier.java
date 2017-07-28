/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import java.time.LocalDateTime;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingBillsAndStatementsVerifier extends TableVerifier {

    public BillingBillsAndStatementsVerifier setDueDate(LocalDateTime value) {
        setValue("Due Date", value.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public BillingBillsAndStatementsVerifier setType(String value) {
        setValue("Type", value);
        return this;
    }

    public BillingBillsAndStatementsVerifier setMinDue(Dollar value) {
        setValue("Minimum Due", value.toString());
        return this;
    }
    
    public BillingBillsAndStatementsVerifier setMinDueZero() {
        setValue("Minimum Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingBillsAndStatementsVerifier setPastDue(Dollar value) {
        setValue("Past Due", value.toString());
        return this;
    }

    public BillingBillsAndStatementsVerifier setPastDueZero() {
        setValue("Past Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingBillsAndStatementsVerifier setTotalDue(Dollar value) {
        setValue("Total Due", value.toString());
        return this;
    }

    @Override
    protected Table getTable() {
        return BillingSummaryPage.tableBillsStatements;
    }

    @Override
    protected String getTableName() {
        return "Bills And Statements";
    }
}
