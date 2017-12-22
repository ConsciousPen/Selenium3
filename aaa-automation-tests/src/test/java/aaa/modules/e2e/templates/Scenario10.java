package aaa.modules.e2e.templates;

import aaa.common.Tab;
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
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingAccountPoliciesTable;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class Scenario10 extends ScenarioBaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 4;
	
	protected List<LocalDateTime> installmentDueDatesOfRenewal;
	protected int installmentsCountOfRenewal = 11;
	
	//protected String[] endorsementReasonDataKeys;
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();
		
		mainApp().open();
		
		createCustomerIndividual();	
		policyNum = createPolicy(policyCreationTD); 
		
		PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Quarterly payment plan", installmentsCount, installmentDueDates.size());
	}
	
	protected void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1)); 
	}
	
	protected void payFirstBill() {
		payCashAndCheckBill(installmentDueDates.get(1));
	}
	
	protected void generateSecondBill() {
		generateAndCheckBill(installmentDueDates.get(2));
	}

	protected  void paySecondBill() {
		payCashAndCheckBill(installmentDueDates.get(2));
	}
	
	protected void generateThirdBill() {
		generateAndCheckBill(installmentDueDates.get(3));
	}

	protected void payThirdBill() {
		payCashAndCheckBill(installmentDueDates.get(3));
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
		verifyRenewOfferGenerated(installmentDueDates);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
			.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount);
		}
		/*
		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}
		*/
	}

	protected void generateRenewalBill() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}
	
	protected void enableAutoPay() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_EnableAutopay"));
		billingAccount.update().start();
		new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(true);
		Tab.buttonCancel.click();
	}
	
	//For Auto SS & Home SS
	protected void changePaymentPlan() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		PolicySummaryPage.buttonRenewals.click(); 
		TestData renewalTD = getTestSpecificTD("TestData_Renewal");
		policy.dataGather().start(); 
		
		if (getPolicyType().isAutoPolicy()) {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get()); 
			new PremiumAndCoveragesTab().fillTab(renewalTD);
			PremiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			new DocumentsAndBindTab().submitTab();
		} 
		else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get()); 
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get()); 
			new PremiumsAndCoveragesQuoteTab().fillTab(renewalTD, true); 
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get()); 
			new BindTab().submitTab();
		}
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly").verifyPresent();
		if (getPolicyType().isAutoPolicy()) {
			new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).setPaymentPlan("Eleven Pay - Standard (Renewal)").verifyPresent(); 
		}
		else {
			new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).setPaymentPlan("Eleven Pay Standard (Renewal)").verifyPresent(); 
		}
		
		BillingSummaryPage.buttonHidePriorTerms.click();
		installmentDueDatesOfRenewal = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Eleven Pay - Standard (Renewal) payment plan", 
				installmentsCountOfRenewal, installmentDueDatesOfRenewal.size());
		
	}
	
	//For Auto CA & Home CA
	protected void changePaymentPlanForCA() {
		LocalDateTime renewOfferDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)").verifyPresent();
		//Dollar minDue = BillingSummaryPage.getMinimumDue();
		
		billingAccount.changePaymentPlan().perform(tdBilling.getTestData("ChangePaymentPlan", "TestData_ChangePaymentPlanToMonthly"));
		
		BillingSummaryPage.showPriorTerms();		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly").verifyPresent();
	
		HashMap<String, String> paymentPlan_row = new HashMap<>();
		paymentPlan_row.put(BillingAccountPoliciesTable.EFF_DATE, policyExpirationDate.format(DateTimeUtils.MM_DD_YYYY));
		paymentPlan_row.put(BillingAccountPoliciesTable.POLICY_STATUS, PolicyStatus.PROPOSED);
		paymentPlan_row.put(BillingAccountPoliciesTable.PAYMENT_PLAN, "Monthly");
		
		BillingSummaryPage.tableBillingAccountPolicies.getRowContains(paymentPlan_row).verify.present();
		
		BillingSummaryPage.buttonHidePriorTerms.click();
		installmentDueDatesOfRenewal = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Standard Monthly (Renewal) payment plan", 
				installmentsCountOfRenewal, installmentDueDatesOfRenewal.size()); 
		
		//new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER).setMinDue(minDue).verifyPresent();
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
			.setSubtypeReason("EFT Installment Fee - Credit Card").verifyPresent(); 
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
			.setSubtypeReason("Non EFT Installment Fee Waived").verifyPresent(); 
		
		verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCountOfRenewal);
		
	}
	
	//For AutoSS
	protected void payRenewalBill() {
		payCashAndCheckBill(policyExpirationDate);
	}
	
	//For AutoCA
	protected void payRenewalOffer() {
		payAndCheckBill(policyExpirationDate);
	}
	
	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate);
	}
	
	protected void generateFirstBillOfRenewal(){
		generateAndCheckBill(installmentDueDatesOfRenewal.get(1)); 
	}
	
	protected void payFirstBillOfRenewal() {
		payAndCheckBill(installmentDueDatesOfRenewal.get(1));
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

	/*
	private void payCreditCardAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDate, BillingBillsAndStatmentsTable.MINIMUM_DUE));
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_CC"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());
	}
	*/
}
