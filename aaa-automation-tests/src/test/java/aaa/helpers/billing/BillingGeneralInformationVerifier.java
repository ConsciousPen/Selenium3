/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingGeneralInformationVerifier extends TableVerifier {

    public BillingGeneralInformationVerifier setMinDue(Dollar value) {
        setValue("Minimum Due", value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setMinDueZero() {
        setValue("Minimum Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setPastDue(Dollar value) {
        setValue("Past Due", value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setPastDueZero() {
        setValue("Past Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setTotalDue(Dollar value) {
        setValue("Total Due", value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setTotalDueZero() {
        setValue("Total Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setTotalPaid(Dollar value) {
        setValue("Total Paid", value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setTotalPaidZero() {
        setValue("Total Paid", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setBillableAmount(Dollar value) {
        setValue("Billable Amount", value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setBillableAmountZero() {
        setValue("Billable Amount", BillingHelper.DZERO.toString());
        return this;
    }

    @Override
    protected Table getTable() {
        return BillingSummaryPage.tableBillingGeneralInformation;
    }

    @Override
    protected String getTableName() {
        return "Billing General Information";
    }
}
