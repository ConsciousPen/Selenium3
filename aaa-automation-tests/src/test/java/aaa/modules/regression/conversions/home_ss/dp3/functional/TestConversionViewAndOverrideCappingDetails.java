package aaa.modules.regression.conversions.home_ss.dp3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.utils.StateList;
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

public class TestConversionViewAndOverrideCappingDetails extends HomeSSDP3BaseTest {

    @Parameters({"state"})
    @StateList(states = {Constants.States.AZ, Constants.States.DE, Constants.States.MD, Constants.States.NJ, Constants.States.PA, Constants.States.VA, Constants.States.UT})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_DP3, testCaseId = "PAS-3002")
    public void testPolicyViewCappingDetails(@Optional("") String state) {

        PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

        TestData td = getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(),
                HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel()),
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
        createCustomerIndividual();

        //Initiate Renewal manual entry
        customer.initiateRenewalEntry().perform(initiateRenewalEntry);

        //Fill Quote
        policy.getDefaultView().fillUpTo(td,PremiumsAndCoveragesQuoteTab.class, true);

        //Check that coverages are rounded to dollar value
        List<String> currentValues = new ArrayList<>();
        currentValues.addAll(Arrays.asList(premiumsAndCoveragesQuoteTab.tableCoverages.getRow(2).getCell("Percentage of Coverage A").getValue()));
        for(String value : currentValues.toString().split("\n")) {
            assertThat(value).as("Coverages should be rounded to dollar value").contains(".00");
        }
        String ceilingCap = PolicyHelper.getCeilingByPolicyNumber(premiumsAndCoveragesQuoteTab.getPolicyNumberForConversion());
        String floorCap = PolicyHelper.getFloorByPolicyNumber(premiumsAndCoveragesQuoteTab.getPolicyNumberForConversion());
        //View Capping Details
        PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

        String calculatedTermPremium = PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CALCULATED_TERM_PREMIUM);
        String cappedTermPremium = PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CAPPED_TERM_PREMIUM);
        //Check Capping Details
        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.RENEWAL_TERM_PREMIUM_OLD_RATER))
                .isEqualTo(new Dollar(renewalTermPremiumOld).toString());
        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CHANGE_IN_POLICY_PREMIUM))
                .isEqualTo(PolicyHelper.calculateChangeInPolicyPremium(calculatedTermPremium, renewalTermPremiumOld));
        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CEILING_CAP)).isEqualTo(String.format("%s.0%%", ceilingCap));
        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.FLOOR_CAP)).isEqualTo(String.format("%s.0%%", floorCap));
        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.SYSTEM_CALCULATED_CAPPING_FACTOR))
                .isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, floorCap));
        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.APPLIED_CAPPING_FACTOR))
                .isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, floorCap));
        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.PROGRAM_CODE)).isEqualTo("LegacyConv");
        //Override Capping Factor
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.VIEW_CAPPING_DETAILS_DIALOG).fill(tdOverrideCappingDetails.getTestData(PremiumsAndCoveragesQuoteTab.class.getSimpleName()), false);

        premiumsAndCoveragesQuoteTab.calculatePremium();
        PremiumsAndCoveragesQuoteTab.linkViewCappingDetails.click();

        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.APPLIED_CAPPING_FACTOR))
                .isEqualTo(String.format("%s.00%%", getTestSpecificTD("TestData_OverideCappingDetails").getTestData
                ("PremiumsAndCoveragesQuoteTab", "View Capping Details").getValue(PolicyConstants.ViewCappingDetailsTable.MANUAL_CAPPING_FACTOR).toString()));
        assertThat(PremiumsAndCoveragesQuoteTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CAPPED_TERM_PREMIUM)).isNotEqualTo(cappedTermPremium);
    }
}
