package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
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
import aaa.main.enums.BillingConstants;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionSubtypeReason;
import aaa.main.enums.BillingConstants.PaymentsAndOtherTransactionType;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;
//import toolkit.verification.CustomAssert;

public class Scenario10 extends ScenarioBaseTest {
	
	protected IPolicy policy;
	protected TestData tdPolicy;
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;

	protected LocalDateTime policyEffectiveDate;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime pligaOrMvleFeeLastTransactionDate;
	
	protected List<LocalDateTime> installmentDueDates;
	protected int installmentsCount = 4;
	
	protected List<LocalDateTime> installmentDueDatesOfRenewal;
	protected int installmentsCountOfRenewal = 11;
	
	protected String policyTerm;
	protected Integer totalVehiclesNumber;
	
	protected void createTestPolicy(TestData policyCreationTD) {
		policy = getPolicyType().get();
		
		mainApp().open();		
		createCustomerIndividual();	
		
		if (getPolicyType().equals(PolicyType.PUP)) {
			policyCreationTD = new PrefillTab().adjustWithRealPolicies(policyCreationTD, getPrimaryPoliciesForPup());
		}
		policyNum = createPolicy(policyCreationTD); 
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(PolicyStatus.POLICY_ACTIVE);
		//PolicySummaryPage.labelPolicyStatus.verify.value(PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Quarterly payment plan", installmentsCount, installmentDueDates.size()); 
		CustomAssertions.assertThat(installmentDueDates.size()).as("Billing Installments count for Quarterly payment plan").isEqualTo(installmentsCount);
		
		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
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
		//PolicySummaryPage.buttonRenewals.verify.enabled(); 
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}
	
	protected void renewalOfferGeneration() {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		//PolicySummaryPage.buttonRenewals.verify.enabled();
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
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
		
		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}
		
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

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billDate, pligaOrMvleFee, installmentsCount);
		verifyRenewPremiumNotice(policyExpirationDate, billDate, pligaOrMvleFee);
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}
	
	protected void enableAutoPay() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_EnableAutopay"));
		billingAccount.update().start();
		//new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).verify.value(true); 
		CustomAssertions.assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY).getValue()).isEqualTo(true);
		Tab.buttonCancel.click();
	}
	
	//For SS - Auto & Home
	protected void changePaymentPlan() {
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		
		PolicySummaryPage.buttonRenewals.click(); 
		TestData renewalTD = getTestSpecificTD("TestData_Renewal");
		policy.dataGather().start(); 		
		//policy.dataGather().perform(renewalTD); 
		
		if (getPolicyType().isAutoPolicy()) {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get()); 
			new PremiumAndCoveragesTab().fillTab(renewalTD);
			PremiumAndCoveragesTab.calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			new DocumentsAndBindTab().submitTab();
		} 
		else if (getPolicyType().equals(PolicyType.PUP)) {
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
			new aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab().fillTab(renewalTD, true);
			//new aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
			new aaa.main.modules.policy.pup.defaulttabs.BindTab().submitTab();
		}
		else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get()); 
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get()); 
			new PremiumsAndCoveragesQuoteTab().fillTab(renewalTD, true); 
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get()); 
			new BindTab().submitTab();
		}
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		verifyPaymentPlanAndStatus(PolicyStatus.POLICY_ACTIVE, PolicyStatus.PROPOSED);
		
		installmentDueDatesOfRenewal = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Eleven Pay - Standard (Renewal) payment plan", installmentsCountOfRenewal, installmentDueDatesOfRenewal.size());
		CustomAssertions.assertThat(installmentDueDatesOfRenewal.size()).as("Billing Installments count for Eleven Pay - Standard (Renewal) payment plan").isEqualTo(installmentsCountOfRenewal); 
		
	}
	
	//For CA - Auto & Home 
	protected void changePaymentPlanForCA() {
		LocalDateTime renewOfferDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly").verifyRowWithEffectiveDate(policyEffectiveDate); 
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate); 
		BillingSummaryPage.buttonHidePriorTerms.click();
		//Dollar minDue = BillingSummaryPage.getMinimumDue();
		
		billingAccount.changePaymentPlan().perform(tdBilling.getTestData("ChangePaymentPlan", "TestData_ChangePaymentPlanToMonthly"));
		
		verifyPaymentPlanAndStatus(PolicyStatus.POLICY_ACTIVE, PolicyStatus.PROPOSED);
		
		installmentDueDatesOfRenewal = BillingHelper.getInstallmentDueDates();
		//CustomAssert.assertEquals("Billing Installments count for Standard Monthly (Renewal) payment plan", installmentsCountOfRenewal, installmentDueDatesOfRenewal.size()); 
		CustomAssertions.assertThat(installmentDueDatesOfRenewal.size()).as("Billing Installments count for Standard Monthly (Renewal) payment plan").isEqualTo(installmentsCountOfRenewal); 
		
		//new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER).setMinDue(minDue).verifyPresent();
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
			.setSubtypeReason("EFT Installment Fee - Credit Card").verifyPresent(); 
		
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
			.setSubtypeReason("Non EFT Installment Fee Waived").verifyPresent(); 
		
		verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCountOfRenewal);
		
	}
	
	//For SS - Auto & Home
	protected void payRenewalBill() {
		payCashAndCheckBill(policyExpirationDate); 
		
		verifyPaymentPlan();
	}
	
	//For CA - Auto & Home
	protected void payRenewalOffer() {
		payAndCheckBill(policyExpirationDate);
		
		verifyPaymentPlan();
	}
	
	protected void updatePolicyStatus() {
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		
		mainApp().open();
		SearchPage.openBilling(policyNum);
		verifyPaymentPlanAndStatus(PolicyStatus.POLICY_EXPIRED, PolicyStatus.POLICY_ACTIVE);
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

	private String getRenewalPaymentPlan() { 
		String renewalPaymentPlan;
		if (getState().equals(Constants.States.CA)) {
			if (getPolicyType().isAutoPolicy()) {
				renewalPaymentPlan = "Standard Monthly (Renewal)";
			}
			else {
				renewalPaymentPlan = "Monthly Standard (Renewal)";
			}
		}
		else {
			if (getPolicyType().isAutoPolicy()) {
				renewalPaymentPlan = "Eleven Pay - Standard (Renewal)";
			}
			else {
				renewalPaymentPlan = "Eleven Pay Standard (Renewal)";
			}
		}
		return renewalPaymentPlan;
	}
	
	protected void verifyPaymentPlanAndStatus(String policyStatus, String renewalStatus) {
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(policyStatus).setPaymentPlan("Quarterly").verifyRowWithEffectiveDate(policyEffectiveDate); 
		new BillingAccountPoliciesVerifier().setPolicyStatus(renewalStatus).setPaymentPlan(getRenewalPaymentPlan()).verifyRowWithEffectiveDate(policyExpirationDate); 
		BillingSummaryPage.buttonHidePriorTerms.click();
	}
	
	protected void verifyPaymentPlan() {
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPaymentPlan("Quarterly").verifyRowWithEffectiveDate(policyEffectiveDate); 
		new BillingAccountPoliciesVerifier().setPaymentPlan(getRenewalPaymentPlan()).verifyRowWithEffectiveDate(policyExpirationDate); 
		BillingSummaryPage.buttonHidePriorTerms.click();
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
