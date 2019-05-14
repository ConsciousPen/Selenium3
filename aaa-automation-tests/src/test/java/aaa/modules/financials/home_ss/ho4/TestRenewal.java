package aaa.modules.financials.home_ss.ho4;

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
        return PolicyType.HOME_SS_HO4;
    }

    @Parameters({"state"})
    @Test(groups = {Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4)
    public void testRenewalScenario_1(@Optional("") String state) {
        testRenewalScenario_1();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4)
    public void testRenewalScenario_2(@Optional("") String state) {
        testRenewalScenario_2();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4)
    public void testRenewalScenario_3(@Optional("") String state) {
        testRenewalScenario_3();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4)
    public void testRenewalScenario_4(@Optional("") String state) {
        testRenewalScenario_4();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4)
    public void testRenewalScenario_5(@Optional("") String state) {
        testRenewalScenario_5();
    }

}
