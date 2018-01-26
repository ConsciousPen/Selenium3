package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author S. Sivaram
 * @name Test Policy Renewal
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Choose Data Gathering Action
 * 4. Fill information up to Reports Tab
 * 5. Order all reports except insurance score
 * 6. Override insurance score
 * 5. Proceed to PremiumsAndCoveragesQuoteTab and calculate premium
 * 6. Verify premium calculates and assert Calculate premium button is visible after calculating premium (No error message fires)
 */

public class TestPolicyRenewalManualEntryFieldsUserDoesNotOrderReportOrOverrideErrorFires extends HomeSSHO3BaseTest {
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6663")
    public void testPolicyRenewal(@Optional("") String state) {

       ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);
       TestData td = getTestSpecificTD("TestData");
       getTestSpecificTD("TD_Renewal_Actions").getTestData("InitiateRenewalEntryActionTab").getValue("Inception Date");

        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(getTestSpecificTD("TD_Renewal_Actions"));
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MAIG_IS_REPORT);
    }
}