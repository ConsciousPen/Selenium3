package aaa.modules.regression.sales.home_ss.ho6.functional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.PolicyHelper;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSHO6BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author R. Kazlauskiene
 * @name Test View And Override Capping Details
 * @scenario
 * Preconditions: policy qualifies for capping and
 * user have the capping privilege
 * 1. Create Individual Customer / Account
 * 2. Create converted SS home policy
 * 3. On the Quote tab of the "Premium & Coverages" page click Calculate Premium button
 * 4. Select the "View capping details" link
 * 5. Check Capping details
 * 6. Override Capping Factor
 * 7. Click Calculate and Save and Return to Premium & Coverages buttons
 * 8. Click Calculate Premium and then "View capping details" link
 * 9. Check Capping Factor and Capped Term Premium values
 *
 **/

public class TestPolicyViewAndOverrideCappingDetails extends HomeSSHO6BaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-3002")
    public void testPolicyViewCappingDetails(@Optional("") String state) {

        TestData td = getTestSpecificTD("TestData");
        TestData tdRenewalActions = getTestSpecificTD("TD_Renewal_Actions");
        TestData tdOverrideCappingDetails = getTestSpecificTD("TestData_Overide_Capping_Details");
        TestData tdViewCappingDetails = getTestSpecificTD("TD_View_Capping_Details");

        PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

        String renewalTermPremiumOld = getTestSpecificTD("TD_Renewal_Actions").getTestData("InitiateRenewalEntryActionTab").getValue("Renewal Policy Premium");
        String ceilingCap = tdViewCappingDetails.getValue("Ceiling Cap");
        String floorCap = tdViewCappingDetails.getValue("Floor Cap");
        String cappingFactor = tdOverrideCappingDetails.getTestData("PremiumsAndCoveragesQuoteTab", "View Capping Details").getValue("Manual Capping Factor (%)");

        mainApp().open();

        createCustomerIndividual();
        //Initiate Renewal manual entry
        customer.initiateRenewalEntry().perform(tdRenewalActions);
        policy.getDefaultView().fillUpTo(td,PremiumsAndCoveragesQuoteTab.class, true);
        //View Capping Details
        premiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

        String calculatedTermPremium = premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(3).getCell(4).getValue();
        String cappedTermPremium = premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(4).getCell(4).getValue();
        //Check Capping Details
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(1).getCell(2).getValue()).isEqualTo(new Dollar(renewalTermPremiumOld).toString());
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(2).getCell(2).getValue()).isEqualTo(PolicyHelper.calculateChangeInPolicyPremium(calculatedTermPremium, renewalTermPremiumOld));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(4).getCell(2).getValue()).isEqualTo(String.format("%s.0%%", ceilingCap.toString()));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(5).getCell(2).getValue()).isEqualTo(String.format("%s.0%%", floorCap.toString()));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(1).getCell(4).getValue()).isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, floorCap));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(2).getCell(4).getValue()).isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, floorCap));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(5).getCell(4).getValue()).isEqualTo("LegacyConv");
        //Override Capping Factor
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.VIEW_CAPPING_DETAILS_DIALOG).fill(tdOverrideCappingDetails.getTestData(PremiumsAndCoveragesQuoteTab.class.getSimpleName()), false);

        premiumsAndCoveragesQuoteTab.calculatePremium();
        premiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(2).getCell(4).getValue()).isEqualTo(String.format("%s.00%%", cappingFactor.toString()));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(4).getCell(4).getValue()).isNotEqualTo(cappedTermPremium);
    }
}
