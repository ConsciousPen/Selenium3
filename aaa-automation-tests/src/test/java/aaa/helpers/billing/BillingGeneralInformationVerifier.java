/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.TableVerifier;
import aaa.main.enums.BillingConstants.BillingGeneralInformationTable;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingGeneralInformationVerifier extends TableVerifier {

	public BillingGeneralInformationVerifier() {}

	public BillingGeneralInformationVerifier(ETCSCoreSoftAssertions softly) {
        this.softly = softly;
	}

	@Override
    protected Table getTable() {
        return BillingSummaryPage.tableBillingGeneralInformation;
    }

    @Override
    protected String getTableName() {
        return "Billing General Information";
    }

    public BillingGeneralInformationVerifier setMinDue(Dollar value) {
        setValue(BillingGeneralInformationTable.MINIMUM_DUE, value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setMinDueZero() {
        setValue(BillingGeneralInformationTable.MINIMUM_DUE, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setPastDue(Dollar value) {
        setValue(BillingGeneralInformationTable.PAST_DUE, value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setPastDueZero() {
        setValue(BillingGeneralInformationTable.PAST_DUE, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setTotalDue(Dollar value) {
        setValue(BillingGeneralInformationTable.TOTAL_DUE, value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setTotalDueZero() {
        setValue(BillingGeneralInformationTable.TOTAL_DUE, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setTotalPaid(Dollar value) {
        setValue(BillingGeneralInformationTable.TOTAL_PAID, value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setTotalPaidZero() {
        setValue(BillingGeneralInformationTable.TOTAL_PAID, BillingHelper.DZERO.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setBillableAmount(Dollar value) {
        setValue(BillingGeneralInformationTable.BILLABLE_AMOUNT, value.toString());
        return this;
    }

    public BillingGeneralInformationVerifier setBillableAmountZero() {
        setValue(BillingGeneralInformationTable.BILLABLE_AMOUNT, BillingHelper.DZERO.toString());
        return this;
    }
}
