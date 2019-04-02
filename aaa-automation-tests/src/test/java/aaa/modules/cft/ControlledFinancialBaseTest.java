/* Copyright ?? 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.cft;

import aaa.admin.modules.reports.operationalreports.OperationalReport;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints;
import aaa.helpers.billing.*;
import aaa.helpers.conversion.ConversionPolicyData;
import aaa.helpers.conversion.ConversionUtils;
import aaa.helpers.conversion.MaigConversionData;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ActionConstants;
import aaa.main.enums.ActivitiesAndUserNotesConstants;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.PaymentsMaintenanceMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.OtherTransactionsActionTab;
import aaa.main.modules.billing.paymentsmaintenance.PaymentsMaintenance;
import aaa.main.modules.billing.paymentsmaintenance.actiontabs.ReverseSuspenseActionTab;
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
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class ControlledFinancialBaseTest extends PolicyBaseTest {

	protected static final String DEFAULT_TEST_DATA_KEY = "TestData";
	protected static final String STATE_PARAM = "state";
	protected static final String SOURCE_DIR = "/home/mp2/pas/sit/FIN_E_EXGPAS_PSFTGL_7000_D/outbound";
	protected static final String CFT_VALIDATION_DIRECTORY = System.getProperty("user.dir") + "/src/test/resources/cft/";

	protected BillingAccount billingAccount = new BillingAccount();
	protected OperationalReport operationalReport = new OperationalReport();

	/**
	 * Accept cash payment on startDate + 25 days
	 */
	protected void acceptPaymentOnStartDatePlus25() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		acceptManualPaymentOnDate(paymentDate);
	}

	/**
	 * Accept cash payment on startDate + 2 days
	 */
	protected void acceptPaymentOnStartDatePlus2() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		acceptManualPaymentOnDate(paymentDate);
	}

	/**
	 * Accept Total Due payment
	 * payment date = current application date
	 */
	protected void acceptTotalDuePayment() {
		log.info("Accept payment action started");
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), BillingSummaryPage.getTotalDue());
		log.info("Accept payment action completed successfully");
	}

	/**
	 * Accept TotalDue + Over payment on first Bill Generation date
	 */
	protected void acceptTotalDuePlusOverpaymentOnBillGenDate(Dollar overpayment) {
		LocalDateTime paymentDate = getTimePoints().getBillGenerationDate(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		acceptTotalDuePlusOverpaymentOnDate(overpayment, paymentDate);
	}

	/**
	 * Accept TotalDue + Over payment on policy Effective date - 20 days (Effective date != Start date)
	 */
	protected void acceptTotalDuePlusOverpaymentOnEffDateMinus20(Dollar overpayment) {
		LocalDateTime paymentDate = getTimePoints().getBillGenerationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate());
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
		log.info("Policy {} for billing account {} is created", BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber(), BillingAccountInformationHolder.getCurrentBillingAccountDetails().getBillingAccountNumber());
	}

	protected void declineSuspensePaymentOnCancellationDate() {
		LocalDateTime declineDate = getTimePoints().getCancellationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(declineDate);
		log.info("Decline Suspense Payment action started on {}", declineDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		LocalDateTime suspenseDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		HashMap<String, String> map = new HashMap<>();
		map.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TRANSACTION_DATE, suspenseDate.format(DateTimeUtils.MM_DD_YYYY));
		map.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.PAYMENT);
		map.put(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SUSPENSE);

		billingAccount.declinePayment().perform(testDataManager.billingAccount.getTestData("DeclinePayment", "TestData_FeeNoRestriction"), map);
		new BillingPaymentsAndTransactionsVerifier()
				.setTransactionDate(suspenseDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.SUSPENSE)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.DECLINED)
				.verifyPresent();
		log.info("Decline Suspense Payment action completed successfully");
	}

	/**
	 * OOS Endorsement on First Earned Premium bill date
	 * Endorsement effective date is 2 days before Cancellation Date
	 */
	protected void endorseOOSPolicyOnFirstEPBillDate(String keyPath) {
		LocalDateTime firstEPBillDate =
				getTimePoints().getEarnedPremiumBillFirst(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(firstEPBillDate);
		log.info("OOS Endorsement action started on {}", firstEPBillDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openPolicy(policyNumber);
		String endorseDate = getTimePoints().getCancellationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1))
				.minusDays(2).format(DateTimeUtils.MM_DD_YYYY);
		policy.endorse().performAndFill(getTestSpecificTD("TestData_OOSEndorsement").adjust(keyPath, endorseDate));
		assertSoftly(softly -> {
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
			policy.rollOn().perform(true, false);
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION,
					String.format("Bind Endorsement effective %1$s for Policy %2$s", endorseDate, policyNumber))).exists();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		});
		log.info("OOS Endorsement action completed successfully");
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar amount = new Dollar(BillingSummaryPage.getTotalDue());
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), amount);
		new BillingPaymentsAndTransactionsVerifier()
				.setTransactionDate(firstEPBillDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
				.setAmount(amount.negate())
				.verifyPresent();
	}

	/**
	 * OOS endorsement
	 * on start date + 16 days
	 */
	protected void endorseOOSPolicyOnStartDatePlus16(String[] endorsementEffDateDataKeys) {
		LocalDateTime date = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		TimeSetterUtil.getInstance().nextPhase(date);
		log.info("OOS Endorsment action started on {}", date);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().performAndFill(getTestSpecificTD("TestData_OOS"));
		String endorsementDate = getTestSpecificTD("TestData_OOS").getValue(endorsementEffDateDataKeys);
		assertSoftly(softly -> {
			softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION,
					String.format("Initiate Endorsement effective %1$s", endorsementDate))).exists();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
			policy.rollOn().perform(false, true);
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		});
		log.info("OOS Endorsment action completed successfully");
	}

	/**
	 * Endorsement
	 * On start date + 2 days
	 */
	protected void endorsePolicyOnStartDatePlus2() {
		LocalDateTime endorsementDate = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		performEndorsementOnDate(endorsementDate);
	}

	/**
	 * Endorsement
	 * On start date + 16 days
	 */
	protected void endorsePolicyOnStartDatePlus16() {
		LocalDateTime endorsementDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		performEndorsementOnDate(endorsementDate);
	}

	protected void endorsePolicyCancellationDate() {
		LocalDateTime endorsementDate = getTimePoints().getCancellationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		performEndorsementOnDate(endorsementDate);
	}

	protected void endorsePolicyOnCancellationNoticeDate() {
		LocalDateTime endorsementDate = getTimePoints().getCancellationNoticeDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		performEndorsementOnDate(endorsementDate);
	}

	/**
	 * Future dated endorsement
	 * on Cancellation Notice Date
	 */
	protected void futureEndorsePolicyOnCancellationNoticeDate(String[] endorsementEffDateDataKeys) {
		LocalDateTime endorsementDate = getTimePoints().getCancellationNoticeDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		performFutureEndorsementOnDate(endorsementDate, endorsementEffDateDataKeys);
	}

	/**
	 * Future dated endorsement
	 * on start date plus 2 days
	 */
	protected void futureEndorsePolicyOnStartDatePlus2(String[] endorsementEffDateDataKeys) {
		LocalDateTime endorsementDate = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		performFutureEndorsementOnDate(endorsementDate, endorsementEffDateDataKeys);

	}

	/**
	 * Bill generation on first Bill Generation date
	 */
	protected void generateInstallmentBill() {
		generateInstallmentBillDueDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate().plusMonths(1));
	}

	/**
	 * Bill generation for provided installment of the policy
	 *
	 * @param installmentNumber number of the installment
	 */
	protected void generateInstallmentBill(int installmentNumber) {
		generateInstallmentBillDueDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
	}

	protected void acceptTotalDuePlusOverpaymentOnRenewCustomerDeclineDate(Dollar overpayment) {
		LocalDateTime paymentDate = getTimePoints().getRenewCustomerDeclineDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept overpayment action started on {}", paymentDate);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = new Dollar(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE).getValue()).add(overpayment);
		billingAccount.acceptPayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), amount);
		new BillingPaymentsAndTransactionsVerifier()
				.setTransactionDate(paymentDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
				.setAmount(amount.negate())
				.verifyPresent();
		log.info("Accept overpayment action completed successfully");
	}

	/**
	 * Accept Min Due payment on DD1 + 30 days
	 */
	protected void acceptMinDuePaymentDD1plus30() {
		LocalDateTime paymentDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1).plusDays(30);
		acceptMinDuePaymentOnDate(paymentDate);
	}

	/**
	 * Accept Min Due payment on Start Date + 25 days
	 */
	protected void acceptMinDuePaymentOnStartDatePlus25() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		acceptMinDuePaymentOnDate(paymentDate);
	}

	/**
	 * Accept Min Due payment on Update Policy Status Date (Expiration date + 1 day)
	 */
	protected void acceptMinDuePaymentOnUpdatePolicyStatusDate() {
		LocalDateTime paymentDate = getTimePoints().getUpdatePolicyStatusDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		acceptMinDuePaymentOnDate(paymentDate);
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
		mainApp().open();
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
		mainApp().open();
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

	protected void approveRefundTotalPremiumOnDD1() {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusMonths(1);
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Refund action started on {}", refundDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS).getValue())
				.isEqualTo(BillingConstants.BillingAccountPoliciesPolicyStatus.POLICY_CANCELLED);
		Dollar refundAmount = BillingSummaryPage.getTotalPaid();
		Map<String, String> query = new HashMap<>();
		query.put(BillingConstants.BillingPendingTransactionsTable.TYPE, BillingConstants.BillingPendingTransactionsType.REFUND);
		query.put(BillingConstants.BillingPendingTransactionsTable.SUBTYPE, BillingConstants.BillingPendingTransactionsSubtype.AUTOMATED_REFUND);
		query.put(BillingConstants.BillingPendingTransactionsTable.REASON, BillingConstants.BillingPendingTransactionsReason.OVERPAYMENT);
		query.put(BillingConstants.BillingPendingTransactionsTable.AMOUNT, refundAmount.toString());
		query.put(BillingConstants.BillingPendingTransactionsTable.STATUS, BillingConstants.BillingPendingTransactionsStatus.PENDING);
		if (BillingSummaryPage.tablePendingTransactions.getRow(query).isPresent()) {
			billingAccount.approveRefund().perform(refundAmount);
		}
		new BillingPaymentsAndTransactionsVerifier()
				.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setReason(BillingConstants.PaymentsAndOtherTransactionReason.OVERPAYMENT)
				.verifyPresent();
		log.info("Refund action completed successfully");
	}

	protected void approveRefundOnRenewCustomerDeclineDatePlusRefundDate() {
		LocalDateTime refundDate = getTimePoints().getRefundDate(
				getTimePoints().getRenewCustomerDeclineDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate()));
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Approve refund action started on {}", refundDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		//billingAccount.approveRefund().perform(1);
		new BillingPaymentsAndTransactionsVerifier()
				.setTransactionDate(refundDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED)
				.verifyPresent();
		log.info("Approve refund action completed successfully");
	}

	protected void pendingRefundOnStartDatePlus16(Dollar refundAmount) {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		pendingRefundOnDate(refundAmount, refundDate);
	}

	protected void automatedRefundOnStartDatePlus16(Dollar refundAmount) {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		automatedRefundOnDate(refundAmount, refundDate);
	}

	protected void automatedRefundOnStartDatePlus25(Dollar refundAmount) {
		LocalDateTime refundDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		automatedRefundOnDate(refundAmount, refundDate);
	}

	protected void automatedRefundOnRefundDate() {
		LocalDateTime refundDate = getTimePoints().getRefundDate(
				getTimePoints().getRenewCustomerDeclineDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate()));
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Verify refund on {}", refundDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPaymentsAndTransactionsVerifier()
				.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED)
				.verifyPresent();
		log.info("Automated refund approved on {}", refundDate);
	}

	protected void voidRefundOnStartDatePlus25() {
		LocalDateTime voidDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25).with(DateTimeUtils.closestFutureWorkingDay);
		TimeSetterUtil.getInstance().nextPhase(voidDate);
		log.info("Void refund action started on {}", voidDate);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE,
				BillingConstants.PaymentsAndOtherTransactionType.REFUND).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE, BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.getCell(BillingConstants.BillingPendingTransactionsTable.ACTION).controls.links.get(ActionConstants.BillingPaymentsAndOtherTransactionAction.VOID).click();
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
	protected void declinePaymentOnCancellationNoticeDate() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		log.info("Decline Payment action started on {}", cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = new Dollar(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getTestData(AcceptPaymentActionTab.class.getSimpleName())
				.getValue(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel()));
		billingAccount.declinePayment().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), amount.negate().toString());
		new BillingPaymentsAndTransactionsVerifier()
				.setTransactionDate(cancellationNoticeDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED)
				.setAmount(amount)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
				.verifyPresent();
		log.info("Decline payment action completed successfully");
	}

	/**
	 * Other Adjustment on cancellation Notice generation day, calculated from one month after policy effective date
	 * Adjustment amount defined in TestData
	 */
	protected void otherAdjustmentOnCancellationNoticeDate() {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate().plusMonths(1));
		otherAdjustmentOnDate(cancellationNoticeDate);
	}

	/**
	 * Other Adjustment on Cancellation day, calculated from one month after policy effective date
	 * Adjustment amount defined in TestData
	 */
	protected void otherAdjustmentOnCancellationDate() {
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate().plusMonths(1));
		otherAdjustmentOnDate(cancellationDate);
	}

	protected void splitPolicyOnFirstDueDate() {
		TimeSetterUtil.getInstance().nextPhase(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		log.info("Split policy action started");
		log.info("Split policy action date: {}", BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().performAndFill(getTestSpecificTD("Endorsement"));
		// split policy
		policy.policySplit().perform(getTestSpecificTD("SplitTestData"));
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION,
				String.format("Policy %1$s has been split to a new quote", policyNumber))).exists();
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

	protected void maigConversionOnStartDatePlus3(String state) {
		LocalDateTime conversionDate = TimeSetterUtil.getInstance().getStartTime().plusDays(3);
		log.info("Conversion started on {}", conversionDate);
		TimeSetterUtil.getInstance().nextPhase(conversionDate);
		LocalDateTime effDate = getTimePoints().getConversionEffectiveDate();
		List<String> maigStates = new ArrayList<>(Arrays.asList(Constants.States.VA, Constants.States.DE, Constants.States.PA, Constants.States.MD, Constants.States.NJ));
		assertThat(maigStates).as("Conversion file for %s state is available", state).contains(state);
		ConversionPolicyData data = new MaigConversionData(String.format("%d", maigStates.indexOf(state) + 1) + ".xml", effDate);
		String policyN = ConversionUtils.importPolicy(data);
		mainApp().open();
		SearchPage.openPolicy(policyN);
		policy.dataGather().start();
		policy.getDefaultView().fill(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath("GeneralTab", "NamedInsuredInformation[0]", "Base Date"),
				effDate.format(DateTimeUtils.MM_DD_YYYY)));
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingAccountInformationHolder.addBillingAccountDetails(
				new BillingAccountDetails.Builder()
						.setBillingAccountNumber(BillingSummaryPage.labelBillingAccountNumber.getValue())
						.addPolicyDetails(new PolicyDetails.Builder()
								.setPolicyNumber(policyN)
								.setPolicyEffectiveDate(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(3).getValue(), DateTimeUtils.MM_DD_YYYY))
								.setPolicyExpirationDate(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(3).getValue(), DateTimeUtils.MM_DD_YYYY).plusYears(1))
								.build())
						.build());

		log.info("Conversion completed successfully");
	}

	protected void manualFutureCancellationOnStartDatePlus25() {
		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		manualFutureCancellationOnDate(cancellationDate);
	}

	protected void manualCancellationOnDD1Plus5() {
		LocalDateTime cancellationDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1).plusDays(5);
		manualCancellationOnDate(cancellationDate);
	}

	protected void manualRenewalEntryOnStartDate() {
		LocalDateTime effDate = getTimePoints().getEffectiveDateForTimePoint(TimeSetterUtil.getInstance().getCurrentTime(), TimePoints.TimepointsList.RENEW_GENERATE_PREVIEW);
		if ("CT".equals(getState())){
			effDate = TimeSetterUtil.getInstance().getPhaseStartTime().plusDays(56);
		}
		mainApp().open();
		createCustomerIndividual();
		TestData policyTd = getConversionPolicyDefaultTD();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd(), effDate);
		getPolicyType().get().getDefaultView().fill(policyTd);
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingAccountInformationHolder.addBillingAccountDetails(
				new BillingAccountDetails.Builder()
						.setBillingAccountNumber(BillingSummaryPage.labelBillingAccountNumber.getValue())
						.addPolicyDetails(new PolicyDetails.Builder()
								.setPolicyNumber(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(1).getValue())
								.setPolicyEffectiveDate(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(3).getValue(), DateTimeUtils.MM_DD_YYYY))
								.setPolicyExpirationDate(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(3).getValue(), DateTimeUtils.MM_DD_YYYY)
										.plusYears(1))
								.build())
						.build());
		log.info("Policy expirition date {}", BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
	}

	protected void flatCancellationOnStartDatePlus16() {
		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		manualCancellationOnDate(cancellationDate);
	}

	protected void flatFutureCancellationOnDD1Minus20() {
		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().getStartTime().plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay);
		manualFutureCancellationOnDate(cancellationDate);
	}

	protected void flatOOSCancellationStartDatePlus16() {
		LocalDateTime cancellationDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		log.info("Out of sequence cancellation action started on {}", cancellationDate);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.cancel().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Manual cancellation action completed successfully");
	}

	protected void rewritePolicyOnCancellationDate() {
		LocalDateTime cDate = getTimePoints().getCancellationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(cDate);

		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
		String rewritePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("TEST: Rewriting Policy #" + rewritePolicyNumber);
		policy.dataGather().start();
		policy.getDefaultView().fill(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void updatePolicyStatusForPendedCancellation() {
		LocalDateTime plus25Days = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		TimeSetterUtil.getInstance().nextPhase(plus25Days.plusDays(2));
		log.info("Policy status update job action started");
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Policy status update job completed successfully");
	}

	protected void manualReinstatement() {
		LocalDateTime reinstatementDate = getTimePoints().getCancellationNoticeDate(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
		log.info("Manual reinstatement action started on {}", reinstatementDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openPolicy(policyNumber);
		policy.reinstate().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION,
				String.format("Bind Reinstatement for Policy %1$s", policyNumber))).exists();
		log.info("Manual reinstatement action completed successfully");
	}

	protected void manualReinstatementOnStartDatePlus25() {
		LocalDateTime reinstatementDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		manualReinstatementOnDate(reinstatementDate);
	}

	/**
	 * Policy Cancellation due DD1 date (i.e. Policy Effective Date + one month)
	 */
	protected void automaticCancellation() {
		automaticCancellationDueInsallmentDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate().plusMonths(1));
	}

	/**
	 * Policy Cancellation for provided installment of the policy
	 *
	 * @param installmentNumber number of the installment
	 */
	protected void automaticCancellation(int installmentNumber) {
		automaticCancellationDueInsallmentDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
	}

	/**
	 * Cancellation Notice due DD1 date (i.e. Policy Effective Date + one month)
	 */
	protected void automaticCancellationNotice() {
		automaticCancellationNoticeDueInsallmentDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate().plusMonths(1));
	}

	/**
	 * Cancellation Notice for provided installment of the policy
	 *
	 * @param installmentNumber number of the installment
	 */
	protected void automaticCancellationNotice(int installmentNumber) {
		automaticCancellationNoticeDueInsallmentDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber));
	}

	protected void generateCollectionOnEPWriteOffDate() {
		LocalDateTime collectionDate = getTimePoints().getEarnedPremiumWriteOff(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1));
		TimeSetterUtil.getInstance().nextPhase(collectionDate);
		log.info("Collection feed file generation started on {}", collectionDate);
		mainApp().open();
		String policyNum = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openBilling(policyNum);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		Dollar minDue = BillingSummaryPage.getMinimumDue();

		if (totalDue.moreThan(new Dollar(100))) {
			File remitanceFile = RemittancePaymentsHelper.createRemittanceFile(getState(), policyNum, minDue, BillingConstants.ExternalPaymentSystem.REGONLN);
			RemittancePaymentsHelper.copyRemittanceFileToServer(remitanceFile);
			log.info("Collection feed file moved successfully");
			JobUtils.executeJob(Jobs.aaaRemittanceFeedAsyncBatchReceiveJob);
			mainApp().open();
			SearchPage.openBilling(policyNum);
			new BillingPaymentsAndTransactionsVerifier()
					.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
					.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REGULUS_ONLINE)
					.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.ISSUED)
					.setAmount(minDue.negate())
					.verifyPresent();
			log.info("Collection transaction generated successfully");
		} else {
			writeOffOnDate(collectionDate);
			log.info("As total due < 100$, Collection not generated but write Off is generated");
		}
	}

	/**
	 * Generate 1st EP bill due DD1 date (i.e. Start Date + one month)
	 */
	protected void generateFirstEarnedPremiumBill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillFirst(TimeSetterUtil.getInstance().getStartTime().plusMonths(1)));
	}

	/**
	 * Generate 1st EP bill for provided installment of the policy
	 *
	 * @param installmentNumber number of the installment
	 */
	protected void generateFirstEarnedPremiumBill(int installmentNumber) {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillFirst(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber)));
	}

	/**
	 * Generate 2nd EP bill due DD1 date (i.e. Start Date + one month)
	 */
	protected void generateSecondEarnedPremiumBill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillSecond(TimeSetterUtil.getInstance().getStartTime().plusMonths(1)));
	}

	/**
	 * Generate 2nd EP bill for provided installment of the policy
	 *
	 * @param installmentNumber number of the installment
	 */
	protected void generateSecondEarnedPremiumBill(int installmentNumber) {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillSecond(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber)));
	}

	/**
	 * Generate 3rd EP bill due DD1 date (i.e. Policy Effective date plus one month)
	 */
	protected void generateThirdEarnedPremiumBill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillThird(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate().plusMonths(1)));
	}

	/**
	 * Generate 3rd EP bill for provided installment of the policy
	 *
	 * @param installmentNumber number of the installment
	 */
	protected void generateThirdEarnedPremiumBill(int installmentNumber) {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillThird(
				BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber)));
	}

	/**
	 * Generate EP write off due DD1 date (i.e. Policy Effective date plus one month)
	 */
	protected void writeOff() {
		writeOffOnDate(getTimePoints().getEarnedPremiumWriteOff(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyEffDate().plusMonths(1)));
	}

	/**
	 * Generate EP write off for provided installment of the policy
	 *
	 * @param installmentNumber number of the installment
	 */
	protected void writeOff(int installmentNumber) {
		writeOffOnDate(getTimePoints().getEarnedPremiumWriteOff(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(installmentNumber)));
	}

	protected void addSuspenseOnStartDatePlus2() {
		LocalDateTime suspenseDate = TimeSetterUtil.getInstance().getStartTime().plusDays(2);
		addSuspenseOnDate(suspenseDate);
	}

	protected void addSuspenseOnStartDatePlus25() {
		LocalDateTime suspenseDate = TimeSetterUtil.getInstance().getStartTime().plusDays(25);
		addSuspenseOnDate(suspenseDate);
	}

	protected void clearSuspenseOnStartDatePlus16() {
		LocalDateTime suspenseDate = TimeSetterUtil.getInstance().getStartTime().plusDays(16);
		TimeSetterUtil.getInstance().nextPhase(suspenseDate);
		log.info("Clear Suspense action started on {}", suspenseDate);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openBilling(policyNumber);
		new PaymentsMaintenance().clearSuspense().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), policyNumber, policyNumber);

		log.info("Suspense cleared successfully");
	}

	protected void refundSuspenseOnDD1plus5() {
		LocalDateTime refundDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getInstallments().get(1).plusDays(5);
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Refund Suspense action started on {}", refundDate);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openBilling(policyNumber);
		BillingSummaryPage.buttonPaymentsBillingMaintenance.click();
		PaymentsAndBillingMaintenancePage.buttonClearSuspense.click();
		new SearchSuspenseActionTab().fillTab(getTestSpecificTD(DEFAULT_TEST_DATA_KEY).adjust(
				TestData.makeKeyPath(PaymentsMaintenanceMetaData.SearchSuspenseActionTab.class.getSimpleName(), PaymentsMaintenanceMetaData.SearchSuspenseActionTab.SUSPENSE_REFERENCE.getLabel()),
				policyNumber));
		SearchSuspenseActionTab.buttonSearch.click();
		SearchSuspenseActionTab.tableSuspenseSearchResults.getRow(1).getCell(BillingConstants.BillingSuspenseSearchResultsTable.ACTION).controls.links.get(ActionConstants.REVERSE).click();
		new ReverseSuspenseActionTab().fillTab(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		Tab.buttonOk.click();
		log.info("Suspense refunded successfully");
	}

	protected void generateRenewalImage() {
		LocalDateTime renewalImageDate = getTimePoints().getRenewImageGenerationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		TimeSetterUtil.getInstance().nextPhase(renewalImageDate);
		log.info("Renewal image generation started on {}", renewalImageDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.DATA_GATHERING).verify(1);
		log.info("Renewal image generated successfully");
	}

	protected void generateRenewalOffer() {
		LocalDateTime renewalOfferDate = getTimePoints().getRenewOfferGenerationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		TimeSetterUtil.getInstance().nextPhase(renewalOfferDate);
		log.info("Renewal offer generation started on {}", renewalOfferDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		if (PolicySummaryPage.buttonRenewals.isPresent()) {
			PolicySummaryPage.buttonRenewals.click();
		}
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		log.info("Renewal offer generated successfully");
	}

	protected void generateRenewalOfferBill() {
		LocalDateTime policyExpDate = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate();
		LocalDateTime renewalOfferBillDate = getTimePoints().getBillGenerationDate(policyExpDate);
		TimeSetterUtil.getInstance().nextPhase(renewalOfferBillDate);
		log.info("Renewal offer bill generation started on {}", renewalOfferBillDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingBillsAndStatementsVerifier()
				.setDueDate(policyExpDate)
				.setMinDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue()))
				.setTotalDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue()))
				.verifyPresent();
		log.info("Renewal offer bill generated successfully");
	}

	protected void verifyRenewCustomerDecline() {
		LocalDateTime declineDate = getTimePoints().getRenewCustomerDeclineDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		TimeSetterUtil.getInstance().nextPhase(declineDate);
		log.info("Verify renew customer decline on {}", declineDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.POLICY_STATUS))
				.hasValue(BillingConstants.BillingAccountPoliciesPolicyStatus.CUSTOMER_DECLINED);
		log.info("Renew is declined");
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

	protected void verifyPolicyActiveOnDD1Minus20() {
		LocalDateTime date = TimeSetterUtil.getInstance().getStartTime().plusMonths(1).minusDays(20);
		verifyPolicyStatusOnDate(date, ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void verifyPolicyActiveOnUpdatePolicyStatusDate() {
		LocalDateTime date = getTimePoints().getUpdatePolicyStatusDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		verifyPolicyStatusOnDate(date, ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void verifyPolicyExpiredOnRenewCustomerDeclineDate() {
		LocalDateTime date = getTimePoints().getRenewCustomerDeclineDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		verifyPolicyStatusOnDate(date, ProductConstants.PolicyStatus.POLICY_EXPIRED);
	}

	protected void verifyPolicyExpiredOnUpdatePolicyStatusDate() {
		LocalDateTime date = getTimePoints().getUpdatePolicyStatusDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate());
		verifyPolicyStatusOnDate(date, ProductConstants.PolicyStatus.POLICY_EXPIRED);
	}

	protected void validatePLIGAFeeOnRenewGenOfferDate() {
		LocalDateTime genOfferDate = getTimePoints().getRenewOfferGenerationDate(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyExpDate().plusHours(1));
		TimeSetterUtil.getInstance().nextPhase(genOfferDate);
		log.info("PLIGA fee validation on {}", genOfferDate);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar pligaFee = BillingHelper.calculatePligaFee(TimeSetterUtil.getInstance().getStartTime(),
				new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, 
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL)
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()));
		new BillingPaymentsAndTransactionsVerifier()
				.setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PLIGA_FEE)
				.setAmount(pligaFee)
				.verifyPresent();
		log.info("PLIGA fee validated successfully");
	}

	protected TestData getPolicyTestData() {
		throw new IstfException("Please override method in appropriate child class with relevant test data preparation");
	}

	protected void runCFTJobs() {
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		//JobUtils.executeJob(Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		JobUtils.executeJob(Jobs.policyTransactionLedgerJob_NonMonthly);
	}

	private void acceptManualPaymentOnDate(LocalDateTime paymentDate) {
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept payment action started on {}", paymentDate);
		mainApp().open();
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

	private void acceptMinDuePaymentOnDate(LocalDateTime paymentDate) {
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept payment action started on {}", paymentDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
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

	private void acceptTotalDuePlusOverpaymentOnDate(Dollar overpayment, LocalDateTime paymentDate) {
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		log.info("Accept overpayment action started on {}", paymentDate);
		mainApp().open();
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

	private void addSuspenseOnDate(LocalDateTime suspenseDate) {
		TimeSetterUtil.getInstance().nextPhase(suspenseDate);
		log.info("Add Suspense action started on {}", suspenseDate);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openBilling(policyNumber);
		new PaymentsMaintenance().addSuspense().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY), policyNumber);
		log.info("Suspense added successfully");
	}

	private void automaticCancellationDueInsallmentDate(LocalDateTime installmentDate) {
		LocalDateTime cancellationDate = getTimePoints().getCancellationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		log.info("Cancellation action started on {}", cancellationDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		//String cancellationPremium = BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains("Subtype/Reason", "Cancellation - Insured Non-Payment Of Premium").getCell("Amount").getValue();
		log.info("Cancellation action completed successfully");
	}

	private void automaticCancellationNoticeDueInsallmentDate(LocalDateTime installmentDate) {
		LocalDateTime cancellationNoticeDate = getTimePoints().getCancellationNoticeDate(installmentDate);
		LocalDateTime expCancellationDate = getTimePoints().getCancellationTransactionDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(cancellationNoticeDate);
		log.info("Cancellation Notice action started on {}", cancellationNoticeDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingBillsAndStatementsVerifier()
				.setDueDate(expCancellationDate)
				.setType(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE)
				.verifyPresent();
		log.info("Cancellation Notice action completed successfully");
	}

	private void generateAndCheckEarnedPremiumBill(LocalDateTime date) {
		TimeSetterUtil.getInstance().nextPhase(date);
		log.info("Earned Premium bill generation started on {}", date);
		JobUtils.executeJob(Jobs.earnedPremiumBillGenerationJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(date);
		log.info("Earned Premium bill generated successfully");
	}

	private void generateInstallmentBillDueDate(LocalDateTime billDueDate) {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(billDueDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		log.info("Installment bill generation started on {}", billGenDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingBillsAndStatementsVerifier()
				.setType(BillingConstants.BillsAndStatementsType.BILL)
				.setDueDate(billDueDate)
				.setMinDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.MIN_DUE).getValue()))
				.setPastDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.PAST_DUE).getValue()))
				.setTotalDue(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue()))
				.verifyPresent();
		log.info("Installment bill generation completed successfully");
	}

	private void automatedRefundOnDate(Dollar refundAmount, LocalDateTime refundDate) {
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Verify refund on {}", refundDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPaymentsAndTransactionsVerifier()
				.setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED)
				.setAmount(refundAmount)
				.verifyPresent();
		log.info("Automated refund approved successfully");
	}

	private void manualCancellationOnDate(LocalDateTime cancellationDate) {
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		log.info("Manual cancellation action started on {}", cancellationDate);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.cancel().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		log.info("Manual cancellation action completed successfully");
	}

	private void manualFutureCancellationOnDate(LocalDateTime cancellationDate) {
		TimeSetterUtil.getInstance().nextPhase(cancellationDate);
		log.info("Manual cancellation action started on {}", cancellationDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		policy.cancel().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
		log.info("Manual cancellation action completed successfully");
	}

	private void manualReinstatementOnDate(LocalDateTime reinstatementDate) {
		TimeSetterUtil.getInstance().nextPhase(reinstatementDate);
		log.info("Manual reinstatement action started on {}", reinstatementDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openPolicy(policyNumber);
		policy.reinstate().perform(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION,
				String.format("Bind Reinstatement for Policy %1$s", policyNumber))).exists();
		log.info("Manual reinstatement action completed successfully");
	}

	private void otherAdjustmentOnDate(LocalDateTime adjustmentDate) {
		TimeSetterUtil.getInstance().nextPhase(adjustmentDate);
		log.info("Other Adjustment action started on {}", adjustmentDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
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

	private void pendingRefundOnDate(Dollar refundAmount, LocalDateTime refundDate) {
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		log.info("Verify refund on {}", refundDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPendingTransactionsVerifier()
				.setType(BillingConstants.BillingPendingTransactionsType.REFUND)
				.setSubtypeReason(BillingConstants.BillingPendingTransactionsSubtype.AUTOMATED_REFUND)
				.setReason(BillingConstants.BillingPendingTransactionsReason.OVERPAYMENT)
				.setAmount(refundAmount)
				.setStatus(BillingConstants.BillingPendingTransactionsStatus.PENDING)
				.verifyPresent();
		log.info("Refund present in Pending Transactions Table");
	}

	private void performEndorsementOnDate(LocalDateTime endorsementDate) {
		TimeSetterUtil.getInstance().nextPhase(endorsementDate);
		log.info("Endorsment action started on {}", endorsementDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		SearchPage.openPolicy(policyNumber);
		policy.endorse().performAndFill(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(
				ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION,
				String.format("Bind Endorsement effective %1$s for Policy %2$s", endorsementDate.format(DateTimeUtils.MM_DD_YYYY), policyNumber))).exists();
		log.info("Endorsment action completed successfully");
	}

	private void performFutureEndorsementOnDate(LocalDateTime endorsementDate, String[] endorsementEffDateDataKeys) {
		TimeSetterUtil.getInstance().nextPhase(endorsementDate);
		log.info("Endorsment action started on {}", endorsementDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		String policyNumber = BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber();
		String endorsementEffDate = getTestSpecificTD(DEFAULT_TEST_DATA_KEY).getValue(endorsementEffDateDataKeys);
		SearchPage.openPolicy(policyNumber);
		policy.endorse().performAndFill(getTestSpecificTD(DEFAULT_TEST_DATA_KEY));
		assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRowContains(
				ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION,
				String.format("Bind Endorsement effective %1$s for Policy %2$s", endorsementEffDate, policyNumber))).exists();
		log.info("Endorsment action completed successfully");
	}

	private void rejectRefundOnDate(LocalDateTime rejectDate) {
		TimeSetterUtil.getInstance().nextPhase(rejectDate);
		log.info("Reject refund action started on {}", rejectDate);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		Dollar amount = new Dollar(BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(BillingConstants.BillingPendingTransactionsTable.AMOUNT).getValue());
		BillingSummaryPage.tablePendingTransactions.getRow(1).getCell(BillingConstants.BillingPendingTransactionsTable.ACTION).controls.links
				.get(ActionConstants.BillingPendingTransactionAction.REJECT).click();
		Page.dialogConfirmation.confirm();
		new BillingPaymentsAndTransactionsVerifier()
				.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DISAPPROVED)
				.setTransactionDate(rejectDate)
				.setAmount(amount.negate())
				.verifyPresent();
		log.info("Reject refund action completed successfully");
	}

	private void verifyPolicyStatusOnDate(LocalDateTime date, String policyStatus) {
		TimeSetterUtil.getInstance().nextPhase(date);
		log.info("Verify policy status on {}", date);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openPolicy(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(policyStatus);
		log.info("Policy status is {}", policyStatus);
	}

	private void waiveFeeOnDate(LocalDateTime waiveDate) {
		TimeSetterUtil.getInstance().nextPhase(waiveDate);
		log.info("Waive action started");
		log.info("Waive date: {}", waiveDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
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

	private void writeOffOnDate(LocalDateTime writeOffDate) {
		TimeSetterUtil.getInstance().nextPhase(writeOffDate);
		log.info("EP Write off generation action started on {}", writeOffDate);
		JobUtils.executeJob(Jobs.cftDcsEodJob);
		mainApp().open();
		SearchPage.openBilling(BillingAccountInformationHolder.getCurrentBillingAccountDetails().getCurrentPolicyDetails().getPolicyNumber());
		new BillingPaymentsAndTransactionsVerifier()
				.setTransactionDate(writeOffDate)
				.setType(BillingConstants.PaymentsAndOtherTransactionType.ADJUSTMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPLIED)
				.verifyPresent();
		log.info("EP Write off generated successfully");
	}
}