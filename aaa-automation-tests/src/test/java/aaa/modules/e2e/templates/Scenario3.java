package aaa.modules.e2e.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
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
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;

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
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Semi-Annual payment plan", installmentsCount, installmentDueDates.size());
		assertThat(installmentDueDates.size()).as("Billing Installments count for Semi-Annual payment plan").isEqualTo(installmentsCount);

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	public void generateFirstBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(1), softly);
	}

	public void generateCancellationNotice() {
		LocalDateTime cancNoticeDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cancNoticeDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.CANCEL_NOTICE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingBillsAndStatementsVerifier().setDueDate(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)))
				.setType(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE).verifyPresent();
	}

	public void createRemittanceFile() {
		LocalDateTime date = getTimePoints().getCancellationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(date.plusDays(5).with(DateTimeUtils.closestFutureWorkingDay));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar cnAmount = BillingHelper.getBillDueAmount(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)),
				BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE);
		File remitanceFile = RemittancePaymentsHelper.createRemittanceFile(getState(), policyNum, cnAmount, BillingConstants.ExternalPaymentSystem.REGLKBX);
		RemittancePaymentsHelper.copyRemittanceFileToServer(remitanceFile);
	}

	public void payCancellationNoticeByRemittance() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		JobUtils.executeJob(Jobs.aaaRemittanceFeedAsyncBatchReceiveJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar cnAmount = BillingHelper.getBillDueAmount(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(1)),
				BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setAmount(cnAmount.negate())
				.setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REGULUS_LOCKBOX)
				.verifyPresent();

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setType(BillingConstants.PaymentsAndOtherTransactionType.PREMIUM)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.REINSTATEMENT).verifyPresent();

		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
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
		//PolicySummaryPage.buttonRenewals.verify.enabled();
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	public void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.buttonRenewals.verify.enabled();
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

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate,getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount, softly);
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
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(policyExpirationDate, billDate, policyEffectiveDate, pligaOrMvleFee);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	public void expirePolicy() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT)) {
			new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate); //PASBB-624/PAS-624
		} else {
			new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		}
	}

	public void customerDeclineRenewal() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	public void payRenewOffer() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPayLapsedRenewShort(policyExpirationDate).plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);

		String billType = getState().equals(Constants.States.CA) ? BillingConstants.BillsAndStatementsType.OFFER : BillingConstants.BillsAndStatementsType.BILL;
		Dollar sum = BillingHelper.getBillMinDueAmount(policyExpirationDate, billType);

		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_CC"), sum);
		//commented according to PASBB-624/PAS-624
		/*if (PolicyType.AUTO_CA_SELECT.equals(getPolicyType())) {
			new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getPayLapsedRenewShort(policyExpirationDate)).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
					.verifyPresent();
		} else { */
			new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
		//}
	}

	public void bindRenew() {
		//added a hour to postpone job execution to avoid conflict with makeManualPaymentInFullRenewalOfferAmount from Scenario2
		LocalDateTime lapsedRenewShort = getTimePoints().getPayLapsedRenewShort(policyExpirationDate).plusHours(1);
		TimeSetterUtil.getInstance().nextPhase(lapsedRenewShort);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
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

		if (PolicySummaryPage.labelPolicyStatus.getValue().equals(ProductConstants.PolicyStatus.POLICY_EXPIRED)) {
			PolicySummaryPage.buttonRenewals.click();
		}

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		if (!getPolicyType().isAutoPolicy()) {
			PolicySummaryPage.verifyLapseExistFlagPresent();
		}

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
		if (!getPolicyType().isAutoPolicy() && 
			!(getPolicyType().equals(PolicyType.PUP) && getState().equals(Constants.States.CA))  ) { //PASBB-775
			//TODO Possible problems with MD state, See QC 35220 for details.
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(lapsedRenewShort).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
		}
	}
}
