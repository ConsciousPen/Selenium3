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
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillsAndStatementsType;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.enums.SearchEnum.SearchBy;
import aaa.main.enums.SearchEnum.SearchFor;
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

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;

	protected List<LocalDateTime> installmentDueDates;
	protected List<Dollar> installmentsAmounts;
	protected Dollar installmentAmount;
	protected Dollar totalDue;
	protected Dollar minDue;

	protected String[] endorsementReasonDataKeys = {"EndorsementActionTab", "Endorsement Reason"};
	protected int installmentsCount = 4;

	protected Dollar endorsementAmount;

	public void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();

		policyNum = "";

		mainApp().open();
		if (policyNum.equals("")) {
			TimeSetterUtil.getInstance().adjustTime();
			createCustomerIndividual();

			if (getPolicyType().equals(PolicyType.PUP)) {
				policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
			}
			policyNum = createPolicy(policyCreationTD);
			// PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
			// policyNum = createPolicy(policyCreationTD);
		} else {
			SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, policyNum);
		}

		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Quaterly payment plan", installmentsCount, installmentDueDates.size());
		installmentAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1));
	}

	public void TC02_Generate_First_Bill() {
		generateAndCheckBill(installmentDueDates.get(1));

		totalDue = BillingSummaryPage.getTotalDue();
		minDue = BillingSummaryPage.getMinimumDue();
		installmentsAmounts = BillingHelper.getInstallmentDues();
	}

	// TODO Check: Removed according to 20427:US CL GD Generate Premium Due Notice v2.0. Form has been renamed to AHIBXX
	public void TC03_Verify_Form_AHIBXX() {
		// TODO DocGen utils
		// DocGenHelper.verifyDocumentsGeneratedByJob(TimeSetterUtil.getInstance().getCurrentTime(), policyNum,
		// OnDemandDocuments.AHIBXX);
	}

	public void TC04_Endorse_Policy() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		TestData endorsementTD = getStateTestData(tdPolicy, "Endorsement", "TestData");
		policy.endorse().performAndFill(endorsementTD.adjust(getTestSpecificTD("TestData_EndorsementRP")));
		PolicyHelper.verifyEndorsementIsCreated();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		String reason = "Endorsement - " + endorsementTD.getValue(endorsementReasonDataKeys);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(TimeSetterUtil.getInstance().getPhaseStartTime()).setPolicy(policyNum).setType(PaymentsAndOtherTransactionType.PREMIUM)
			.setSubtypeReason(reason).verifyPresent();
		endorsementAmount = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, reason).getCell(
			BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());

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
		// replace with verify.lessThan if 42369 Defect will be fixed
		for (int i = 1; i < installmentsAmounts.size(); i++) {
			BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(i)).verify.equals(installmentsAmounts.get(i));
		}
	}

	public void TC05_Pay_First_Bill() {
		// TODO
		// replace with payAndCheckBill() if 42369 Defect will be fixed
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.recurringPaymentsJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE)).add(endorsementAmount);
		new BillingPaymentsAndTransactionsVerifier().verifyAutoPaymentGenerated(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}

	public void TC06_Generate_CancellNotice() {
		LocalDateTime cnDate = getTimePoints().getCancellationNoticeDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(cnDate);
		JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNum);

		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.verifyCancelNoticeFlagNotPresent();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.DEFAULT).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	public void TC07_Generate_Second_Bill() {
		LocalDateTime genDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(2));
		TimeSetterUtil.getInstance().nextPhase(genDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar billAmount = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(2));
		billAmount = billAmount.add(BillingHelper.getFeesValue(getTimePoints().getBillGenerationDate(installmentDueDates.get(2))));
		new BillingBillsAndStatementsVerifier().setDueDate(installmentDueDates.get(2)).setType(BillingConstants.BillsAndStatementsType.BILL).setMinDue(billAmount).verifyPresent();
	}

	public void TC08_Pay_Second_Bill() {
		payAndCheckBill(installmentDueDates.get(2));
	}

	public void TC09_Generate_Third_Bill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	public void TC10_Pay_Third_Bill() {
		payAndCheckBill(installmentDueDates.get(3));
	}

	public void TC11_Set_Do_Not_Renew_Flag() {
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		mainApp().open();
		SearchPage.openPolicy(policyNum);

		policy.doNotRenew().perform(getStateTestData(tdPolicy, "DoNotRenew", "TestData"));
		// PolicySummaryPage.setActionAndGo(PolicyAction.DO_NOT_RENEW);
		// DoNotRenewEditorPanel.fillDoNotRenewTab("DoNotRenew");
		// DoNotRenewEditorPanel.clickOkButton();
		PolicySummaryPage.verifyDoNotRenewFlagPresent();

	}

	public void TC12_Renewal_Image_Generation() {
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

	public void TC13_Renewal_Preview_Generation() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);
	}

	public void TC14_Renewal_Offer_Generation() {
		LocalDateTime policyExpirationDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDateOffer);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(policyExpirationDate).setType(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL)
			.verifyPresent(false);
	}

	public void TC15_Manual_Renew_Policy() {
		CustomAssert.enableSoftMode();
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.removeDoNotRenew().perform(new SimpleDataProvider());
		policy.manualRenew().perform(getStateTestData(tdPolicy, "ManualRenew", "TestData"));
		PolicySummaryPage.verifyManualRenewFlagPresent();

		policy.createRenewal(getStateTestData(tdPolicy, "Renew", "TestData_Today").adjust(getTestSpecificTD("TestData_ProposeRenew")));
		// policy.renew().perform(getStateTestData(tdPolicy, "Renew", "TestData_Today"));
		// policy.calculatePremiumAndPurchase(getTestSpecificTD("TestData_ProposeRenew"));

		// adjust(getTestSpecificTD("TestData_ProposeRenew")));
		// policy.dataGather().perform(getTestSpecificTD("TestData_ProposeRenew"));
		// policy.calculatePremium(getStateTestData(tdPolicy, "DataGather", "TestData"));
		// policy.propose();

		// EditorPanel.navigateTo(TabNames.QuoteHSSTabs.PREMIUMS_AND_COVERAGES_QUOTE.get());
		// PremiumsAndCoveragesTabEditorPanel.rate();
		// EditorPanel.navigateTo(TabNames.QuoteHSSTabs.BIND.get());
		// BindTabEditorPanel.proposeRenew();
		//
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(policyExpirationDate, installmentDueDates);

		// ArrayList<LocalDateTime> dates = FileHelper.getRenewInstallmentsDueDates(PaymentPlan.QUARTERLY);
		// for (LocalDateTime date : dates) {
		// installmentsSum = installmentsSum.add(BillingHelper.getInstallmentDueByDueDate(date));
		// }
		totalDue = BillingSummaryPage.getTotalDue();
	}

	public void TC16_Verify_Form_AHR1XX_And_HSRNXX() {
		// TODO DocGen utils
		// DocGenHelper.verifyDocumentsGeneratedByJob(TimeSetterUtil.getInstance().getCurrentTime(), policyNum,
		// Arrays.asList(OnDemandDocuments.AHRBXX, OnDemandDocuments.HSRNXX));
	}

	public void TC17_Pay_Renew_Offer() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(policyExpirationDate));
		mainApp().open();
		SearchPage.openBilling(policyNum);
		// Dollar rAmount = getRenewOfferAmount().add(200);
		// BillingSummaryPage.btnAcceptPayment.click();
		// AcceptPaymentEditorPanel.fillAcceptPaymentTab("Cash", rAmount.toString());
		// AcceptPaymentEditorPanel.ok();
		//
		// BillingSummaryPage.showPriorTerms();
		// new
		// BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
		//
		// Dollar renewInstallmentsSum = new Dollar();
		// ArrayList<LocalDateTime> dates = FileHelper.getRenewInstallmentsDueDates(PaymentPlan.QUARTERLY);
		// for (LocalDateTime date : dates) {
		// renewInstallmentsSum = renewInstallmentsSum.add(BillingHelper.getInstallmentDueByDueDate(date));
		// }
		// CustomAssert.assertEquals("Overpayment is not moved to the next Term. Total Due is not reduced. ",
		// totalDue.subtract(rAmount), getGeneralInfoValue(GeneralInfoColumns.TOTAL_DUE));
		// CustomAssert.assertEquals("Overpayment is not moved to the next Term. Installments are not reduced. ",
		// installmentsSum.subtract(200), renewInstallmentsSum);
	}

	public void TC18_Update_Policy_Status() {
		LocalDateTime date = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		// LocalDateTime cross_date = getTimePoints().getBillDueDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(date);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);

		/*
		 * HashMap<PaymentsColumns, String> values = new HashMap<BillingMeta.PaymentsColumns, String>();
		 * values.put(PaymentsColumns.TRANSACTION_DATE, cross_date.toString(DATE_FORMAT));
		 * values.put(PaymentsColumns.AMOUNT, "($800.00)"); values.put(PaymentsColumns.TYPE, "Payment");
		 * values.put(PaymentsColumns.SUBTYPE_REASON, "Cross Policy Transfer"); values.put(PaymentsColumns.STATUS,
		 * "Cleared"); BillingHelper.getPaymentsRow(values).verify.present();
		 * 
		 * values.clear(); values.put(PaymentsColumns.TRANSACTION_DATE, cross_date.toString(DATE_FORMAT));
		 * values.put(PaymentsColumns.AMOUNT, "$800.00"); values.put(PaymentsColumns.TYPE, "Adjustment");
		 * values.put(PaymentsColumns.SUBTYPE_REASON, "Cross Policy Transfer"); values.put(PaymentsColumns.STATUS,
		 * "Applied"); BillingHelper.getPaymentsRow(values).verify.present();
		 */
		BillingSummaryPage.showPriorTerms();
		// BillingHelper.verifyPolicyStatus(policyEffectiveDate, PolicyStatus.POLICY_EXPIRED);
		// BillingHelper.verifyPolicyStatus(policyExpirationDate, PolicyStatus.POLICY_ACTIVE);
	}

	public void TC19_Automatic_Refund_Not_Generated() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRefundDate(getTimePoints().getBillDueDate(policyExpirationDate)));
		JobUtils.executeJob(Jobs.refundGenerationJob);
		mainApp().open();
		// SearchPage.openBilling(billingNum);
		//
		// HashMap<PaymentsColumns, String> values = new HashMap<BillingMeta.PaymentsColumns, String>();
		// values.put(PaymentsColumns.TYPE, "Refund");
		// values.put(PaymentsColumns.SUBTYPE_REASON, "Automated Refund");
		// values.put(PaymentsColumns.REASON, "Overpayment");
		// BillingHelper.getPaymentsRow(values).verify.present(false);
	}
}