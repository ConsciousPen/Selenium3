package aaa.modules.conversion.manual.pup;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ClaimsTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.modules.conversion.manual.ConvPUPBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestOverrideBindDriversWithMajorViolations extends ConvPUPBaseTest {

    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);

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
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974TestOverrideBindWithAlcoholRelatedViolationNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = getDefaultTdWithDriver();

        // Create NB PUP policy
        policy.initiate();
        fillPupWithAlcoholRelatedViolation(td);

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
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974TestOverrideBindWithMajorViolationNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = getDefaultTdWithDriver();

        // Create NB PUP policy
        policy.initiate();
        fillPupWithMajorViolation(td);

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
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974TestOverrideBindWithAlcoholRelatedViolationConversion(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = getDefaultTdWithDriver();

        // Create PUP conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd35());
        fillPupWithAlcoholRelatedViolation(td);

        // Verify the error message, finish binding the policy, and confirm active/successful
        verifyErrorsOverrideAndBind(td, ErrorEnum.Errors.ERROR_AAA_PUP_SSER10054);
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
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6974")
    public void pas6974TestOverrideBindWithMajorViolationConversion(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = getDefaultTdWithDriver();

        // Create PUP conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd35());
        fillPupWithMajorViolation(td);

        // Verify the error message, finish binding the policy, and confirm active/successful
        verifyErrorsOverrideAndBind(td, ErrorEnum.Errors.ERROR_AAA_PUP_SSER10054);
    }

    /**
     * @return Returns the default PUP test data with a Driver
     */
    private TestData getDefaultTdWithDriver() {
        return getPolicyDefaultTD()
            .mask(TestData.makeKeyPath(PremiumAndCoveragesQuoteTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel()))
            .adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel()), getTestSpecificTD("TestData_Driver"));
    }

    /**
     * Fills a PUP policy with the specified major violation
     * @param td the test data being used to fill the policy (Major violation will be added)
     */
    private void fillPupWithMajorViolation(TestData td) {
        td.adjust(ClaimsTab.class.getSimpleName(), getTestSpecificTD("TestData_MajorViolation"));
        policy.getDefaultView().fillUpTo(td, BindTab.class, true);
        bindTab.submitTab();
    }

    /**
     * Fills a PUP policy with the specified alcohol-related violation
     * @param td the test data being used to fill the policy (Alohol-related violation will be added)
     */
    private void fillPupWithAlcoholRelatedViolation(TestData td) {
        td.adjust(ClaimsTab.class.getSimpleName(), getTestSpecificTD("TestData_AlcoholViolation"));
        policy.getDefaultView().fillUpTo(td, BindTab.class, true);
        bindTab.submitTab();
    }
}
