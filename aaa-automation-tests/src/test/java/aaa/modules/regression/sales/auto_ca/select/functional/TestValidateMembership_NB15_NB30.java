package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestMembershipTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;


@StateList(states = Constants.States.AZ)
public class TestValidateMembership_NB15_NB30 extends TestMembershipTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    /**
     * @author Dakota Berg
     * @name Test Validate Membership with No Hit Membership Number
     * @scenario
     * 1. Create Customer
     * 2. Create a Policy
     * 2.1 Enter a membership number that returns an error
     * 2.2 Open the Member Since Dialog and add a member since date
     * 3. Verify that Membership Status shows "Error" in data base
     * 4. Update the membership number in the data base with a valid number
     * 5. Run 'membershipValidationJob' at NB15 and NB30
     * 6. Verify that Membership Status shows as "Active" in the data base
     * @details
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16457")
    public void pas16457_validateMembershipNB15_Active(@Optional("AZ") String state) {

        pas16457_validateMembershipNB15();
    }
}
