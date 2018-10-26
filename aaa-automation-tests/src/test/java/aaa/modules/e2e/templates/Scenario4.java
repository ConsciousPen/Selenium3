package aaa.modules.e2e.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.*;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.MyWorkConstants;
import aaa.main.enums.ProductConstants;
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
import toolkit.verification.ETCSCoreSoftAssertions;

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
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		assertThat(installmentDueDates.size()).as("Billing Installments count for Pay in Full payment plan").isEqualTo(installmentsCount);

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
				.setAmount(cashOverpaymentLow.negate()).setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.CLEARED).verifyPresent();
	}

	protected void automaticRefund() {
		LocalDateTime refund = getTimePoints().getRefundDate(policyEffectiveDate.plusMonths(1));
		TimeSetterUtil.getInstance().nextPhase(refund);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob); //.refundGenerationJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refund)
				.setAmount(cashOverpaymentLow).setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED)
				.setReason(BillingConstants.PaymentsAndOtherTransactionReason.OVERPAYMENT).verifyPresent();
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
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob); //.refundGenerationJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPendingTransactionsVerifier().setTransactionDate(refundDate)
				.setAmount(cashOverpaymentHigh).setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setReason(BillingConstants.PaymentsAndOtherTransactionReason.OVERPAYMENT)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.PENDING).verifyPresent();

		BillingHelper.approvePendingTransaction(refundDate, BillingConstants.PaymentsAndOtherTransactionType.REFUND);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(refundDate)
				.setAmount(cashOverpaymentHigh).setType(BillingConstants.PaymentsAndOtherTransactionType.REFUND)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND)
				.setReason(BillingConstants.PaymentsAndOtherTransactionReason.OVERPAYMENT)
				.setStatus(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED).verifyPresent();
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
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(transactionDate).setPolicy(policyNum).setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
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
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setMinDue(endorsementDue)
				.setPastDueZero().verifyRowWithDueDate(endorsementInstallmentDueDate);
	}

	protected void generateCancelNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(endorsementInstallmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		if (getState().equals(Constants.States.NJ) && getPolicyType().isAutoPolicy()) {
			//Cancel Notice should not be generated for NJ policy cause cancel notice date is before Paid Through date
			PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			endorsementDue = BillingSummaryPage.getTotalDue();
			new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).setMinDue(endorsementDue)
					.setPastDueZero().verifyRowWithDueDate(endorsementInstallmentDueDate);
		}
		else {
			PolicySummaryPage.verifyCancelNoticeFlagPresent();
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.CANCEL_NOTICE).verifyRowWithEffectiveDate(policyEffectiveDate);

			new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE)
					.setMinDue(endorsementDue).setPastDue(endorsementDue).setTotalDue(endorsementDue)
					.verifyRowWithDueDate(getTimePoints().getCancellationTransactionDate(endorsementInstallmentDueDate));
		}
	}

	protected void paymentInFullCancellNoticeAmount() {
		// Set any date prior to Cancellation date
		LocalDateTime cnDate = getTimePoints().getCancellationDate(endorsementInstallmentDueDate).minusDays(5).with(DateTimeUtils.closestFutureWorkingDay);
		TimeSetterUtil.getInstance().nextPhase(cnDate);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		if (!getState().equals(Constants.States.NJ) && getPolicyType().isAutoPolicy()) {
			PolicySummaryPage.verifyCancelNoticeFlagPresent();
		}
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar sum = new Dollar(BillingHelper.getBillCellValue(endorsementInstallmentDueDate, BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), sum);

		BillingSummaryPage.openPolicy(1);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.labelCancelNotice).isPresent(false);
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
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(installmentDueDates, softly);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDateOffer)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

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
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

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
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		if (!getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		}
	}

	protected void customerDeclineRenewal() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void payRenewOffer() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPayLapsedRenewLong(policyExpirationDate).plusHours(2));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);

		String billType = getState().equals(Constants.States.CA) ? BillingConstants.BillsAndStatementsType.OFFER : BillingConstants.BillsAndStatementsType.BILL;
		Dollar rAmount = BillingHelper.getBillMinDueAmount(policyExpirationDate, billType);

		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), rAmount);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void bindRenew() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(2));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonTasks.click();
		assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME, "Attempt to secure rewrite")).isPresent();
		MyWorkSummaryPage.buttonCancel.click();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verify(1);

		if (getPolicyType().isAutoPolicy()) {
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			new DocumentsAndBindTab().submitTab();
		} else {
			policy.manualRenewalWithOrWithoutLapse().perform(getStateTestData(tdPolicy, "ManualRenewalWithOrWithoutLapse", "TestData"));
		}

		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		if (!getPolicyType().isAutoPolicy()) {
			PolicySummaryPage.verifyLapseExistFlagPresent();
		}

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);

		if (!getPolicyType().isAutoPolicy() && 
		    !(getPolicyType().equals(PolicyType.PUP) && getState().equals(Constants.States.CA))  ) { //PASBB-775
			// TODO Possible problems with MD and MT state. See QC 35220 for details.
			//if (!getState().equals(Constants.States.MD) && !getState().equals(Constants.States.MT)) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getPayLapsedRenewLong(policyExpirationDate)).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
					.verifyPresent();
		}
	}
}
