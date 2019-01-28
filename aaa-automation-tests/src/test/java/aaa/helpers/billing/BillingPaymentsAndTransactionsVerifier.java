/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.TableVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.composite.table.Table;

public class BillingPaymentsAndTransactionsVerifier extends TableVerifier {

	public BillingPaymentsAndTransactionsVerifier() {}

	public BillingPaymentsAndTransactionsVerifier(ETCSCoreSoftAssertions softly) {
		this.softly = softly;
	}

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

	public BillingPaymentsAndTransactionsVerifier setReason(String value) {
		setValue(BillingPaymentsAndOtherTransactionsTable.REASON, value);
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

	public void verifyManualPaymentAccepted(LocalDateTime transactionDate, Dollar value) {
		setTransactionDate(transactionDate);
		setAmount(value);
		setType(PaymentsAndOtherTransactionType.PAYMENT);
		setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT);
		verifyPresent();
	}

	public void verifyPaymentDeclined(LocalDateTime transactionDate) {
		setTransactionDate(transactionDate);
		setType(PaymentsAndOtherTransactionType.PAYMENT);
		setStatus(PaymentsAndOtherTransactionStatus.DECLINED);
		verifyPresent();
	}

	public void verifyPligaFee(LocalDateTime transactionDate) {
		verifyPligaFee(transactionDate, BillingHelper.calculatePligaFee(transactionDate));
	}

	public void verifyPligaFee(LocalDateTime transactionDate, Dollar expectedPligaFee) {
		verifyFee(PaymentsAndOtherTransactionSubtypeReason.PLIGA_FEE, transactionDate, expectedPligaFee);
	}

	public void verifyMVLEFee(LocalDateTime transactionDate, Dollar expectedMVLEFee) {
		verifyFee(PaymentsAndOtherTransactionSubtypeReason.MVLE_FEE, transactionDate, expectedMVLEFee);
	}

	public void verifyFee(String subtypeReason, LocalDateTime transactionDate, Dollar expectedAmount) {
		setTransactionDate(transactionDate);
		setType(BillingConstants.PaymentsAndOtherTransactionType.FEE);
		setSubtypeReason(subtypeReason);
		setAmount(expectedAmount);
		verifyPresent();
	}
	
}
