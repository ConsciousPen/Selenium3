package aaa.modules.e2e.templates;

import java.time.LocalDateTime;
import java.util.List;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.PolicyHelper;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
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
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.e2e.ScenarioBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

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
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		policyTerm = getPolicyTerm(policyCreationTD);
		totalVehiclesNumber = getVehiclesNumber(policyCreationTD);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		installmentDueDates = BillingHelper.getInstallmentDueDates();
		CustomAssertions.assertThat(installmentDueDates.size()).as("Billing Installments count for Quarterly payment plan").isEqualTo(installmentsCount);

		verifyPligaOrMvleFee(TimeSetterUtil.getInstance().getPhaseStartTime(), policyTerm, totalVehiclesNumber);
	}
	
	protected void generateFirstBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(1), softly);
	}

	protected void payFirstBill() {
		payCashAndCheckBill(installmentDueDates.get(1));
	}
	
	protected void generateSecondBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(2), softly);
	}

	protected  void paySecondBill() {
		payCashAndCheckBill(installmentDueDates.get(2));
	}
	
	protected void generateThirdBill(ETCSCoreSoftAssertions softly) {
		generateAndCheckBill(installmentDueDates.get(3), softly);
	}

	protected void payThirdBill() {
		payCashAndCheckBill(installmentDueDates.get(3));
	}

	protected void renewalImageGeneration() {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		PolicyHelper.verifyAutomatedRenewalGenerated(renewImageGenDate);
	}

	protected void renewalPreviewGeneration() {
		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNum);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	protected void renewalOfferGeneration(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		CustomAssertions.assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		verifyRenewOfferGenerated(installmentDueDates, softly);
		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferGenDate)
				.setSubtypeReason(BillingConstants.PaymentsAndOtherTransactionSubtypeReason.RENEWAL_POLICY_RENEWAL_PROPOSAL).verifyPresent();

		if (getState().equals(Constants.States.CA)) {
			verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCount, softly);
		}

		if (verifyPligaOrMvleFee(renewOfferGenDate, policyTerm, totalVehiclesNumber)) {
			pligaOrMvleFeeLastTransactionDate = renewOfferGenDate;
		}

	}

	protected void generateRenewalBill() {
		LocalDateTime billDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billDate);
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		Dollar pligaOrMvleFee = getPligaOrMvleFee(policyNum, pligaOrMvleFeeLastTransactionDate, policyTerm, totalVehiclesNumber);
		verifyRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), billDate, pligaOrMvleFee, installmentsCount);
		verifyRenewPremiumNotice(policyExpirationDate, billDate, pligaOrMvleFee);

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
	}

	protected void enableAutoPay() {
		mainApp().open();
		SearchPage.openBilling(policyNum);
		billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_EnableAutopay"));
		billingAccount.update().start();
		CustomAssertions.assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(true);
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
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
			new DocumentsAndBindTab().submitTab();
		}
		else if (getPolicyType().equals(PolicyType.PUP)) {
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
			new PremiumAndCoveragesQuoteTab().fillTab(renewalTD, true);
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
		verifyPaymentPlanAndStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE, ProductConstants.PolicyStatus.PROPOSED);

		installmentDueDatesOfRenewal = BillingHelper.getInstallmentDueDates();
		CustomAssertions.assertThat(installmentDueDatesOfRenewal.size()).as("Billing Installments count for Eleven Pay - Standard (Renewal) payment plan").isEqualTo(installmentsCountOfRenewal);
	}

	//For CA - Auto & Home
	protected void changePaymentPlanForCA(ETCSCoreSoftAssertions softly) {
		LocalDateTime renewOfferDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE).setPaymentPlan("Quarterly").verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).setPaymentPlan("Quarterly (Renewal)").verifyRowWithEffectiveDate(policyExpirationDate);
		BillingSummaryPage.buttonHidePriorTerms.click();
		//Dollar minDue = BillingSummaryPage.getMinimumDue();

		billingAccount.changePaymentPlan().perform(tdBilling.getTestData("ChangePaymentPlan", "TestData_ChangePaymentPlanToMonthly"));

		verifyPaymentPlanAndStatus(ProductConstants.PolicyStatus.POLICY_ACTIVE, ProductConstants.PolicyStatus.PROPOSED);

		installmentDueDatesOfRenewal = BillingHelper.getInstallmentDueDates();
		CustomAssertions.assertThat(installmentDueDatesOfRenewal.size()).as("Billing Installments count for Standard Monthly (Renewal) payment plan").isEqualTo(installmentsCountOfRenewal);

		//new BillingBillsAndStatementsVerifier().setDueDate(policyExpirationDate).setType(BillingConstants.BillsAndStatementsType.DISCARDED_OFFER).setMinDue(minDue).verifyPresent();

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason("EFT Installment Fee - Credit Card").verifyPresent();

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(renewOfferDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE)
				.setSubtypeReason("Non EFT Installment Fee Waived").verifyPresent();

		verifyCaRenewalOfferPaymentAmount(policyExpirationDate, getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), installmentsCountOfRenewal, softly);

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
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openBilling(policyNum);
		verifyPaymentPlanAndStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED, ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}	
	
	protected void generateFirstBillOfRenewal(ETCSCoreSoftAssertions softly){
		generateAndCheckBill(installmentDueDatesOfRenewal.get(1), softly);
	}
	
	protected void payFirstBillOfRenewal() {
		payAndCheckBill(installmentDueDatesOfRenewal.get(1));
	}
	
	private void payCashAndCheckBill(LocalDateTime installmentDueDate) {
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDate).minusDays(1);
		TimeSetterUtil.getInstance().nextPhase(billDueDate);

		mainApp().open();
		SearchPage.openBilling(policyNum);

		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDate, BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE));
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
