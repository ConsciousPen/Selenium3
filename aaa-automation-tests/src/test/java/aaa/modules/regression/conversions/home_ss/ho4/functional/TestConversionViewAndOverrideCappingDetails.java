package aaa.modules.regression.conversions.home_ss.ho4.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.PolicyHelper;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.regression.conversions.ConvHomeSsHO4BaseTest;
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

public class TestConversionViewAndOverrideCappingDetails extends ConvHomeSsHO4BaseTest {

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO4, testCaseId = "PAS-3002")
    public void testPolicyViewCappingDetails(@Optional("VA") String state) {

        PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

        TestData td = getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath(ReportsTab.class.getSimpleName(),
                HomeSSMetaData.ReportsTab.INSURANCE_SCORE_REPORT.getLabel()), getTestSpecificTD("TestData")
                    .getTestData("ReportsTab").getTestDataList("InsuranceScoreReport"))
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel()),
                        getTestSpecificTD("TestData").getTestData("GeneralTab").getValue("Immediate prior carrier"));
        TestData initiateRenewalEntry = getManualConversionInitiationTd()
                .adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(),
                        CustomerMetaData.InitiateRenewalEntryActionTab.RENEWAL_POLICY_PREMIUM.getLabel()),
                        getTestSpecificTD("TestData").getTestData("InitiateRenewalEntryActionTab")
                                .getValue("Renewal Policy Premium"));

        TestData tdOverrideCappingDetails = getTestSpecificTD("TestData_OverideCappingDetails");
        TestData tdViewCappingDetails = getTestSpecificTD("TD_ViewCappingDetails");
        String renewalTermPremiumOld = getTestSpecificTD("TestData").getTestData
                ("InitiateRenewalEntryActionTab").getValue("Renewal Policy Premium");

        mainApp().open();

        //Initiate Renewal manual entry
        initiateManualConversion(initiateRenewalEntry);

        //Fill Quote
        policy.getDefaultView().fillUpTo(td,PremiumsAndCoveragesQuoteTab.class, true);
        //View Capping Details
        premiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

        String calculatedTermPremium = premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(3).getCell(4).getValue();
        String cappedTermPremium = premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(4).getCell(4).getValue();
        //Check Capping Details
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(1).getCell(2).getValue()).isEqualTo(new Dollar(renewalTermPremiumOld).toString());
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(2).getCell(2).getValue()).isEqualTo(PolicyHelper.calculateChangeInPolicyPremium(calculatedTermPremium, renewalTermPremiumOld));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(4).getCell(2).getValue()).isEqualTo(String.format("%s.0%%", tdViewCappingDetails.getValue
                ("Ceiling Cap").toString()));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(5).getCell(2).getValue()).isEqualTo(String.format("%s.0%%", tdViewCappingDetails.getValue
                ("Floor Cap").toString()));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(1).getCell(4).getValue()).isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, tdViewCappingDetails.getValue("Floor Cap")));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(2).getCell(4).getValue()).isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, tdViewCappingDetails.getValue("Floor Cap")));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(5).getCell(4).getValue()).isEqualTo("LegacyConv");
        //Override Capping Factor
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.VIEW_CAPPING_DETAILS_DIALOG).fill(tdOverrideCappingDetails.getTestData(PremiumsAndCoveragesQuoteTab.class.getSimpleName()), false);

        premiumsAndCoveragesQuoteTab.calculatePremium();
        premiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(2).getCell(4).getValue()).isEqualTo(String.format("%s.00%%", tdOverrideCappingDetails.getTestData
                ("PremiumsAndCoveragesQuoteTab", "View Capping Details").getValue("Manual Capping Factor (%)").toString()));
        assertThat(premiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getRow(4).getCell(4).getValue()).isNotEqualTo(cappedTermPremium);
    }
}
