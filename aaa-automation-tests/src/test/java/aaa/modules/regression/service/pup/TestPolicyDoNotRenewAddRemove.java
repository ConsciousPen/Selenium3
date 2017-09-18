package aaa.modules.regression.service.pup;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyDoNotRenewAddRemove;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Add and Remove 'Do Not Renew' for Umbrella Policy
 * @scenario 1. Create Customer
 * 2. Create Umbrella  Policy
 * 3. Set Do Not Renew for Policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * 6. Remove Do Not Renew for Policy
 * 7. Verify Policy status is 'Policy Active'
 * 8. Verify 'Do Not Renew' flag isn't displayed in the policy overview header
 * @details
 */

public class TestPolicyDoNotRenewAddRemove extends PolicyDoNotRenewAddRemove {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void testPolicyDoNotRenewAddRemove(String state) {

		super.testPolicyDoNotRenewAddRemove();
	}
}
