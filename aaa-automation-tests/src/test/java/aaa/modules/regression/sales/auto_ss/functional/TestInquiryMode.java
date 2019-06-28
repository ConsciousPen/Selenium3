package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInquiryModeTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestInquiryMode extends TestInquiryModeTemplate {

    @Override
    protected PolicyType getPolicyType() { return PolicyType.AUTO_SS; }

    @Parameters({"state"})
    @StateList(statesExcept = Constants.States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-31369")
    public void pas31369_testInquiryMode(@Optional("") String state) {
        super.testInquiryMode();
    }
}
