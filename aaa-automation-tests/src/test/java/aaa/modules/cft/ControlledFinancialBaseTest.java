/* Copyright ?? 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.cft;

import java.time.LocalDateTime;
import java.util.HashMap;

import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import aaa.admin.modules.reports.operationalreports.OperationalReport;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints.TimepointsList;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.BillingPendingTransactionsVerifier;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.ActivitiesAndUserNotesConstants;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.BillingPendingTransactionsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.main.modules.billing.paymentsmaintenance.PaymentsMaintenance;
import aaa.main.modules.billing.paymentsmaintenance.actiontabs.SearchSuspenseActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.billing.PaymentsAndBillingMaintenancePage;
import aaa.modules.cft.details.BillingAccountDetails;
import aaa.modules.cft.details.BillingAccountInformationHolder;
import aaa.modules.cft.details.PolicyDetails;
import aaa.modules.policy.PolicyBaseTest;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

public class ControlledFinancialBaseTest extends PolicyBaseTest {

	protected static final String DEFAULT_TEST_DATA_KEY = "TestData";
	protected static final String STATE_PARAM = "state";

	protected BillingAccount billingAccount = new BillingAccount();
	protected OperationalReport operationalReport = new OperationalReport();

	/**
	 * Creating of the policy for test
	 */
	protected void createPolicyForTest() {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTestData();
		String policyN = createPolicy(td);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingAccountInformationHolder.addBillingAccountDetails(
			new BillingAccountDetails.Builder()
				.setBillingAccountNumber(BillingSummaryPage.labelBillingAccountNumber.getValue())
				.addPolicyDetails(new PolicyDetails.Builder()
					.setPolicyNumber(policyN)
					.setPolicyInstallmentsSchedule(BillingHelper.getInstallmentDueDates())
					.setPolicyEffectiveDate(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(3).getValue(), DateTimeUtils.MM_DD_YYYY))
					.setPolicyExpirationDate(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(3).getValue(), DateTimeUtils.MM_DD_YYYY).plusYears(1))
					.build())
				.build());
	}

	/**
	 * Endorsement of the policy
	 * today(suite start time) + 2 day
	 */
	protected void endorsePolicyEffDatePlus2Days() {
		LocalDateTime endorsePlus2 = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		performEndorsementOnDate(endorsePlus2, endorsePlus2.plusDays(2));
	}

	/**
	 * Endorsement of the policy
	 * today(suite start time) + 16 day
	 */
	protected void endorsePolicyEffDatePlus16Days() {
		LocalDateTime endorsePlus16 = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		performEndorsementOnDate(endorsePlus16);
	}

	/**
	 * Endorsement of the policy
	 * today(suite start time) + 16 day
	 */
	protected void endorseOOSPolicyEffDatePlus16Days() {
		LocalDateTime endorsePlus16 = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		TimeSetterUtil.getInstance().nextPhase(endorsePlus16);
		log.info("OOS Endorsment action started");
		log.info("OOS Endorsement date: {}", endorsePlus16);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.endorse().performAndFill(getTestSpecificTD("TestData_OOS"));
		String endorseDate = getTestSpecificTD("TestData_OOS").getValue("EndorsementActionTab", "Endorsement Date");
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Endorsement effective %1$s for Policy %2$s", endorseDate,
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber()));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
		policy.rollOn().perform(false, false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("OOS Endorsment action completed successfully");
	}

	protected void endorsePolicyCancellationNoticeDate() {
		LocalDateTime endorsementDate = getTimePoints().getCancellationNoticeDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		performEndorsementOnDate(endorsementDate);
	}

	protected void futureEndorsePolicyCancellationNoticeDate() {
		LocalDateTime endorsementDate = getTimePoints().getCancellationNoticeDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		performEndorsementOnDate(endorsementDate, endorsementDate.plusDays(2));
	}

	protected void endorsePolicyCancellationDate() {
		LocalDateTime endorsementDate = getTimePoints().getCancellationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		performEndorsementOnDate(endorsementDate);
	}

	protected void endorseFirstEPBillDate(String keyPath) {
		LocalDateTime firstEPBillDate =
			getTimePoints().getEarnedPremiumBillFirst(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(firstEPBillDate);

		log.info("OOS Endorsement action started on {}", firstEPBillDate);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());

		String endorseDate = getTimePoints().getCancellationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1))
			.minusDays(2).format(DateTimeUtils.MM_DD_YYYY);
		policy.endorse().performAndFill(getTestSpecificTD("TestData_OOSEndorsement").adjust(keyPath, endorseDate));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Endorsement effective %1$s for Policy %2$s", endorseDate,
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber()));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);

		policy.rollOn().perform(true, false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("OOS Endorsement action completed successfully");

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar amount = new Dollar(BillingSummaryPage.tableBillsStatements
			.getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE, BillingConstants.BillsAndStatementsType.BILL)
			.getCell(BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE).getValue()).add(600);
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), amount);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(firstEPBillDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
			.setAmount(amount.negate())
			.verifyPresent();
	}
	protected void declineSuspensePaymentCancellationDate() {
		LocalDateTime declineDate = getTimePoints().getCancellationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(declineDate);
		log.info("Decline Suspense Payment action started");
		log.info("Action date: {}", declineDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());

		LocalDateTime suspenseDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		HashMap<String, String> map = new HashMap<>();
		map.put(BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, suspenseDate.format(DateTimeUtils.MM_DD_YYYY));
		map.put(BillingPaymentsAndOtherTransactionsTable.TYPE, PaymentsAndOtherTransactionType.PAYMENT);
		map.put(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, PaymentsAndOtherTransactionSubtypeReason.SUSPENSE);

		IBillingAccount billing = new BillingAccount();
		TestData tdBilling = testDataManager.billingAccount;
		billing.declinePayment().perform(tdBilling.getTestData("DeclinePayment", "TestData_FeeNoRestriction"), map);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(suspenseDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SUSPENSE)
			.setStatus(PaymentsAndOtherTransactionStatus.DECLINED)
			.verifyPresent();
		log.info("Decline Suspense Payment action completed successfully");
	}

	/**
	 * Accept 10$ cash payment on startDate + 25 days
	 */
	protected void acceptPaymentEffDatePlus25() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		acceptManualPaymentOnDate(paymentDate);
	}

	/**
	 * Accept 100$ cash payment on startDate + 2 days
	 */
	protected void acceptPaymentStartDatePlus2() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		acceptManualPaymentOnDate(paymentDate);
	}

	/**
	 * Accept TotalDue + Over payment on first bill generation date
	 */
	protected void acceptTotalDuePlusOverpaymentOnBillGenDate(Dollar overpayment) {
		LocalDateTime paymentDate = getTimePoints().getBillGenerationDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		acceptTotalDuePlusOverpaymentOnDate(overpayment, paymentDate);
	}

	/**
	 * Accept TotalDue + Over payment on start date + 2 days
	 */
	protected void acceptTotalDuePlusOverpaymentOnStartDatePlus2(Dollar overpayment) {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		acceptTotalDuePlusOverpaymentOnDate(overpayment, paymentDate);
	}

	/**
	 * Accept TotalDue + Over payment on start date + 16 days
	 */
	protected void acceptTotalDuePlusOverpaymentOnStartDatePlus16(Dollar overpayment) {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		acceptTotalDuePlusOverpaymentOnDate(overpayment, paymentDate);
	}

	protected void acceptTotalDuePlusOverpaymentOnRenewCustomerDeclineDate(Dollar overpayment) {
		LocalDateTime paymentDate = getTimePoints().getRenewCustomerDeclineDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());;
		acceptTotalDuePlusOverpaymentOnDate(overpayment, paymentDate);
		log.info("Validate Customer declined status on {}", paymentDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(BillingConstants.BillingAccountPoliciesPolicyStatus.CUSTOMER_DECLINED).verifyPresent();
	}

	/**
	 * Accept Min Due payment on DD1 + 30 days
	 */
	protected void acceptMinDuePaymentDD1plus30() {
		LocalDateTime paymentDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1).plusDays(30);
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept payment action started");
		log.info("Accept payment date: {}", paymentDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar minDue = new Dollar(BillingSummaryPage.tableBillsStatements
			.getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE, BillingConstants.BillsAndStatementsType.BILL)
			.getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		billingAccount.acceptPayment().perform(getTestSpecificTD("AcceptPayment"), minDue);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(paymentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
			.setAmount(minDue.negate())
			.verifyPresent();
		log.info("Accept payment action completed successfully");
	}

	/**
	 * Accept payment on EP3 date
	 */
	protected void acceptPaymentEP3After10PM(int installmentNumber) {
		LocalDateTime paymentDate = getTimePoints().getEarnedPremiumBillThird(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber)).withHour(22).withMinute(1);
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept payment action started");
		log.info("Accept payment date: {}", paymentDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		billingAccount.acceptPayment().perform(getTestSpecificTD("AcceptPayment50"));
		String expValue = getTestSpecificTD("AcceptPayment50")
			.getTestData(AcceptPaymentActionTab.class.getSimpleName())
			.getValue(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel());
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(paymentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
			.setAmount(new Dollar(expValue).negate())
			.verifyPresent();
		log.info("Accept payment action completed successfully");
	}

	/**
	 * Fully pay installment with min Due
	 */
	protected void payInstallmentWithMinDue() {
		log.info("Accept payment action started");
		billingAccount.acceptPayment().perform(getTestSpecificTD("AcceptPayment"), BillingSummaryPage.getMinimumDue());
		log.info("Accept payment action completed successfully");
	}

	protected void approveRefundOnStartDatePlus25(Dollar refundAmount) {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25).with(DateTimeUtils.closestFutureWorkingDay);
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Approve refund action started on {}", refundDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		billingAccount.approveRefund().perform(refundAmount);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(refundDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
			.setAmount(refundAmount)
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED)
			.verifyPresent();
		log.info("Approve refund action completed successfully");
	}

	protected void pendingRefundOnStartDatePlus16(Dollar refundAmount) {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		pendingRefundOnDate(refundAmount, refundDate);
	}

	protected void pendingRefundOnStartDatePlus25(Dollar refundAmount) {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25).with(DateTimeUtils.closestFutureWorkingDay);
		pendingRefundOnDate(refundAmount, refundDate);
	}

	protected void issuedRefundOnStartDatePlus16(Dollar refundAmount) {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		issuedRefundOnDate(refundAmount, refundDate);
	}

	protected void issuedRefundOnStartDatePlus25(Dollar refundAmount) {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		issuedRefundOnDate(refundAmount, refundDate);
	}

	protected void issuedRefundOnExpDatePlus25(Dollar refundAmount) {
		LocalDateTime refundDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate().plusDays(25);
		issuedRefundOnDate(refundAmount, refundDate);
	}

	protected void voidRefundOnStartDatePlus25() {
		LocalDateTime voidDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25).with(DateTimeUtils.closestFutureWorkingDay);
		TimeSetterUtil.getInstance().nextPhase(voidDate);
		log.info("Void refund action started on {}", voidDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE,
			BillingConstants.PaymentsAndOtherTransactionType.REFUND).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.REFUND)
			.getCell(BillingPendingTransactionsTable.ACTION).controls.links.get(ActionConstants.BillingPaymentsAndOtherTransactionAction.VOID).click();
		Page.dialogConfirmation.confirm();
		new BillingPaymentsAndTransactionsVerifier()
			.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.VOIDED)
			.setAmount(amount)
			.verifyPresent();
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(voidDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REFUND_PAYMENT_VOIDED)
			.setAmount(amount.negate())
			.verifyPresent();
		log.info("Void refund action completed successfully");
	}

	protected void rejectRefundOnStartDatePlus25() {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25).with(DateTimeUtils.closestFutureWorkingDay);
		rejectRefundOnDate(refundDate);
	}

	protected void rejectRefundOnCancellationNoticeDate() {
		LocalDateTime refundDate = getTimePoints().getCancellationNoticeDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		rejectRefundOnDate(refundDate);
	}

	/**
	 * Decline Payment on cancellation Notice generation day
	 */
	protected void decline10DollarsPaymentOnCancellationNoticeDate() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		log.info("Decline Payment action started");
		log.info("Decline Payment date: {}", cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		billingAccount.declinePayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), new Dollar(-10).toString());
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(cancellationNoticeDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED)
			.setAmount(new Dollar(10))
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
			.verifyPresent();
		log.info("Decline payment action completed successfully");
	}

	/**
	 * Other Adjustment on cancellation Notice generation day
	 * Adjustment amount defined in TestData
	 */
	protected void otherAdjustmentOnCancellationNoticeDate() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		otherAdjustmentOnDate(cancellationNoticeDate);
	}

	/**
	 * Other Adjustment on Cancellation day
	 * Adjustment amount defined in TestData
	 */
	protected void otherAdjustmentOnCancellationDate() {
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		otherAdjustmentOnDate(cancellationDate);
	}

	/**
	 * Bill generation for provided installment of the policy
	 *
	 * @param installmentNumber number of the installment
	 */
	protected void generateInstallmentBill(int installmentNumber) {
		LocalDateTime billDueDate = getTimePoints().getBillGenerationDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		log.info("{} Installment bill generation started on {}", installmentNumber, billDueDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingBillsAndStatementsVerifier()
			.setType(BillingConstants.BillsAndStatementsType.BILL)
			.setDueDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber))
			.setMinDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue()))
			.setPastDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAST_DUE).getValue()))
			.setTotalDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue()))
			.verifyPresent();
		log.info("{} Installment bill generation completed successfully", installmentNumber);
	}

	protected void splitPolicyOnFirstDueDate() {
		TimeSetterUtil.getInstance().nextPhase(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		log.info("Split policy action started");
		log.info("Split policy action date: {}", BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.endorse().performAndFill(getTestSpecificTD("Endorsement"));
		// split policy
		policy.policySplit().perform(getTestSpecificTD("SplitTestData"));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION, String.format(
			"Policy %1$s has been split to a new quote", BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber())).verify.present();
		log.info("Split policy action completed successfully");
	}

	protected void waiveFeeOnStartDatePlus16() {
		LocalDateTime waiveDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		waiveFeeOnDate(waiveDate);
	}

	protected void waiveFeeOnCancellationDate(int installmentNumber) {
		LocalDateTime waiveDate = getTimePoints().getCancellationDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		waiveFeeOnDate(waiveDate);
	}

	protected void manualFutureCancellationEffDatePlus25Days() {
		LocalDateTime plus25Days = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		TimeSetterUtil.getInstance().nextPhase(plus25Days);
		log.info("Manual cancellation action started");
		log.info("Manual cancellation date: {}", plus25Days);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.cancel().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
		log.info("Manual cancellation action completed successfully");
	}

	protected void manualCancellationDD1Plus5(String keyPath) {
		LocalDateTime cancellationDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1).plusDays(5);
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		log.info("Manual cancellation action started");
		log.info("Manual cancellation date: {}", cancellationDate);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		LocalDateTime effectiveDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate();
		policy.cancel().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).adjust(keyPath, effectiveDate.format(DateTimeUtils.MM_DD_YYYY)));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Manual cancellation action completed successfully");
	}

	protected void manualCancellationStartDatePlus16(String keyPath) {
		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		log.info("Manual cancellation action started");
		log.info("Manual cancellation date: {}", cancellationDate);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		String effectiveDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue();
		policy.cancel().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).adjust(keyPath, effectiveDate));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Manual cancellation action completed successfully");
	}

	protected void rewritePolicyOnCancellationDate() {
		LocalDateTime cDate = getTimePoints().getCancellationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(cDate);

		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);

		String rewritePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("TEST: Rewriting Policy #" + rewritePolicyNumber);

		policy.dataGather().start();
		policy.getDefaultView().fill(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void updatePolicyStatusForPendedCancellation() {
		LocalDateTime plus25Days = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		TimeSetterUtil.getInstance().nextPhase(plus25Days.plusDays(2));
		log.info("Policy status update job action started");
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Policy status update job completed successfully");
	}

	protected void manualReinstatement() {
		LocalDateTime reinstatementDate = getTimePoints().getCancellationNoticeDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
		log.info("Manual reinstatement action started");
		log.info("Manual reinstatement date: {}", reinstatementDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.reinstate().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Reinstatement for Policy %1$s", BillingAccountInformationHolder.getCurrentBillingAccountDetails()
			.getCurrentPolicyDetails().getPolicyNumber()));
		log.info("Manual reinstatement action completed successfully");
	}

	protected void manualReinstatementStartDatePlus25() {
		LocalDateTime reinstatementDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		manualReinstatementOnDate(reinstatementDate);
	}

	/**
	 * Cancellation Notice for the policy
	 */
	protected void automaticCancellationNotice(int installmentNumber) {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		LocalDateTime expCancellationDate = getTimePoints().getCancellationTransactionDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		log.info("Cancellation Notice action started");
		log.info("Cancellation Notice date: {}", cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingBillsAndStatementsVerifier()
			.setDueDate(expCancellationDate)
			.setType(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE)
			.verifyPresent();
		log.info("Cancellation Notice action completed successfully");
	}

	/**
	 * Cancellation of the policy
	 */
	protected void automaticCancellation(int installmentNumber) {
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		log.info("Cancellation action started");
		log.info("Cancellation date: {}", cancellationDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Cancellation action completed successfully");
	}

	/**
	 * Generate 1st EP bill
	 */
	protected void generateFirstEarnedPremiumBill(int installmentNumber) {
		LocalDateTime firstEPBillDate = getTimePoints().getEarnedPremiumBillFirst(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		log.info("First EP bill generation started");
		log.info("First EP bill generated date: {}", firstEPBillDate);
		generateAndCheckEarnedPremiumBill(firstEPBillDate);
		log.info("First EP bill generated successfully");
	}

	/**
	 * Generate 2st EP bill
	 */
	protected void generateSecondEarnedPremiumBill(int installmentNumber) {
		LocalDateTime secondEPBillDate = getTimePoints().getEarnedPremiumBillSecond(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		log.info("Second EP bill generation started");
		log.info("Second EP bill generated date: {}", secondEPBillDate);
		generateAndCheckEarnedPremiumBill(secondEPBillDate);
		log.info("Second EP bill generated successfully");
	}

	/**
	 * Generate 3st EP bill
	 */
	protected void generateThirdEarnedPremiumBill(int installmentNumber) {
		LocalDateTime thirdEPBillDate = getTimePoints().getEarnedPremiumBillThird(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		log.info("Third EP bill generation started");
		log.info("Third EP bill generated date: {}", thirdEPBillDate);
		generateAndCheckEarnedPremiumBill(thirdEPBillDate);
		log.info("Third EP bill generated successfully");
	}

	/**
	 * Generate EP write off
	 */
	protected void writeOff(int installmentNumber) {
		LocalDateTime writeOffDate = getTimePoints().getEarnedPremiumWriteOff(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
		TimeSetterUtil.getInstance().nextPhase(writeOffDate);
		log.info("EP Write off generation action started");
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(writeOffDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF)
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
			.verifyPresent();
		log.info("EP Write off generated successfully");
	}

	protected void addSuspenseEffDatePlus2() {
		LocalDateTime suspenseDate = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		addSuspenseOnDate(suspenseDate);
	}

	protected void addSuspenseStartDatePlus25() {
		LocalDateTime suspenseDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		addSuspenseOnDate(suspenseDate);
	}

	protected void clearSuspenseEffDatePlus16() {
		LocalDateTime suspenseDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		TimeSetterUtil.getInstance().nextPhase(suspenseDate);
		log.info("Clear Suspense action started");
		log.info("Action date: {}", suspenseDate);
		mainApp().reopen();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openBilling(policyNumber);
		new PaymentsMaintenance().clearSuspense().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), policyNumber);
		log.info("Suspense cleared successfully");
	}

	protected void refundSuspenseDD1plus5() {
		LocalDateTime refundDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1).plusDays(5);
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Refund Suspense action started");
		log.info("Action date: {}", refundDate);

		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());

		BillingSummaryPage.buttonPaymentsBillingMaintenance.click();
		PaymentsAndBillingMaintenancePage.buttonClearSuspense.click();
		new SearchSuspenseActionTab().fillTab(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		SearchSuspenseActionTab.buttonSearch.click();
		SearchSuspenseActionTab.tableSuspenseSearchResults.getRow(1).getCell(BillingConstants.BillingSuspenseSearchResultsTable.ACTION).controls.links.get(ActionConstants.REVERSE).click();
		Tab.buttonOk.click();

		log.info("Suspense refunded successfully");
	}

	protected void generateRenewalOffer() {
		LocalDateTime renewalOfferDate = getTimePoints().getRenewOfferGenerationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		TimeSetterUtil.getInstance().nextPhase(renewalOfferDate);
		log.info("Renewal offer generation started on {}", renewalOfferDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		log.info("Renewal offer generated successfully");
	}

	protected void generateRenewalOfferBill() {
		LocalDateTime policyExpDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate();
		LocalDateTime renewalOfferBillDate = getTimePoints().getTimepoint(policyExpDate, TimepointsList.BILL_GENERATION, false);
		TimeSetterUtil.getInstance().nextPhase(renewalOfferBillDate);
		log.info("Renewal offer bill generation started on {}", renewalOfferBillDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(getTimePoints().getRenewOfferGenerationDate(policyExpDate))
			.setEffectiveDate(policyExpDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL)
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
			.verifyPresent();
		new BillingBillsAndStatementsVerifier()
			.setDueDate(policyExpDate)
			.setMinDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue()))
			.setTotalDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue()))
			.verifyPresent();
		log.info("Renewal offer bill generated successfully");
	}

	protected void verifyEscheatmentOnExpDatePlus25Plus13Months() {
		LocalDateTime escheatmentDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate().plusDays(25).plusMonths(13);
		TimeSetterUtil.getInstance().nextPhase(escheatmentDate);
		log.info("Verify escheatment on {}", escheatmentDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(escheatmentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REFUND_PAYMENT_VOIDED)
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
			.verifyPresent();
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(escheatmentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.ESCHEATMENT)
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
			.verifyPresent();
		log.info("Escheatment is created");
	}

	protected TestData getPolicyTestData() {
		throw new IstfException("Please override method in appropriate child class with relevant test data preparation");
	}

	protected void runCFTJobs() {
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		JobUtils.executeJob(Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		JobUtils.executeJob(Jobs.policyTransactionLedgerJob);
	}

	private void generateAndCheckEarnedPremiumBill(LocalDateTime date) {
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(date);
	}

	private void acceptManualPaymentOnDate(LocalDateTime paymentDate) {
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept payment action started");
		log.info("Accept payment date: {}", paymentDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		String expValue = getTestSpecificTD(DEFAULT_TEST_DATA_KEY)
			.getTestData(AcceptPaymentActionTab.class.getSimpleName())
			.getValue(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel());
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(paymentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
			.setAmount(new Dollar(expValue).negate())
			.verifyPresent();
		log.info("Accept payment action completed successfully");
	}

	private void acceptTotalDuePlusOverpaymentOnDate(Dollar overpayment, LocalDateTime paymentDate) {
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept overpayment action started on {}", paymentDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = BillingSummaryPage.getTotalDue().add(overpayment);
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), amount);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(paymentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
			.setAmount(amount.negate())
			.verifyPresent();
		log.info("Accept overpayment action completed successfully");
	}

	private void performEndorsementOnDate(LocalDateTime endorsementDate) {
		performEndorsementOnDate(endorsementDate, endorsementDate);
	}

	private void issuedRefundOnDate(Dollar refundAmount, LocalDateTime refundDate) {
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Verify refund on {}", refundDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPaymentsAndTransactionsVerifier()
			.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.ISSUED)
			.setAmount(refundAmount)
			.verifyPresent();
		log.info("Refund presents in Payments & Other Transactions Table");
	}

	private void pendingRefundOnDate(Dollar refundAmount, LocalDateTime refundDate) {
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Verify refund on {}", refundDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPendingTransactionsVerifier()
			.setTransactionDate(refundDate)
			.setType(BillingConstants.BillingPendingTransactionsType.REFUND)
			.setSubtypeReason(BillingConstants.BillingPendingTransactionsSubtype.AUTOMATED_REFUND)
			.setStatus(BillingConstants.BillingPendingTransactionsStatus.PENDING)
			.setAmount(refundAmount)
			.verifyPresent();
		log.info("Refund present in Pending Transactions Table");
	}

	private void rejectRefundOnDate(LocalDateTime rejectDate) {
		TimeSetterUtil.getInstance().nextPhase(rejectDate);
		log.info("Reject refund action started on {}", rejectDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = new Dollar(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(BillingPendingTransactionsTable.AMOUNT).getValue());
		BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(BillingPendingTransactionsTable.ACTION).controls.links.get(ActionConstants.BillingPendingTransactionAction.REJECT).click();
		Page.dialogConfirmation.confirm();
		new BillingPaymentsAndTransactionsVerifier()
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DISAPPROVED)
			.setTransactionDate(rejectDate)
			.setAmount(amount.negate())
			.verifyPresent();
		log.info("Reject refund action completed successfully");
	}

	private void performEndorsementOnDate(LocalDateTime endorsementDate, LocalDateTime endorsementDueDate) {
		TimeSetterUtil.getInstance().nextPhase(endorsementDate);
		log.info("Endorsment action started");
		log.info("Endorsement date: {}", endorsementDate);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.endorse().performAndFill(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Endorsement effective %1$s for Policy %2$s", endorsementDueDate.format(DateTimeUtils.MM_DD_YYYY),
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber()));
		log.info("Endorsment action completed successfully");
	}

	private void otherAdjustmentOnDate(LocalDateTime adjustmentDate) {
		TimeSetterUtil.getInstance().nextPhase(adjustmentDate);
		log.info("Other Adjustment action started on {}", adjustmentDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		billingAccount.otherTransactions().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(adjustmentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.OTHER)
			.setAmount(
				new Dollar(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData(OtherTransactionsActionTab.class.getSimpleName()).getValue(
					BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel())))
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
			.verifyPresent();
		log.info("Other Adjustment action completed successfully");
	}

	private void manualReinstatementOnDate(LocalDateTime reinstatementDate) {
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
		log.info("Manual reinstatement action started");
		log.info("Manual reinstatement date: {}", reinstatementDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.reinstate().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Reinstatement for Policy %1$s", BillingAccountInformationHolder.getCurrentBillingAccountDetails()
			.getCurrentPolicyDetails().getPolicyNumber()));
		log.info("Manual reinstatement action completed successfully");
	}

	private void addSuspenseOnDate(LocalDateTime suspenseDate) {
		TimeSetterUtil.getInstance().nextPhase(suspenseDate);
		log.info("Add Suspense action started");
		log.info("Suspense date: {}", suspenseDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new PaymentsMaintenance().addSuspense().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		log.info("Suspense added successfully");
	}

	private void waiveFeeOnDate(LocalDateTime waiveDate) {
		TimeSetterUtil.getInstance().nextPhase(waiveDate);
		log.info("Waive action started");
		log.info("Waive date: {}", waiveDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		BillingSummaryPage.tablePaymentsOtherTransactions
			.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE)
			.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION).controls.links.get(BillingConstants.PaymentsAndOtherTransactionAction.WAIVE).click();
		BillingSummaryPage.dialogConfirmation.confirm();
		new BillingPaymentsAndTransactionsVerifier()
			.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE_WAIVED)
			.setTransactionDate(waiveDate)
			.verifyPresent();
		log.info("Waive action completed successfully");
	}
}