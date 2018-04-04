package aaa.modules.regression.sales.home_ss.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TimePoints;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.RollOnChangesActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class HelperTestPaymentPlanChangeOnEndorsement extends PolicyBaseTest {

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();

	public void pas11338_AC1(PolicyType policyType, String paymentPlan) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		initiateEndorsementAndValidatePaymentPlanWithoutChanging(policyType, paymentPlan);
		}

	public void pas11338_AC2_AC3(PolicyType policyType, String paymentPlan) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		initiateEndorsementAndValidatePaymentPlanWithChanging(policyType, paymentPlan);

	}

	public void pas11338_AC2_negative(PolicyType policyType, String paymentPlan) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		initiateEndorsementAndNavigateToQuoteTab(policyType);

		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.doesNotContain(BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN, BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN);//TODO-mstrazds:update with both Low Down pay plans

	}

	public void pas11338_AC3_AC6_Renewal(PolicyType policyType, String paymentPlan, TimePoints timePoints) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		generateRenewalImageAndRetrievePolicy(timePoints);

		PolicySummaryPage.buttonRenewals.click();
		policyType.get().dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN))
				.hasValue(BillingConstants.PaymentPlan.PAY_IN_FULL); //TODO-mstrazds:Step 5 (correct payment plan)
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.doesNotContain(BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN, BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN);

	}

	public void pas11338_AC4(PolicyType policyType, String paymentPlan, TimePoints timePoints) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		generateRenewalImageAndRetrievePolicy(timePoints);
		initiateEndorsementAndValidatePaymentPlanWithChanging(policyType, paymentPlan);

	}

	public void pas11338_AC5(PolicyType policyType, String paymentPlan, TimePoints timePoints) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		generateRenewalImageAndRetrievePolicy(timePoints);
		initiateEndorsementAndValidatePaymentPlanWithoutChanging(policyType, paymentPlan);
	}

	public void createPolicyWithSpecificPaymentPlan(PolicyType policyType, String paymentPlan) {
		TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

		testData.adjust(TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()),
				paymentPlan);

		testData.adjust(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()), "@home_ss_ho3@DataGather@PurchaseTab_WithAutopay");

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(testData);

	}

	private void initiateEndorsementAndNavigateToQuoteTab(PolicyType policyType) {
		TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus10Day");

		policyType.get().endorse().perform(testData);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
	}

	public void initiateEndorsementAndValidatePaymentPlanWithoutChanging(PolicyType policyType, String paymentPlan) {
		initiateEndorsementAndNavigateToQuoteTab(policyType);

		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.btnPurchase.click();
		bindTab.confirmEndorsementPurchase.buttonYes.click();

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void initiateEndorsementAndValidatePaymentPlanWithChanging(PolicyType policyType, String paymentPlan) {
		initiateEndorsementAndNavigateToQuoteTab(policyType);

		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue(BillingConstants.PaymentPlan.PAY_IN_FULL);
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(BillingConstants.PaymentPlan.ELEVEN_PAY);//TODO-mstrazds:update with both Low Down pay plans

		//Navigate to different Tab and back to Quote Tab
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(BillingConstants.PaymentPlan.PAY_IN_FULL);
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(BillingConstants.PaymentPlan.ELEVEN_PAY);//update with both Low Down pay plans

		//Calculate premium and 'Save and Exit'
		premiumsAndCoveragesQuoteTab.btnCalculatePremium().click();
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(BillingConstants.PaymentPlan.PAY_IN_FULL);
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(BillingConstants.PaymentPlan.ELEVEN_PAY);//update with both Low Down pay plans
		premiumsAndCoveragesQuoteTab.saveAndExit();

		//Retrieve Pended endorsement
		PolicySummaryPage.buttonPendedEndorsement.click();
		NavigationPage.comboBoxListAction.setValue("Data Gathering");
		Tab.buttonGo.click();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(BillingConstants.PaymentPlan.PAY_IN_FULL);
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(BillingConstants.PaymentPlan.ELEVEN_PAY);//TODO-mstrazds:update with both Low Down pay plans

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.btnPurchase.click();
		bindTab.confirmEndorsementPurchase.buttonYes.click();

		//get Test Data to fill Purchase Tab
		TestData tdPurchase = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData").resolveLinks();
		policyType.get().getDefaultView().fillFromTo(tdPurchase, PurchaseTab.class, PurchaseTab.class, true);
		PurchaseTab.btnApplyPayment.click();
		PurchaseTab.confirmPurchase.buttonYes.click();

		if (RollOnChangesActionTab.buttonCancel.isPresent()) {
			RollOnChangesActionTab.buttonCancel.click();
		}
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
		TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus10Day");
		policyType.get().endorse().perform(testData);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(BillingConstants.PaymentPlan.PAY_IN_FULL);
		assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.doesNotContain(BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN, BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN);//TODO-mstrazds:update with both Low Down pay plans

		PremiumsAndCoveragesQuoteTab.buttonCancel.click();
		PremiumsAndCoveragesQuoteTab.dialogCancelAction.buttonYes.click();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	public void generateRenewalImageAndRetrievePolicy(TimePoints timePoints) {
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//Generate renewal image at renewal image generation date
		LocalDateTime renewImageGenDate = timePoints.getRenewImageGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Image Generation Date " + renewImageGenDate);

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}
}
