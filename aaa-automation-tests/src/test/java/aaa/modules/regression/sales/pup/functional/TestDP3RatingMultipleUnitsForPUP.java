package aaa.modules.regression.sales.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestDP3RatingMultipleUnitsForPUP extends PersonalUmbrellaBaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-8369")
    public void pas8369_testPUPWithMultipleUnitsOnDP3ForSS(@Optional("") String state) {

        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");
        TestData tdDP3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_DP3), "DataGather", "TestData");
        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", "TestData");
        TestData tdPUP = getPolicyTD();

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto Policy
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        String autoPolicy = PolicySummaryPage.getPolicyNumber();
        TestData tdOtherActiveHO = getTestSpecificTD("TestData_OtherActive_HO").adjust("ActiveUnderlyingPoliciesSearch|Policy Number", autoPolicy);
        tdHO3.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActiveHO);

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3);
        String hoPolicy = PolicySummaryPage.getPolicyNumber();
        TestData tdApplicantTabDP = getTestSpecificTD("TestData_ApplicantTab_DP3")
                .adjust("OtherActiveAAAPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", autoPolicy)
                .adjust("OtherActiveAAAPolicies[1]|ActiveUnderlyingPoliciesSearch|Policy Number", hoPolicy);
        tdDP3.adjust(ApplicantTab.class.getSimpleName(), tdApplicantTabDP).adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(),
                HomeSSMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(),
                HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel()), "index=3");

        // Create DP3 Policy with above underlying policies AND more than 1 unit (3 - triplex)
        PolicyType.HOME_SS_DP3.get().createPolicy(tdDP3);
        String dpPolicy = PolicySummaryPage.getPolicyNumber();
        TestData tdPrefillTabPUP = getTestSpecificTD("TestData_PrefillTab_PUP")
                .adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", hoPolicy)
                .adjust("ActiveUnderlyingPolicies[1]|ActiveUnderlyingPoliciesSearch|Policy Number", dpPolicy)
                .adjust("ActiveUnderlyingPolicies[2]|ActiveUnderlyingPoliciesSearch|Policy Number", autoPolicy);
        tdPUP.adjust(PrefillTab.class.getSimpleName(), tdPrefillTabPUP);

        // Initiate PUP policy and fill up to P&C tab
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, PremiumAndCoveragesQuoteTab.class, true);

        // Open rating details and verify the number of units charged is correct
        PremiumAndCoveragesQuoteTab.RatingDetailsView.open();
    }
}
