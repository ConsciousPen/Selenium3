package aaa.modules.e2e.templates;

import aaa.common.Tab;
import aaa.common.enums.Constants;
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
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;
import java.util.List;

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
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Monthly (Eleven Pay) payment plan", installmentsCount, installmentDueDates.size());

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	protected void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
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
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
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
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(2), secondBillGenDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(secondBillGenDate)
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void paySecondBill() {
		generateAndCheckBill(installmentDueDates.get(2));
	}

	protected void generateThirdBill() {
		generateAndCheckBill(installmentDueDates.get(3), policyEffectiveDate);
	}

	protected void payThirdBill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	protected void generateFourthBill() {
		generateAndCheckBill(installmentDueDates.get(4));
	}

	protected void payFourthBill() {
		payAndCheckBill(installmentDueDates.get(4));
	}

	protected void generateFifthBill() {
		generateAndCheckBill(installmentDueDates.get(5));
	}

	protected void payFifthBill() {
		payAndCheckBill(installmentDueDates.get(5));
	}

	protected void generateSixthBill() {
		generateAndCheckBill(installmentDueDates.get(6));
	}

	protected void paySixthBill() {
		payAndCheckBill(installmentDueDates.get(6));
	}

	protected void generateSeventhBill() {
		generateAndCheckBill(installmentDueDates.get(7));
	}

	protected void paySeventhBill() {
		payAndCheckBill(installmentDueDates.get(7));
	}

	protected void generateEighthBill() {
		generateAndCheckBill(installmentDueDates.get(8));
	}

	protected void payEighthBill() {
		payAndCheckBill(installmentDueDates.get(8));
	}

	protected void generateNinthBill() {
		generateAndCheckBill(installmentDueDates.get(9));
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
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(10), getTimePoints().getBillGenerationDate(installmentDueDates.get(10)));
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(installmentDueDates.get(10)))
				.setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
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
		if (getState().equals("MD") && DateTimeUtils.getCurrentDateTime().isAfter(renewPreviewGenDate)) {
			renewPreviewGenDate = DateTimeUtils.getCurrentDateTime();
		}
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration() {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
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

		verifyRenewOfferGenerated(policyExpirationDate, installmentDueDates);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount);
		}

		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}
	}

	//Skip this step for CA
	protected void renewalPremiumNotice() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);

		// TODO Renew premium verification was excluded, due to unexpected installment calculations
//		if (!getState().equals(States.KY) && !getState().equals(States.WV)) {
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billGenDate, pligaOrMvleFee, installmentsCount);
//		}
		verifyRenewPremiumNotice(policyExpirationDate, billGenDate, pligaOrMvleFee);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void verifyDocGenForms() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenEnum.Documents[] documents;
		if (getState().equals(Constants.States.CA)) {
			documents = new DocGenEnum.Documents[]{DocGenEnum.Documents._61_3026, DocGenEnum.Documents.AHRBXX, DocGenEnum.Documents._61_3000, DocGenEnum.Documents._61_5121, DocGenEnum.Documents._61_6530};
		} else {
			documents = new DocGenEnum.Documents[]{DocGenEnum.Documents.AH35XX, DocGenEnum.Documents.AHRBXX};
		}
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, documents);
	}

	protected void removeAutoPay() {
		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime());
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_RemoveAutopay"));
		billingAccount.update().start();
		new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(false);
		Tab.buttonCancel.click();
	}

	protected void renewalPaymentNotGenerated() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setType(PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).verifyPresent(false);
	}

	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void makeManualPaymentInFullRenewalOfferAmount() {
		LocalDateTime renewCustomerDecline = getTimePoints().getTimepoint(policyExpirationDate, TimePoints.TimepointsList.RENEW_CUSTOMER_DECLINE, false);
		TimeSetterUtil.getInstance().nextPhase(renewCustomerDecline);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		Dollar sum = BillingHelper.getPolicyRenewalProposalSum(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_CC"), sum);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
}
