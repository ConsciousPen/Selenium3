package aaa.modules.regression.conversions.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.MD, Constants.States.PA, Constants.States.DE, Constants.States.NJ, Constants.States.VA})
public class TestOverrideBindRelaxedCoverageLimits extends ConvPUPBaseTest {

    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);

    /**
     * @author Josh Carpenter
     * @name Test that during NB for a PUP Policy an authorized user can override and bind when:
     *      a. the Auto BI limit is less than $250K/$500K, OR
     *      b. the Auto PD limit is less than $100K, OR
     *      c. Property limit of liability is less than $500K with a swimming pool, OR
     *      d. Property limit of liability is less than $300K without a swimming pool.
     * @scenario
     * 1. Create customer
     * 2. Create HO Policy with property limit less than $300K WITHOUT a swimming pool
     * 3. Create HO Policy with property limit less than $500K WITH a swimming pool
     * 4. Create Auto Policy with BI limit less than $250K/$500K and PD limit less than $100K
     * 5. Create PUP policy with previously created underlying auto and home policies
     * 6. Attempt to bind and verify rules are fired and can be overridden
     * 7. Bind policy and confirm active
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6971")
    public void pas6971_TestOverrideBindLimitsNB(@Optional("NJ") String state) {

        TestData td = getPolicyTD()
            .mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()))
            .mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel()));

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Gather PUP test data
        TestData tdPUP = getTdWithSpecificUnderlyingPolicies(td);

        // Create PUP policy
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        verifyErrorsOverrideAndBind(tdPUP,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS2220189,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS2220190,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS4242760,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS4242761,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS4242880,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS7160090);
    }

    /**
     * @author Josh Carpenter
     * @name Test that during conversion for a PUP Policy an authorized user can override and bind when:
     *      a. the Auto BI limit is less than $250K/$500K, OR
     *      b. the Auto PD limit is less than $100K, OR
     *      c. Property limit of liability is less than $500K with a swimming pool, OR
     *      d. Property limit of liability is less than $300K without a swimming pool.
     * @scenario
     * 1. Create customer
     * 2. Create HO Policy with property limit less than $300K WITHOUT a swimming pool
     * 3. Create HO Policy with property limit less than $500K WITH a swimming pool
     * 4. Create Auto Policy with BI limit less than $250K/$500K and PD limit less than $100K
     * 5. Initiate PUP conversion policy with previously created underlying auto and home policies
     * 6. Attempt to bind and verify rules are fired and can be overridden
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6971")
    public void pas6971_TestOverrideBindLimitsConversion(@Optional("NJ") String state) {

        TestData td = getPolicyTD("Conversion", "TestData")
            .mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()))
            .mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel()));

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Gather PUP test data
        TestData tdPUP = getTdWithSpecificUnderlyingPolicies(td)
            .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
                    PersonalUmbrellaMetaData.PrefillTab.NamedInsured.OCCUPATION.getLabel()), "index=3");

        // Create PUP conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        verifyErrorsAndOverride(
            ErrorEnum.Errors.ERROR_AAA_PUP_SS2220189,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS2220190,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS4242760,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS4242761,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS4242880,
            ErrorEnum.Errors.ERROR_AAA_PUP_SS7160090);
    }

    /**
     * @author Josh Carpenter
     * @name Test that during NB for a PUP Policy an authorized user can override and bind when the Auto combined single
     * limit is less than $500K.
     * @scenario
     * 1. Create customer
     * 2. Create HO Policy
     * 3. Create PUP policy with previously created HO policy and set Auto combined single limit to less than $500K
     * 4. Attempt to bind and verify rules are fired and can be overridden
     * 5. Bind policy and confirm active
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6971")
    public void pas6971_TestOverrideBindAutoCombinedSingleLimitNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        TestData tdPUP = getTdForAutoSingleLimit(getPolicyTD());

        // Create PUP policy
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        verifyErrorsOverrideAndBind(tdPUP, ErrorEnum.Errors.ERROR_AAA_PUP_SS4245880);
    }

    /**
     * @author Josh Carpenter
     * @name Test that during conversion for a PUP Policy an authorized user can override and bind when the Auto
     * combined single limit is less than $500K.
     * @scenario
     * 1. Create customer
     * 2. Create HO Policy
     * 3. Initiate PUP conversion policy with previously created HO policy and set Auto combined single limit to less than $500K
     * 4. Attempt to bind and verify rules are fired and can be overridden
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6971")
    public void pas6971_TestOverrideBindAutoCombinedSingleLimitConversion(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        TestData tdPUP = getTdForAutoSingleLimit(getPolicyTD("Conversion", "TestData"));

        // Create PUP conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        verifyErrorsAndOverride(ErrorEnum.Errors.ERROR_AAA_PUP_SS4245880);
    }

    /**
     * @param tdPUP the PUP test data to be adjusted with the specific policies for testing
     * @return test data for a PUP policy that includes policies with specific Property/BI/PD limits for testing
     */
    private TestData getTdWithSpecificUnderlyingPolicies(TestData tdPUP) {
        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()))
                .adjust(TestData.makeKeyPath(PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel()), "$200,000");

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                    AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel()), "contains=$100,000/$300,000");

        // Create HO policy without a pool
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3);
        String hoPolicy1 = PolicySummaryPage.getPolicyNumber();

        // Adjust test data to include a pool
        tdHO3.adjust(TestData.makeKeyPath(PropertyInfoTab.class.getSimpleName(), HomeSSMetaData.PropertyInfoTab.RECREATIONAL_EQUIPMENT.getLabel(),
                HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel()), "index=3");

        // Create HO policy with a pool
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3);
        String hoPolicy2 = PolicySummaryPage.getPolicyNumber();

        // Create Auto policy
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        String autoPolicy = PolicySummaryPage.getPolicyNumber();

        // Make test data adjustments for active policies
        TestData tdPrefillTab = getTestSpecificTD("TestData_PrefillTab")
                .adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", hoPolicy1)
                .adjust("ActiveUnderlyingPolicies[1]|ActiveUnderlyingPoliciesSearch|Policy Number", hoPolicy2)
                .adjust("ActiveUnderlyingPolicies[2]|ActiveUnderlyingPoliciesSearch|Policy Number", autoPolicy);

        return tdPUP.adjust(PrefillTab.class.getSimpleName(), tdPrefillTab);
    }

    /**
     * @param tdPUP the PUP test data to be adjusted with the specific policies for testing
     * @return specific Test data for the combined Auto Single limit less than $500K
     */
    private TestData getTdForAutoSingleLimit(TestData tdPUP) {
        TestData tdHO = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()));

        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO);
        String automobileKeyPath = TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel() + "[0]");

        return tdPUP.adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel() + "[0]",
                PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(),
                PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_NUMBER.getLabel()),
        PolicySummaryPage.getPolicyNumber())
            .adjust(TestData.makeKeyPath(automobileKeyPath, PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.COVERAGE_TYPE.getLabel()), "Single")
            .adjust(TestData.makeKeyPath(automobileKeyPath, PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.COMBINED_SINGLE_LIMIT.getLabel()), "300000")
            .mask(TestData.makeKeyPath(automobileKeyPath, PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.BI_LIMITS.getLabel()))
            .mask(TestData.makeKeyPath(automobileKeyPath, PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.PD_LIMITS.getLabel()));
    }
}
