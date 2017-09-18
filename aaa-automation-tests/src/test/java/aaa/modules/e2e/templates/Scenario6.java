package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
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
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.BillingConstants.BillsAndStatementsType;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.BillingConstants.PolicyFlag;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

public class Scenario6 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;
	protected List<Dollar> installmentsAmounts;
	protected Dollar installmentAmount;
	protected Dollar totalDue;
	protected Dollar minDue;
	protected Dollar endorsementAmount;
	protected Dollar installmentsSum = new Dollar(0);

	protected String[] endorsementReasonDataKeys;
	protected int installmentsCount = 4;

	public void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();

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
		CustomAssert.assertEquals("Billing Installments count for Quaterly payment plan", installmentsCount, installmentDueDates.size());
		installmentAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1));
	}

	public void Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(1));

		totalDue = BillingSummaryPage.getTotalDue();
		minDue = BillingSummaryPage.getMinimumDue();
		installmentsAmounts = BillingHelper.getInstallmentDues();
	}

	// TODO Check: Removed according to 20427:US CL GD Generate Premium Due Notice v2.0. Form has been renamed to AHIBXX
	public void Verify_Form_AHIBXX() {
		// TODO DocGen utils
		// DocGenHelper.verifyDocumentsGeneratedByJob(TimeSetterUtil.getInstance().getCurrentTime(), policyNum,
		// OnDemandDocuments.AHIBXX);
	}

	public void Endorse_Policy() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		if (getPolicyType().equals(PolicyType.PUP) || getPolicyType().equals(PolicyType.AUTO_SS)) {
			policy.endorse().perform(endorsementTD);
			removeSecondVehicle();
			policy.getDefaultView().fill(getTestSpecificTD("TestData_Endorsement"));
		} else {
			policy.endorse().performAndFill(endorsementTD.adjust(getTestSpecificTD("TestData_EndorsementRP")));
		}
		PolicyHelper.verifyEndorsementIsCreated();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(TimeSetterUtil.getInstance().getPhaseStartTime()).setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason).verifyPresent();
		endorsementAmount = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, reason).getCell(
			BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());

		// Min due on General info section is reduced after RP Endorse
		new Dollar(BillingSummaryPage.getMinimumDue()).verify.lessThan(minDue);

		// Min due on Billing Account Policies section is reduced after RP Endorse
		new Dollar(BillingHelper.getPolicyMinimumDueAmount(policyNum)).verify.lessThan(minDue);

		// Min due on Bills & Statements section is NOT changed after RP Endorse
		new Dollar(BillingHelper.getBillMinDueAmount(installmentDueDates.get(1), BillsAndStatementsType.BILL)).verify.equals(minDue);

		// Total due on General info section is reduced after RP Endorse
		new Dollar(BillingSummaryPage.getTotalDue()).verify.lessThan(totalDue);

		// Total due on Billing Account Policies section is reduced after RP Endorse
		new Dollar(BillingHelper.getPolicyTotalDueAmount(policyNum)).verify.lessThan(totalDue);

		// Total due on Bills & Statements section is NOT changed after RP Endorse
		new Dollar(BillingHelper.getBillTotalDueAmount(installmentDueDates.get(1), BillsAndStatementsType.BILL)).verify.equals(totalDue);

		// TODO
		// Replace with verify.less if 42369 Defect will be fixed
		for (int i = 2; i < installmentsAmounts.size(); i++) {
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(i)).verify.equals(installmentsAmounts.get(i));
		}
	}

	public void Pay_First_Bill() {
		payCashAndCheckBill(installmentDueDates.get(1));
	}

	public void Generate_CancellNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(PolicyFlag.DEFAULT).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	public void Generate_Second_Bill() {
		LocalDateTime genDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(genDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar billAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(2));
		billAmount = billAmount.add(BillingHelper.getFeesValue(getTimePoints().getBillGenerationDate(installmentDueDates.get(2))));
		new BillingBillsAndStatementsVerifier().setDueDate(installmentDueDates.get(2)).setType(BillsAndStatementsType.BILL).setMinDue(billAmount).verifyPresent();
	}

	public void Pay_Second_Bill() {
		payCashAndCheckBill(installmentDueDates.get(2));
	}

	public void Generate_Third_Bill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	public void Pay_Third_Bill() {
		payCashAndCheckBill(installmentDueDates.get(3));
	}

	public void Set_Do_Not_Renew_Flag() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		policy.doNotRenew().perform(getStateTestData(tdPolicy, "DoNotRenew", "TestData"));
		PolicySummaryPage.verifyDoNotRenewFlagPresent();

	}

	public void Renewal_Image_Generation() {
		LocalDateTime policyExpirationDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalNotGenerated(policyExpirationDateImage);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
	}

	public void Renewal_Preview_Generation() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
	}

	public void Renewal_Offer_Generation() {
		LocalDateTime policyExpirationDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(policyExpirationDate).setType(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent(false);
	}

	public void Manual_Renew_Policy() {
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		policy.manualRenew().perform(getStateTestData(tdPolicy, "ManualRenew", "TestData"));
		PolicySummaryPage.verifyManualRenewFlagPresent();

		policy.createRenewal(getStateTestData(tdPolicy, "Renew", "TestData_Today").adjust(getTestSpecificTD("TestData_ProposeRenew")));

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(policyExpirationDate, installmentDueDates);

		for (LocalDateTime date : installmentDueDates) {
			installmentsSum = installmentsSum.add(BillingHelper.getInstallmentDueByDueDate(date.plusYears(1)));
		}
		totalDue = BillingSummaryPage.getTotalDue();
	}

	public void Verify_Form_AHR1XX_And_HSRNXX() {
		// TODO DocGen utils
		// DocGenHelper.verifyDocumentsGeneratedByJob(TimeSetterUtil.getInstance().getCurrentTime(), policyNum,
		// Arrays.asList(OnDemandDocuments.AHRBXX, OnDemandDocuments.HSRNXX));
	}

	public void Pay_Renew_Offer() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(policyExpirationDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar overpayment = new Dollar(200);
		Dollar renewOfferAmount = BillingHelper.getBillMinDueAmount(policyExpirationDate, BillsAndStatementsType.OFFER).add(overpayment);

		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), renewOfferAmount);

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar renewInstallmentsSum = new Dollar(0);
		for (LocalDateTime date : installmentDueDates) {
			renewInstallmentsSum = renewInstallmentsSum.add(BillingHelper.getInstallmentDueByDueDate(date.plusYears(1)));
		}

		// Overpayment is moved to the next Term. Total Due is reduced
		installmentsSum.subtract(overpayment).verify.equals(renewInstallmentsSum);
		totalDue.subtract(renewOfferAmount).verify.equals(BillingSummaryPage.getTotalDue());
	}

	public void Update_Policy_Status() {
		LocalDateTime date = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		if (!getPolicyType().equals(PolicyType.PUP) && !getPolicyType().equals(PolicyType.AUTO_SS))
			new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	public void Automatic_Refund_Not_Generated() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRefundDate(getTimePoints().getBillDueDate(policyExpirationDate)));
		JobUtils.executeJob(Jobs.refundGenerationJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.REFUND).setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.AUTOMATED_REFUND).setReason(
			PaymentsAndOtherTransactionReason.OVERPAYMENT).verifyPresent(false);
	}

	private void payCashAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDate, BillingBillsAndStatmentsTable.MINIMUM_DUE));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}

	protected void removeSecondVehicle() {
		// TODO Auto-generated method stub
	}
}