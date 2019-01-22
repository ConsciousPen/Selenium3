package aaa.modules.regression.conversions.pup.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
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
public class TestOverrideBindWithPropertyLosses extends ConvPUPBaseTest {

    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);

    /**
     * @author Josh Carpenter
     * @name Test that when a rule fires related to property loss claims in the past 36 months for the applicant/insured
     * that the user is able to override and bind the policy for NB
     * @scenario
     * 1. Create customer
     * 2. Create HO3 Policy
     * 3. Initiate PUP policy
     * 4. Add 2 incidents in the previous 36 months
     * 5. Verify error during bind and override
     * 6. Bind policy and confirm it is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-7242")
    public void pas7242_TestOverrideBindWithPropertyLossesNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = getPolicyDefaultTD();

        // Create PUP policy
        policy.initiate();
        fillPupPolicyWithAutoViolations(td);

        verifyErrorsOverrideAndBind(td, ErrorEnum.Errors.ERROR_AAA_PUP_SS4300780_AE);
    }

    /**
     * @author Josh Carpenter
     * @name Test that when a rule fires related to property loss claims in the past 36 months for the applicant/insured
     * that the user is able to override and bind the policy for conversions
     * @scenario
     * 1. Create customer
     * 2. Create HO3 Policy
     * 3. Initiate PUP conversion policy
     * 4. Add 2 incidents in the previous 36 months
     * 5. Verify error during bind and override
     * 6. Bind policy and confirm it is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-7242")
    public void pas7242_TestOverrideBindWithPropertyLossesConversion(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Initiate Test Data and create pre-conditions
        TestData td = getConversionPolicyDefaultTD();

        // Create PUP conversion policy
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        fillPupPolicyWithAutoViolations(td);

        verifyErrorsAndOverride(ErrorEnum.Errors.ERROR_AAA_PUP_SS4300780_AE);
    }

    /**
     * Fills the PUP policy with default data but additionally creates a driver and adds 2 minor violations.
     * @param td Test data used to fill the policy
     */
    private void fillPupPolicyWithAutoViolations(TestData td) {
        TestData tdClaim1 = getTestSpecificTD("TestData_ClaimsTab1");
        TestData tdClaim2 = getTestSpecificTD("TestData_ClaimsTab2");
        TestData tdDriver = getTestSpecificTD("TestData_Driver");

        td.mask(TestData.makeKeyPath(PremiumAndCoveragesQuoteTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel()))
            .adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel()), tdDriver)
            .adjust(ClaimsTab.class.getSimpleName(), tdClaim1);

        policy.getDefaultView().fillUpTo(td, ClaimsTab.class, true);
        td.adjust(ClaimsTab.class.getSimpleName(), tdClaim2);
        policy.getDefaultView().fillFromTo(td, ClaimsTab.class, BindTab.class);
        bindTab.submitTab();
    }
}
