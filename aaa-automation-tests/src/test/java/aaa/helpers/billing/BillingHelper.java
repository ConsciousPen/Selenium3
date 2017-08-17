/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aaa.common.enums.Constants;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;

import aaa.main.enums.BillingConstants.*;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
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


	// ------- Billing Account Policies table -------

	public static Row getPolicyRowByEffDate(LocalDateTime date) {
		return BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingAccountPoliciesTable.EFF_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
	}

	public static String getPaymentPlan(int index) {
		String value = BillingSummaryPage.tableBillingAccountPolicies.getRow(index).getCell(BillingAccountPoliciesTable.PAYMENT_PLAN).getValue();
		return value.replace(" (Renewal)", "");
	}

	// ------- Installments table-------

	/**
	 * Get all Due Dates from Installments table
	 * @return - list of dates including Deposit payments, so index of first Installment is usualy 1
	 */
	public static List<LocalDateTime> getInstallmentDueDates() {
		ArrayList<LocalDateTime> dates = new ArrayList<>();
		for (String value : BillingSummaryPage.tableInstallmentSchedule.getColumn(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE).getValue()) {
			dates.add(TimeSetterUtil.getInstance().parse(value, DateTimeUtils.MM_DD_YYYY));
		}
		return dates;
	}

	/**
	 * Get all Dues from Installments table
	 * @return - list of Dollar including Deposit payments, so index of first Installment is usualy 1
	 */
	public static List<Dollar> getInstallmentDues() {
		ArrayList<Dollar> dues = new ArrayList<>();
		for (String value : BillingSummaryPage.tableInstallmentSchedule.getColumn(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE).getValue()) {
			dues.add(new Dollar(value));
		}
		return dues;
	}
	
	public static Dollar getInstallmentDueByDueDate(LocalDateTime date) {
		String value = BillingSummaryPage.tableInstallmentSchedule.getRow(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE,
				date.format(DateTimeUtils.MM_DD_YYYY)).getCell(BillingInstallmentScheduleTable.SCHEDULE_DUE_AMOUNT).getValue();
		return new Dollar(value);
	}
	
	// ------- Bills and Statements table-------
	
	public static Row getBillRowByDate(LocalDateTime date) {
		return BillingSummaryPage.tableBillsStatements.getRow(BillingBillsAndStatmentsTable.DUE_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
	}
	
	public static String getBillCellValue(LocalDateTime date, String columnName) {
		return getBillRowByDate(date).getCell(columnName).getValue();
	}

	public static void verifyRenewOfferGenerated(LocalDateTime date, List<LocalDateTime> installmentDates) {
		installmentDates.remove(0); //Do not include Deposit bill
		BillingSummaryPage.showPriorTerms();

		CustomAssert.enableSoftMode();
		for (LocalDateTime installmentDate : installmentDates) {
			new BillingInstallmentsScheduleVerifier().setDescription(InstallmentDescription.INSTALLMENT)
					.setInstallmentDueDate(installmentDate.plusYears(1)).verifyPresent();
		}
		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.OFFER).verifyPresent(false);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @param expirationDate - original policy expiration date
	 * @param renewOfferDate - Renew generate offer date
	 * @param billGenDate - Bill generation date
	 * @param installmentsCount:
	 * MONTHLY_STANDARD or ELEVEN_PAY: 11 installments
	 * QUARTERLY: 4 installments
	 * SEMI_ANNUAL: 2 installments
	 * PAY_IN_FULL or ANNUAL: 1 installment
	 */
	public static void verifyRenewalOfferPaymentAmount(LocalDateTime expirationDate, LocalDateTime renewOfferDate, LocalDateTime billGenDate, Integer installmentsCount) {
		BillingSummaryPage.showPriorTerms();
		Dollar fullAmount = getPolicyRenewalProposalSum(renewOfferDate);
		//Get fees for both CA and non-CA states
		Dollar fee = getFeesValue(renewOfferDate).add(getFeesValue(billGenDate));

		Dollar expOffer = calculateFirstInstallmentAmount(fullAmount, installmentsCount).add(fee);
		if (BaseTest.getState().equals(Constants.States.CA)) {
			new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.OFFER)
					.setDueDate(expirationDate).setMinDue(expOffer).verifyPresent();
		} else {
			new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL)
					.setDueDate(expirationDate).setMinDue(expOffer).verifyPresent();
		}
	}

	public static void verifyRenewPremiumNotice(LocalDateTime renewDate, LocalDateTime billGenerationDate) {
		BillingSummaryPage.showPriorTerms();
		Dollar billAmount = getInstallmentDueByDueDate(renewDate).add(getFeesValue(billGenerationDate));
		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).verifyRowWithDueDate(renewDate);
		if (!BaseTest.getState().equals(Constants.States.KY) && !BaseTest.getState().equals(Constants.States.WV)) {
			new BillingBillsAndStatementsVerifier().setMinDue(billAmount).verifyRowWithDueDate(renewDate);
		}
	}

	
	// ------- Payments & Other Transactions table -------
	
	/**
	 * Search for all Fee transactions in specified date
	 */
	public static Dollar getFeesValue(LocalDateTime date) {
		Dollar amount = new Dollar(0);
		HashMap<String, String> values = new HashMap<>();
		values.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.FEE);
		values.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
		List<String> feeValues = BillingSummaryPage.tablePaymentsOtherTransactions.getValuesFromRows(values, BillingPaymentsAndOtherTransactionsTable.AMOUNT);
		for (String fee : feeValues) {
			amount = amount.add(new Dollar(fee));
		}
		return amount;
	}

	public static Dollar getPolicyRenewalProposalSum(LocalDateTime renewDate) {
		HashMap<String, String> values = new HashMap<>();
		values.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, renewDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PREMIUM);
		values.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL);
		return new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(values).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
	}

	// ------- Other -------

	public static Dollar calculateFirstInstallmentAmount(Dollar totalAmount, Integer installmentsCount) {
		Dollar other = calculateLastInstallmentAmount(totalAmount, installmentsCount).multiply(installmentsCount - 1);
		return totalAmount.subtract(other);
	}

	public static Dollar calculateLastInstallmentAmount(Dollar totalAmount, Integer installmentsCount) {
		return totalAmount.divide(installmentsCount);
	}
}
