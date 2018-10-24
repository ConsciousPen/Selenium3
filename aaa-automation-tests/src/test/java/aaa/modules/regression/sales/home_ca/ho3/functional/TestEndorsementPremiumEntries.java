package aaa.modules.regression.sales.home_ca.ho3.functional;

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
        return PolicyType.HOME_CA_HO3;
    }

    /**
     * @author Josh Carpenter
     * @name Test no duplicate endorsement premium entries exist for CA HO3 copied policies
     * @scenario
     * 1.
     * 2.
     * 3.
     * 4.
     * 5.
     * 6.
     * 7.
     * 8.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = {"PAS-19204"})
    public void pas19204_testEndorsementPremiumEntriesCopyFromPolicy(@Optional("CA") String state) {
        testEndorsementPremiumEntriesCopyFromPolicy();
    }

}
