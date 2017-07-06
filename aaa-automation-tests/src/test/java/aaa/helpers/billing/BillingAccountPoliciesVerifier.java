/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingAccountPoliciesVerifier extends TableVerifier {

    public BillingAccountPoliciesVerifier setPolicyStatus(String valueProductStatus) {
        setValue("Policy Status", valueProductStatus);
        return this;
    }

    public BillingAccountPoliciesVerifier setPolicyFlag(String valueProductFlag) {
        setValue("Policy Flag", valueProductFlag);
        return this;
    }

    public BillingAccountPoliciesVerifier setMinDue(Dollar value) {
        setValue("Min. Due", value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setMinDueZero() {
        setValue("Min. Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setPastDue(Dollar value) {
        setValue("Past Due", value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setPastDueZero() {
        setValue("Past Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setTotalDue(Dollar value) {
        setValue("Total Due", value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setTotalDueZero() {
        setValue("Total Due", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setTotalPaid(Dollar value) {
        setValue("Total Paid", value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setTotalPaidZero() {
        setValue("Total Paid", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setPrepaid(Dollar value) {
        setValue("Prepaid", value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setPrepaidZero() {
        setValue("Prepaid", BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setBillableAmount(Dollar value) {
        setValue("Billable Amount", value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setBillableAmountZero() {
        setValue("Billable Amount", BillingHelper.DZERO.toString());
        return this;
    }

    public void verifyZeros() {
        verifyZeros(1);
    }

    public void verifyZeros(int rowNumber) {
        setMinDue(BillingHelper.DZERO).setPastDue(BillingHelper.DZERO).setTotalDue(BillingHelper.DZERO)
                .setTotalDue(BillingHelper.DZERO).setTotalPaid(BillingHelper.DZERO).setPrepaid(BillingHelper.DZERO)
                .setBillableAmount(BillingHelper.DZERO).verify(rowNumber);
    }

    @Override
    protected Table getTable() {
        return BillingSummaryPage.tableBillingAccountPolicies;
    }

    @Override
    protected String getTableName() {
        return "Billing Account Policies";
    }
}
