package aaa.modules.regression.sales.home_ss.ho3.functional;


import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


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
 * 7. Proceed to PremiumsAndCoveragesQuoteTab and calculate premium
 * 8. Calculate Premium and move to Mortgagee & Additional interest Tab
 * 9. Verify Mortgagee Yes/No Radio Button is present
 *
 */

public class TestPolicyRenewalManualEntryFieldsOverrideInsuranceScore extends HomeSSHO3BaseTest {
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6663")
    public void testPolicyRenewal(@Optional("NJ") String state) {

       MortgageesTab mortgageesTab = new MortgageesTab();
       TestData td = getTestSpecificTD("TestData");
       getTestSpecificTD("TD_Renewal_Actions").getTestData("InitiateRenewalEntryActionTab").getValue("Inception Date");

        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(getTestSpecificTD("TD_Renewal_Actions"));
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
        assertThat(mortgageesTab.getAssetList().getAsset(HomeSSMetaData.MortgageesTab.MORTGAGEE.getLabel()).isPresent());
      }
}