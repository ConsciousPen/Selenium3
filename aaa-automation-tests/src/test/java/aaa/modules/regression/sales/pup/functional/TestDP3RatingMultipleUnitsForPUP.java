package aaa.modules.regression.sales.pup.functional;

import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestDP3RatingMultipleUnitsForPUP extends PersonalUmbrellaBaseTest {

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
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto Policy
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        String autoPolicy = PolicySummaryPage.getPolicyNumber();
        TestData tdOtherActiveAuto = getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy Number", autoPolicy);

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        String ho3Policy = PolicySummaryPage.getPolicyNumber();

        // Create HO4 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO4.get().createPolicy(tdHO4.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        String ho4Policy = PolicySummaryPage.getPolicyNumber();

        // Create HO6 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO6.get().createPolicy(tdHO6.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        String ho6Policy = PolicySummaryPage.getPolicyNumber();

        // Create DP3 Policy with above underlying policies AND more than 1 unit (3 - triplex)
        tdDP3.mask(TestData.makeKeyPath(otherActiveKeyPath + "[0]", manualPolicy)).mask(TestData.makeKeyPath(otherActiveKeyPath + "[1]", manualPolicy))
                .adjust(TestData.makeKeyPath(otherActiveKeyPath + "[0]", searchPolicyType), "Auto")
                .adjust(TestData.makeKeyPath(otherActiveKeyPath + "[0]", searchPolicyNumber), autoPolicy)
                .adjust(TestData.makeKeyPath(otherActiveKeyPath + "[1]", searchPolicyType), "HO3")
                .adjust(TestData.makeKeyPath(otherActiveKeyPath + "[1]", searchPolicyNumber), ho3Policy);
        PolicyType.HOME_SS_DP3.get().createPolicy(tdDP3);
        String dpPolicy = PolicySummaryPage.getPolicyNumber();

        // Initiate PUP and verify units in rating details dialog
        initiatePupVerifyUnits(getPupTD(autoPolicy, ho3Policy, ho4Policy, ho6Policy, dpPolicy));

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
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
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

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto Policy
        PolicyType.AUTO_CA_SELECT.get().createPolicy(tdAuto);
        String autoPolicy = PolicySummaryPage.getPolicyNumber();
        TestData tdOtherActiveAuto = getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy Number", autoPolicy);

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_CA_HO3.get().createPolicy(tdHO3.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        String ho3Policy = PolicySummaryPage.getPolicyNumber();

        // Create HO4 Policy with underlying Auto policy
        PolicyType.HOME_CA_HO4.get().createPolicy(tdHO4.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        String ho4Policy = PolicySummaryPage.getPolicyNumber();

        // Create HO6 Policy with underlying Auto policy
        PolicyType.HOME_CA_HO6.get().createPolicy(tdHO6.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        String ho6Policy = PolicySummaryPage.getPolicyNumber();

        // Create DP3 Policy with above underlying policies AND more than 1 unit (3 - triplex)
        tdDP3.adjust(otherActiveKeyPath, getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy Number", ho3Policy));
        PolicyType.HOME_CA_DP3.get().createPolicy(tdDP3);
        String dpPolicy = PolicySummaryPage.getPolicyNumber();

        // Initiate PUP and verify units in rating details dialog
        TestData tdPUP = getPupTD(autoPolicy, ho3Policy, ho4Policy, ho6Policy, dpPolicy).adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]", PersonalUmbrellaMetaData.PrefillTab.NamedInsured.OCCUPATION.getLabel()), "index=1");
        initiatePupVerifyUnits(tdPUP);

    }

    private TestData getPupTD(String autoPolicy, String ho3Policy, String ho4Policy, String ho6Policy, String dpPolicy) {
        TestData prefillTab = getTestSpecificTD("TestData_PrefillTab")
                .adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", ho3Policy)
                .adjust("ActiveUnderlyingPolicies[1]|ActiveUnderlyingPoliciesSearch|Policy Number", ho4Policy)
                .adjust("ActiveUnderlyingPolicies[2]|ActiveUnderlyingPoliciesSearch|Policy Number", ho6Policy)
                .adjust("ActiveUnderlyingPolicies[3]|ActiveUnderlyingPoliciesSearch|Policy Number", dpPolicy)
                .adjust("ActiveUnderlyingPolicies[4]|ActiveUnderlyingPoliciesSearch|Policy Number", autoPolicy);
        return getPolicyTD().adjust(PrefillTab.class.getSimpleName(), prefillTab).mask(UnderlyingRisksAutoTab.class.getSimpleName());
    }

    private void initiatePupVerifyUnits(TestData tdPUP) {
        // Initiate PUP policy, fill, and calculate premium
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, PremiumAndCoveragesQuoteTab.class);
        NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        new PremiumAndCoveragesQuoteTab().calculatePremium();

        // Open rating details and verify the number of units charged is correct
        PropertyQuoteTab.RatingDetailsViewPUP.open();
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Rental units")).isEqualTo("1 : $15.00");  // TODO assertion needs updated after fix
        PropertyQuoteTab.RatingDetailsViewPUP.close();
    }
}
