package aaa.modules.regression.conversions.home_ss.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.conversions.template.TestPolicyRmeNyLegacyTierFieldTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestPolicyRmeNyLegacyTierField extends TestPolicyRmeNyLegacyTierFieldTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
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
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3, testCaseId = {"PAS-23185", "PAS-23421"})
    public void testPolicyRmeLegacyTier (@Optional("NY") String state) { testPolicyRmeLegacyTier(); }

    /**
     * @author Parth Varmora
     * @name Test Policy Legacy Tier Mapping and Tier Locked
     * @scenario
     * 1.Create a NY conversion HO policy.  Use the legacy tier of 1, and set it up to generate a tier J.
     * 2.Rate the policy and determine the tier using the view rating details popup.
     * 3.Verify Market tier in view rating details popup
     * 4.Complete the entry and save the policy.
     * 5.initiate second renewal
     * 6.Verify Market tier in view rating details popup not getting changed
     */
    @Parameters({"state"})
    @StateList(states = {Constants.States.NY})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3, testCaseId = {"PAS-23184, PAS-23187"})
    public void testNYLegacyTierMapping(@Optional("NY") String state) {
        testPolicyLegacyTierMapping(1);
    }

    /**
     * @author Parth Varmora
     * @name Test Policy Legacy Tier Mapping and Tier Locked
     * @scenario
     * 1.Create a NY conversion HO policy.  Use the legacy tier of 50, and set it up to generate a tier E.
     * 2.Rate the policy and determine the tier using the view rating details popup.
     * 3.Verify Market tier in view rating details popup
     * 4.Complete the entry and save the policy.
     * 5.initiate second renewal
     * 6.Verify Market tier in view rating details popup not getting changed
     */
    @Parameters({"state"})
    @StateList(states = {Constants.States.NY})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
        @TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3, testCaseId = {"PAS-23184, PAS-23187"})
    public void testNYLegacyTierMapping_edgeCase (@Optional("NY") String state) {
        testPolicyLegacyTierMapping(50);
    }
}