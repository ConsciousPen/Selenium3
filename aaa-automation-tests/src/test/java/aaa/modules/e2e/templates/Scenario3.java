package aaa.modules.e2e.templates;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.*;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.*;
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
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class Scenario3 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;

	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 2;

	protected String policyTerm;
	protected Integer totalVehiclesNumber;

	public void createTestPolicy(TestData policyCreationTD) {
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
		CustomAssert.assertEquals("Billing Installments count for Semi-Annual payment plan", installmentsCount, installmentDueDates.size());

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	public void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	public void generateCancellationNotice() {
		LocalDateTime cancNoticeDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cancNoticeDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(PolicyFlag.CANCEL_NOTICE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingBillsAndStatementsVerifier().setDueDate(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)))
				.setType(BillsAndStatementsType.CANCELLATION_NOTICE).verifyPresent();
	}

	public void createRemittanceFile() {
		LocalDateTime date = getTimePoints().getCancellationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(date.plusDays(5).with(DateTimeUtils.closestFutureWorkingDay));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar cnAmount = BillingHelper.getBillDueAmount(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)),
				BillsAndStatementsType.CANCELLATION_NOTICE);
		File remitanceFile = RemittancePaymentsHelper.createRemittanceFile(getState(), policyNum, cnAmount, ExternalPaymentSystem.REGLKBX);
		RemittancePaymentsHelper.copyRemittanceFileToServer(remitanceFile);
	}

	public void payCancellationNoticeByRemittance() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		JobUtils.executeJob(Jobs.remittanceFeedBatchReceiveJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar cnAmount = BillingHelper.getBillDueAmount(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)),
				BillsAndStatementsType.CANCELLATION_NOTICE);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setAmount(cnAmount.negate())
				.setType(PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REGULUS_LOCKBOX)
				.verifyPresent();

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setType(PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT).verifyPresent();

		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	public void renewalImageGeneration() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}

	public void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	public void renewalOfferGeneration() {
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

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate,getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount);
		}

		if (verifyPligaOrMvleFee(renewDateOffer, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewDateOffer;
		}
	}

	//Skip this step for CA
	public void renewalPremiumNotice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(policyExpirationDate, billDate, policyEffectiveDate, pligaOrMvleFee);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	public void expirePolicy() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	public void customerDeclineRenewal() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	public void payRenewOffer() {
		goToBillingPage(policyNum);
		String billType = getState().equals(Constants.States.CA) ? BillsAndStatementsType.OFFER : BillsAndStatementsType.BILL;
		Dollar sum = BillingHelper.getBillMinDueAmount(policyExpirationDate, billType);
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_CC"), sum);
	}

	public void bindRenew() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		LocalDateTime lapsedRenewShort = getTimePoints().getPayLapsedRenewShort(policyExpirationDate).plusHours(1);
		TimeSetterUtil.getInstance().nextPhase(lapsedRenewShort);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
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

		if (PolicySummaryPage.labelPolicyStatus.getValue().equals(PolicyStatus.POLICY_EXPIRED)) {
			PolicySummaryPage.buttonRenewals.click();
		}

		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		if (!getPolicyType().isAutoPolicy()) {
			PolicySummaryPage.verifyLapseExistFlagPresent();
		}

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
		if (!getPolicyType().isAutoPolicy()) {
			//TODO Possible problems with MD state, See QC 35220 for details.
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(lapsedRenewShort).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
		}
	}
}
