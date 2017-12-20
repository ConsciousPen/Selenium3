package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.BillingPendingTransactionsVerifier;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillsAndStatementsType;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.BillingConstants.PolicyFlag;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.MyWorkConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class Scenario4 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	protected String[] endorsementReasonDataKeys;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;

	protected List<LocalDateTime> installmentDueDates;
	protected LocalDateTime endorsementInstallmentDueDate;
	private Dollar endorsementDue;

	protected int installmentsCount = 1;
	protected Dollar cashOverpaymentLow = new Dollar(999.99);
	protected Dollar cashOverpaymentHigh = new Dollar(1000);

	protected String policyTerm;
	protected Integer totalVehiclesNumber;

	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();

		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}

		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);

		policyNum = createPolicy(policyCreationTD);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Pay in Full payment plan", installmentsCount, installmentDueDates.size());

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);

		//TODO Get 6th installment due date
		endorsementInstallmentDueDate = policyEffectiveDate.plusMonths(6);
	}

	protected void overpayment() {
		LocalDateTime overpayment = policyEffectiveDate.plusMonths(1);
		TimeSetterUtil.getInstance().nextPhase(overpayment);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), cashOverpaymentLow);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(overpayment)
				.setAmount(cashOverpaymentLow.negate()).setType(PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
				.setStatus(PaymentsAndOtherTransactionStatus.CLEARED).verifyPresent();
	}

	protected void automaticRefund() {
		LocalDateTime refund = getTimePoints().getRefundDate(policyEffectiveDate.plusMonths(1));
		TimeSetterUtil.getInstance().nextPhase(refund);
		JobUtils.executeJob(Jobs.refundGenerationJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refund)
				.setAmount(cashOverpaymentLow).setType(PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setStatus(PaymentsAndOtherTransactionStatus.APPROVED)
				.setReason(PaymentsAndOtherTransactionReason.OVERPAYMENT).verifyPresent();
	}

	protected void overpaymentHigh() {
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(2));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), cashOverpaymentHigh);
	}

	protected void automaticRefundHigh() {
		LocalDateTime refundDate = getTimePoints().getRefundDate(policyEffectiveDate.plusMonths(2));
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		JobUtils.executeJob(Jobs.refundGenerationJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPendingTransactionsVerifier().setTransactionDate(refundDate)
				.setAmount(cashOverpaymentHigh).setType(PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setReason(PaymentsAndOtherTransactionReason.OVERPAYMENT)
				.setStatus(PaymentsAndOtherTransactionStatus.PENDING).verifyPresent();

		BillingHelper.approvePendingTransaction(refundDate, PaymentsAndOtherTransactionType.REFUND);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refundDate)
				.setAmount(cashOverpaymentHigh).setType(PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setReason(PaymentsAndOtherTransactionReason.OVERPAYMENT)
				.setStatus(PaymentsAndOtherTransactionStatus.APPROVED).verifyPresent();
	}

	protected void endorsePolicy() {
		// Set Date before BillGenerationDate
		TimeSetterUtil.getInstance().nextPhase(endorsementInstallmentDueDate.minusDays(28).with(DateTimeUtils.closestFutureWorkingDay));

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		TestData endorsementTD = getTestSpecificTD("TestData_Endorsement").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		policy.endorse().performAndFill(endorsementTD);
		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		PolicyHelper.verifyEndorsementIsCreated();

		// Endorsement transaction displaing on billing in Payments & Other transactions section
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate).setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(reason).verifyPresent();

		int vehiclesNumber = getVehiclesNumber(endorsementTD);
		if (verifyPligaOrMvleFee(transactionDate, policyTerm, vehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = transactionDate;
			totalVehiclesNumber += vehiclesNumber;
		}
	}

	protected void generateOffCycleBill() {
		LocalDateTime dueDate = getTimePoints().getOffcycleBillGenerationDate(endorsementInstallmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(dueDate);
		JobUtils.executeJob(Jobs.offCycleBillingInvoiceAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		endorsementDue = BillingSummaryPage.getTotalDue();
		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).setMinDue(endorsementDue)
				.setPastDueZero().verifyRowWithDueDate(endorsementInstallmentDueDate);
	}

	protected void generateCancelNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(endorsementInstallmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(PolicyFlag.CANCEL_NOTICE).verifyRowWithEffectiveDate(policyEffectiveDate);

		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.CANCELLATION_NOTICE)
				.setMinDue(endorsementDue).setPastDue(endorsementDue).setTotalDue(endorsementDue)
				.verifyRowWithDueDate(getTimePoints().getCancellationTransactionDate(endorsementInstallmentDueDate));
	}

	protected void paymentInFullCancellNoticeAmount() {
		// Set any date prior to Cancellation date
		LocalDateTime cnDate = getTimePoints().getCancellationDate(endorsementInstallmentDueDate).minusDays(5).with(DateTimeUtils.closestFutureWorkingDay);
		TimeSetterUtil.getInstance().nextPhase(cnDate);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar sum = new Dollar(BillingHelper.getBillCellValue(endorsementInstallmentDueDate, BillingBillsAndStatmentsTable.TOTAL_DUE));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), sum);

		BillingSummaryPage.openPolicy(1);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelCancelNotice.verify.present(false);
	}

	protected void verifyFormAHCWXX() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AHCWXX);
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	protected void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration() {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(installmentDueDates);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDateOffer)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (verifyPligaOrMvleFee(renewDateOffer, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewDateOffer;
		}
	}

	//Skip this step for CA
	protected void renewalPremiumNotice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewPremiumNotice(policyExpirationDate, billDate, pligaOrMvleFee);
	}

	protected void expirePolicy() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		// JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void customerDeclineRenewal() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void payRenewOffer() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPayLapsedRenewLong(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);

		String billType = getState().equals(Constants.States.CA) ? BillsAndStatementsType.OFFER : BillsAndStatementsType.BILL;
		Dollar rAmount = BillingHelper.getBillMinDueAmount(policyExpirationDate, billType);

		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), rAmount);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void bindRenew() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME, "Attempt to secure rewrite").verify.present();
		MyWorkSummaryPage.buttonCancel.click();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.CUSTOMER_DECLINED).verify(1);

		if (getPolicyType().isAutoPolicy()) {
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			PremiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			new DocumentsAndBindTab().submitTab();
		} else {
			policy.manualRenewalWithOrWithoutLapse().perform(getStateTestData(tdPolicy, "ManualRenewalWithOrWithoutLapse", "TestData"));
		}

		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		if (!getPolicyType().isAutoPolicy()) {
			PolicySummaryPage.verifyLapseExistFlagPresent();
		}

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);

		if (!getPolicyType().isAutoPolicy()) {
			// TODO Possible problems with MD and MT state. See QC 35220 for details.
			//if (!getState().equals(Constants.States.MD) && !getState().equals(Constants.States.MT)) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getPayLapsedRenewLong(policyExpirationDate)).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
		}
	}
}
