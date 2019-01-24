package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.AZ)
public class TestMembershipOverrideBusinessRule  extends AutoSSBaseTest {
    /**
     * @author Robert Boles
     * @name Test Membership Override Pay Plan - PAS-10845 - Scenario 1 - Membership = Membership Override
     * @scenario
     * Precondition: Agent is expected to have the Membership override privilege.
     * 1. Create Customer.
     * 2. Create Auto SS Policy with Membership "Membership Override"
     * 3. Select Override Prefilled Current Carrier and populate information.
     * 4. Navigate to Premium & Coverages tab and select Eleven pay - Standard
     * 8. Bind Quote ---> quote will bind
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10845")

    public void pas10845_Validate_Membership_Override_BusinessRule_MSOverriden() {

        TestData testData = getPolicyTD();
        TestData tdSpecific = getTestSpecificTD("AAAProductOwned_Override").resolveLinks();
        TestData currentCarrierSection = getTestSpecificTD("CurrentCarrierInformation_BR").resolveLinks();
        TestData pNcTab = getTestSpecificTD("PremiumAndCoveragesTab").resolveLinks();

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel()),
                tdSpecific);

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()),
                currentCarrierSection);

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Eleven Pay - Standard");

        mainApp().open();
        createCustomerIndividual();
        createPolicy(testData);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }


    /**
     * @author Robert Boles
     * @name Test Membership Override Pay Plan - PAS-10845 - Scenario 2 - Membership Pending
     * @scenario
     * Precondition: Agent is expected to have the Membership override privilege.
     * 1. Create Customer.
     * 2. Create Auto SS Policy with Membership "Membership Pending"
     * 3. Select Override Prefilled Current Carrier and populate information.
     * 4. Navigate to Premium & Coverages tab and select Eleven pay - Standard
     * 8. Bind Quote ---> quote will bind
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10845")
    public void pas10845_Validate_Membership_Override_BusinessRule_MSPending() {

        TestData testData = getPolicyTD();
        TestData tdSpecificProductOwned = getTestSpecificTD("AAAProductOwned_Pend").resolveLinks();
        TestData tdCurrentCarrierSection = getTestSpecificTD("CurrentCarrierInformation_BR").resolveLinks();
        TestData tdPremium = getTestSpecificTD("PremiumAndCoveragesTab").resolveLinks();

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel()), tdSpecificProductOwned)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()),tdCurrentCarrierSection)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Eleven Pay - Standard");

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
        new DocumentsAndBindTab().submitTab();
        verifyErrorAndExit();
    }

    /**
     * @author Robert Boles
     * @name Test Membership Override Pay Plan - PAS-10845 - Scenario 3 - Membership = Yes
     * @scenario
     * Precondition: Agent is expected to have the Membership override privilege.
     * 1. Create Customer.
     * 2. Create Auto SS Policy with Membership "Yes" - add membership information for active membership
     * 3. Select Override Prefilled Current Carrier and populate information.
     * 4. Navigate to Premium & Coverages tab and select Eleven pay - Standard
     * 8. Bind Quote ---> quote will bind
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10845")
    public void pas10845_Validate_Membership_Override_BusinessRule_MSYes() {

        TestData testData = getPolicyTD();
        TestData tdSpecificProductOwned = getTestSpecificTD("AAAProductOwned_Yes").resolveLinks();
        TestData tdCurrentCarrierSection = getTestSpecificTD("CurrentCarrierInformation_BR").resolveLinks();
        TestData tdPremium = getTestSpecificTD("PremiumAndCoveragesTab").resolveLinks();

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel()), tdSpecificProductOwned)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()),
                        tdCurrentCarrierSection)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                        AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Eleven Pay - Standard");

        mainApp().open();
        createCustomerIndividual();
        createPolicy(testData);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /**
     * @author Robert Boles
     * @name Test Membership Override Pay Plan - PAS-10845  -Scenario 4 - Membership = No
     * @scenario
     * Precondition: Agent is expected to have the Membership override privilege.
     * 1. Create Customer.
     * 2. Create Auto SS Policy with Membership "No"
     * 3. Select Override Prefilled Current Carrier and populate information.
     * 4. Navigate to Premium & Coverages tab and select Eleven pay - Standard
     * 8. Bind Quote ---> Rule should fire -
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Feature 29838 - Newly Acquired AAA Membership, Validation Override")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10845")
    public void pas10845_Validate_Membership_Override_BusinessRule_MSNo() {
        TestData testData = getPolicyTD();
        TestData tdSpecificProductOwned = getTestSpecificTD("AAAProductOwned_No").resolveLinks();
        TestData tdCurrentCarrierSection = getTestSpecificTD("CurrentCarrierInformation_BR").resolveLinks();
        TestData tdPremium = getTestSpecificTD("PremiumAndCoveragesTab").resolveLinks();

        testData.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
				AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel()), tdSpecificProductOwned)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(),
                        AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()),
                        tdCurrentCarrierSection)
                .adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                        AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Eleven Pay - Standard");

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class, true);
        new DocumentsAndBindTab().submitTab();
        verifyErrorAndExit();
    }

    private void verifyErrorAndExit() {
        ErrorTab errorTab = new ErrorTab();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_200127);
        mainApp().close();
    }
}