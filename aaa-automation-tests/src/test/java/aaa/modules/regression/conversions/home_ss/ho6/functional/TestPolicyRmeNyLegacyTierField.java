package aaa.modules.regression.conversions.home_ss.ho6.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.modules.regression.conversions.template.TestPolicyRmeNyLegacyTierFieldTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

public class TestPolicyRmeNyLegacyTierField extends TestPolicyRmeNyLegacyTierFieldTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO6;
    }

    /**
     * @author Sushil Sivaram, Rokas Lazdauskas
     * @name Test Policy RME Legacy Tier field
     * @scenario
     * 1. Create Individual Customer / Account
     * 2. Select RME Action with HSS product
     * 3. Fill everything in RME screen except "Legacy Tier"
     * 4. Verify "Legacy  Tier" field is exist on RME screen
     * 5. Submit tab and check "Legacy Tier Is Required" message
     * 6. Try filling alphabetical, special charecter or numeric value which is not in range 1-50 and submiting tab
     * 7. Check that "Legacy tier message is out of range" message appears.
     * 8. Fill "Legacy Tier" field with correct value
     * 9. Check that user is able to proceed.
     */
    @Parameters({"state"})
    @StateList(states = {Constants.States.NY})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO6, testCaseId = {"PAS-23185", "PAS-23421"})
    public void testPolicyRmeLegacyTier (@Optional("NY") String state) {
        testPolicyRmeLegacyTier();
    }
}