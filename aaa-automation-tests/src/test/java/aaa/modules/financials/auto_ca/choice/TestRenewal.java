package aaa.modules.financials.auto_ca.choice;

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

@StateList(states = Constants.States.CA)
public class TestRenewal extends TestRenewalTemplate {

    @Override
    public PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
    public void testRenewalScenario_1(@Optional("CA") String state) {
        testRenewalScenario_1();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
    public void testRenewalScenario_2(@Optional("CA") String state) {
        testRenewalScenario_2();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
    public void testRenewalScenario_3(@Optional("CA") String state) {
        testRenewalScenario_3();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CFT})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
    public void testRenewalScenario_4(@Optional("CA") String state) {
        testRenewalScenario_4();
    }

}

