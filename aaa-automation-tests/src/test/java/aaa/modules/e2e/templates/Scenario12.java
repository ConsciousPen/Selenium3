package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.Tab;
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
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
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
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;

public class Scenario12 extends ScenarioBaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	
	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	
	protected LocalDateTime policyExpirationDate_FirstRenewal; 
	//protected LocalDateTime policyExpirationDate_SecondRenewal; 
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 2; 
	protected List<LocalDateTime> installmentDueDates_FirstRenewal;
	protected int installmentsCount_FirstRenewal = 4; 
	protected List<LocalDateTime> installmentDueDates_SecondRenewal;
	protected int installmentsCount_SecondRenewal = 2; 
	
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
		CustomAssert.assertEquals("Billing Installments count for Semi-Annual payment plan", installmentsCount, installmentDueDates.size()); 		
	}

	protected void generateFirstBill() {
		generateAndCheckBill(installmentDueDates.get(1));
	}
	
	protected void payFirstBill() {
		payCashAndCheckBill(installmentDueDates.get(1));
	}
	
	protected void renewalImageGeneration() {
		renewalImageGeneration(policyExpirationDate);
	}

	protected void renewalPreviewGeneration() {
		renewalPreviewGeneration(policyExpirationDate);
	}
	
	protected void renewalOfferGeneration() {
		renewalOfferGeneration(policyEffectiveDate, policyExpirationDate, installmentDueDates);
	}
	
	//Skip this step for CA
	protected void generateRenewalBill() {
		generateRenewalBill(policyEffectiveDate, policyExpirationDate);
	}
	
	private void generateRenewalBill(LocalDateTime policyEffectiveDate, LocalDateTime policyExpirationDate) {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		//Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewPremiumNotice(policyExpirationDate, billDate);
	}

	protected void changePaymentPlan() {
		LocalDateTime dueDate = getTimePoints().getBillGenerationDate(policyExpirationDate).plusHours(1);
		TimeSetterUtil.getInstance().nextPhase(dueDate);
		
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		TestData renewalTD = getTestSpecificTD("TestData_FirstRenewal");
		changePaymentPlan(renewalTD);
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Semi-Annual").verifyPresent();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)").verifyPresent(); 
		
		BillingSummaryPage.buttonHidePriorTerms.click();
		installmentDueDates_FirstRenewal = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Quarterly (Renewal) payment plan", 
				installmentsCount_FirstRenewal, installmentDueDates_FirstRenewal.size());
		
	} 
	
	private void changePaymentPlan(TestData td) {
		PolicySummaryPage.buttonRenewals.click(); 
		policy.dataGather().start(); 
		if (getPolicyType().isAutoPolicy()) {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get()); 
			new PremiumAndCoveragesTab().fillTab(td);
			PremiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			new DocumentsAndBindTab().submitTab();
		} 
		else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get()); 
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get()); 
			new PremiumsAndCoveragesQuoteTab().fillTab(td, true); 
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get()); 
			new BindTab().submitTab();
		}
	}
	
	protected void changePaymentPlanForCA() {
		LocalDateTime renewOfferDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		LocalDateTime dueDate = getTimePoints().getBillGenerationDate(policyExpirationDate).plusHours(1);
		TimeSetterUtil.getInstance().nextPhase(dueDate);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).setPaymentPlan("Semi-Annual (Renewal)").verifyPresent();
		
		billingAccount.changePaymentPlan().perform(tdBilling.getTestData("ChangePaymentPlan", "TestData_ChangePaymentPlanToQuarterly"));
		
		BillingSummaryPage.showPriorTerms();		
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Semi-Annual").verifyPresent(); 
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)").verifyPresent(); 		
		BillingSummaryPage.buttonHidePriorTerms.click();
		
		installmentDueDates_FirstRenewal = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Quarterly (Renewal) payment plan", 
				installmentsCount_FirstRenewal, installmentDueDates_FirstRenewal.size()); 
		
		new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.OFFER).verifyPresent();
		new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER).verifyPresent();
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
		.setSubtypeReason("EFT Installment Fee").verifyPresent(); 
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
		.setSubtypeReason("Non EFT Installment Fee Waived").verifyPresent(); 	
		
	}
	
	protected void enableAutoPay() { 
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_EnableAutopay"));
		billingAccount.update().start();
		new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(true);
		Tab.buttonCancel.click();
	}
	
	protected void payRenewalBill(){
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDueDate.plusHours(1));
		JobUtils.executeJob(Jobs.recurringPaymentsJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(policyExpirationDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
		//new BillingPaymentsAndTransactionsVerifier().verifyAutoPaymentGenerated(DateTimeUtils.getCurrentDateTime(), minDue.negate()); 
		//verify recurring payment is not generated
		
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), minDue);
		new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());			
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
		
		SearchPage.openPolicy(policyNum, PolicyStatus.POLICY_ACTIVE);
		policyExpirationDate_FirstRenewal = PolicySummaryPage.getExpirationDate();		
	}
	
	protected void generateFirstBillOfFirstRenewal(){
		generateAndCheckBill(installmentDueDates_FirstRenewal.get(1)); 
	}
	
	protected void payFirstBillOfFirstRenewal() {
		payAndCheckBill(installmentDueDates_FirstRenewal.get(1));
	}
	
	protected void generateSecondBillOfFirstRenewal(){
		generateAndCheckBill(installmentDueDates_FirstRenewal.get(2)); 
	}
	
	protected void paySecondBillOfFirstRenewal() {
		payAndCheckBill(installmentDueDates_FirstRenewal.get(2));
	}
	
	protected void generateThirdBillOfFirstRenewal(){
		generateAndCheckBill(installmentDueDates_FirstRenewal.get(3)); 
	}
	
	protected void payThirdBillOfFirstRenewal() {
		payAndCheckBill(installmentDueDates_FirstRenewal.get(3));
	}
	
	protected void renewalImageGeneration_FirstRenewal() {
		renewalImageGeneration(policyExpirationDate_FirstRenewal);
	}
	
	protected void renewalPreviewGeneration_FirstRenewal() {
		renewalPreviewGeneration(policyExpirationDate_FirstRenewal);
	}
	
	protected void renewalOfferGeneration_FirstRenewal() {
		renewalOfferGeneration(policyExpirationDate, policyExpirationDate_FirstRenewal, installmentDueDates_FirstRenewal);
	}
	
	protected void changePaymentPlan_FirstRenewal() {
		mainApp().open();
		SearchPage.openPolicy(policyNum, PolicyStatus.POLICY_ACTIVE);
		
		TestData renewalTD = getTestSpecificTD("TestData_SecondRenewal");
		changePaymentPlan(renewalTD);
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();	
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).setPaymentPlan("Semi-Annual").verifyPresent();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly (Renewal)").verifyPresent();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).setPaymentPlan("Semi-Annual (Renewal)").verifyPresent(); 
		
		BillingSummaryPage.buttonHidePriorTerms.click();
		installmentDueDates_SecondRenewal = BillingHelper.getInstallmentDueDates();
		CustomAssert.assertEquals("Billing Installments count for Semi-Annual (Renewal) payment plan", 
				installmentsCount_SecondRenewal, installmentDueDates_SecondRenewal.size());
	}

	protected void generateRenewalBill_FirstRenewal() {
		generateRenewalBill(policyExpirationDate, policyExpirationDate_FirstRenewal);
	} 
	
	protected void payRenewalBill_FirstRenewal() {
		payAndCheckBill(policyExpirationDate_FirstRenewal);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate_FirstRenewal);
	}
	
	protected void updatePolicyStatus_FirstRenewal() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate_FirstRenewal);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyExpirationDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyExpirationDate_FirstRenewal);
		
		//SearchPage.openPolicy(policyNum, PolicyStatus.POLICY_ACTIVE);
		//policyExpirationDate_SecondRenewal = PolicySummaryPage.getExpirationDate();
	}
	
	protected void generateFirstBillOfSecondRenewal() {
		generateAndCheckBill(installmentDueDates_SecondRenewal.get(1)); 
	}
	
	protected void payFirstBillOfSecondRenewal() {
		payAndCheckBill(installmentDueDates_SecondRenewal.get(1));
	}
	
	private void renewalImageGeneration(LocalDateTime policyExpirationDate) {
		LocalDateTime renewDateImage = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateImage);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewDateImage);
	}
	
	private void renewalPreviewGeneration(LocalDateTime policyExpirationDate) {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicySummaryPage.buttonRenewals.verify.enabled();

		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	private void renewalOfferGeneration(LocalDateTime policyEffectiveDate, LocalDateTime policyExpirationDate, List<LocalDateTime> installmentDueDates) {
		LocalDateTime renewDateOffer = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDateOffer);
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
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewDateOffer)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();
		/*
		if (verifyPligaOrMvleFee(renewDateOffer, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewDateOffer;
		}
		*/
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
}
