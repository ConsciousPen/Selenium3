package aaa.modules.e2e.templates;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.*;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;
import java.util.List;


public class Scenario6 extends ScenarioBaseTest {

	protected IPolicy policy;
	protected TestData tdPolicy;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;
	protected Dollar installmentAmount;
	protected Dollar totalDue;
	protected Dollar minDue;
	protected Dollar billAmount;

	protected String[] endorsementReasonDataKeys;
	protected int installmentsCount = 4;
	protected Dollar TOLLERANCE_AMOUNT = new Dollar(10);

	public void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();

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

//	public void TC02_Generate_First_Bill() {
//		generateAndCheckBill(installmentDueDates.get(1));
//		totalDue = getGeneralInfoValue(BillingGeneralInformationTable.TOTAL_DUE);
//		minDue = getGeneralInfoValue(BillingGeneralInformationTable.MINIMUM_DUE);
//	}
//
//	//TODO Check: Removed according to  20427:US CL GD Generate Premium Due Notice v2.0. Form has been renamed to AHIBXX
//	public void TC03_Verify_Form_AHIBXX() {
//		//TODO DocGen utils
//		//DocGenHelper.verifyDocumentsGeneratedByJob(TimeSetterUtil.getInstance().getCurrentTime(), policyNum, OnDemandDocuments.AHIBXX);
//	}

//	public void TC04_Endorse_Policy() {
//		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
//		String endorsementDataset = "DefaultDataset";
//		mainApp().open();
//		SearchPage.openPolicy(policyNum);
//		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
//		policy.endorse().performAndFill(getTestSpecificTD("TestData_Endorsement").adjust(endorsementTD));
//		PolicyHelper.verifyEndorsementIsCreated();
//
//		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
//		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
//		new BillingPaymentsAndTransactionsVerifier()
//				.setTransactionDate(TimeSetterUtil.getInstance().getPhaseStartTime())
//				.setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
//				.setSubtypeReason(reason).verifyPresent();
//
//		//TODO check all verifications logic, rename some variables!
//		CustomAssert.enableSoftMode();
//		CustomAssert.assertTrue("Min due on General info section is NOT reduced after RP Endorse", getGeneralInfoValue(GeneralInfoColumns.MINIMUM_DUE).lessThan(minDue));
//		Dollar newMinDue = getPolicyAccValue(AccountPoliciesColumns.MIN_DUE);
//		CustomAssert.assertTrue("Min due on Billing Account Policies section is NOT resuced after RP Endorse", newMinDue.lessThan(minDue));
//		CustomAssert.assertTrue("Min due on Bills & Statements section is changed after RP Endorse", getBillValue(installmentDueDates.get(1), BillColumns.MINIMUM_DUE).equals(minDue));
//
//		CustomAssert.assertTrue("Total due on General info section is NOT reduced after RP Endorse, but should become less than before endorse; ", getGeneralInfoValue(GeneralInfoColumns.TOTAL_DUE)
//				.lessThan(totalDue));
//		CustomAssert.assertTrue("Total due on Billing Account Policies section is NOT resuced after RP Endorse, but should become less than before endorse; ",
//				getPolicyAccValue(AccountPoliciesColumns.TOTAL_DUE).lessThan(totalDue));
//		CustomAssert.assertTrue("Total due on Bills & Statements section is changed after RP Endorse, but should be the same as before endorse; ", getBillValue(installmentDueDates.get(1), BillColumns.TOTAL_DUE)
//				.equals(totalDue));
//
//		BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1)).verify.lessThan(installmentAmount);
//		BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(2)).verify.lessThan(installmentAmount);
//		BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(3)).verify.lessThan(installmentAmount);
//
//		billAmount = newMinDue;
//
//		CustomAssert.disableSoftMode();
//		CustomAssert.assertAll();
//	}
//
//	public void TC05_Pay_First_Bill_Substract_Tollerance_Amount() {
//		payAndCheckBill(installmentDueDates.get(1), billAmount.subtract(TOLLERANCE_AMOUNT));
//	}
//
//	public void TC06_Generate_CancellNotice() {
//		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1));
//		TimeSetterUtil.getInstance().nextPhase(cnDate);
//		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
//
//		mainApp().open();
//		SearchPage.openPolicy(policyNum);
//		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
//		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
//		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
//		BillingHelper.verifyPolicyFlag(policyEffDate, PolicyFlag.DEFAULT);
//	}
//
//	public void TC07_Generate_Second_Bill() {
//		LocalDateTime genDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
//		TimeSetterUtil.getInstance().nextPhase(genDate);
//		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
//		mainApp().open();
//		SearchPage.openBilling(policyNum);
//
//		Dollar billAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(2));
//		billAmount = billAmount.add(BillingHelper.getFeeValue(getTimePoints().getBillGenerationDate(installmentDueDates.get(2)))).add(TOLLERANCE_AMOUNT);
//		HashMap<BillColumns, String> values = new HashMap<BillColumns, String>();
//		values.put(BillColumns.DATE, installmentDueDates.get(2).toString(DATE_FORMAT));
//		values.put(BillColumns.TYPE, "Bill");
//		values.put(BillColumns.MINIMUM_DUE, billAmount.toString());
//		values.put(BillColumns.PAST_DUE, TOLLERANCE_AMOUNT.toString());
//		BillingHelper.getBillRow(values).verify.present();
//	}
//
//	public void TC08_Pay_Second_Bill() {
//		payAndCheckBill(installmentDueDates.get(2), null);
//	}
//
//	public void TC09_Generate_Third_Bill() {
//		generateAndCheckBill(installmentDueDates.get(3));
//	}
//
//	public void TC10_Pay_Third_Bill() {
//		payAndCheckBill(installmentDueDates.get(3), null);
//	}
//
//	public void TC11_Set_Do_Not_Renew_Flag() {
//		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
//		mainApp().open();
//		SearchPage.openPolicy(policyNum);
//		PolicySummaryPage.setActionAndGo(PolicyAction.DO_NOT_RENEW);
//		DoNotRenewEditorPanel.fillDoNotRenewTab("DoNotRenew");
//		DoNotRenewEditorPanel.clickOkButton();
//		PolicySummaryPage.verifyDoNotRenewFlagPresent();
//
//	}
//
//	public void TC12_Renewal_Image_Generation() {
//		LocalDateTime policyExpirationDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
//		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateImage);
//		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
//		HttpStub.executeAllBatches();
//		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
//
//		mainApp().open();
//		SearchPage.openPolicy(policyNum);
//		PolicyHelper.verifyAutomatedRenewalNotGenerated(policyExpirationDateImage);
//		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
//	}
//
//	public void TC13_Renewal_Preview_Generation() {
//		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
//		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
//
//		mainApp().open();
//		SearchPage.openPolicy(policyNum);
//		PolicySummaryPage.buttonRenewals.verify.enabled(false);
//		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
//	}
//
//	public void TC14_Renewal_Offer_Generation() {
//		LocalDateTime policyExpirationDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
//		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateOffer);
//		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
//
//		mainApp().open();
//		SearchPage.openPolicy(policyNum);
//		PolicySummaryPage.buttonRenewals.verify.enabled(false);
//		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
//
//		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
//		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
//		BillingHelper.verifyRenewTransactionNotGenerated(policyExpirationDate);
//	}
//
//	public void TC15_Manual_Renew_Policy() {
//		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
//		mainApp().open();
//		SearchPage.openPolicy(policyNum);
//		PolicySummaryPage.setActionAndGo(PolicyAction.REMOVE_DO_NOT_RENEW);
//		RemoveDoNotRenewEditorPanel.clickOkButton();
//
//		PolicySummaryPage.setActionAndGo(PolicyAction.MANUAL_RENEW);
//		ManualRenewEditorPanel.fillManualRenewTab("Dataset1");
//		ManualRenewEditorPanel.clickOkButton();
//		PolicySummaryPage.verifyManualRenewFlagPresent();
//
//		PolicySummaryPage.setActionAndGo(PolicyAction.RENEW);
//		RenewEditorPanel.fillManualRenewTab("DefaultDataset");
//		RenewEditorPanel.clickOkButton();
//		EditorPanel.navigateTo(TabNames.QuoteHSSTabs.PREMIUMS_AND_COVERAGES_QUOTE.get());
//		PremiumsAndCoveragesTabEditorPanel.rate();
//		EditorPanel.navigateTo(TabNames.QuoteHSSTabs.BIND.get());
//		BindTabEditorPanel.proposeRenew();
//
//		PolicySummaryPage.buttonRenewals.click();
//		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
//		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
//		BillingSummaryPage.showPriorTerms();
//		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
//		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
//		verifyRenewOfferGenerated(policyExpirationDate, installmentDueDates);
//
//		ArrayList<LocalDateTime> dates = FileHelper.getRenewInstallmentsDueDates(PaymentPlan.QUARTERLY);
//		for (LocalDateTime date : dates) {
//			installmentsSum = installmentsSum.add(BillingHelper.getInstallmentDueByDueDate(date));
//		}
//		totalDue = getGeneralInfoValue(BillingGeneralInformationTable.TOTAL_DUE);
//	}
//
//	public void TC16_Verify_Form_AHR1XX_And_HSRNXX() {
//		//TODO DocGen utils
//		//DocGenHelper.verifyDocumentsGeneratedByJob(TimeSetterUtil.getInstance().getCurrentTime(), policyNum, Arrays.asList(OnDemandDocuments.AHRBXX, OnDemandDocuments.HSRNXX));
//	}
//
//	public void TC17_Pay_Renew_Offer() {
//		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(policyExpirationDate));
//		mainApp().open();
//		SearchPage.openBilling(policyNum);
//		Dollar rAmount = getRenewOfferAmount().add(200);
//		BillingSummaryPage.btnAcceptPayment.click();
//		AcceptPaymentEditorPanel.fillAcceptPaymentTab("Cash", rAmount.toString());
//		AcceptPaymentEditorPanel.ok();
//
//		BillingSummaryPage.showPriorTerms();
//		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
//
//		Dollar renewInstallmentsSum = new Dollar();
//		ArrayList<LocalDateTime> dates = FileHelper.getRenewInstallmentsDueDates(PaymentPlan.QUARTERLY);
//		for (LocalDateTime date : dates) {
//			renewInstallmentsSum = renewInstallmentsSum.add(BillingHelper.getInstallmentDueByDueDate(date));
//		}
//		CustomAssert.assertEquals("Overpayment is not moved to the next Term. Total Due is not reduced. ", totalDue.subtract(rAmount), getGeneralInfoValue(GeneralInfoColumns.TOTAL_DUE));
//		CustomAssert.assertEquals("Overpayment is not moved to the next Term. Installments are not reduced. ", installmentsSum.subtract(200), renewInstallmentsSum);
//	}
//
//	public void TC18_Update_Policy_Status() {
//		LocalDateTime date = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
//		// LocalDateTime cross_date = getTimePoints().getBillDueDate(policyExpirationDate);
//		TimeSetterUtil.getInstance().nextPhase(date);
//		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
//		mainApp().open();
//		SearchPage.openBilling(policyNum);
//
//		/*
//		 * HashMap<PaymentsColumns, String> values = new
//		 * HashMap<BillingMeta.PaymentsColumns, String>();
//		 * values.put(PaymentsColumns.TRANSACTION_DATE,
//		 * cross_date.toString(DATE_FORMAT)); values.put(PaymentsColumns.AMOUNT,
//		 * "($800.00)"); values.put(PaymentsColumns.TYPE, "Payment");
//		 * values.put(PaymentsColumns.SUBTYPE_REASON, "Cross Policy Transfer");
//		 * values.put(PaymentsColumns.STATUS, "Cleared");
//		 * BillingHelper.getPaymentsRow(values).verify.present();
//		 *
//		 * values.clear(); values.put(PaymentsColumns.TRANSACTION_DATE,
//		 * cross_date.toString(DATE_FORMAT)); values.put(PaymentsColumns.AMOUNT,
//		 * "$800.00"); values.put(PaymentsColumns.TYPE, "Adjustment");
//		 * values.put(PaymentsColumns.SUBTYPE_REASON, "Cross Policy Transfer");
//		 * values.put(PaymentsColumns.STATUS, "Applied");
//		 * BillingHelper.getPaymentsRow(values).verify.present();
//		 */
//		BillingSummaryPage.showPriorTerms();
//		BillingHelper.verifyPolicyStatus(policyEffectiveDate, PolicyStatus.POLICY_EXPIRED);
//		BillingHelper.verifyPolicyStatus(policyExpirationDate, PolicyStatus.POLICY_ACTIVE);
//	}
//
//	public void TC19_Automatic_Refund_Not_Generated() {
//		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRefundDate(getTimePoints().getBillDueDate(policyExpirationDate)));
//		JobUtils.executeJob(Jobs.refundGenerationJob);
//		mainApp().open();
//		SearchPage.openBilling(billingNum);
//
//		HashMap<PaymentsColumns, String> values = new HashMap<BillingMeta.PaymentsColumns, String>();
//		values.put(PaymentsColumns.TYPE, "Refund");
//		values.put(PaymentsColumns.SUBTYPE_REASON, "Automated Refund");
//		values.put(PaymentsColumns.REASON, "Overpayment");
//		BillingHelper.getPaymentsRow(values).verify.present(false);
//	}
//
//	private void payAndCheckBill(LocalDateTime date, Dollar amount) {
//		LocalDateTime dd = getTimePoints().getBillDueDate(date);
//		TimeSetterUtil.getInstance().nextPhase(dd);
//		mainApp().open();
//		SearchPage.openBilling(billingNum);
//		if (amount == null) {
//			amount = getBillValue(date, BillColumns.MINIMUM_DUE);
//		}
//		BillingSummaryPage.btnAcceptPayment.click();
//		AcceptPaymentEditorPanel.fillAcceptPaymentTabForPolicyWithoutDue("Cash", amount.toString());
//		AcceptPaymentEditorPanel.ok();
//
//		String value = "(" + amount.toString() + ")";
//		HashMap<PaymentsColumns, String> values = new HashMap<BillingMeta.PaymentsColumns, String>();
//		values.put(PaymentsColumns.TRANSACTION_DATE, dd.toString(DATE_FORMAT));
//		values.put(PaymentsColumns.AMOUNT, value);
//		values.put(PaymentsColumns.TYPE, "Payment");
//		values.put(PaymentsColumns.SUBTYPE_REASON, "Manual Payment");
//		BillingHelper.getPaymentsRow(values).verify.present();
//	}
//
//	private Dollar getGeneralInfoValue(String columnName) {
//		return new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(columnName).getValue());
//	}
//
//	private Dollar getPolicyAccValue(AccountPoliciesColumns columnName) {
//		return new Dollar(BillingHelper.getPolicyCellValueByEffDate(policyEffDate, columnName));
//	}
//
//	private Dollar getBillValue(LocalDateTime date, BillColumns columnName) {
//		return new Dollar(BillingHelper.getBillCellValue(date, columnName));
//	}
//
//	private Dollar getRenewOfferAmount() {
//		return new Dollar(BillingHelper.getBillRowByDate(policyExpirationDate).getCell(BillColumns.MINIMUM_DUE.get()).getValue());
//	}

}