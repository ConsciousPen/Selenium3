package aaa.modules.regression.sales.pup.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksPropertyTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPupRatingWithMultipleUnits extends PersonalUmbrellaBaseTest {

    /**
     * @author Josh Carpenter
     * @name Test PUP policies that have an underlying DP3 policy with multiple units is rated properly for SS states.
     * @scenario
     * 1. Create customer
     * 2. Create Auto policy
     * 3. Create HO3 policy with underlying Auto from above
     * 4. Create DP3 policy with underlying Auto/HO3 from above; select '3- triplex' for number of units
     * 5. Initiate PUP policy and calculate premium
     * 6. Verify the number of units is correct in rating details dialog
     * @details
     */
    @StateList(statesExcept = Constants.States.CA)
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-8369")
    public void pas8369_testPUPWithMultipleUnitsOnDP3ForSS(@Optional("") String state) {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", "TestData");
        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");
        TestData tdHO4 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO4), "DataGather", "TestData");
        TestData tdHO6 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO6), "DataGather", "TestData");
        TestData tdDP3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_DP3), "DataGather", "TestData")
            .adjust(TestData.makeKeyPath(HomeSSMetaData.PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(),
                    HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel()), "3-Triplex");

        String otherActiveKeyPath = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        String manualPolicy = HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel();
        String searchPolicyType = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(),
                                        HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesSearch.POLICY_TYPE.getLabel());
        String searchPolicyNumber = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(),
                                        HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesSearch.POLICY_NUMBER.getLabel());
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

        // Create HO4 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO4.get().createPolicy(tdHO4.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho4Policy", PolicySummaryPage.getPolicyNumber());

        // Create HO6 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO6.get().createPolicy(tdHO6.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho6Policy", PolicySummaryPage.getPolicyNumber());

        // Create 2 DP3 Policies with above underlying policies AND more than 1 unit (3 - triplex)
        tdDP3.mask(TestData.makeKeyPath(otherActiveKeyPath + "[0]", manualPolicy)).mask(TestData.makeKeyPath(otherActiveKeyPath + "[1]", manualPolicy))
                .adjust(TestData.makeKeyPath(otherActiveKeyPath + "[0]", searchPolicyType), "Auto")
                .adjust(TestData.makeKeyPath(otherActiveKeyPath + "[0]", searchPolicyNumber), policies.get("autoPolicy"))
                .adjust(TestData.makeKeyPath(otherActiveKeyPath + "[1]", searchPolicyType), "HO3")
                .adjust(TestData.makeKeyPath(otherActiveKeyPath + "[1]", searchPolicyNumber), policies.get("ho3Policy"));

        PolicyType.HOME_SS_DP3.get().createPolicy(tdDP3);
        policies.put("dpPolicy1", PolicySummaryPage.getPolicyNumber());
        PolicyType.HOME_SS_DP3.get().createPolicy(tdDP3);
        policies.put("dpPolicy2", PolicySummaryPage.getPolicyNumber());


        // Initiate PUP and verify units in rating details dialog
        initiateFillPup(getPupTD(policies));

        // Open rating details and verify the number of units charged is correct
        PropertyQuoteTab.RatingDetailsViewPUP.open();
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Rental units")).isEqualTo("6 : $90.00");
        PropertyQuoteTab.RatingDetailsViewPUP.close();

    }

    /**
     * @author Josh Carpenter
     * @name Test PUP policies that have an underlying DP3 policy with multiple units is rated properly for CA.
     * @scenario
     * 1. Create customer
     * 2. Create CA Auto policy
     * 3. Create CA HO3 policy with underlying Auto from above
     * 4. Create CA DP3 policy with underlying Auto/HO3 from above; select '3- triplex' for number of units
     * 5. Initiate PUP policy and calculate premium
     * 6. Verify the number of units is correct in rating details dialog
     * @details
     */
    @StateList(states = Constants.States.CA)
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-8369")
    public void pas8369_testPUPWithMultipleUnitsOnDP3ForCA(@Optional("CA") String state) {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT), "DataGather", "TestData");
        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO3), "DataGather", "TestData");
        TestData tdHO4 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO4), "DataGather", "TestData");
        TestData tdHO6 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO6), "DataGather", "TestData");
        TestData tdDP3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_DP3), "DataGather", "TestData")
                .adjust(TestData.makeKeyPath(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(), HomeCaMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(),
                        HomeCaMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel()), "3-Triplex");

        String otherActiveKeyPath = TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        Map<String, String> policies = new HashMap<>();

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto Policy
        PolicyType.AUTO_CA_SELECT.get().createPolicy(tdAuto);
        policies.put("autoPolicy", PolicySummaryPage.getPolicyNumber());
        TestData tdOtherActiveAuto = getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy number", policies.get("autoPolicy"));

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_CA_HO3.get().createPolicy(tdHO3.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());

        // Create HO4 Policy with underlying Auto policy
        PolicyType.HOME_CA_HO4.get().createPolicy(tdHO4.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho4Policy", PolicySummaryPage.getPolicyNumber());

        // Create HO6 Policy with underlying Auto policy
        PolicyType.HOME_CA_HO6.get().createPolicy(tdHO6.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho6Policy", PolicySummaryPage.getPolicyNumber());

        // Create 2 DP3 Policies with above underlying policies AND more than 1 unit (3 - triplex)
        tdDP3.adjust(otherActiveKeyPath, getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy number", policies.get("ho3Policy")));
        PolicyType.HOME_CA_DP3.get().createPolicy(tdDP3);
        policies.put("dpPolicy1", PolicySummaryPage.getPolicyNumber());
        PolicyType.HOME_CA_DP3.get().createPolicy(tdDP3);
        policies.put("dpPolicy2", PolicySummaryPage.getPolicyNumber());

        // Initiate PUP and verify units in rating details dialog
        TestData tdPUP = getPupTD(policies).adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]", PersonalUmbrellaMetaData.PrefillTab.NamedInsured.OCCUPATION.getLabel()), "index=1");
        initiateFillPup(tdPUP);

        PropertyQuoteTab.RatingDetailsViewPUP.open();
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Residences rented to others")).isEqualTo("$60");
        PropertyQuoteTab.RatingDetailsViewPUP.close();

    }

    private TestData getPupTD(Map<String, String> policies) {
        TestData prefillTab = getTestSpecificTD("TestData_PrefillTab")
                .adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("ho3Policy"))
                .adjust("ActiveUnderlyingPolicies[1]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("ho4Policy"))
                .adjust("ActiveUnderlyingPolicies[2]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("ho6Policy"))
                .adjust("ActiveUnderlyingPolicies[3]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("dpPolicy1"))
                .adjust("ActiveUnderlyingPolicies[4]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("dpPolicy2"))
                .adjust("ActiveUnderlyingPolicies[5]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("autoPolicy"));
        return getPolicyTD().adjust(PrefillTab.class.getSimpleName(), prefillTab).mask(UnderlyingRisksAutoTab.class.getSimpleName());
    }

    private void initiateFillPup(TestData tdPUP) {
        // Initiate PUP policy, fill, and calculate premium
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, UnderlyingRisksPropertyTab.class);
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        new PremiumAndCoveragesQuoteTab().calculatePremium();
    }
}
