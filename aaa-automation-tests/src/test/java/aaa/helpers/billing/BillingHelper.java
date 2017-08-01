/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.table.Row;

public final class BillingHelper {

	//TODO remove after "base" tests adaptation
	public static final String ZERO = new Dollar(0).toString();
	public static final Dollar DZERO = new Dollar(0);
	public static final Dollar INSTALLMENT_FEE = new Dollar(5);
	public static final Dollar INSTALLMENT_FEE_X2 = INSTALLMENT_FEE.multiply(2);
	public static final Dollar INSTALLMENT_FEE_X3 = INSTALLMENT_FEE.multiply(3);
	public static final Dollar INSTALLMENT_FEE2 = new Dollar(10);
	public static final Dollar PAYMENT_DECLINE_FEE = new Dollar(7);
	public static final Dollar CANCEL_NOTICE_FEE = new Dollar(10);
	public static final Dollar CANCELLATION_FEE = new Dollar(15);
	public static final Dollar REINSTATEMENT_FEE_WITHOUT_LAPSE = new Dollar(20);
	public static final Dollar REINSTATEMENT_FEE_WITH_LAPSE = new Dollar(25);
	public static final Dollar OFFER_WITHOUT_LAPSE = new Dollar(30);
	public static final Dollar OFFER_WITH_LAPSE = new Dollar(35);
	public static final int DAYS_CANCEL_NOTICE = 5;
	public static final int DAYS_CANCELLATION = 18;
	public static final int DAYS_REINSTATEMENT_WITHOUT_LAPSE = 2;
	public static final int DAYS_REINSTATEMENT_WITH_LAPSE = 7;
	public static final int DAYS_WRITE_OFF = 6;
	public static final int DAYS_RENEW_STRATEGY = 60;
	public static final int DAYS_ENDORSEMENT_STRATEGY = 305;
	public static final int DAYS_RENEW_WITHOUT_LAPSE = 10;
	public static final int DAYS_RENEW_WITH_LAPSE = 25;
	public static final int DAYS_OFF_CYCLE = 6;

	// ------- Installments table-------
	
	public static List<LocalDateTime> getInstallmentDueDates() {
		ArrayList<LocalDateTime> dates = new ArrayList<LocalDateTime>();
		for (String value : BillingSummaryPage.tableInstallmentSchedule.getColumn(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE).getValue()) {
			dates.add(LocalDateTime.parse(value, DateTimeUtils.MM_DD_YYYY));
		}
		return dates;
	}
	
	public static List<Dollar> getInstallmentDues() {
		ArrayList<Dollar> dues = new ArrayList<Dollar>();
		for (String value : BillingSummaryPage.tableInstallmentSchedule.getColumn(BillingInstallmentScheduleTable.INSTALLMENT_DUE).getValue()) {
			dues.add(new Dollar(value));
		}
		return dues;
	}
	
	public static Dollar getInstallmentDueByDueDate(LocalDateTime date) {
		String value = BillingSummaryPage.tableInstallmentSchedule.getRow(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE,
				date.format(DateTimeUtils.MM_DD_YYYY)).getCell(BillingInstallmentScheduleTable.INSTALLMENT_DUE).getValue();
		return new Dollar(value);
	}
	
	// ------- Bills and Statements table-------
	
	public static Row getBillRowByDate(LocalDateTime date) {
		return BillingSummaryPage.tableBillsStatements.getRow(BillingBillsAndStatmentsTable.DUE_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
	}
	
	public static String getBillCellValue(LocalDateTime date, String columnName) {
		return getBillRowByDate(date).getCell(columnName).getValue();
	}
	
	public static void verifyBillGenerated(LocalDateTime installmentDate, LocalDateTime feesTransacionDate) {
		Dollar billAmount = getInstallmentDueByDueDate(installmentDate);
		billAmount = billAmount.add(getFeesValue(feesTransacionDate));
		Row row = getBillRowByDate(installmentDate);
		row.getCell(BillingBillsAndStatmentsTable.DUE_DATE).verify.value(installmentDate.format(DateTimeUtils.MM_DD_YYYY));
		row.getCell(BillingBillsAndStatmentsTable.TYPE).verify.value("Bill");
		row.getCell(BillingBillsAndStatmentsTable.MINIMUM_DUE).verify.value(billAmount.toString());
		row.getCell(BillingBillsAndStatmentsTable.PAST_DUE).verify.value(new Dollar(0).toString());
	}
	
	// ------- Payments & Other Transactions table -------
	
	/**
	 * Search for all Fee transactions in specified date
	 */
	public static Dollar getFeesValue(LocalDateTime date) {
		Dollar amount = new Dollar(0);
		HashMap<String, String> values = new HashMap<String, String>();
		values.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.FEE);
		values.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
		List<Row> feeRows = BillingSummaryPage.tablePaymentsOtherTransactions.getRows(values);
		
		for (Row row : feeRows) {
			amount.add(new Dollar(row.getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT)));
		}
		return amount;
	}
	
	public static void verifyFeeTransactionGenerated(LocalDateTime installmentDate) {
		HashMap<String, String> values = new HashMap<String, String>();
		values.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, installmentDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingPaymentsAndOtherTransactionsTable.TYPE, "Fee");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(values).verify.present();
	}
}
