package aaa.modules.regression.sales.home_ss.ho4.functional;

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
        return PolicyType.HOME_SS_HO4;
    }

    /**
     * @author Josh Carpenter
     * @name Test Scheduled Personal Property max limits for SS HO3 Quote
     * @scenario
     * 1.  Initiate SS HO3 policy and fill up to Documents Tab
     * 2.  Capture Coverage C and calculate percentages
     * 3.  Navigate to SPP section and add HS 04 61 endorsement
     * 4.  Add one item above threshold
     * 5.  Navigate to Bind, validate error message(s)
     * 6.  Navigate back to SPP tab and remove items
     * 7.  Add multiple items each under threshold and aggregate total above category threshold
     * 8.  Navigate back to SPP tab and update last item
     * 9.  Navigate to Bind, validate no errors
     * 10. Start data gather mode and navigate to SPP tab and remove entries4
     * 11. Add items at max threshold from multiple categories until 50% of cov C is exceeded
     * 12. Navigate to bind tab and validate error
     * 13. Navigate back to SPP tab and remove last item
     * 14. Bind policy and validate successful
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-29091")
    public void pas29091_testScheduledPersonalPropertyMaxLimits(@Optional("") String state) {
        testScheduledPersonalPropertyMaxLimitsNB();

    }

}
