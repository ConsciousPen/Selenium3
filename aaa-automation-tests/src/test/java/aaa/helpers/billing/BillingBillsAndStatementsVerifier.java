/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import aaa.helpers.TableVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.pages.summary.BillingSummaryPage;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

import java.time.LocalDateTime;
import java.util.Map;

public class BillingBillsAndStatementsVerifier extends TableVerifier {

	@Override
	protected Table getTable() {
		return BillingSummaryPage.tableBillsStatements;
	}

	@Override
	protected String getTableName() {
		return "Bills And Statements";
	}

	public BillingBillsAndStatementsVerifier setDueDate(LocalDateTime value) {
		setValue(BillingBillsAndStatmentsTable.DUE_DATE, value.format(DateTimeUtils.MM_DD_YYYY));
		return this;
	}

	public BillingBillsAndStatementsVerifier setType(String value) {
		setValue(BillingBillsAndStatmentsTable.TYPE, value);
		return this;
	}

	public BillingBillsAndStatementsVerifier setMinDue(Dollar value) {
		setValue(BillingBillsAndStatmentsTable.MINIMUM_DUE, value.toString());
		return this;
	}

	public BillingBillsAndStatementsVerifier setMinDueZero() {
		setValue(BillingBillsAndStatmentsTable.MINIMUM_DUE, BillingHelper.DZERO.toString());
		return this;
	}

	public BillingBillsAndStatementsVerifier setPastDue(Dollar value) {
		setValue(BillingBillsAndStatmentsTable.PAST_DUE, value.toString());
		return this;
	}

	public BillingBillsAndStatementsVerifier setPastDueZero() {
		setValue(BillingBillsAndStatmentsTable.PAST_DUE, BillingHelper.DZERO.toString());
		return this;
	}

	public BillingBillsAndStatementsVerifier setTotalDue(Dollar value) {
		setValue(BillingBillsAndStatmentsTable.TOTAL_DUE, value.toString());
		return this;
	}

	// ----- Verify methods -----

	public void verifyRowWithDueDate(LocalDateTime date) {
		Row row = getTable().getRow(BillingBillsAndStatmentsTable.DUE_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
		for (Map.Entry<String, String> entry : values.entrySet()) {
			String message = String.format("Table '%s', Due Date '%s', Column '%s'", getTableName(), date.format(DateTimeUtils.MM_DD_YYYY), entry.getKey());
			row.getCell(entry.getKey()).verify.value(message, entry.getValue());
		}
	}

	public void verifyBillGenerated(LocalDateTime installmentDate, LocalDateTime feesTransacionDate) {
		verifyBillGenerated(installmentDate, feesTransacionDate, null, BillingHelper.DZERO);
	}

	public void verifyBillGenerated(LocalDateTime installmentDate, LocalDateTime feesTransactionDate, LocalDateTime effectiveDate, Dollar pligaOrMvleFee) {
		Dollar pastDue = effectiveDate == null ? BillingHelper.DZERO : new Dollar(BillingHelper.getPolicyRowByEffDate(effectiveDate).getCell(BillingConstants.BillingAccountPoliciesTable.PAST_DUE).getValue());
		Dollar billAmount = BillingHelper.getInstallmentDueByDueDate(installmentDate).add(BillingHelper.getFeesValue(feesTransactionDate)).add(pastDue).add(pligaOrMvleFee);
		setType(BillingConstants.BillsAndStatementsType.BILL);
		setMinDue(billAmount).setPastDue(pastDue);
		verifyRowWithDueDate(installmentDate);
	}

}
