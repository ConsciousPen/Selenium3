package aaa.modules.regression.sales.home_ca.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestEndorsementPremiumEntriesTemplate;
import toolkit.utils.TestInfo;

public class TestEndorsementPremiumEntries extends TestEndorsementPremiumEntriesTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_DP3;
    }

    /**
     * @author Josh Carpenter
     * @name Test no duplicate endorsement premium entries exist for CA DP3 copied policies
     * @scenario
     * 1. Create policy
     * 2. Validate Included Endorsements matches what is on P & C tab under Endorsements section (name and amount)
     * 3. Validate DB entries match the included endorsements by name and amount and contain no duplicates
     * 3. Initiate copy from policy transaction
     * 4. Navigate to P & C tab and calculate premium
     * 5. Validate Endorsements entries in DB do not contain duplicate values and the premium amounts are correct
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = {"PAS-19204"})
    public void pas19204_testEndorsementPremiumEntriesCopyFromPolicy(@Optional("CA") String state) {
        testEndorsementPremiumEntriesCopyFromPolicy();
    }

    /**
     * @author Josh Carpenter
     * @name Test no duplicate endorsement premium entries exist for CA DP3 copied quotes
     * @scenario
     * 1. Create quote, fill up to P & C tab
     * 2. Calculate premium, save and exit
     * 3. Validate Included Endorsements matches what is on P & C tab under Endorsements section (name and amount)
     * 4. Validate DB entries match the included endorsements by name and amount and contain no duplicates
     * 5. Initiate copy from quote transaction
     * 6. Navigate to P & C tab and calculate premium
     * 7. Validate Endorsements entries in DB do not contain duplicate values and the premium amounts are correct
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = {"PAS-19204"})
    public void pas19204_testEndorsementPremiumEntriesCopyFromQuote(@Optional("CA") String state) {
        testEndorsementPremiumEntriesCopyFromQuote();
    }

    /**
     * @author Josh Carpenter
     * @name Test no duplicate endorsement premium entries exist for CA DP3 renewals
     * @scenario
     * 1. Create policy
     * 2. Validate Included Endorsements matches what is on P & C tab under Endorsements section (name and amount)
     * 3. Validate DB entries match the included endorsements by name and amount and contain no duplicates
     * 4. Create renewal image
     * 5. Navigate to P & C tab and calculate premium
     * 6. Validate Endorsements entries in DB do not contain duplicate values and the premium amounts are correct
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = {"PAS-19204"})
    public void pas19204_testEndorsementPremiumEntriesRenewal(@Optional("CA") String state) {
        testEndorsementPremiumEntriesRenewal();
    }

    /**
     * @author Josh Carpenter
     * @name Test no duplicate endorsement premium entries exist for CA DP3 rewrites
     * @scenario
     * 1. Create policy
     * 2. Validate Included Endorsements matches what is on P & C tab under Endorsements section (name and amount)
     * 3. Cancel policy
     * 4. Rewrite policy
     * 5. Navigate to P & C tab and calculate premium
     * 6. Validate Endorsements entries in DB do not contain duplicate values and the premium amounts are correct
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = {"PAS-19204"})
    public void pas19204_testEndorsementPremiumEntriesRewrite(@Optional("CA") String state) {
        testEndorsementPremiumEntriesRewrite();
    }

}
