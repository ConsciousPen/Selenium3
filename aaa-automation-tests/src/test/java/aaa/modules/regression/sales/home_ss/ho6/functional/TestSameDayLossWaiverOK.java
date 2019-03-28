package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestSameDayLossWaiverOKTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.OK)
public class TestSameDayLossWaiverOK extends TestSameDayLossWaiverOKTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO6;
    }

    /**
     * @author Josh Carpenter
     * @name Test First Clue loss within experience period is waived during new business
     * @scenario
     * 1. Initiate OK property quote with customer that returns multiple Clue claims within experience period
     * 2. Fill up to Premium & Coverages Tab
     * 3. Calculate premium
     * 4. Navigate back to Property Info tab
     * 5. Validate first claim is waived
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-25467")
    public void pas25467_testFirstLossWaivedClueNB(@Optional("OK") String state) {
        testFirstLossWaivedClueNB();
    }

    /**
     * @author Josh Carpenter
     * @name Test same day waiver is applied for multiple Clue claims with the same loss date during new business
     * @scenario
     * 1. Initiate OK property quote with customer that returns multiple Clue claims within experience period with same loss date
     * 2. Fill up to Premium & Coverages Tab
     * 3. Calculate premium
     * 4. Navigate back to Property Info tab
     * 5. Validate that loss with the least severity in terms of premium (claim points, type of loss, etc.) is given same day waiver
     * @details
     **/
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-25467")
    public void pas25467_testSameDayWaiverClueNB(@Optional("OK") String state) {
        testSameDayWaiverClueNB();
    }

}
