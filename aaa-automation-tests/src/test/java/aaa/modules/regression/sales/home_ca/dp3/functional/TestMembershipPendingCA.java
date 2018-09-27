package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestMembershipTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestMembershipPendingCA extends TestMembershipTemplate {
    @Override
    protected PolicyType getPolicyType() {return PolicyType.HOME_CA_DP3;}
    /**
     * @author Robert Boles
     * @name Test Align Current AAA Member for CA products (Auto and Property) with SS - PAS-17784
     * @scenario
     * 1. Create Customer.
     * 2. Create CA DP3 Quote
     * 3. Current AAA member will have the option with Membership "Membership Pending"
     * 4. Navigate to Premium & Coverages tab and Calculate Premium
     * 5. Premium will reduce and AAA Membership discount applies in Discounts section
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "17784: Align Current AAA Member for CA products (Auto and Property) with SS")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-17784")
    public void pas17784_AC1_CA_DP3_Pending_Option(@Optional("") String state) {
        setKeyPathsandGenerateQuote();
        pendingMembershipValidations_all_ACs();
    }
}
