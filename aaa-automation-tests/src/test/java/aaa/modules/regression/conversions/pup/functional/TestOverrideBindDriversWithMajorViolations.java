package aaa.modules.regression.conversions.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ClaimsTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.modules.regression.conversions.ConvPUPBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.MD, Constants.States.PA, Constants.States.DE, Constants.States.NJ, Constants.States.VA})
public class TestOverrideBindDriversWithMajorViolations extends ConvPUPBaseTest {

    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);
    private ClaimsTab claimsTab = policy.getDefaultView().getTab(ClaimsTab.class);
    private PremiumAndCoveragesQuoteTab premiumAndCoveragesQuoteTab = policy.getDefaultView().getTab(PremiumAndCoveragesQuoteTab.class);

    /**
     * @author Josh Carpenter
     * @name Test that when a rule fires related to a driver having an alcohol-related violation in the last 5 years,
     * an authorized user can override and bind the policy for NB.
     * @scenario
     * 1. Create customer
     * 2. Create HO3 Policy
     * 3. Initiate PUP policy
     * 4. Add an alcohol-related violation in the previous 5 years
     * 5. Verify error during bind and override
     * 6. Bind policy and confirm it is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974_TestOverrideBindWithAlcoholRelatedViolationNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = adjustTdWithDriver(getPolicyDefaultTD());

        // Create NB PUP policy
        policy.initiate();
        td.adjust(ClaimsTab.class.getSimpleName(), getTestSpecificTD("TestData_AlcoholViolation"));
        policy.getDefaultView().fillUpTo(td, BindTab.class, true);
        bindTab.submitTab();

        // Verify the error message, finish binding the policy, and confirm active/successful
        verifyErrorsOverrideAndBind(td, ErrorEnum.Errors.ERROR_AAA_PUP_SSER10054);
    }

    /**
     * @author Josh Carpenter
     * @name Test that when a rule fires related to a driver having a major violation in the last 5 years, an
     * authorized user can override and bind the policy for NB.
     * @scenario
     * 1. Create customer
     * 2. Create HO3 Policy
     * 3. Initiate PUP policy
     * 4. Add a major violation in the previous 5 years
     * 5. Verify error during bind and override
     * 6. Bind policy and confirm it is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974_TestOverrideBindWithMajorViolationNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = adjustTdWithDriver(getPolicyDefaultTD());

        // Create NB PUP policy
        policy.initiate();
        td.adjust(ClaimsTab.class.getSimpleName(), getTestSpecificTD("TestData_MajorViolation"));
        policy.getDefaultView().fillUpTo(td, BindTab.class, true);
        bindTab.submitTab();

        // Verify the error message, finish binding the policy, and confirm active/successful
        verifyErrorsOverrideAndBind(td, ErrorEnum.Errors.ERROR_AAA_PUP_SSER10054);
    }

    /**
     * @author Josh Carpenter
     * @name Test that when a rule fires related to a driver having an alcohol-related violation in the last 5 years,
     * an authorized user can override and bind the policy for conversion.
     * @scenario
     * 1. Create customer
     * 2. Create HO3 Policy
     * 3. Initiate PUP conversion policy
     * 4. Add an alcohol-related violation in the previous 5 years
     * 5. Verify error during bind and override
     * 6. Bind policy and confirm it is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974_TestOverrideBindWithAlcoholRelatedViolationConversion(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = adjustTdWithDriver(getConversionPolicyDefaultTD())
                .adjust(ClaimsTab.class.getSimpleName(), getTestSpecificTD("TestData_AlcoholViolation"));

        // Create PUP conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(td, BindTab.class, true);
        bindTab.submitTab();

        // Verify the error message, finish binding the policy, and confirm active/successful
        verifyErrorsAndOverride(ErrorEnum.Errors.ERROR_AAA_PUP_SSER10054);
    }

    /**
     * @author Josh Carpenter
     * @name Test that when a rule fires related to a driver having a major violation in the last 5 years, an
     * authorized user can override and bind the policy for conversion.
     * @scenario
     * 1. Create customer
     * 2. Create HO3 Policy
     * 3. Initiate PUP conversion policy
     * 4. Add a major violation in the previous 5 years
     * 5. Verify error during bind and override
     * 6. Bind policy and confirm it is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974_TestOverrideBindWithMajorViolationConversion(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = adjustTdWithDriver(getConversionPolicyDefaultTD())
                .adjust(ClaimsTab.class.getSimpleName(), getTestSpecificTD("TestData_MajorViolation"));

        // Create PUP conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        policy.getDefaultView().fillUpTo(td, BindTab.class, true);
        bindTab.submitTab();

        // Verify the error message, finish binding the policy, and confirm active/successful
        verifyErrorsAndOverride(ErrorEnum.Errors.ERROR_AAA_PUP_SSER10054);
    }

    /**
     * @author Josh Carpenter
     * @name Test that when a rule fires related to a driver having a major violation in the last 5 years,
     * an authorized user can override and bind an endorsement
     * @scenario
     * 1. Create customer
     * 2. Create HO3 Policy
     * 3. Create PUP policy
     * 4. Initiate an endorsement and add a major violation in the previous 5 years
     * 5. Verify error during bind and override
     * 6. Bind policy and confirm it is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974_TestOverrideBindWithMajorViolationEndorsement(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = adjustTdWithDriver(getPolicyDefaultTD());

        // Create NB PUP policy
        createPolicy(td);

        // Create an endorsement
        createClaimEndorsement(td, getTestSpecificTD("TestData_MajorViolation"));

        // Verify the error message, finish binding the policy, and confirm active/successful
        verifyErrorsOverrideAndBind(td, ErrorEnum.Errors.ERROR_AAA_PUP_SSER10054);
    }

    /**
     * @author Josh Carpenter
     * @name Test that when a rule fires related to a driver having an alcohol-related violation in the last 5 years,
     * an authorized user can override and bind an endorsement
     * @scenario
     * 1. Create customer
     * 2. Create HO3 Policy
     * 3. Create PUP policy
     * 4. Initiate an endorsement and add an alcohol-related violation in the previous 5 years
     * 5. Verify error during bind and override
     * 6. Bind policy and confirm it is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974_TestOverrideBindWithAlcoholRelatedViolationEndorsement(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = adjustTdWithDriver(getPolicyDefaultTD());

        // Create NB PUP policy
        createPolicy(td);

        // Create an endorsement
        createClaimEndorsement(td, getTestSpecificTD("TestData_AlcoholViolation"));

        // Verify the error message, finish binding the policy, and confirm active/successful
        verifyErrorsOverrideAndBind(td, ErrorEnum.Errors.ERROR_AAA_PUP_SSER10054);
    }

    /**
     * @param td The test data to be adjusted
     * @return Returns the given test data with a Driver added
     */
    private TestData adjustTdWithDriver(TestData td) {
        return td
            .mask(TestData.makeKeyPath(PremiumAndCoveragesQuoteTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel()))
            .adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel()), getTestSpecificTD("TestData_Driver"));
    }

    /**
     * 1. Initiate endorsement
     * 2. Navigate to Claims tab and add claim
     * 3. Navigate to Premiums & Coverages to rate policy
     * 4. Navigate to Bind tab and complete endorsement
     * @param tdPolicy policy test data
     * @param tdEndorsement endorsement test data
     */
    private void createClaimEndorsement(TestData tdPolicy, TestData tdEndorsement) {
        policy.createEndorsement(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.CLAIM.get());
        claimsTab.fillTab(tdPolicy.adjust(ClaimsTab.class.getSimpleName(), tdEndorsement));
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        premiumAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.BIND.get());
        bindTab.submitTab();
    }
}
