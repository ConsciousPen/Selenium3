package aaa.modules.regression.sales.home_ss.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestPersonalPropertyLimitsTemplate;
import toolkit.utils.TestInfo;

public class TestPersonalPropertyLimits extends TestPersonalPropertyLimitsTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

    /**
     * @author Josh Carpenter
     * @name Test Scheduled Personal Property max limits for SS HO3
     * @scenario
     * 1.
     * 2.
     * 3.
     * 4.
     * 5.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-29091")
    public void pas29091_testScheduledPersonalPropertyMaxLimits(@Optional("") String state) {
        testScheduledPersonalPropertyMaxLimitsNB();

    }

}
