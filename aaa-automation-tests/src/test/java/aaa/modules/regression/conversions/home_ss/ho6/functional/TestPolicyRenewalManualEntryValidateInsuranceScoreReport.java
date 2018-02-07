package aaa.modules.regression.conversions.home_ss.ho6.functional;


import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSHO6BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestPolicyRenewalManualEntryValidateInsuranceScoreReport extends HomeSSHO6BaseTest {
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6663")
    public void testPolicyRenewalValidateOrderInsuranceScoreNoErrorFires(@Optional("VA") String state) {
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
        MortgageesTab mortgageesTab = new MortgageesTab();
        TestData td = getTestSpecificTD("TestData_OverrideScore");
        td = td.adjust(TestData.makeKeyPath("ApplicantTab","NamedInsured", "Marital status"),"Single")
               .adjust(TestData.makeKeyPath("ApplicantTab","AAAMembership","Current AAA Member"),"Yes");

        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(getTestSpecificTD("TD_Renewal_Actions"));
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
        assertThat(mortgageesTab.getAssetList().getAsset(HomeSSMetaData.MortgageesTab.MORTGAGEE.getLabel()).isPresent());
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6663")
    public void testPolicyRenewalValidateOverrideingReportDoesNotThrowError(@Optional("VA") String state) {
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
        ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);
        TestData td = getTestSpecificTD("TestData").resolveLinks();

        //Adjust test data, added blank array list to InsuranceScoreReport so that report is not ordered
        td = td.adjust(TestData.makeKeyPath("ReportsTab", "InsuranceScoreReport"), new ArrayList<TestData>())
                .adjust(TestData.makeKeyPath("ApplicantTab","NamedInsured", "Marital status"),"Single")
                .adjust(TestData.makeKeyPath("ApplicantTab","AAAMembership","Current AAA Member"),"Yes");
        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().perform(getTestSpecificTD("TD_Renewal_Actions"));
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MAIG_IS_REPORT);
    }
}
