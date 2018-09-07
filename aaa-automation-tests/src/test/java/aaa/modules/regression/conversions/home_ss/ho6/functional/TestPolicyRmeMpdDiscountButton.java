package aaa.modules.regression.conversions.home_ss.ho6.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Jovita Pukenaite
 * @name Test Policy RME MPD button
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Verify "Legacy policy had Multi-Policy discount" radio button is exist on RME screen
 * 4. Verify "Legacy policy had Multi-Policy discount" radio button is mandatory
 * 5. TBD (PAS-2310 should be cover)
 */

public class TestPolicyRmeMpdDiscountButton extends HomeSSHO6BaseTest {

    @Parameters({"state"})
    @StateList(states = {Constants.States.NJ})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO6, testCaseId = "PAS-2293,PAS-7979")
    public void testPolicyRmeMpd (@Optional("NJ") String state) {

        InitiateRenewalEntryActionTab initiateRenewalEntryActionTab = new InitiateRenewalEntryActionTab();
        GeneralTab generalTab = new GeneralTab();

        mainApp().open();

        // Create customer
        createCustomerIndividual();
        customer.initiateRenewalEntry().start();
        initiateRenewalEntryActionTab.fillTab(getTestSpecificTD("TD_Renewal_Actions"));

        //Verify "Legacy policy had Multi-Policy discount" radio button is exist on RME screen
        assertThat(initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
                .InitiateRenewalEntryActionTab.LEGACY_POLICY_HAD_MULTI_POLICY_DISCOUNT)).isPresent();

        //Verify that "Legacy policy had Multi-Policy discount" radio button is mandatory on RME screen
        initiateRenewalEntryActionTab.submitTab();
		assertThat(initiateRenewalEntryActionTab.getAssetList()
                .getAsset(CustomerMetaData.InitiateRenewalEntryActionTab.LEGACY_POLICY_HAD_MULTI_POLICY_DISCOUNT)
                .getWarning().toString())
				.contains(PolicyConstants.InitiateRenewalEntryScreenErrorMessages.LEGACY_POLICY_HAD_MULTI_POLICY_DISCOUNT_SHOULD_BE_SELECTED);

        initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
                .InitiateRenewalEntryActionTab.LEGACY_POLICY_HAD_MULTI_POLICY_DISCOUNT).setValue("Yes");
        initiateRenewalEntryActionTab.submitTab();

        new CustomerActions.InitiateRenewalEntry().submit();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.COMMISSION_TYPE)).hasValue("Renewal");
    }
}
