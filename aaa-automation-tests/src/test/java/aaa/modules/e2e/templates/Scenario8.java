package aaa.modules.e2e.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.*;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;

public class Scenario8 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;

	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 11;

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
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		assertThat(installmentDueDates.size()).as("Billing Installments count for Monthly (Eleven Pay) payment plan").isEqualTo(installmentsCount);

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime());
	}

	protected void generateFirstBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(1), softly);
	}
	
	protected void generateAndCheckBillAfterEndorsement(LocalDateTime installmentDate, LocalDateTime effectiveDate, ETCSCoreSoftAssertions softly) {
		LocalDateTime previousDate = DateTimeUtils.getCurrentDateTime();
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		Dollar correctionAmount = BillingHelper.DZERO;
		correctionAmount = BillingHelper.getTransactionsAmountSum(previousDate).add(BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1)));
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDate, billGenDate, effectiveDate, correctionAmount);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage); // verify Not Generated in excel
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalsStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
	}

	protected void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime policyExpirationDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateOffer);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		verifyRenewalsStatus(ProductConstants.PolicyStatus.PROPOSED);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY)
				.verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL)
				.verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(Arrays.asList(installmentDueDates.get(0), installmentDueDates.get(3), installmentDueDates.get(6), installmentDueDates.get(9)), softly);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(policyExpirationDateOffer).setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		/* TODO Why 9??? */
		new BillingInstallmentsScheduleVerifier().setDescription(BillingConstants.InstallmentDescription.INSTALLMENT).verifyCount(9);
		if (verifyPligaOrMvleFee(policyExpirationDateOffer)) {
			pligaOrMvleFeeLastTransactionDate = policyExpirationDateOffer;
		}
	}

	protected void renewalPremiumNotice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();

		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY)
				.verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL)
				.verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewPremiumNotice(policyExpirationDate, billDate, getPligaOrMvleFee(pligaOrMvleFeeLastTransactionDate));
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
			PurchaseTab purchaseTab = new PurchaseTab();
			if (purchaseTab.isVisible()) {
				purchaseTab.payRemainingBalance().submitTab();
			}
			//in case of Difference screen appears according to PAS-10738
			if (Tab.buttonCancel.isPresent()&&Tab.buttonCancel.isVisible()) {
				Tab.buttonCancel.click();
			}

			PolicyHelper.verifyEndorsementIsCreated();
		}

		LocalDateTime transactionDate = TimeSetterUtil.getInstance().getCurrentTime();
		TimeSetterUtil.getInstance().nextPhase(transactionDate);
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
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

		if (verifyPligaOrMvleFee(transactionDate)) {
			pligaOrMvleFeeLastTransactionDate = transactionDate;
		}
	}

	private void verifyRenewalsStatus(String status) {
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(status).verify(1);
	}

}
