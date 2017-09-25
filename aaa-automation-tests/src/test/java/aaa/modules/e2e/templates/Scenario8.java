package aaa.modules.e2e.templates;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingInstallmentsScheduleVerifier;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scenario8 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;

	protected List<LocalDateTime> installmentDueDates;

	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();

		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD);
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Monthly (Eleven Pay) payment plan", 11, installmentDueDates.size());

		if (getState().equals(Constants.States.NJ)) {
			new BillingPaymentsAndTransactionsVerifier().verifyPligaFee(policyEffectiveDate);
		} else if (getState().equals(Constants.States.NY)) {
			new BillingPaymentsAndTransactionsVerifier().verifyMVLEFee(policyEffectiveDate);
		}
	}

	protected void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage); // verify Not Generated in excel
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalsStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
	}

	protected void renewalOfferGeneration() {
		LocalDateTime policyExpirationDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		verifyRenewalsStatus(ProductConstants.PolicyStatus.PROPOSED);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY)
				.verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL)
				.verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(policyExpirationDate, Arrays.asList(installmentDueDates.get(0), installmentDueDates.get(3), installmentDueDates.get(6), installmentDueDates.get(9)));

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(policyExpirationDateOffer).setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		/** TODO Why 9??? */
		new BillingInstallmentsScheduleVerifier().setDescription(BillingConstants.InstallmentDescription.INSTALLMENT).verifyCount(9);

		if (getState().equals(Constants.States.NJ)) {
			pligaOrMvleFeeLastTransactionDate = policyExpirationDateOffer;
			new BillingPaymentsAndTransactionsVerifier().verifyPligaFee(pligaOrMvleFeeLastTransactionDate);
		} else if (getState().equals(Constants.States.NY)) {
			pligaOrMvleFeeLastTransactionDate = policyExpirationDateOffer;
			new BillingPaymentsAndTransactionsVerifier().verifyMVLEFee(pligaOrMvleFeeLastTransactionDate);
		}
	}

	protected void renewalPremiumNotice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();

		Dollar pligaOrMvleFee = BillingHelper.DZERO;
		if (getState().equals(Constants.States.NJ)) {
			pligaOrMvleFee = BillingSummaryPage.getPligaFee(pligaOrMvleFeeLastTransactionDate);
		} else if (getState().equals(Constants.States.NY)) {
			pligaOrMvleFee = new Dollar(10);
		}

		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY)
				.verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL)
				.verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewPremiumNotice(policyExpirationDate, billDate, pligaOrMvleFee);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();

		BillingSummaryPage.showPriorTerms();
		new BillingInstallmentsScheduleVerifier().setDescription(BillingConstants.InstallmentDescription.INSTALLMENT).verifyCount(9);
	}

	protected void changePaymentPlanAndCheckInstallments(TestData td, String paymentPlan, int expectedInstallmentsNumber) {
		changePaymentPlanAndCheckInstallments(td, paymentPlan, false, false, expectedInstallmentsNumber, policyEffectiveDate);
	}

	protected void changePaymentPlanAndCheckInstallments(TestData td, String paymentPlan, boolean renew, boolean verifyRenewalsStatusIsPremiumCalculated, int expectedInstallmentsNumber, LocalDateTime expectedEffectiveDate) {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		if (renew) {
			policy.renew().performAndFill(td);
		} else {
			policy.endorse().performAndFill(td);
			PolicyHelper.verifyEndorsementIsCreated();
		}

		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		TimeSetterUtil.getInstance().nextPhase(transactionDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();

		if (verifyRenewalsStatusIsPremiumCalculated) {
			verifyRenewalsStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		} else {
			SearchPage.openBilling(policyNum);
		}

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPaymentPlan(paymentPlan).verifyRowWithEffectiveDate(expectedEffectiveDate);
		new BillingInstallmentsScheduleVerifier().setDescription(BillingConstants.InstallmentDescription.INSTALLMENT).verifyCount(expectedInstallmentsNumber);

		if (getState().equals(Constants.States.NJ)) {
			Dollar expectedPligaFee = BillingSummaryPage.calculatePligaFee(transactionDate);
			if (!expectedPligaFee.isZero()) {
				pligaOrMvleFeeLastTransactionDate = transactionDate;
				new BillingPaymentsAndTransactionsVerifier().verifyPligaFee(expectedPligaFee, pligaOrMvleFeeLastTransactionDate);
			}
		} else if (getState().equals(Constants.States.NY)) {
			pligaOrMvleFeeLastTransactionDate = transactionDate;
			new BillingPaymentsAndTransactionsVerifier().verifyMVLEFee(pligaOrMvleFeeLastTransactionDate);
		}
	}

	private void verifyRenewalsStatus(String status) {
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(status).verify(1);
	}

}
