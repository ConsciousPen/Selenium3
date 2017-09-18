package aaa.modules.e2e.templates;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.RemittancePaymentsHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillsAndStatementsType;
import aaa.main.enums.BillingConstants.ExternalPaymentSystem;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionStatus;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.MyWorkConstants;
import aaa.main.enums.PolicyConstants.PolicyRenewalsTable;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

public class Scenario7 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 4;

	protected Tab premiumTab;
	protected CommonErrorTab errorTab;

	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();

		TimeSetterUtil.getInstance().adjustTime(); // *** Debug
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
		CustomAssert.assertEquals("Billing Installments count for Quaterly payment plan", installmentsCount, installmentDueDates.size());
	}

	protected void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	protected void payFirstBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).setType(
			PaymentsAndOtherTransactionType.PAYMENT).verifyPresent(false);

		Dollar billDue = BillingHelper.getBillDueAmount(installmentDueDates.get(1), "Bill");
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), billDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setAmount(billDue.negate()).setType(PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(
			PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT).setStatus(PaymentsAndOtherTransactionStatus.CLEARED).verifyPresent();
	}

	protected void enableAutopay() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openBilling(policyNum);

		BillingSummaryPage.linkUpdateBillingAccount.click();
		new UpdateBillingAccountActionTab().fillTab(getTestSpecificTD("TestData_EnableAutopay"));
		UpdateBillingAccountActionTab.buttonSave.click();
	}

	protected void generateSecondBill() {
		generateAndCheckBill(installmentDueDates.get(2));
	}

	public void paySecondBill() {
		payAndCheckBill(installmentDueDates.get(2));
	}

	protected void generateThirdBill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	public void payThirdBill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	// TODO (temporaly?) skipped for property products
	protected void cantChangePaymentPlan() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.endorse().perform(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_EndorsementPlan"), premiumTab.getClass(), true);
		// TODO Update error code
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_TODO);
		errorTab.cancel();

		// TODO Verify There is no Payment Plan discount.

		premiumTab.buttonCancel.click();
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.deletePendedTransaction().start().submit();
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
	}

	protected void payTotalDue() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		BillingSummaryPage.getTotalDue().verify.equals(new Dollar(0));
		for (int i = 3; i < installmentsCount; i++) {
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(i)).verify.equals(new Dollar(0));
		}
	}

	// protected void generateThirdBill() {
	// LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(3));
	// TimeSetterUtil.getInstance().nextPhase(billGenDate);
	// JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
	// mainApp().open();
	// SearchPage.openBilling(policyNum);
	// new
	// BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(3)).verifyPresent(false);
	// new
	// BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.FEE).setTransactionDate(billGenDate).verifyPresent(false);
	// new
	// BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT).verify(1);
	// }

	protected void generateTenthBill() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(10));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.tableBillsStatements.verify.rowsCount(2);
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT).verify(1);
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalNotGenerated(renewImageGenDate);
	}

	protected void renewalPreviewGeneration() {
		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	/**
	 * Policy Renewal Preview is available 1. Mid-term change i.e. RP
	 * Endorsement is performed effective Renewal Term while the renewal quote
	 * is in Premium Calculated Status(i.e, prior to Proposed Status) 2.
	 * Additional premium for Renewal term is recalculated due to Mid - term
	 * change US 15596
	 */
	protected void TC12_Endorsement_RP_Before_Renewal() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		Dollar premiumBeforeEndorsement = PolicySummaryPage.getProposedRenewalPremium();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDueBeforeEndorsment = BillingSummaryPage.getTotalDue();
		BillingSummaryPage.openPolicy(policyEffectiveDate);

		policy.endorse().performAndFill(getTestSpecificTD("TestData_EndorsementRP").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData")));
		PolicyHelper.verifyEndorsementIsCreated();

		CustomAssert.enableSoftMode();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
		Dollar premiumAfterEndorsement = new Dollar(PolicySummaryPage.tableRenewals.getColumn(PolicyRenewalsTable.PREMIUM).getCell(1).getValue());
		premiumAfterEndorsement.verify.lessThan(premiumBeforeEndorsement);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.getTotalDue().verify.lessThan(totalDueBeforeEndorsment);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * Policy Renewal Preview is available 1. Mid-term change i.e. AP
	 * Endorsement is performed effective Renewal Term while the renewal quote
	 * is in Premium Calculated Status(i.e, prior to Proposed Status) 2.
	 * Additional premium for Renewal term is recalculated due to Mid - term
	 * change US 15595
	 */
	protected void TC13_Endorsement_AP_Before_Renewal() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		Dollar premiumBeforeEndorsement = PolicySummaryPage.getProposedRenewalPremium();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDueBeforeEndorsment = BillingSummaryPage.getTotalDue();
		BillingSummaryPage.openPolicy(policyEffectiveDate);

		policy.endorse().performAndFill(getTestSpecificTD("TestData_EndorsementRP").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData")));
		PolicyHelper.verifyEndorsementIsCreated();

		CustomAssert.enableSoftMode();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
		Dollar premiumAfterEndorsement = new Dollar(PolicySummaryPage.tableRenewals.getColumn(PolicyRenewalsTable.PREMIUM).getCell(1).getValue());
		premiumAfterEndorsement.verify.moreThan(premiumBeforeEndorsement);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		// TODO ?
		// BillingSummaryPage.getTotalDue().verify.equals(premiumAfterEndorsement.subtract(premiumBeforeEndorsement));
		BillingSummaryPage.getTotalDue().verify.moreThan(totalDueBeforeEndorsment);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
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
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount);
		}
	}

	protected void endorsementRPAfterRenewal() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		Dollar premiumBeforeEndorsement = PolicySummaryPage.getProposedRenewalPremium();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDueBeforeEndorsment = BillingSummaryPage.getTotalDue();
		BillingSummaryPage.openPolicy(policyEffectiveDate);

		policy.endorse().performAndFill(getTestSpecificTD("TestData_EndorsementRP").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData")));
		PolicyHelper.verifyEndorsementIsCreated();

		CustomAssert.enableSoftMode();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
		Dollar premiumAfterEndorsement = new Dollar(PolicySummaryPage.tableRenewals.getColumn(PolicyRenewalsTable.PREMIUM).getCell(1).getValue());
		premiumAfterEndorsement.verify.lessThan(premiumBeforeEndorsement);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.getTotalDue().verify.lessThan(totalDueBeforeEndorsment);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	protected void endorsementAPAfterRenewal() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		Dollar premiumBeforeEndorsement = PolicySummaryPage.getProposedRenewalPremium();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDueBeforeEndorsment = BillingSummaryPage.getTotalDue();
		BillingSummaryPage.openPolicy(policyEffectiveDate);

		policy.endorse().performAndFill(getTestSpecificTD("TestData_EndorsementRP").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData")));
		PolicyHelper.verifyEndorsementIsCreated();

		CustomAssert.enableSoftMode();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);

		Dollar premiumAfterEndorsement = new Dollar(PolicySummaryPage.tableRenewals.getColumn(PolicyRenewalsTable.PREMIUM).getCell(1).getValue());
		premiumAfterEndorsement.verify.moreThan(premiumBeforeEndorsement);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		// TODO ?
		// BillingSummaryPage.getTotalDue().verify.equals(premiumAfterEndorsement.subtract(premiumBeforeEndorsement));
		BillingSummaryPage.getTotalDue().verify.moreThan(totalDueBeforeEndorsment);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	// Skip this step for CA
	protected void renewalPremiumNotice() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billDate, installmentsCount);
		verifyRenewPremiumNotice(policyExpirationDate, getTimePoints().getBillGenerationDate(policyExpirationDate));
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void checkRenewalStatusAndPaymentNotGenerated() {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(policyExpirationDate)).setType(PaymentsAndOtherTransactionType.FEE).verify(1); // No
																																																// new
																																																// transactions
	}

	protected void expirePolicy() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	protected void generateFirstRenewalBill() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1).plusYears(1));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(1).plusYears(1)).verifyPresent(false);
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.FEE).setTransactionDate(billGenDate).verifyPresent(false);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(policyExpirationDate)).setType(PaymentsAndOtherTransactionType.FEE).verify(1); // No
																																																// new
																																																// transactions
	}

	protected void customerDeclineRenewal() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);

		// TODO "Non EFT Installment Fee Waived" and "Renewal - Policy Renewal Proposal Reversal" transactions are
		// generated on Payments & Other Transactions section.
	}

	protected void createRemittanceFile() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPayLapsedRenewLong(policyExpirationDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1).plusYears(1), BillingBillsAndStatmentsTable.MINIMUM_DUE));
		File remitanceFile = RemittancePaymentsHelper.createRemittanceFile(getState(), policyNum, minDue, ExternalPaymentSystem.REGONLN);
		RemittancePaymentsHelper.copyRemittanceFileToServer(remitanceFile);
	}

	protected void payRenewalBillByRemittance() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		JobUtils.executeJob(Jobs.remittanceFeedBatchReceiveJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1).plusYears(1), BillingBillsAndStatmentsTable.MINIMUM_DUE));

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setAmount(minDue.negate()).setType(PaymentsAndOtherTransactionType.PAYMENT).setStatus(
			PaymentsAndOtherTransactionStatus.ISSUED).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REGULUS_ONLINE).verifyPresent();

		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);

		BillingSummaryPage.openPolicy(policyExpirationDate);
		PolicySummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME, "Qualify for manual renewal").verify.present(false);
	}

	protected void qualifyForManualRenewalTaskCreated() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME, "Qualify for manual renewal").verify.present();

		// TODO verify all task info
	}
}