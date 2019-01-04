package aaa.modules.regression.conversions.home_ss.dp3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Sushil Sivaram
 * @name Test Policy RME Legacy Tier field
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Verify "Legacy  Tier" field is exist on RME screen
 */

public class TestPolicyRmeNyLegacyTierField extends HomeSSDP3BaseTest {

    @Parameters({"state"})
    @StateList(states = {Constants.States.NY})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_DP3, testCaseId = "PAS-23185")
    public void testPolicyRmeMpd (@Optional("NY") String state) {

        InitiateRenewalEntryActionTab initiateRenewalEntryActionTab = new InitiateRenewalEntryActionTab();

        mainApp().open();

        // Create customer
        createCustomerIndividual();
        customer.initiateRenewalEntry().start();
        initiateRenewalEntryActionTab.fillTab(getTestSpecificTD("TD_Renewal_Actions"));

        //Verify "Legacy Tier" Text Box is exist on RME screen
        assertThat(initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
                .InitiateRenewalEntryActionTab.LEGACY_TIER)).isPresent();

    }
}
