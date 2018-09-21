package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestMembershipTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestMembershipPendingHomeSS extends TestMembershipTemplate {
    @Override
    protected PolicyType getPolicyType() {return PolicyType.HOME_SS_DP3;}
    /**
     * @author Robert Boles
     * @name Test Current AAA Member for Home SS products cannot endorse or renew with Membership Pending - PAS-17784
     * @scenario
     * 1. Create DP3 Policy.
     * 2. Bind policy and move to renewal TP1 - Run policyAutomatedRenewalAsyncTaskGenerationJob
     * 3. Select renewal image and validate Membership Pending is not an available option for Current AAA Member
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17784: Align Current AAA Member for CA products (Auto and Property) with SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-17784")
    public void pas17784_AC4_AC5_CA_Pending_Removed_Renewal_DP3(@Optional("") String state) {
        openAppAndCreatePolicy();
        generateRenewalImageAndCheckForMSPending();
    }
}
