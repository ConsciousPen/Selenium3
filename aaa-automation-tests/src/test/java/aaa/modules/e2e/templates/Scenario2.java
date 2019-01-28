package aaa.modules.e2e.templates;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.BillingStatus;
import aaa.main.enums.BillingConstants.BillsAndStatementsType;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;

public class Scenario2 extends ScenarioBaseTest {

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

	protected void createTestPolicy(TestData policyCreationTD) {
		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);

		policyNum = createPolicy(policyCreationTD);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		assertThat(installmentDueDates.size()).as("Billing Installments count for Monthly (Eleven Pay) payment plan").isEqualTo(installmentsCount);

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	protected void generateFirstBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(1), softly);
	}

	protected void payFirstBill() {
		payAndCheckBill(installmentDueDates.get(1));
	}

	protected void billingOnHold() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		mainApp().open();
		SearchPage.openBilling(policyNum);
		// Set on hold status
		billingAccount.addHold().perform(tdBilling.getTestData("AddHold", "TestData"));
		new BillingAccountPoliciesVerifier().setBillingStatus(BillingStatus.ON_HOLD).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	protected void billNotGenerated() {
		LocalDateTime secondBillGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(secondBillGenDate);
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		// Verify Bill is not generated
		new BillingBillsAndStatementsVerifier().setDueDate(secondBillGenDate).setType(BillsAndStatementsType.BILL).verifyPresent(false);
		billingAccount.removeHold().perform(tdBilling.getTestData("RemoveHold", "TestData"));
		new BillingAccountPoliciesVerifier().setBillingStatus(BillingStatus.ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	protected void generateSecondBill() {
		LocalDateTime secondBillGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(2), secondBillGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(secondBillGenDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void paySecondBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(2), softly);
	}

	protected void generateThirdBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(3), policyEffectiveDate, softly);
	}

	protected void payThirdBill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	protected void generateFourthBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(4), softly);
	}

	protected void payFourthBill() {
		payAndCheckBill(installmentDueDates.get(4));
	}

	protected void generateFifthBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(5), softly);
	}

	protected void payFifthBill() {
		payAndCheckBill(installmentDueDates.get(5));
	}

	protected void generateSixthBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(6), softly);
	}

	protected void paySixthBill() {
		payAndCheckBill(installmentDueDates.get(6));
	}

	protected void generateSeventhBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(7), softly);
	}

	protected void paySeventhBill() {
		payAndCheckBill(installmentDueDates.get(7));
	}

	protected void generateEighthBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(8), softly);
	}

	protected void payEighthBill() {
		payAndCheckBill(installmentDueDates.get(8));
	}

	protected void generateNinthBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(9), softly);
	}

	protected void payNinthBill() {
		payAndCheckBill(installmentDueDates.get(9));
	}

	protected void generateTenthBill() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(10));
		if (DateTimeUtils.getCurrentDateTime().isAfter(billDate)) {
			billDate = DateTimeUtils.getCurrentDateTime();
		}
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(10), billDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		if (DateTimeUtils.getCurrentDateTime().isAfter(renewImageGenDate)) {
			renewImageGenDate = DateTimeUtils.getCurrentDateTime();
		}
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewImageGenDate);
	}

	protected void payTenthBill() {
		payAndCheckBill(installmentDueDates.get(10));
	}

	protected void renewalPreviewGeneration() {
		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		if (DateTimeUtils.getCurrentDateTime().isAfter(renewPreviewGenDate)) { //case: payTenthBill() is Saturday -> Monday
			renewPreviewGenDate = DateTimeUtils.getCurrentDateTime();
		}
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		verifyRenewOfferGenerated(installmentDueDates, softly);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount, softly);
		}

		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}
	}

	// Skip this step for CA
	protected void renewalPremiumNotice() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);

		// verify using installment amount in separate cases
		if (getState().equals(States.KY) && getPolicyType().equals(PolicyType.AUTO_SS)) {
			verifyRenewalOfferPaymentAmountByIntallmentAmount(policyExpirationDate, billGenDate);
		} else {
			verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billGenDate, pligaOrMvleFee, installmentsCount);
		}
		verifyRenewPremiumNotice(policyExpirationDate, billGenDate, pligaOrMvleFee);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void verifyDocGenForms() {
		if (getState().equals(Constants.States.CA)) {
			verifyDocGenForms(new DocGenEnum.Documents[]{DocGenEnum.Documents.AHRBXX});
		} else {
			verifyDocGenForms(new DocGenEnum.Documents[]{DocGenEnum.Documents.AHRBXX, DocGenEnum.Documents.AH35XX});
		}
	}

	protected void verifyDocGenForms(DocGenEnum.Documents[] documents) {
		verifyDocGenForms(true, documents);
	}
	

	protected void verifyDocGenForms(boolean generated, DocGenEnum.Documents... documents) {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(generated, true, policyNum, documents);
	}

	protected void removeAutoPay() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
		billingAccount.update().start();
		assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(false);
		Tab.buttonCancel.click();
	}

	protected void renewalPaymentNotGenerated() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setType(PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).verifyPresent(false);
	}

	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		
	}

	protected void makeManualPaymentInFullRenewalOfferAmount() {
		LocalDateTime renewCustomerDecline = getTimePoints().getTimepoint(policyExpirationDate, TimePoints.TimepointsList.RENEW_CUSTOMER_DECLINE, false).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(renewCustomerDecline);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar sum = BillingHelper.getPolicyRenewalProposalSum(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), policyNum);
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_CC"), sum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
	
}
