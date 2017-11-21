package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints.TimepointsList;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
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
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;

	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 11;

	protected String policyTerm;
	protected Integer totalVehiclesNumber;

	public void createTestPolicy(TestData policyCreationTD) {

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
		CustomAssert.assertEquals("Billing Installments count for Monthly (Eleven Pay) payment plan", installmentsCount, installmentDueDates.size());

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	public void generateFirstBillOneDayBefore() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1)).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingBillsAndStatementsVerifier().setDueDate(installmentDueDates.get(1)).setType(BillingConstants.BillsAndStatementsType.BILL).verifyPresent(false);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent(false);
	}

	public void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	public void payFirstBillOneDayBefore() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(1)).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setType(BillingConstants.PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(
			BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).verifyPresent(false);
	}

	public void payFirstBill() {
		payAndCheckBill(installmentDueDates.get(1));
	}

	public void generateSecondBill() {
		generateAndCheckBill(installmentDueDates.get(2));
	}

	public void paySecondBill() {
		payAndCheckBill(installmentDueDates.get(2));
	}

	public void declinePayments() {
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

	public void generateCancellNoticeOneDayBefore() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(2)).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.DEFAULT).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingBillsAndStatementsVerifier().setDueDate(getTimePoints().getCancellationTransactionDate(installmentDueDates.get(2))).setType(
			BillingConstants.BillsAndStatementsType.CANCELLATION_NOTICE).verifyPresent(false);
	}

	public void generateCancellNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(2));
		// TODO Why?
		if (getState().equals(States.AZ))
			cnDate = cnDate.minusHours(1);
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

	public void verifyFormAH34XX() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH34XX);
	}

	public void cancelPolicyOneDayBefore() {
		LocalDateTime cDate = getTimePoints().getCancellationDate(installmentDueDates.get(2)).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(cDate);
		JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();
	}

	public void verifyFormAH67XX() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH67XX);
	}

	public void generateFirstEPBillOneDayBefore() {
		LocalDateTime epDate = getTimePoints().getEarnedPremiumBillFirst(installmentDueDates.get(2)).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(epDate);
		JobUtils.executeJob(Jobs.earnedPremiumBillGenerationJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingBillsAndStatementsVerifier().setDueDate(epDate).setType(BillingConstants.BillsAndStatementsType.BILL).verifyPresent(false);
	}

	public void generateFirstEPBill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillFirst(installmentDueDates.get(2)), DocGenEnum.Documents._55_6101);
	}

	public void generateSecondEPBill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillSecond(installmentDueDates.get(2)), DocGenEnum.Documents._55_6102);
	}

	public void generateThirdEPBill() {
		generateAndCheckEarnedPremiumBill(getTimePoints().getEarnedPremiumBillThird(installmentDueDates.get(2)), DocGenEnum.Documents._55_6103);
	}

	public void generateEPWriteOffOneDayBefore() {
		LocalDateTime date = getTimePoints().getEarnedPremiumWriteOff(installmentDueDates.get(2)).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.collectionFeedBatch_earnedPremiumWriteOff);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(date).setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF).verifyPresent(false);
	}

	public void generateEPWriteOff() {
		LocalDateTime date = getTimePoints().getEarnedPremiumWriteOff(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.collectionFeedBatch_earnedPremiumWriteOff);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(date).setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.EARNED_PREMIUM_WRITE_OFF).verifyPresent();
	}

	public void renewalImageGeneration() {
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

	public void renewalPreviewGeneration() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_CANCELLED);
	}

	public void renewalOfferGeneration() {
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

	protected void generateAndCheckEarnedPremiumBill(LocalDateTime date, DocGenEnum.Documents document) {
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.earnedPremiumBillGenerationJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, document);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(date);
	}
}
