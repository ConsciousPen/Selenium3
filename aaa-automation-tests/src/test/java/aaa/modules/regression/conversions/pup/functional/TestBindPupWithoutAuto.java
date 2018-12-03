package aaa.modules.regression.conversions.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.MD, Constants.States.PA, Constants.States.DE, Constants.States.NJ, Constants.States.VA})
public class TestBindPupWithoutAuto extends ConvPUPBaseTest {

    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);

    /**
     * @author Josh Carpenter
     * @name Test that a PUP without an active Auto Policy or Exclusion endorsement can be bound for NB
     * @scenario
     * 1. Create customer
     * 2. Create HO3 policy
     * 3. Initiate and attempt to bind PUP policy
     * 4. Verify error with code AAA_PUP_SS507440 exists and can be overridden
     * 5. Bind/purchase policy
     * 6. Verify the policy is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6957")
    public void pas6957_TestBindPupWithoutAutoNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Create HO3 policy
        PolicyType.HOME_SS_HO3.get().createPolicy(getTdHome());

        // Create Test Data
        TestData tdOtherActive = getTestSpecificTD("TestData_ActiveUnderlyingPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy Number"), PolicySummaryPage.getPolicyNumber());
        TestData tdPUP = getPolicyTD()
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel()), tdOtherActive)
                .mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()));

        // Initiate PUP policy
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        verifyErrorsOverrideAndBind(tdPUP, ErrorEnum.Errors.ERROR_AAA_PUP_SS5071440);
    }

    /**
     * @author Josh Carpenter
     * @name Test that a PUP without an active Auto Policy or Exclusion endorsement can be bound during manual entry
     * @scenario
     * 1. Create customer
     * 2. Create HO3 policy
     * 3. Initiate Manual Renewal Entry for PUP policy
     * 4. Verify error with code AAA_PUP_SS507440 exists and can be overridden
     * 5. Bind/purchase policy
     * 6. Verify the policy is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6957")
    public void pas6957_TestBindPupWithoutAutoConversion(@Optional("NJ") String state) {

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create HO3 policy
        PolicyType.HOME_SS_HO3.get().createPolicy(getTdHome());

        // Create Test Data
        TestData tdOtherActive = getTestSpecificTD("TestData_ActiveUnderlyingPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy Number"), PolicySummaryPage.getPolicyNumber());
        TestData tdPUP = getPolicyTD("Conversion", "TestData")
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel()), tdOtherActive)
                .mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()));

        // Initiate manual renewal entry for PUP
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        verifyErrorsAndOverride(ErrorEnum.Errors.ERROR_AAA_PUP_SS5071440);
    }

    /**
     * @return Test Data for an HO3 policy with no other active policies
     */
    private TestData getTdHome() {
        return getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()));
    }
}
