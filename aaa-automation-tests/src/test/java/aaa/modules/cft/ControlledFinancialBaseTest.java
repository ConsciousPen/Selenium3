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
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.ActivitiesAndUserNotesConstants;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ProductConstants;
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
	 * Accept TotalDue + 600$ cash payment on first bill generation date
	 */
	protected void acceptTotalDuePlus600BillGenDate() {
		LocalDateTime paymentDate = getTimePoints().getBillGenerationDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		log.info("Accept payment action started");
		log.info("Accept payment date: {}", paymentDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = new Dollar(BillingSummaryPage.tableBillsStatements
			.getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE, BillingConstants.BillsAndStatementsType.BILL)
			.getCell(BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE).getValue()).add(600);
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), amount);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(paymentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
			.setAmount(amount.negate())
			.verifyPresent();
		log.info("Accept payment action completed successfully");
	}

	/**
	 * Accept TotalDue + 600$ cash payment on start date + 16 days
	 */

	protected void acceptTotalDuePlus600StartDatePlus16() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		log.info("Accept payment action started on {}", paymentDate);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = new Dollar(BillingSummaryPage.tableBillsStatements
			.getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE, BillingConstants.BillsAndStatementsType.BILL)
			.getCell(BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE).getValue()).add(600);
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), amount);
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(paymentDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
			.setAmount(amount.negate())
			.verifyPresent();
		log.info("Accept payment action completed successfully");
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

	protected void refundPaymentAndApproveStartDatePlus25() {
		refundPaymentStartDatePlus25();
		Dollar refundAmount = new Dollar(600);
		billingAccount.approveRefund().perform(refundAmount);
		log.info("Approve refund action completed successfully");
	}

	protected void refundPaymentStartDatePlus25() {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25).with(DateTimeUtils.closestFutureWorkingDay);
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Refund payment action started on {}", refundDate);
		mainApp().reopen();
		Dollar refundAmount = new Dollar(600);
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		billingAccount.refund().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), refundAmount);
		log.info("Refund payment action completed successfully");
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
	 * decrease for 30$  (i.e. -30)
	 */
	protected void otherAdjustmentOnCancellationNoticeDate() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		log.info("Other Adjustment action started");
		log.info("Other Adjustment date: {}", cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		billingAccount.otherTransactions().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(cancellationNoticeDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.OTHER)
			.setAmount(
				new Dollar(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData(OtherTransactionsActionTab.class.getSimpleName()).getValue(
					BillingAccountMetaData.OtherTransactionsActionTab.AMOUNT.getLabel())))
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
			.verifyPresent();
		log.info("Decline payment action completed successfully");
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
		log.info("{} Installment bill generation started", installmentNumber);
		log.info("{} Installment bill generation date: {}", installmentNumber, billDueDate);
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

	/**
	 * Waive fee
	 * X+16(BOA reconciliation JOBS)
	 * BAOCheckconcillationBatch orderJOB,
	 * BAOCheckconcillationBatch recieveJOB
	 */
	protected void waiveFee() {
		LocalDateTime plus16Days = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		TimeSetterUtil.getInstance().nextPhase(plus16Days);
		log.info("Waive action started");
		log.info("Waive date: {}", plus16Days);
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
			.setTransactionDate(plus16Days)
			.verifyPresent();
		log.info("Waive action completed successfully");
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
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber), getPolicyType(), getState());
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
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber), getPolicyType(), getState());
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
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber), getPolicyType(), getState());
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
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber), getPolicyType(), getState());
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
			BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber), getPolicyType(), getState());
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

	private void performEndorsementOnDate(LocalDateTime endorsementDate) {
		performEndorsementOnDate(endorsementDate, endorsementDate);
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
}
