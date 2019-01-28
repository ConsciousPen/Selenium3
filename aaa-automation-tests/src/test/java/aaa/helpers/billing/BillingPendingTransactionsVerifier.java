/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.TableVerifier;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsTable;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingPendingTransactionsVerifier extends TableVerifier {

	public BillingPendingTransactionsVerifier() {}

	public BillingPendingTransactionsVerifier(ETCSCoreSoftAssertions softly) {
        this.softly = softly;
	}

	@Override
    protected Table getTable() {
        return BillingSummaryPage.tablePendingTransactions;
    }

    @Override
    protected String getTableName() {
        return "Pending Transactions";
    }

    public BillingPendingTransactionsVerifier setTransactionDate(LocalDateTime value) {
        setValue(BillingPendingTransactionsTable.TRANSACTION_DATE, value.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public BillingPendingTransactionsVerifier setEffectiveDate(LocalDateTime value) {
        setValue(BillingPendingTransactionsTable.EFFECTIVE_DATE, value.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public BillingPendingTransactionsVerifier setType(String value) {
        setValue(BillingPendingTransactionsTable.TYPE, value);
        return this;
    }

    public BillingPendingTransactionsVerifier setSubtypeReason(String value) {
        setValue(BillingPendingTransactionsTable.SUBTYPE, value);
        return this;
    }

    public BillingPendingTransactionsVerifier setReason(String value) {
        setValue(BillingPendingTransactionsTable.REASON, value);
        return this;
    }

    public BillingPendingTransactionsVerifier setAmount(Dollar value) {
        setValue(BillingPendingTransactionsTable.AMOUNT, value.toString());
        return this;
    }

    public BillingPendingTransactionsVerifier setStatus(String value) {
        setValue(BillingPendingTransactionsTable.STATUS, value);
        return this;
    }
}
