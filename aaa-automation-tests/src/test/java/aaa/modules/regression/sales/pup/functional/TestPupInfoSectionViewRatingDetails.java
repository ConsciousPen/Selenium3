package aaa.modules.regression.sales.pup.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.PA)
public class TestPupInfoSectionViewRatingDetails extends PersonalUmbrellaBaseTest {

    private UnderlyingRisksAutoTab underlyingRisksAutoTab = new UnderlyingRisksAutoTab();
    private PremiumAndCoveragesQuoteTab premiumAndCoveragesQuoteTab = new PremiumAndCoveragesQuoteTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private ErrorTab errorTab = new ErrorTab();
    private BindTab bindTab = new BindTab();

    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private PurchaseTab purchaseTabAuto = new PurchaseTab();

    /**
     * @author Dominykas Razgunas
     * @name Test PUP VRD Auto tier and Underlying risks Auto tier with underlying Auto policy
     * @scenario
     * 1. Create customer
     * 2. Create Auto policy
     * 3. Create HO3 policy with underlying Auto from above
     * 4. Initiate PUP Policy
     * 5. Navigate to Underlying Risks Auto Tab
     * 6. Validate Auto tier
     * 7. Navigate Premium Page
     * 8. Calculate Premium and check Auto tier value
     * 9. Renew the policy
     * 10. Navigate to Underlying Risks Auto Tab
     * 11. Validate Auto tier
     * 12. Navigate Premium Page
     * 13. Calculate Premium
     * 14. Check Auto tier value
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-10397, PAS-10391")
    public void pas10397_testPupInfoSectionViewRatingDetails(@Optional("PA") String state) {

        List<String> rangeTier = IntStream.rangeClosed(1, 16).boxed().map(String::valueOf).collect(Collectors.toList());
        rangeTier.add("N/A");

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", "TestData");
        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");

        String otherActiveKeyPath = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        Map<String, String> policies = new HashMap<>();

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto Policy
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        policies.put("autoPolicy", PolicySummaryPage.getPolicyNumber());
        TestData tdOtherActiveAuto = getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy number", policies.get("autoPolicy"));

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());

        // Initiate PUP and verify  in rating details dialog
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPupTD(policies), UnderlyingRisksPropertyTab.class);

        // Check if Auto tier value is 1-16.   PAS-10391
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
        assertThat(rangeTier).contains(underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).getValue());

        openRatingDetails();

        // Verify That Auto tier is N/A or between 1-16. PAS-10397
        assertThat(rangeTier).contains(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier"));

        // Issue Policy
        issuePupFromPremiumTab();

        // Initiate renewal and navigate to P&C Quote tab calculate premium
        policy.renew().start().submit();

        // Check if Auto tier value is 1-16.   PAS-10391
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
        assertThat(rangeTier).contains(underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).getValue());

        // Open rating details
		openRatingDetails();

        // Verify That Auto tier is N/A or between 1-16. PAS-10397
        assertThat(rangeTier).contains(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier"));

        PropertyQuoteTab.RatingDetailsViewPUP.close();
    }

    /**
     * @author Dominykas Razgunas
     * @name Test PUP VRD Auto tier with no underlying Auto policy
     * @scenario
     * 1. Create customer
     * 2. Create HO3 policy without underlying Auto
     * 3. Initiate PUP Policy
     * 4. Calculate Premium and check Auto tier value
     * 5. Issue the policy
     * 6. Renew the policy
     * 7. Navigate to P&C page
     * 8. Calculate Premium
     * 9. Check Auto tier value
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-10397")
    public void pas10397_testPupInfoSectionViewRatingDetailsNoAuto(@Optional("PA") String state) {

        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");

        Map<String, String> policies = new HashMap<>();

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3);
        policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());

        // Initiate PUP and verify  in rating details dialog
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPupTDNoAuto(policies), UnderlyingRisksPropertyTab.class);

        openRatingDetails();

        // Verify That Auto tier is empty. PAS-10397
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier").isEmpty()).isTrue();

        // Issue Policy
        issuePupFromPremiumTab();

        // Initiate renewal and navigate to P&C Quote tab calculate premium
        policy.renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        premiumAndCoveragesQuoteTab.calculatePremium();
        PropertyQuoteTab.RatingDetailsViewPUP.open();

        // Verify That Auto tier is empty. PAS-10397
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier").isEmpty()).isTrue();

        PropertyQuoteTab.RatingDetailsViewPUP.close();
    }

    /**
     * @author Dominykas Razgunas
     * @name Test PUP VRD Auto tier and Underlying risks Auto tier with non PA Auto policy
     * @scenario
     * 1. Create customer
     * 2. Create Non PA Auto policy
     * 3. Create HO3 policy with underlying Auto from above
     * 4. Initiate PUP Policy
     * 5. Navigate to Underlying Risks Auto Tab
     * 6. Validate Auto tier
     * 7. Navigate Premium Page
     * 8. Calculate Premium and check Auto tier value
     * 9. Renew the policy
     * 10. Navigate to Underlying Risks Auto Tab
     * 11. Validate Auto tier
     * 12. Navigate Premium Page
     * 13. Calculate Premium
     * 14. Check Auto tier value
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-10397, PAS-10391")
    public void pas10397_testPupInfoSectionViewRatingDetailsNonPAAuto(@Optional("PA") String state) {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData_AZ")
                .adjust(PrefillTab.class.getSimpleName(), getTestSpecificTD("PrefillTab_AZ"))
                .adjust(DocumentsAndBindTab.class.getSimpleName(), getTestSpecificTD("DocumentsAndBindTab_AZ"));
        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");

        String otherActiveKeyPath = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        Map<String, String> policies = new HashMap<>();

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto Policy
        PolicyType.AUTO_SS.get().initiate();
        PolicyType.AUTO_SS.get().getDefaultView().fillUpTo(tdAuto, DocumentsAndBindTab.class, true);
        documentsAndBindTab.submitTab();
        errorTab.overrideAllErrors();
        errorTab.override();
        documentsAndBindTab.submitTab();
        purchaseTabAuto.fillTab(tdAuto);
        purchaseTabAuto.submitTab();


        policies.put("autoPolicy", PolicySummaryPage.getPolicyNumber());
        TestData tdOtherActiveAuto = getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy number", policies.get("autoPolicy"));

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());

        // Initiate PUP and verify  in rating details dialog
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPupTD(policies), UnderlyingRisksPropertyTab.class);

        // Check if Auto tier value is N/A.   PAS-10391
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
        assertThat(underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER)).hasValue("N/A");

        openRatingDetails();

        // Verify That Auto tier is N/A or between 1. PAS-10397
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier").contains("1")).isTrue();

        // Issue Policy
        issuePupFromPremiumTab();

        // Initiate renewal and navigate to P&C Quote tab calculate premium
        policy.renew().start().submit();

        // Check if Auto tier value is N/A.   PAS-10391
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
        assertThat(underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER)).hasValue("N/A");

        // Open rating details
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        premiumAndCoveragesQuoteTab.calculatePremium();
        PropertyQuoteTab.RatingDetailsViewPUP.open();

        // Verify That Auto tier is 1. PAS-10397
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier").contains("1")).isTrue();

        PropertyQuoteTab.RatingDetailsViewPUP.close();
    }

    /**
     * @name Test PUP VRD Auto tier with manually added Auto policy NB
     * @scenario
     * 1. Create customer
     * 2. Create Auto policy
     * 3. Create HO3 policy with underlying Auto from above
     * 4. Initiate PUP Policy
     * 5. Navigate to Underlying Risks Auto Tab
     * 6. Validate Auto tier
     * 7. Manually add Auto Policy
     * 8. Select manually added auto policy as the primary
     * 9. Navigate Premium And Coverages Page
     * 10. Calculate Premium and check Auto tier value is taken from manually added Auto Policy
     * 11. Navigate to Underlying Risks Auto Tab
     * 12. Change Primary Auto Policies tier
     * 13. Navigate Premium And Coverages Page
     * 14. Calculate Premium and check Auto tier value is taken from manually added Auto Policy
     * 15. Issue Policy
     * 16. Initiate Renewal
     * 17. Navigate Premium And Coverages Page
     * 18. Calculate Premium and check Auto tier value is taken from manually added Auto Policy
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-15989")
    public void pas15989_testManuallyAddedAutoTier(@Optional("PA") String state) {

        List<String> rangeTier = IntStream.rangeClosed(1, 16).boxed().map(String::valueOf).collect(Collectors.toList());
        rangeTier.add("N/A");

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", "TestData");
        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");

        String otherActiveKeyPath = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        Map<String, String> policies = new HashMap<>();

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto Policy
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        policies.put("autoPolicy", PolicySummaryPage.getPolicyNumber());
        TestData tdOtherActiveAuto = getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy number", policies.get("autoPolicy"));

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());

        // Initiate PUP and verify  in rating details dialog
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPupTD(policies), UnderlyingRisksPropertyTab.class);

        // Check if Auto tier value is 1-16.   PAS-10391
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
        assertThat(rangeTier).contains(underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).getValue());

        openRatingDetails();

        // Verify That Auto tier is N/A or between 1-16. PAS-10397
        assertThat(rangeTier).contains(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier"));
        PropertyQuoteTab.RatingDetailsViewPUP.close();

        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());

        underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.PRIMARY_AUTO_POLICY).setValue("No");
        underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.ADD).click();
        underlyingRisksAutoTab.fillTab(getPolicyTD());
        underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).setValueByIndex(2);
        String autoTierValue = underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).getValue();

        openRatingDetails();

        // Verify That Auto tier is the same as Manually added policies
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier")).isEqualTo(autoTierValue);
        PropertyQuoteTab.RatingDetailsViewPUP.close();

        // Change Manually Added Auto policies tier
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
        underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).setValue("11");
        String autoTierValue1 = underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).getValue();

        openRatingDetails();

        // Verify That Auto tier is the same as Manually added policies
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier")).isEqualTo(autoTierValue1);

        // Issue Policy
        issuePupFromPremiumTab();

        // Initiate renewal and navigate to P&C Quote tab calculate premium
        policy.renew().start().submit();

        openRatingDetails();

        // Verify That Auto tier is the same as Manually added policies
        assertThat(autoTierValue1).isEqualTo(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier"));

        PropertyQuoteTab.RatingDetailsViewPUP.close();
    }


	/**
	 * @author Dominykas Razgunas
	 * @name Test NY PUP VRD Market Tier Value Lock
	 * @scenario
	 * 1. Create customer
	 * 2. Create HO3 policy
	 * 3. Initiate PUP Policy
	 * 4. Navigate to Underlying Risks Auto Tab
	 * 5. Select Auto tier
	 * 6. Navigate Premium Page
	 * 7. Calculate Premium and check Market tier value
	 * 8. Flat Endorse the policy
	 * 9. Navigate to Underlying Risks Auto Tab
	 * 10. Select different Auto tier
	 * 11. Navigate Premium Page
	 * 12. Calculate Premium
	 * 13. Check Market Tier value changed
	 * 14. Issue endorsement
	 * 15. Mid-term Endorse the policy
	 * 16. Navigate to Underlying Risks Auto Tab
	 * 17. Select different Auto tier
	 * 18. Navigate Premium Page
	 * 19. Calculate Premium
	 * 20. Check Market Tier value used from the flat endorsement
	 * 21. Issue endorsement
	 * 22. Initiate renewal
	 * 23. Navigate to Underlying Risks Auto Tab
	 * 24. Select different Auto tier
	 * 25. Navigate Premium Page
	 * 26. Calculate Premium
	 * 27. Check Market Tier value used from the flat endorsement
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.NY)
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-14037")
	public void pas14037_testNYMarketTierLockVRD(@Optional("NY") String state) {

		TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");

		Map<String, String> policies = new HashMap<>();

		// Create Customer
		mainApp().open();
		createCustomerIndividual();

		// Create HO3 Policy with underlying Auto policy
		PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3);
		policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());

		// Initiate PUP and verify  in rating details dialog
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPupTDNoAuto(policies), UnderlyingRisksPropertyTab.class);

		// Select Auto tier value to 2.
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
		underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.ADD_AUTOMOBILE).setValue("Yes");
		underlyingRisksAutoTab.fillTab(getPolicyTD());
		underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).setValue("2");

		openRatingDetails();
		String marketTier1 = PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Market tier");

		// Issue Policy
		issuePupFromPremiumTab();

		// Initiate renewal and navigate to P&C Quote tab calculate premium
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		// change auto tier value
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
		underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).setValue("15");

		// Open rating details
		openRatingDetails();

		// Save Market Tier 2. Assert that market tier 1 is not the same as market tier 2
		String marketTier2 = PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Market tier");
		assertThat(marketTier1).isNotEqualTo(marketTier2);
		PropertyQuoteTab.RatingDetailsViewPUP.close();
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		bindTab.submitTab();

		// Initiate Mid-term endorsement
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

		// change auto tier value
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
		underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).setValue("1");

		// Open rating details
		openRatingDetails();
		// Verify That Market tier is the value from flat endorsement.
		assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Market tier")).contains(marketTier2);
		PropertyQuoteTab.RatingDetailsViewPUP.close();
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
		bindTab.submitTab();

		// Initiate renewal and navigate to P&C Quote tab calculate premium
		policy.renew().start().submit();

		// Set new auto tier value
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS.get());
		NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.UNDERLYING_RISKS_AUTO.get());
		underlyingRisksAutoTab.getAutomobilesAssetList().getAsset(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER).setValue("7");

		// Open rating details
		openRatingDetails();

		// Verify That Market tier is the value from flat endorsement.
		assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Market tier")).contains(marketTier2);

		PropertyQuoteTab.RatingDetailsViewPUP.close();
	}


    private TestData getPupTD(Map<String, String> policies) {
        TestData prefillTab = getTestSpecificTD("TestData_PrefillTab")
                .adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("ho3Policy"))
                .adjust("ActiveUnderlyingPolicies[1]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("autoPolicy"));
        return getPolicyTD().adjust(PrefillTab.class.getSimpleName(), prefillTab).mask(UnderlyingRisksAutoTab.class.getSimpleName());
    }

    private TestData getPupTDNoAuto(Map<String, String> policies) {
        TestData prefillTab = getTestSpecificTD("TestData_PrefillTab_NoAuto")
                .adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("ho3Policy"));
        return getPolicyTD().adjust(PrefillTab.class.getSimpleName(), prefillTab).mask(UnderlyingRisksAutoTab.class.getSimpleName());
    }

    private void issuePupFromPremiumTab() {
		PropertyQuoteTab.RatingDetailsViewPUP.close();
		premiumAndCoveragesQuoteTab.submitTab();
		policy.getDefaultView().fillFromTo(getPolicyTD(), UnderwritingAndApprovalTab.class, BindTab.class, true);
		bindTab.submitTab();

		// Override errors for scenarios with overridable errors
		if(errorTab.isVisible()){
		errorTab.overrideAllErrors();
		errorTab.override();
		bindTab.submitTab();
	}

        purchaseTab.fillTab(getPolicyTD());
        purchaseTab.submitTab();
    }

    private void openRatingDetails(){
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        premiumAndCoveragesQuoteTab.calculatePremium();
        PropertyQuoteTab.RatingDetailsViewPUP.open();
    }

}