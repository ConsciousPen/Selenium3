/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.TableVerifier;
import aaa.main.enums.BillingConstants.BillingAccountPoliciesTable;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingAccountPoliciesVerifier extends TableVerifier {

	public BillingAccountPoliciesVerifier() {}

	public BillingAccountPoliciesVerifier(ETCSCoreSoftAssertions softly) {
        this.softly = softly;
	}

	@Override
    protected Table getTable() {
        return BillingSummaryPage.tableBillingAccountPolicies;
    }

    @Override
    protected String getTableName() {
        return "Billing Account Policies";
    }

    public BillingAccountPoliciesVerifier setPolicyStatus(String valueProductStatus) {
        setValue(BillingAccountPoliciesTable.POLICY_STATUS, valueProductStatus);
        return this;
    }

    public BillingAccountPoliciesVerifier setPaymentPlan(String value) {
        setValue(BillingAccountPoliciesTable.PAYMENT_PLAN, value);
        return this;
    }

    public BillingAccountPoliciesVerifier setPolicyFlag(String valueProductFlag) {
        setValue(BillingAccountPoliciesTable.POLICY_FLAG, valueProductFlag);
        return this;
    }

    public BillingAccountPoliciesVerifier setBillingStatus(String billingStatus) {
        setValue(BillingAccountPoliciesTable.BILLING_STATUS, billingStatus);
        return this;
    }

    public BillingAccountPoliciesVerifier setMinDue(Dollar value) {
        setValue(BillingAccountPoliciesTable.MIN_DUE, value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setMinDueZero() {
        setValue(BillingAccountPoliciesTable.MIN_DUE, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setPastDue(Dollar value) {
        setValue(BillingAccountPoliciesTable.PAST_DUE, value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setPastDueZero() {
        setValue(BillingAccountPoliciesTable.PAST_DUE, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setTotalDue(Dollar value) {
        setValue(BillingAccountPoliciesTable.TOTAL_DUE, value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setTotalDueZero() {
        setValue(BillingAccountPoliciesTable.TOTAL_DUE, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setTotalPaid(Dollar value) {
        setValue(BillingAccountPoliciesTable.TOTAL_PAID, value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setTotalPaidZero() {
        setValue(BillingAccountPoliciesTable.TOTAL_PAID, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setPrepaid(Dollar value) {
        setValue(BillingAccountPoliciesTable.PREPAID, value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setPrepaidZero() {
        setValue(BillingAccountPoliciesTable.PREPAID, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setBillableAmount(Dollar value) {
        setValue(BillingAccountPoliciesTable.BILLABLE_AMOUNT, value.toString());
        return this;
    }

    public BillingAccountPoliciesVerifier setBillableAmountZero() {
        setValue(BillingAccountPoliciesTable.BILLABLE_AMOUNT, BillingHelper.DZERO.toString());
        return this;
    }

    public void verifyRowWithEffectiveDate(LocalDateTime date) {
        Row row = getTable().getRow(BillingAccountPoliciesTable.EFF_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
        if (softly != null) {
            values.forEach((key, value) -> softly.assertThat(row)
                    .as("Table '%s', Effective Date '%s', Column '%s'", getTableName(), date.format(DateTimeUtils.MM_DD_YYYY), key).hasCellWithValue(key, value));
        } else {
            values.forEach((key, value) -> assertThat(row)
                    .as("Table '%s', Effective Date '%s', Column '%s'", getTableName(), date.format(DateTimeUtils.MM_DD_YYYY), key).hasCellWithValue(key, value));
        }
    }
}
