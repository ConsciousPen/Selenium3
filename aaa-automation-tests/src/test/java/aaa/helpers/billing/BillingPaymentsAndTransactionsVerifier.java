/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TableVerifier;
import aaa.main.enums.BillingConstants.*;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.table.Table;

import java.time.LocalDateTime;

public class BillingPaymentsAndTransactionsVerifier extends TableVerifier {

    @Override
    protected Table getTable() {
        return BillingSummaryPage.tablePaymentsOtherTransactions;
    }

    @Override
    protected String getTableName() {
        return "Payments & Other Transactions";
    }

    public BillingPaymentsAndTransactionsVerifier setTransactionDate(LocalDateTime value) {
        setValue(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, value.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setEffectiveDate(LocalDateTime value) {
        setValue(BillingPaymentsAndOtherTransactionsTable.EFF_DATE, value.format(DateTimeUtils.MM_DD_YYYY));
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setType(String value) {
        setValue(BillingPaymentsAndOtherTransactionsTable.TYPE, value);
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setSubtypeReason(String value) {
        setValue(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, value);
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setAmount(Dollar value) {
        setValue(BillingPaymentsAndOtherTransactionsTable.AMOUNT, value.toString());
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setStatus(String value) {
        setValue(BillingPaymentsAndOtherTransactionsTable.STATUS, value);
        return this;
    }

    public BillingPaymentsAndTransactionsVerifier setPolicy(String value) {
        setValue(BillingPaymentsAndOtherTransactionsTable.POLICY, value);
        return this;
    }

    // ----- Verify methods -----

    public void verifyAutoPaymentGenerated(LocalDateTime transactionDate, Dollar value) {
        setTransactionDate(transactionDate);
        setAmount(value);
        setType(PaymentsAndOtherTransactionType.PAYMENT);
        setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT);
        verifyPresent();
    }
}
