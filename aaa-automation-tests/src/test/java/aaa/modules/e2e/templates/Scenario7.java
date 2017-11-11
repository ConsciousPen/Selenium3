package aaa.modules.e2e.templates;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.table.Table;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
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
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;

	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 11;

	protected Tab premiumTab;
	protected CommonErrorTab errorTab;
	protected Table tableDiscounts;

	protected String policyTerm;
	protected Integer totalVehiclesNumber;

	protected void createTestPolicy(TestData policyCreationTD) {
		// TimeSetterUtil.getInstance().adjustTime(); // Debug
		policy = getPolicyType().get();

		mainApp().open();
		createCustomerIndividual();

		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}

		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);

		policyNum = createPolicy(policyCreationTD);

		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Monthly (Eleven Pay) payment plan", installmentsCount, installmentDueDates.size());

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}

	protected void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}

	protected void payFirstBill() {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT).setType(
			PaymentsAndOtherTransactionType.PAYMENT).verifyPresent(false);

		Dollar billDue = BillingHelper.getBillDueAmount(installmentDueDates.get(1), "Bill");
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), billDue);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDueDate).setAmount(billDue.negate()).setType(PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(
			PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT).setStatus(PaymentsAndOtherTransactionStatus.CLEARED).verifyPresent();
	}

	protected void generateSecondBill() {
		generateAndCheckBill(installmentDueDates.get(2));
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

	protected void generateThirdBill() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(3));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingBillsAndStatementsVerifier().setType(BillsAndStatementsType.BILL).setDueDate(installmentDueDates.get(3)).verifyPresent(false);
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.FEE).setTransactionDate(billGenDate).verifyPresent(false);
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

		PolicyHelper.verifyAutomatedRenewalGenerated(renewImageGenDate);
	}

	protected void generateTenthBill() {
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(10));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		BillingSummaryPage.tableBillsStatements.verify.rowsCount(2);
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.PAYMENT).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.MANUAL_PAYMENT).verify(1);
	}

	protected void cantChangePaymentPlan() {
		// verify that error page is opened with with message "It is too late in the term to change to the selected bill plan."
		// PAS 13 RCA fix: US 150-141-3CL The error appears only if the payment plan is changed on a day greater than effective date+10 months-20 days+1 day
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getOffcycleBillGenerationDate(installmentDueDates.get(10)).plusDays(2));

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		policy.endorse().perform(getStateTestData(tdPolicy, "Endorsement", "TestData"));
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_EndorsementPlan"), premiumTab.getClass(), true);

		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_SS9140068);
		errorTab.cancel();

		CustomAssert.assertFalse(tableDiscounts.getRow(1).getCell(1).getValue().contains("Payment Plan Discount"));

		premiumTab.cancel();
		Page.dialogConfirmation.buttonDeleteEndorsement.click();

		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
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
	protected void endorsementRPBeforeRenewal() {
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
	protected void endorsementAPBeforeRenewal() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		Dollar premiumBeforeEndorsement = PolicySummaryPage.getProposedRenewalPremium();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDueBeforeEndorsment = BillingSummaryPage.getTotalDue();
		BillingSummaryPage.openPolicy(policyEffectiveDate);

		policy.endorse().performAndFill(getTestSpecificTD("TestData_EndorsementAP").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData")));
		PolicyHelper.verifyEndorsementIsCreated();

		CustomAssert.enableSoftMode();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
		Dollar premiumAfterEndorsement = new
			Dollar(PolicySummaryPage.tableRenewals.getColumn(PolicyRenewalsTable.PREMIUM).getCell(1).getValue());
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

		if (getState().equals(States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount);
		}

		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}
	}

	protected void endorsementRPAfterRenewal() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		Dollar premiumBeforeEndorsement = PolicySummaryPage.getProposedRenewalPremium();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDueBeforeEndorsment = BillingSummaryPage.getTotalDue();
		BillingSummaryPage.openPolicy(policyExpirationDate);

		policy.endorse().performAndFill(getTestSpecificTD("TestData_EndorsementRP").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData")));
		PolicyHelper.verifyEndorsementIsCreated();

		CustomAssert.enableSoftMode();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
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
		BillingSummaryPage.openPolicy(policyExpirationDate);

		policy.endorse().performAndFill(getTestSpecificTD("TestData_EndorsementAP").adjust(getStateTestData(tdPolicy, "Endorsement", "TestData")));
		PolicyHelper.verifyEndorsementIsCreated();

		CustomAssert.enableSoftMode();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);

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

		//TODO-dchubkov: to be changed PLIGA & Fee calculation...
		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billDate, pligaOrMvleFee, installmentsCount);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE).verifyPresent();
		if (getState().equals(States.NY)) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate)).setSubtypeReason(
					PaymentsAndOtherTransactionSubtypeReason.MVLE_FEE).verifyPresent();
		} else if (getState().equals(States.NJ)) {
			new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate)).setSubtypeReason(
					PaymentsAndOtherTransactionSubtypeReason.PLIGA_FEE).verifyPresent();
		}
	}

	protected void checkRenewalStatusAndPaymentNotGenerated() {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		// No new transactions
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(policyExpirationDate)).setType(PaymentsAndOtherTransactionType.FEE).verify(1);
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
		// No new transactions
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.FEE).setTransactionDate(billGenDate).verifyPresent(false);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(getTimePoints().getBillGenerationDate(policyExpirationDate)).setType(PaymentsAndOtherTransactionType.FEE).verify(1);
	}

	protected void customerDeclineRenewal() {
		LocalDateTime declineDate = getTimePoints().getRenewCustomerDeclineDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(declineDate);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.CUSTOMER_DECLINED).verifyRowWithEffectiveDate(policyExpirationDate);
		// TODO "Non EFT Installment Fee Waived" and "Renewal - Policy Renewal Proposal Reversal" transactions are generated on Payments & Other Transactions section
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(declineDate).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL_REVERSAL)
			.verifyPresent();
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(declineDate).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE_WAIVED).verifyPresent();
	}

	protected void createRemittanceFile() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getPayLapsedRenewLong(policyExpirationDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingBillsAndStatmentsTable.MINIMUM_DUE));
		File remitanceFile = RemittancePaymentsHelper.createRemittanceFile(getState(), policyNum, minDue, ExternalPaymentSystem.REGONLN);
		RemittancePaymentsHelper.copyRemittanceFileToServer(remitanceFile);
	}

	protected void payRenewalBillByRemittance() {
		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
		TimeSetterUtil.getInstance().nextPhase(paymentDate);
		JobUtils.executeJob(Jobs.remittanceFeedBatchReceiveJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingBillsAndStatmentsTable.MINIMUM_DUE));

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(paymentDate).setAmount(minDue.negate()).setType(PaymentsAndOtherTransactionType.PAYMENT).setStatus(
			PaymentsAndOtherTransactionStatus.ISSUED).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.REGULUS_ONLINE).verifyPresent();

		// TODO Why Auto policy status Customer Declined, Home policy status Active?
		String policyStatus;
		if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT) || getPolicyType().equals(PolicyType.AUTO_SS)) {
			policyStatus = PolicyStatus.CUSTOMER_DECLINED;
		} else {
			policyStatus = PolicyStatus.POLICY_ACTIVE;
		}
		new BillingAccountPoliciesVerifier().setPolicyStatus(policyStatus).verifyRowWithEffectiveDate(policyExpirationDate);

		BillingSummaryPage.openPolicy(policyExpirationDate);
		PolicySummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME, "Qualify for manual Renewal").verify.present(false);
	}

	protected void qualifyForManualRenewalTaskCreated() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME, "Qualify for manual Renewal").verify.present();

		// TODO ? verify all task info
	}
}
