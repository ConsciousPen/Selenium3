/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers.billing;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingAccountPoliciesTable;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsActions;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionAction;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.actiontabs.DeclinePaymentActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.table.Row;

public final class BillingHelper {

	// TODO remove after "base" tests adaptation
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

	protected static Logger log = LoggerFactory.getLogger(BillingHelper.class);

	// ------- Billing Account Policies table -------

	public static Row getPolicyRowByEffDate(LocalDateTime date) {
		return BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingAccountPoliciesTable.EFF_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
	}

	public static String getPaymentPlan(int index) {
		String value = BillingSummaryPage.tableBillingAccountPolicies.getRow(index).getCell(BillingAccountPoliciesTable.PAYMENT_PLAN).getValue();
		return value.replace(" (Renewal)", "");
	}

	public static Dollar getPolicyMinimumDueAmount(String policyNumber) {
		String value = BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingAccountPoliciesTable.POLICY_NUM, policyNumber).getCell(BillingAccountPoliciesTable.MIN_DUE).getValue();
		return new Dollar(value);
	}

	public static Dollar getPolicyTotalDueAmount(String policyNumber) {
		String value = BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingAccountPoliciesTable.POLICY_NUM, policyNumber).getCell(BillingAccountPoliciesTable.TOTAL_DUE).getValue();
		return new Dollar(value);
	}

	// ------- Installments table-------

	/**
	 * Get all Due Dates from Installments table
	 * 
	 * @return - list of dates including Deposit payments, so index of first
	 *         Installment is usualy 1
	 */
	public static List<LocalDateTime> getInstallmentDueDates() {
		List<LocalDateTime> installments = BillingSummaryPage.tableInstallmentSchedule.getValuesFromRows(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE).stream().map(
			value -> TimeSetterUtil.getInstance().parse(value, DateTimeUtils.MM_DD_YYYY)).collect(Collectors.toList());
		log.info("Billing installments due dates: " + installments.stream().map(date -> date.format(DateTimeUtils.MM_DD_YYYY)).collect(Collectors.joining(", ")));
		return installments;
	}

	/**
	 * Get all Dues from Installments table
	 * 
	 * @return - list of Dollar including Deposit payments, so index of first
	 *         Installment is usualy 1
	 */
	public static List<Dollar> getInstallmentDues() {
		return BillingSummaryPage.tableInstallmentSchedule.getValuesFromRows(BillingInstallmentScheduleTable.SCHEDULE_DUE_AMOUNT).stream().map(Dollar::new).collect(Collectors.toList());
	}

	public static Dollar getInstallmentDueByDueDate(LocalDateTime date) {
		String value = BillingSummaryPage.tableInstallmentSchedule.getRow(BillingInstallmentScheduleTable.INSTALLMENT_DUE_DATE, date.format(DateTimeUtils.MM_DD_YYYY)).getCell(
			BillingInstallmentScheduleTable.SCHEDULE_DUE_AMOUNT).getValue();
		return new Dollar(value);
	}

	// ------- Bills and Statements table-------

	public static Row getBillRowByDate(LocalDateTime date) {
		return BillingSummaryPage.tableBillsStatements.getRow(BillingBillsAndStatmentsTable.DUE_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
	}

	public static String getBillCellValue(LocalDateTime date, String columnName) {
		return getBillRowByDate(date).getCell(columnName).getValue();
	}

	public static Dollar getBillDueAmount(LocalDateTime billDueDate, String billType) {
		Map<String, String> values = new HashMap<>();
		values.put(BillingBillsAndStatmentsTable.DUE_DATE, billDueDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingBillsAndStatmentsTable.TYPE, billType);
		return new Dollar(BillingSummaryPage.tableBillsStatements.getRow(values).getCell(BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
	}

	public static Dollar getBillMinDueAmount(LocalDateTime billDueDate, String billType) {
		Map<String, String> values = new HashMap<>();
		values.put(BillingBillsAndStatmentsTable.DUE_DATE, billDueDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingBillsAndStatmentsTable.TYPE, billType);
		return new Dollar(BillingSummaryPage.tableBillsStatements.getRow(values).getCell(BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
	}

	public static Dollar getBillTotalDueAmount(LocalDateTime billDueDate, String billType) {
		Map<String, String> values = new HashMap<>();
		values.put(BillingBillsAndStatmentsTable.DUE_DATE, billDueDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingBillsAndStatmentsTable.TYPE, billType);
		return new Dollar(BillingSummaryPage.tableBillsStatements.getRow(values).getCell(BillingBillsAndStatmentsTable.TOTAL_DUE).getValue());
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
	
	/**
	 * Search for all transactions in specified date
	 */
	public static Dollar getTransactionsAmountSum(LocalDateTime date) {
		Dollar amount = new Dollar(0);
		HashMap<String, String> values = new HashMap<>();
		values.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, date.format(DateTimeUtils.MM_DD_YYYY));
		List<String> feeValues = BillingSummaryPage.tablePaymentsOtherTransactions.getValuesFromRows(values, BillingPaymentsAndOtherTransactionsTable.AMOUNT);
		for (String fee : feeValues) {
			amount = amount.add(new Dollar(fee));
		}
		return amount;
	}

	public static Dollar getPolicyRenewalProposalSum(LocalDateTime renewDate, String policyNum) {
		HashMap<String, String> query = new HashMap<>();
		query.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, renewDate.format(DateTimeUtils.MM_DD_YYYY));
		query.put(BillingPaymentsAndOtherTransactionsTable.POLICY, policyNum);
		query.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PREMIUM);

		Dollar summ = new Dollar(0);
		for (String amount : BillingSummaryPage.tablePaymentsOtherTransactions.getValuesFromRows(query, BillingPaymentsAndOtherTransactionsTable.AMOUNT)) {
			summ = summ.add(new Dollar(amount));
		}
		return summ;
	}

	public static void declinePayment(LocalDateTime transactionDate) {
		HashMap<String, String> values = new HashMap<>();
		values.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, transactionDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PAYMENT);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(values).getCell(BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(PaymentsAndOtherTransactionAction.DECLINE).click();
		DeclinePaymentActionTab declinePaymentActionTab = new DeclinePaymentActionTab();
		if (declinePaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.DeclinePaymentActionTab.DECLINE_REASON).isPresent()) {
			declinePaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.DeclinePaymentActionTab.DECLINE_REASON.getLabel(), ComboBox.class).setValue("index=1");
			DeclinePaymentActionTab.buttonOk.click();
		}
	}

	public static int getPremiumTransactionsCount(String policyNum) {
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.POLICY, policyNum);
		query.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PREMIUM);
		return BillingSummaryPage.tablePaymentsOtherTransactions.getRows(query).size();
	}

	// ------- Pending Transactions table -------

	public static void changePendingTransaction(LocalDateTime transactionDate, String type) {
		HashMap<String, String> values = new HashMap<>();
		values.put(BillingPendingTransactionsTable.TRANSACTION_DATE, transactionDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingPendingTransactionsTable.TYPE, type);
		BillingSummaryPage.tablePendingTransactions.getRow(values).getCell(BillingPendingTransactionsTable.ACTION).controls.links.get(BillingPendingTransactionsActions.CHANGE).click();
	}

	public static void approvePendingTransaction(LocalDateTime transactionDate, String type) {
		HashMap<String, String> values = new HashMap<>();
		values.put(BillingPendingTransactionsTable.TRANSACTION_DATE, transactionDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingPendingTransactionsTable.TYPE, type);
		BillingSummaryPage.tablePendingTransactions.getRow(values).getCell(BillingPendingTransactionsTable.ACTION).controls.links.get(BillingPendingTransactionsActions.APPROVE).click();
		BillingSummaryPage.dialogApprovePendingTransaction.confirm();
	}

	public static void rejectPendingTransaction(LocalDateTime transactionDate, String type) {
		HashMap<String, String> values = new HashMap<>();
		values.put(BillingPendingTransactionsTable.TRANSACTION_DATE, transactionDate.format(DateTimeUtils.MM_DD_YYYY));
		values.put(BillingPendingTransactionsTable.TYPE, type);
		BillingSummaryPage.tablePendingTransactions.getRow(values).getCell(BillingPendingTransactionsTable.ACTION).controls.links.get(BillingPendingTransactionsActions.REJECT).click();
		BillingSummaryPage.dialogApprovePendingTransaction.confirm();
	}

	// ------- Other -------

	public static Dollar calculateFirstInstallmentAmount(Dollar totalAmount, Integer installmentsCount) {
		Dollar other = calculateLastInstallmentAmount(totalAmount, installmentsCount).multiply(installmentsCount - 1);
		return totalAmount.subtract(other);
	}

	public static Dollar calculateLastInstallmentAmount(Dollar totalAmount, Integer installmentsCount) {
		return totalAmount.divide(installmentsCount);
	}
	
	//  PLIGA fee calculation based on old algorithm. Now used for current transaction 
	public static Dollar calculatePligaFeeCurrentTransaction(LocalDateTime transactionDate) {
		Map<String, String> premiumRowSearchQuery = new HashMap<>();
		premiumRowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, transactionDate.format(DateTimeUtils.MM_DD_YYYY));
		premiumRowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.PREMIUM);
		Dollar totalPremiumAmount = DZERO;
		if (BillingSummaryPage.tablePaymentsOtherTransactions.getRows(premiumRowSearchQuery).isEmpty()) {
			log.warn(String.format("There is no Premium transaction(s) with query %s, assume PLIGA Fee should be $0", premiumRowSearchQuery.entrySet()));
			return totalPremiumAmount;
		}

		for (String amount : BillingSummaryPage.tablePaymentsOtherTransactions.getValuesFromRows(premiumRowSearchQuery, BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT)) {
			totalPremiumAmount = totalPremiumAmount.add(new Dollar(amount));
		}
		return calculatePligaFee(transactionDate, totalPremiumAmount);
	}
	
	
	/**
	 * PLIGA fee current value gets from screen. PLIGA fee verified based on new algorithm (PASBB-703)
	 */
	public static Dollar calculatePligaFee(LocalDateTime transactionDate) {
		Map<String, String> premiumRowSearchQuery = new HashMap<>();
		premiumRowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, transactionDate.format(DateTimeUtils.MM_DD_YYYY));
		premiumRowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.PREMIUM);
		Dollar totalPremiumAmount = DZERO;
		if (BillingSummaryPage.tablePaymentsOtherTransactions.getRows(premiumRowSearchQuery).isEmpty()) {
			log.warn(String.format("There is no Premium transaction(s) with query %s, assume PLIGA Fee should be $0", premiumRowSearchQuery.entrySet()));
			return totalPremiumAmount;
		}

		Map<String, String> feePLIGARowSearchQuery = new HashMap<>();
		feePLIGARowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, transactionDate.format(DateTimeUtils.MM_DD_YYYY));
		feePLIGARowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.FEE);
		feePLIGARowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PLIGA_FEE);
		Dollar feePLIGAAmount = DZERO;
		for (String amount : BillingSummaryPage.tablePaymentsOtherTransactions.getValuesFromRows(feePLIGARowSearchQuery, BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT)) {
			feePLIGAAmount = feePLIGAAmount.add(new Dollar(amount));
		}
		
		verifyPligaFee(transactionDate);
		
		return feePLIGAAmount;
	}
	
	public static void verifyPligaFee(LocalDateTime transactionDate) {
		
		Map<String, String> premiumRowSearchQuery = new HashMap<>();
		premiumRowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.PREMIUM);
		Dollar totalPremiumAmount = DZERO;
		for (String amount : BillingSummaryPage.tablePaymentsOtherTransactions.getValuesFromRows(premiumRowSearchQuery, BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT)) {
			totalPremiumAmount = totalPremiumAmount.add(new Dollar(amount));
		}
		
		Dollar calculatedTotalPLIGAFeeAmount = calculatePligaFee(transactionDate, totalPremiumAmount);
		
		Map<String, String> feePLIGARowSearchQuery = new HashMap<>();
		feePLIGARowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.FEE);
		feePLIGARowSearchQuery.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PLIGA_FEE);
		Dollar totalPLIGAFeeAmount = DZERO;
		for (String amount : BillingSummaryPage.tablePaymentsOtherTransactions.getValuesFromRows(feePLIGARowSearchQuery, BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT)) {
			totalPLIGAFeeAmount = totalPLIGAFeeAmount.add(new Dollar(amount));
		}
		
		log.warn("PLIGA fee verification. Calculated total PLIGA fee amount: "+calculatedTotalPLIGAFeeAmount+". Displayed total PLIGA fee amount: "+totalPLIGAFeeAmount);
		
		// The PLIGA fee value can differ on 1$ due to calculation / round issue 
		if ( !(totalPLIGAFeeAmount.equals(calculatedTotalPLIGAFeeAmount) || totalPLIGAFeeAmount.equals(calculatedTotalPLIGAFeeAmount.add(1)) || totalPLIGAFeeAmount.equals(calculatedTotalPLIGAFeeAmount.add(-1))) ) {
			CustomAssertions.assertThat(calculatedTotalPLIGAFeeAmount).isEqualTo(totalPLIGAFeeAmount);
		}
		
	}
	
	/**
	 * PLIGA fee calculation for specified amount (can be separate used for Term Total premium per policy according to PASBB-703)
	 */
	public static Dollar calculatePligaFee(LocalDateTime transactionDate, Dollar totalPremiumAmount) {
		final double pligaFeePercentage;
		switch (transactionDate.getYear()) {
		// PAS12: PLIGAFEE is configured as 0.7% of the premium for 1-Jan-2017 to 31-Dec-2017
			case 2017 :
				pligaFeePercentage = 0.7;
				break;
			// PAS13 ER: PLIGAFEE is configured as 0.6% of the premium for 1-Jan-2018 to 31-Dec-2019
			case 2018 :
			case 2019 :
				pligaFeePercentage = 0.6;
				break;
			default :
				pligaFeePercentage = 0.6;
				log.warn(String.format("PLIGA Fee charge percent for %s year is unknown, default %s charge percent will be used for calculation.", transactionDate.getYear(), pligaFeePercentage));
		}
		return new Dollar(Math.round(Double.valueOf(totalPremiumAmount.getPercentage(pligaFeePercentage).toPlaingString())));
	}

	/**
	 * Applicable only for NY state and AutoSS product
	 */
	public static Dollar calculateMvleFee(String policyTerm, int numberOfVehiclesExceptTrailers) {
		Dollar termFee;
		if (BillingConstants.PolicyTerm.SEMI_ANNUAL.equals(policyTerm)) {
			termFee = new Dollar(5);
		} else if (BillingConstants.PolicyTerm.ANNUAL.equals(policyTerm)) {
			termFee = new Dollar(10);
		} else {
			throw new IstfException(String.format("Unable to calculate MVLE Fee for unknown policy term \"%1$s\", only \"%2$s\" and \"%3$s\" are allowed.",
				policyTerm, BillingConstants.PolicyTerm.ANNUAL, BillingConstants.PolicyTerm.SEMI_ANNUAL));
		}

		if (numberOfVehiclesExceptTrailers > 0) {
			termFee = termFee.multiply(numberOfVehiclesExceptTrailers);
		}
		return termFee;
	}
}
