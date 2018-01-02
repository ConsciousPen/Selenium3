package aaa.modules.regression.sales.home_ss.ho4.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.modules.policy.HomeSSHO4BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Jovita Pukenaite
 * For PAS-2293
 * @name Test Policy RME MPD button
 * @scenario
 * 1. Create Individual Customer / Account
 * 2. Select RME Action with HSS product
 * 3. Verify "Legacy policy had MPD discount" radio button is exist on RME screen
 * 4. Verify "Legacy policy had MPD discount" radio button isn't mandator
 * 5. TBD (PAS-2310 should be cover)
 */

public class TestPolicyRmeMpdDiscountButton extends HomeSSHO4BaseTest{

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4)

    public void testPolicyRmeMpd (@Optional("") String state) {

        InitiateRenewalEntryActionTab initiateRenewalEntryActionTab = new InitiateRenewalEntryActionTab();
        GeneralTab generalTab = new GeneralTab();

        mainApp().open();
        createCustomerIndividual();
        customer.initiateRenewalEntry().start();
        initiateRenewalEntryActionTab.fillTab(getTestSpecificTD("TD_Renewal_Actions_NJ"));

        //Verify "Legacy policy had MPD discount" radio button is exist on RME screen
        assertThat(initiateRenewalEntryActionTab.getAssetList().getAsset(CustomerMetaData
                .InitiateRenewalEntryActionTab.LEGACY_POLICY_HAD_MPD_DISCOUNT.getLabel())
                .isPresent()).isTrue();

        initiateRenewalEntryActionTab.submitTab();

        //Verify "Legacy policy had MPD discount" radio button isn't mandator
        new CustomerActions.InitiateRenewalEntry().submit();
        assertThat(generalTab.getAssetList().getAsset(HomeSSMetaData.GeneralTab.COMMISSION_TYPE.getLabel()).getValue()).isEqualTo("Renewal");
    }
}
