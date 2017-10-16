/* Copyright ?? 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.cft;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ActivitiesAndUserNotesConstants;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang3.StringUtils;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ControlledFinancialBaseTest extends PolicyBaseTest {

	protected static final String DEFAULT_TEST_DATA_KEY = "TestData";
	protected static final String STATE_PARAM = "state";

	protected BillingAccount billingAccount = new BillingAccount();

	protected ThreadLocal<List<LocalDateTime>> installments = new ThreadLocal<>();
	protected ThreadLocal<String> policyNumber = ThreadLocal.withInitial(() -> StringUtils.EMPTY);

	/**
	 * Creating of the policy for test
	 */
	protected void createPolicyForTest() {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTestData();
		policyNumber.set(createPolicy(td));
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installments.set(BillingHelper.getInstallmentDueDates());
	}

	/**
	 * Endorsement of the policy
	 * today(suite start time) + 2 day
	 */
	protected void endorsePolicyEffDatePlus2Days() {
		LocalDateTime endorsePlus2 = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		TimeSetterUtil.getInstance().nextPhase(endorsePlus2);
		log.info("Endorsment action started");
		log.info("Endorsement date: {}", endorsePlus2);
		performAndCheckEndorsement(endorsePlus2.plusDays(2)); // future dated endorse +2 d
		log.info("Endorsment action completed successfully");
	}

	/**
	 * Endorsement of the policy
	 * today(suite start time) + 16 day
	 */
	protected void endorsePolicyEffDatePlus16Days() {
		LocalDateTime endorsePlus16 = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		TimeSetterUtil.getInstance().nextPhase(endorsePlus16);
		log.info("Endorsment action started");
		log.info("Endorsement date: {}", endorsePlus16);
		performAndCheckEndorsement(endorsePlus16);
		log.info("Endorsment action completed successfully");
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
		SearchPage.openPolicy(policyNumber.get());
		policy.endorse().performAndFill(getTestSpecificTD("TestData_OOS"));
		String endorseDate = getTestSpecificTD("TestData_OOS").getValue("EndorsementActionTab", "Endorsement Date");
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Endorsement effective %1$s for Policy %2$s", endorseDate, policyNumber.get()));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
		policy.rollOn().perform(false, false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("OOS Endorsment action completed successfully");
	}

	/**
	 * Accept 10$ cash payment on startDate + 25 days
	 */
	protected void acceptPaymentEffDatePlus25() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept payment action started");
		log.info("Accept payment date: {}", paymentDate);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
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

	/**
	 * Accept Min Due payment on DD1 + 30 days
	 */
	protected void acceptMinDuePaymentDD1plus30() {
		LocalDateTime paymentDate = getTimePoints().getBillDueDate(installments.get().get(1)).plusDays(30);
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept payment action started");
		log.info("Accept payment date: {}", paymentDate);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		Dollar minDue = new Dollar(BillingSummaryPage.tableBillsStatements
				.getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE,BillingConstants.BillsAndStatementsType.BILL)
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
	 * Fully pay installment with min Due
	 */
	protected void payInstallmentWithMinDue() {
		log.info("Accept payment action started");
		billingAccount.acceptPayment().perform(getTestSpecificTD("AcceptPayment"), BillingSummaryPage.getMinimumDue());
		log.info("Accept payment action completed successfully");
	}

	/**
	 * Decline Payment on cancellation Notice generation day
	 */
	protected void decline10DollarsPaymentOnCancellationNoticeDate() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(installments.get().get(1));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		log.info("Decline Payment action started");
		log.info("Decline Payment date: {}", cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
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
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(installments.get().get(1));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		log.info("Other Adjustment action started");
		log.info("Other Adjustment date: {}", cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
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
		LocalDateTime billDueDate = getTimePoints().getBillGenerationDate(installments.get().get(installmentNumber));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		log.info("{} Installment bill generation started", installmentNumber);
		log.info("{} Installment bill generation date: {}", installmentNumber, billDueDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		new BillingBillsAndStatementsVerifier()
			.setType(BillingConstants.BillsAndStatementsType.BILL)
			.setDueDate(installments.get().get(installmentNumber))
			.setMinDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue()))
			.setPastDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAST_DUE).getValue()))
			.setTotalDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue()))
			.verifyPresent();
		log.info("{} Installment bill generation completed successfully", installmentNumber);
	}

	protected void splitPolicyOnFirstDueDate() {
		TimeSetterUtil.getInstance().nextPhase(installments.get().get(1));
		log.info("Split policy action started");
		log.info("Split policy action date: {}", installments.get().get(1));
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		policy.endorse().performAndFill(getTestSpecificTD("Endorsement"));
		//split policy
		policy.policySplit().perform(getTestSpecificTD("SplitTestData"));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION, String.format("Policy %1$s has been split to a new quote", policyNumber.get())).verify.present();
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
		SearchPage.openBilling(policyNumber.get());
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
		SearchPage.openPolicy(policyNumber.get());
		policy.cancel().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
		log.info("Manual cancellation action completed successfully");
	}

	protected void updatePolicyStatusForPendedCancellation() {
		LocalDateTime plus25Days = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		TimeSetterUtil.getInstance().nextPhase(plus25Days.plusDays(2));
		log.info("Policy status update job action started");
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Policy status update job completed successfully");
	}

	protected void manualReinstatement() {
		LocalDateTime reinstatementDate = getTimePoints().getCancellationNoticeDate(installments.get().get(1));
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
		log.info("Manual reinstatement action started");
		log.info("Manual reinstatement date: {}", reinstatementDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		policy.reinstate().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Reinstatement for Policy %1$s", policyNumber.get()));
		log.info("Manual reinstatement action completed successfully");
	}

	/**
	 * Cancellation Notice for the policy
	 */
	protected void automaticCancellationNotice(int installmentNumber) {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(installments.get().get(installmentNumber));
		LocalDateTime expCancellationDate = getTimePoints().getCancellationTransactionDate(installments.get().get(installmentNumber));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		log.info("Cancellation Notice action started");
		log.info("Cancellation Notice date: {}", cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
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
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(installments.get().get(installmentNumber));
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		log.info("Cancellation action started");
		log.info("Cancellation date: {}", cancellationDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Cancellation action completed successfully");
	}

	/**
	 * Generate 1st EP bill
	 */
	protected void generateFirstEarnedPremiumBill(int installmentNumber) {
		LocalDateTime firstEPBillDate = getTimePoints().getEarnedPremiumBillFirst(installments.get().get(installmentNumber));
		log.info("First EP bill generation started");
		log.info("First EP bill generated date: {}", firstEPBillDate);
		generateAndCheckEarnedPremiumBill(firstEPBillDate);
		log.info("First EP bill generated successfully");
	}

	/**
	 * Generate 2st EP bill
	 */
	protected void generateSecondEarnedPremiumBill(int installmentNumber) {
		LocalDateTime secondEPBillDate = getTimePoints().getEarnedPremiumBillSecond(installments.get().get(installmentNumber));
		log.info("Second EP bill generation started");
		log.info("Second EP bill generated date: {}", secondEPBillDate);
		generateAndCheckEarnedPremiumBill(secondEPBillDate);
		log.info("Second EP bill generated successfully");
	}

	/**
	 * Generate 3st EP bill
	 */
	protected void generateThirdEarnedPremiumBill(int installmentNumber) {
		LocalDateTime thirdEPBillDate = getTimePoints().getEarnedPremiumBillThird(installments.get().get(installmentNumber));
		log.info("Third EP bill generation started");
		log.info("Third EP bill generated date: {}", thirdEPBillDate);
		generateAndCheckEarnedPremiumBill(thirdEPBillDate);
		log.info("Third EP bill generated successfully");
	}

	/**
	 * Generate EP write off
	 */
	protected void writeOff(int installmentNumber) {
		LocalDateTime writeOffDate = getTimePoints().getEarnedPremiumWriteOff(installments.get().get(installmentNumber));
		TimeSetterUtil.getInstance().nextPhase(writeOffDate);
		log.info("EP Write off generation action started");
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		new BillingPaymentsAndTransactionsVerifier()
			.setTransactionDate(writeOffDate)
			.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
			.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF)
			.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
			.verifyPresent();
		log.info("EP Write off generated successfully");
	}

	protected TestData getPolicyTestData() {
		throw new IstfException("Please override method in appropriate child class with relevant test data preparation");
	}

	protected void runCFTJobs(){
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		JobUtils.executeJob(Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		JobUtils.executeJob(Jobs.policyTransactionLedgerJob);
	}

	private void generateAndCheckEarnedPremiumBill(LocalDateTime date) {
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(date);
	}

	private void performAndCheckEndorsement(LocalDateTime endorsementDueDate) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		policy.endorse().performAndFill(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Endorsement effective %1$s for Policy %2$s", endorsementDueDate.format(DateTimeUtils.MM_DD_YYYY),
			policyNumber.get()));
	}

}
