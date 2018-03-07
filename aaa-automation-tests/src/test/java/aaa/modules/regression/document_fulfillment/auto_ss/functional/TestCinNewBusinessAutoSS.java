package aaa.modules.regression.document_fulfillment.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractAutoSS;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCinNewBusinessAutoSS extends TestCinAbstractAutoSS {

    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7515")
    public void testCinNewBusinessMVR(@Optional("PA") String state) {
        TestData policyTD = getPolicyDefaultTD().adjust(getTestSpecificTD("TestData").resolveLinks())
                .adjust(PRODUCT_OWNED_PATH, getTestSpecificTD("AAAProductOwned"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(SUPPRESS_INSURANCE_SCORE_TRIGGER, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        ssNewBusinessMainFlow(policyTD);
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }
}
