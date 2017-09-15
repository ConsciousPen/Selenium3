package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

public class Scenario5 extends ScenarioBaseTest {

	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 11;

	public void createTestPolicy(TestData policyCreationTD) {
		TimeSetterUtil.getInstance().adjustTime(); // *** Debug
		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Monthly (Eleven Pay) payment plan", installmentsCount, installmentDueDates.size());
	}

	public void Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	public void Pay_First_Bill() {
		payAndCheckBill(installmentDueDates.get(1));
	}

	public void Generate_Second_Bill() {
		generateAndCheckBill(installmentDueDates.get(2));
	}

	public void Pay_Second_Bill() {
		payAndCheckBill(installmentDueDates.get(2));
	}

	public void Decline_Payments() {
		mainApp().open();
		SearchPage.openBilling(policyNum);

		LocalDateTime paymentDate = getTimePoints().getBillDueDate(installmentDueDates.get(1));
		BillingHelper.declinePayment(paymentDate);
		new BillingPaymentsAndTransactionsVerifier().verifyPaymentDeclined(paymentDate);

		paymentDate = getTimePoints().getBillDueDate(installmentDueDates.get(2));
		BillingHelper.declinePayment(paymentDate);
		new BillingPaymentsAndTransactionsVerifier().verifyPaymentDeclined(paymentDate);

		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
	}

	public void Generate_CancellNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(2));
		// TODO Why?
		if (getState().equals(Constants.States.AZ))
			cnDate.minusHours(1);
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.CANCEL_NOTICE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE).verifyRowWithDueDate(
			getTimePoints().getCancellationTransactionDate(installmentDueDates.get(2)));
	}

	public void Verify_Form_AH34XX() {
		// DocGenHelper.verifyDocumentsGeneratedByJob(TimeSetterUtil.getInstance().getCurrentTime(),
		// policyNum, OnDemandDocuments.AH34XX);
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH34XX);
	}

	public void Cancel_Policy() {
		cancelPolicy(installmentDueDates.get(2));
	}

	public void Generate_First_EP_Bill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillFirst(installmentDueDates.get(2)));
		// DocGenHelper.verifyDocumentsGeneratedByJob(policyNum,
		// OnDemandDocuments._55_6101, XPathInfo.INVOICE_BILLS_STATEMENTS);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._55_6101);
	}

	public void Generate_Second_EP_Bill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillSecond(installmentDueDates.get(2)));
		// DocGenHelper.verifyDocumentsGeneratedByJob(policyNum,
		// OnDemandDocuments._55_6102, XPathInfo.INVOICE_BILLS_STATEMENTS);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._55_6102);
	}

	public void Generate_Third_EP_Bill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillThird(installmentDueDates.get(2)));
		// DocGenHelper.verifyDocumentsGeneratedByJob(policyNum,
		// OnDemandDocuments._55_6103, XPathInfo.INVOICE_BILLS_STATEMENTS);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents._55_6103);
	}

	public void Generate_EP_Write_Off() {
		LocalDateTime date = getTimePoints().getEarnedPremiumWriteOff(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.collectionFeedBatch_earnedPremiumWriteOff);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(date).setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF).verifyPresent();
	}

	public void Renewal_Image_Generation() {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicyHelper.verifyAutomatedRenewalNotGenerated(renewDateImage);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
	}

	public void Renewal_Preview_Generation() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
	}

	public void Renewal_Offer_Generation() {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_CANCELLED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(policyExpirationDate).setSubtypeReason(
			BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent(false);
	}

	protected void generateAndCheckEarnedPremiumBill(LocalDateTime date) {
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.earnedPremiumBillGenerationJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(date);
	}
}
