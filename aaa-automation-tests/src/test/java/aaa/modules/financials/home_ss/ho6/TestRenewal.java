package aaa.modules.financials.home_ss.ho6;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.financials.template.TestRenewalTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestRenewal extends TestRenewalTemplate {

    @Override
    public PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO6;
    }

    @Parameters({"state"})
    @Test(groups = {Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6)
    public void testRenewalScenario_1(@Optional("") String state) {
        testRenewalScenario_1();
    }

}
