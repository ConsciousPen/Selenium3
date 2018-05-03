package aaa.modules.regression.sales.home_ss.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
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
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.RollOnChangesActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class HelperTestPaymentPlanChangeOnEndorsement extends PolicyBaseTest {

	private static final String[] ALL_LOW_DOWN_PAYMENT_PLANS = {BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN, BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN};
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabHo = new PremiumsAndCoveragesQuoteTab();
	private PremiumAndCoveragesQuoteTab premiumsAndCoveragesQuoteTabPup = new PremiumAndCoveragesQuoteTab();

	private aaa.main.modules.policy.home_ss.defaulttabs.BindTab bindTabHo = new aaa.main.modules.policy.home_ss.defaulttabs.BindTab();
	private BindTab bindTabPup = new BindTab();

	public void pas11338_pas11785_AC1(PolicyType policyType, String paymentPlan) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		initiateEndorsementAndValidatePaymentPlanWithoutChanging(policyType, paymentPlan);
	}

	public void pas11338_pas11785_AC2_AC3(PolicyType policyType, String paymentPlan) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);

		if (policyType.equals(PolicyType.PUP)) {
			initiateEndorsementAndValidatePaymentPlanWithChangingPup(policyType, paymentPlan, false);
		} else {
			initiateEndorsementAndValidatePaymentPlanWithChangingHo(policyType, paymentPlan, false);
		}

	}

	public void pas11338_pas11785_AC2_negative(PolicyType policyType, String paymentPlan) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		initiateEndorsementAndNavigateToQuoteTab(policyType);

		if (policyType.equals(PolicyType.PUP)) {
			assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);
			assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.doesNotContain(ALL_LOW_DOWN_PAYMENT_PLANS);

		} else {
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.doesNotContain(ALL_LOW_DOWN_PAYMENT_PLANS);

		}

	}

	public void pas11338_pas11785_AC3_AC6_Renewal(PolicyType policyType, String paymentPlan, TimePoints timePoints) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		generateRenewalImageAndRetrievePolicy(timePoints);

		PolicySummaryPage.buttonRenewals.click();
		policyType.get().dataGather().start();

		if (policyType.equals(PolicyType.PUP)) {
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());

			assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN))
					.hasValue(getExpectedPaymentPlanForRenewal(paymentPlan)); //At NB 'Payment plan at renewal' gets defaulted to MONTHLY_RENEWAL / ELEVEN_PAY_RENEWAL (existing functionality)
			assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.doesNotContain(ALL_LOW_DOWN_PAYMENT_PLANS);
		} else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN))
					.hasValue(getExpectedPaymentPlanForRenewal(paymentPlan)); //At NB 'Payment plan at renewal' gets defaulted to MONTHLY_RENEWAL / ELEVEN_PAY_RENEWAL (existing functionality)
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.doesNotContain(ALL_LOW_DOWN_PAYMENT_PLANS);

		}

	}

	public void pas11338_pas11785_AC4(PolicyType policyType, String paymentPlan, TimePoints timePoints) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		generateRenewalImageAndRetrievePolicy(timePoints);

		if (policyType.equals(PolicyType.PUP)) {
			initiateEndorsementAndValidatePaymentPlanWithChangingPup(policyType, paymentPlan, false);
		} else {
			initiateEndorsementAndValidatePaymentPlanWithChangingHo(policyType, paymentPlan, false);
		}

	}

	public void pas11338_pas11785_AC5(PolicyType policyType, String paymentPlan, TimePoints timePoints) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		generateRenewalImageAndRetrievePolicy(timePoints);
		initiateEndorsementAndValidatePaymentPlanWithoutChanging(policyType, paymentPlan);
	}

	public void pas11338_pas11785_AC2_randomLowDown(PolicyType policyType, String paymentPlan) {
		createPolicyWithSpecificPaymentPlan(policyType, paymentPlan);
		if (policyType.equals(PolicyType.PUP)) {
			initiateEndorsementAndValidatePaymentPlanWithChangingPup(policyType, paymentPlan, true);
		} else {
			initiateEndorsementAndValidatePaymentPlanWithChangingHo(policyType, paymentPlan, true);
		}

	}

	private void createPolicyWithSpecificPaymentPlan(PolicyType policyType, String paymentPlan) {
		TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

		//adjust test data with Specific payment plan
		if (policyType.equals(PolicyType.PUP)) {
			testData.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(),
					PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()),
					paymentPlan);

			testData.adjust(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()), "@home_ss_ho3@DataGather@PurchaseTab_WithAutopay");

		} else {
			testData.adjust(TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()),
					paymentPlan);

			testData.adjust(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()), "@home_ss_ho3@DataGather@PurchaseTab_WithAutopay");

		}

		mainApp().open();
		createCustomerIndividual();

		//IF PUP policy: Creates pup Underlying HO3 policy for PUP and adjusts PUP test data with Policy Number of created HO3 policy
		if (policyType.equals(PolicyType.PUP)) {
			Map<String, String> primaryPolicy = getPrimaryPoliciesForPup();
			testData = new PrefillTab().adjustWithRealPolicies(testData, primaryPolicy);
		}

		policyType.get().createPolicy(testData);

	}

	private void initiateEndorsementAndNavigateToQuoteTab(PolicyType policyType) {
		TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus10Day");

		policyType.get().endorse().perform(testData);

		if (policyType.equals(PolicyType.PUP)) {
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());

		} else {
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		}

	}

	private void initiateEndorsementAndValidatePaymentPlanWithoutChanging(PolicyType policyType, String paymentPlan) {
		initiateEndorsementAndNavigateToQuoteTab(policyType);

		if (policyType.equals(PolicyType.PUP)) {
			assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.contains(ALL_LOW_DOWN_PAYMENT_PLANS);

			premiumsAndCoveragesQuoteTabHo.btnCalculatePremium().click();

			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.contains(ALL_LOW_DOWN_PAYMENT_PLANS);

			NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
			bindTabPup.btnPurchase.click();
			bindTabPup.confrimEndorsementPurchase.buttonYes.click();
		} else {
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.contains(ALL_LOW_DOWN_PAYMENT_PLANS);

			premiumsAndCoveragesQuoteTabHo.btnCalculatePremium().click();

			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.contains(ALL_LOW_DOWN_PAYMENT_PLANS);

			NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
			bindTabHo.btnPurchase.click();
			bindTabHo.confirmEndorsementPurchase.buttonYes.click();
		}

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	//The same method as initiateEndorsementAndValidatePaymentPlanWithChangingPup(), but for HO
	private void initiateEndorsementAndValidatePaymentPlanWithChangingHo(PolicyType policyType, String paymentPlan, boolean changeToAnotherLowDown) {
		String paymentPlanChangeTo = null; //change to this Payment plan during endorsement

		// IF changeToAnotherLowDown is TRUE, change payment plan to Different Low Down payment plan, else change to non Low Down plan
		if (changeToAnotherLowDown) {
			if (paymentPlan.equals(BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN)) {
				paymentPlanChangeTo = BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;//change to this Payment plan during endorsement

			} else if (paymentPlan.equals(BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN)) {
				paymentPlanChangeTo = BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN;//change to this Payment plan during endorsement
			} else {
				paymentPlanChangeTo = BillingConstants.PaymentPlan.PAY_IN_FULL; //change to this Payment plan during endorsement
			}
		}

		initiateEndorsementAndNavigateToQuoteTab(policyType);

		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//
		premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue(paymentPlanChangeTo);
		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(ALL_LOW_DOWN_PAYMENT_PLANS);

		//Navigate to different Tab and back to Quote Tab
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlanChangeTo);
		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(ALL_LOW_DOWN_PAYMENT_PLANS);//both Low Down pay plans

		//Calculate premium and 'Save and Exit'
		premiumsAndCoveragesQuoteTabHo.btnCalculatePremium().click();
		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlanChangeTo);
		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(ALL_LOW_DOWN_PAYMENT_PLANS);//both Low Down pay plans
		premiumsAndCoveragesQuoteTabHo.saveAndExit();

		//Retrieve Pended endorsement
		PolicySummaryPage.buttonPendedEndorsement.click();
		NavigationPage.comboBoxListAction.setValue("Data Gathering");
		Tab.buttonGo.click();

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlanChangeTo);
		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(ALL_LOW_DOWN_PAYMENT_PLANS);

		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTabHo.btnPurchase.click();
		bindTabHo.confirmEndorsementPurchase.buttonYes.click();

		//fill Purchase tab if it is present
		if (PurchaseTab.btnApplyPayment.isPresent()) {
			//get Test Data to fill Purchase Tab
			TestData tdPurchase = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData").resolveLinks();
			policyType.get().getDefaultView().fillFromTo(tdPurchase, PurchaseTab.class, PurchaseTab.class, true);
			PurchaseTab.btnApplyPayment.click();
			PurchaseTab.confirmPurchase.buttonYes.click();
		}

		if (RollOnChangesActionTab.buttonCancel.isPresent()) {
			RollOnChangesActionTab.buttonCancel.click();
		}
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//Initiate endorsement for the policy and validate that 'Low Down' payment plan option is NOT available in dropdown anymore
		TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus10Day");
		policyType.get().endorse().perform(testData);
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlanChangeTo);

		//IF during midterm endorsement changing payment plan from one Low Down plan to another Low down plan, Low Down plans still should be available during next endorsement
		if (changeToAnotherLowDown) {
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.doesNotContain(ALL_LOW_DOWN_PAYMENT_PLANS);
		} else {
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.contains(ALL_LOW_DOWN_PAYMENT_PLANS);
		}

		PremiumsAndCoveragesQuoteTab.buttonCancel.click();
		PremiumsAndCoveragesQuoteTab.dialogCancelAction.buttonYes.click();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	//The same method as initiateEndorsementAndValidatePaymentPlanWithChangingHo(), but for PUP
	private void initiateEndorsementAndValidatePaymentPlanWithChangingPup(PolicyType policyType, String paymentPlan, boolean changeToAnotherLowDown) {
		String paymentPlanChangeTo = null; //change to this Payment plan during endorsement

		// IF changeToAnotherLowDown is TRUE, change payment plan to Different Low Down payment plan, else change to non Low Down plan
		if (changeToAnotherLowDown) {
			if (paymentPlan.equals(BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN)) {
				paymentPlanChangeTo = BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;//change to this Payment plan during endorsement

			} else if (paymentPlan.equals(BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN)) {
				paymentPlanChangeTo = BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN;//change to this Payment plan during endorsement
			} else {
				paymentPlanChangeTo = BillingConstants.PaymentPlan.PAY_IN_FULL; //change to this Payment plan during endorsement
			}
		}

		initiateEndorsementAndNavigateToQuoteTab(policyType);

		assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlan);//
		premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).setValue(paymentPlanChangeTo);
		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(ALL_LOW_DOWN_PAYMENT_PLANS);

		//Navigate to different Tab and back to Quote Tab
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.GENERAL.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());

		assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlanChangeTo);
		assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(ALL_LOW_DOWN_PAYMENT_PLANS);//both Low Down pay plans

		//Calculate premium and 'Save and Exit'
		premiumsAndCoveragesQuoteTabPup.calculatePremium();
		assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlanChangeTo);
		assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(ALL_LOW_DOWN_PAYMENT_PLANS);//both Low Down pay plans
		premiumsAndCoveragesQuoteTabHo.saveAndExit();

		//Retrieve Pended endorsement
		PolicySummaryPage.buttonPendedEndorsement.click();
		NavigationPage.comboBoxListAction.setValue("Data Gathering");
		Tab.buttonGo.click();

		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
		assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlanChangeTo);
		assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
				.contains(ALL_LOW_DOWN_PAYMENT_PLANS);

		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		bindTabPup.btnPurchase.click();
		bindTabPup.confrimEndorsementPurchase.buttonYes.click();

		//fill Purchase tab if it is present
		if (PurchaseTab.btnApplyPayment.isVisible()) {
			//get Test Data to fill Purchase Tab
			TestData tdPurchase = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData").resolveLinks();
			policyType.get().getDefaultView().fillFromTo(tdPurchase, PurchaseTab.class, PurchaseTab.class, true);
			PurchaseTab.btnApplyPayment.click();
			PurchaseTab.confirmPurchase.buttonYes.click();
		}

		if (RollOnChangesActionTab.buttonCancel.isPresent()) {
			RollOnChangesActionTab.buttonCancel.click();
		}
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		//Initiate endorsement for the policy and validate 'Low Down' payment plan option in dropdown anymore
		TestData testData = getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus10Day");
		policyType.get().endorse().perform(testData);
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());

		assertThat(premiumsAndCoveragesQuoteTabPup.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN)).hasValue(paymentPlanChangeTo);

		//IF during midterm endorsement changing payment plan from one Low Down plan to another Low down plan, Low Down plans still should be available during next endorsement
		if (changeToAnotherLowDown) {
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.doesNotContain(ALL_LOW_DOWN_PAYMENT_PLANS);
		} else {
			assertThat(premiumsAndCoveragesQuoteTabHo.getAssetList().getAsset(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues())
					.contains(ALL_LOW_DOWN_PAYMENT_PLANS);
		}

		premiumsAndCoveragesQuoteTabPup.calculatePremium();
		PremiumsAndCoveragesQuoteTab.dialogCancelAction.buttonYes.click();//TODO-mstrazds:possibly will not work
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	private void generateRenewalImageAndRetrievePolicy(TimePoints timePoints) {
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

		//Generate renewal image at renewal image generation date
		LocalDateTime renewImageGenDate = timePoints.getRenewImageGenerationDate(policyExpirationDate);

		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	private String getExpectedPaymentPlanForRenewal(String paymentPlan) {
		String expectedPaymentPlanAtRenewal = null;
		if (paymentPlan.equals(BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN)) {
			expectedPaymentPlanAtRenewal = BillingConstants.PaymentPlan.MONTHLY_RENEWAL;

		} else if (paymentPlan.equals(BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN)) {
			expectedPaymentPlanAtRenewal = BillingConstants.PaymentPlan.ELEVEN_PAY_RENEWAL;
		}
		return expectedPaymentPlanAtRenewal;
	}

	public String getRandomLowDownPaymentPlan() {
		ArrayList<String> lowDownPaymentPlans = new ArrayList<String>();

		lowDownPaymentPlans.add(BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN);
		lowDownPaymentPlans.add(BillingConstants.PaymentPlan.ELEVEN_PAY_LOW_DOWN);

		// Get Random Low Down payment plan from Arraylist using Random().nextInt()
		String lowDownPaymentPlan = lowDownPaymentPlans.get(new Random().nextInt(lowDownPaymentPlans.size()));
		log.info("====Randomly selected Low Down payment plan to use for NB: " + lowDownPaymentPlan);
		return lowDownPaymentPlan;
	}
}
