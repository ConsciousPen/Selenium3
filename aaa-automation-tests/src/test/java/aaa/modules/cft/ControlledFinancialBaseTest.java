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
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
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

	private ThreadLocal<List<LocalDateTime>> installments = new ThreadLocal<>();
	private ThreadLocal<String> policyNumber = ThreadLocal.withInitial(() -> StringUtils.EMPTY);
	private ThreadLocal<BillingBillsAndStatementsVerifier> billingBillsAndStatementsVerifier = ThreadLocal.withInitial(BillingBillsAndStatementsVerifier::new);
	private ThreadLocal<BillingPaymentsAndTransactionsVerifier> billingPaymentsAndTransactionsVerifier = ThreadLocal.withInitial(BillingPaymentsAndTransactionsVerifier::new);

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
		log.info("Endorsment action started");
		LocalDateTime endorsePlus2 = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		log.info("Endorsement date: " + endorsePlus2);
		TimeSetterUtil.getInstance().nextPhase(endorsePlus2.plusDays(2)); // check future dated endorsement
		performAndCheckEndorsement(endorsePlus2.plusDays(2));
		log.info("Endorsment action finished successfully");
	}


	/**
	 * Endorsement of the policy
	 * today(suite start time) + 16 day
	 */
	protected void endorsePolicyEffDatePlus16Days() {
		log.info("Endorsment action started");
		LocalDateTime endorsePlus16 = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		log.info("Endorsement date: " + endorsePlus16);
		TimeSetterUtil.getInstance().nextPhase(endorsePlus16);
		performAndCheckEndorsement(endorsePlus16);
		log.info("Endorsment action finished successfully");
	}

	/**
	 * Accept 10$ cash payment on startDate + 25 days
	 */
	protected void acceptPaymentEffDatePlus25() {
		log.info("Accept payment action started");
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		log.info("Accept payment date: " + paymentDate);
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		String expValue = getTestSpecificTD(DEFAULT_TEST_DATA_KEY)
				.getTestData(AcceptPaymentActionTab.class.getSimpleName())
				.getValue(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel());
		getBillingPaymentsAndTransactionsVerifier().getValues().clear();
		getBillingPaymentsAndTransactionsVerifier()
				.setTransactionDate(paymentDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
				.setAmount(new Dollar(expValue).negate())
				.verifyPresent();
		log.info("Accept payment action finished successfully");
	}

	/**
	 * Fully pay installment with min Due
	 */
	protected void payInstallmentWithMinDue() {
		log.info("Accept payment action started");
		billingAccount.acceptPayment().perform(getTestSpecificTD("AcceptPayment"), BillingSummaryPage.getMinimumDue());
		log.info("Accept payment action finished successfully");
	}

	/**
	 * Decline Payment on cancellation Notice generation day
	 */
	protected void decline10DollarsPayment() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(installments.get().get(1));
		log.info("Decline Payment action started");
		log.info("Decline Payment date: " + cancellationNoticeDate);
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		billingAccount.declinePayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), new Dollar(-10).toString());
		getBillingPaymentsAndTransactionsVerifier().getValues().clear();
		getBillingPaymentsAndTransactionsVerifier()
				.setTransactionDate(cancellationNoticeDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED)
				.setAmount(new Dollar(10))
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
				.verifyPresent();
		log.info("Decline payment action finished successfully");
	}

	/**
	 * Bill generation for first installment of the policy
	 */
	protected void generateFirstInstallmentBill() {
		LocalDateTime billDueDate = getTimePoints().getBillGenerationDate(installments.get().get(1));
		log.info("Installment generation started");
		log.info("Installment generation date: " + billDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		getBillingBillsAndStatementsVerifier().getValues().clear();
		getBillingBillsAndStatementsVerifier()
				.setType(BillingConstants.BillsAndStatementsType.BILL)
				.setDueDate(installments.get().get(1))
				.setMinDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue()))
				.setPastDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAST_DUE).getValue()))
				.setTotalDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue()))
				.verifyPresent();
		log.info("Installment generation finished successfully");
	}

	/**
	 * Waive fee
	 * X+16(BOA reconciliation JOBS)
	 * BAOCheckconcillationBatch orderJOB,
	 * BAOCheckconcillationBatch recieveJOB
	 */
	protected void waiveFee() {
		log.info("Waive action started");
		LocalDateTime plus16Days = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		log.info("Waive date: " + plus16Days);
		TimeSetterUtil.getInstance().nextPhase(plus16Days);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		BillingSummaryPage.tablePaymentsOtherTransactions
				.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,
						BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE)
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.ACTION)
				.controls.links.get(BillingConstants.PaymentsAndOtherTransactionAction.WAIVE).click();
		BillingSummaryPage.dialogConfirmation.confirm();
		getBillingPaymentsAndTransactionsVerifier().getValues().clear();
		getBillingPaymentsAndTransactionsVerifier()
				.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE_WAIVED)
				.setTransactionDate(plus16Days)
				.verifyPresent();
		log.info("Waive action comleted successfully");
	}

	protected void manualFutureCancellationEffDatePlus25Days() {
		log.info("Manual cancellation action started");
		LocalDateTime plus25Days = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		log.info("Manual cancellation date: " + plus25Days);
		TimeSetterUtil.getInstance().nextPhase(plus25Days);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		policy.cancel().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
	}

	protected void updatePolicyStatusForPendedCancellation() {
		log.info("Policy status update job action started");
		LocalDateTime plus25Days = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		TimeSetterUtil.getInstance().nextPhase(plus25Days.plusDays(2));
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Policy status update job finished successfully");
	}

	protected void manualReinstatement() {
		log.info("Manual reinstatement action started");
		LocalDateTime reinstatementDate = getTimePoints().getCancellationNoticeDate(installments.get().get(1));
		log.info("Manual reinstatement date: " + reinstatementDate);
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
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
	protected void automaticCancellationNotice() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(installments.get().get(1));
		log.info("Cancellation Notice action started");
		log.info("Cancellation Notice date: " + cancellationNoticeDate);
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(installments.get().get(1));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		getBillingBillsAndStatementsVerifier().getValues().clear();
		getBillingBillsAndStatementsVerifier()
				.setDueDate(cancellationDate)
				.setType(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE)
				.verifyPresent();
		log.info("Cancellation Notice action finished successfully");
	}

	/**
	 * Endorsement of the policy
	 */
	protected void automaticCancellation() {
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(installments.get().get(1));
		log.info("Cancellation action started");
		log.info("Cancellation date: " + cancellationDate);
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Cancellation action finished successfully");
	}

	/**
	 * Generate 1st EP bill
	 */
	protected void generateFirstEarnedPremiumBill() {
		LocalDateTime firstEPBillDate = getTimePoints().getEarnedPremiumBillFirst(installments.get().get(1));
		log.info("First EP bill generation started");
		log.info("First EP bill generated date: " + firstEPBillDate);
		generateAndCheckEarnedPremiumBill(firstEPBillDate);
		log.info("First EP bill generated successfully");
	}

	/**
	 * Generate 2st EP bill
	 */
	protected void generateSecondEarnedPremiumBill() {
		LocalDateTime secondEPBillDate = getTimePoints().getEarnedPremiumBillSecond(installments.get().get(1));
		log.info("Second EP bill generation started");
		log.info("Second EP bill generated date: " + secondEPBillDate);
		generateAndCheckEarnedPremiumBill(secondEPBillDate);
		log.info("Second EP bill generated successfully");
	}

	/**
	 * Generate 3st EP bill
	 */
	protected void generateThirdEarnedPremiumBill() {
		LocalDateTime thirdEPBillDate = getTimePoints().getEarnedPremiumBillThird(installments.get().get(1));
		log.info("Third EP bill generation started");
		log.info("Third EP bill generated date: " + thirdEPBillDate);
		generateAndCheckEarnedPremiumBill(thirdEPBillDate);
		log.info("Third EP bill generated successfully");
	}

	/**
	 * Generate EP write off
	 */
	protected void writeOff() {
		LocalDateTime writeOffDate = getTimePoints().getEarnedPremiumWriteOff(installments.get().get(1));
		TimeSetterUtil.getInstance().nextPhase(writeOffDate);
		log.info("EP Write off generation action started");
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().reopen();
		SearchPage.openBilling(policyNumber.get());
		getBillingPaymentsAndTransactionsVerifier().getValues().clear();
		getBillingPaymentsAndTransactionsVerifier()
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

	private void generateAndCheckEarnedPremiumBill(LocalDateTime date) {
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber.get());
		getBillingBillsAndStatementsVerifier().getValues().clear();
		getBillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(date);
	}

	private void runCFTJobs() {
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		JobUtils.executeJob(Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		JobUtils.executeJob(Jobs.policyTransactionLedgerJob);
	}

	private BillingBillsAndStatementsVerifier getBillingBillsAndStatementsVerifier() {
		return billingBillsAndStatementsVerifier.get();
	}

	private BillingPaymentsAndTransactionsVerifier getBillingPaymentsAndTransactionsVerifier() {
		return billingPaymentsAndTransactionsVerifier.get();
	}

	private void performAndCheckEndorsement(LocalDateTime endorsementDate) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber.get());
		policy.endorse().performAndFill(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.descriptionExist(String.format("Bind Endorsement effective %1$s for Policy %2$s", endorsementDate.format(DateTimeUtils.MM_DD_YYYY), policyNumber.get()));
	}

}
