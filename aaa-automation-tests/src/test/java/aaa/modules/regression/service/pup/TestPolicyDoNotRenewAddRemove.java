package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyDoNotRenewAddRemove;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test Add and Remove 'Do Not Renew' for Umbrella Policy </b>
 * <p> Steps: 1. Create Customer
 * <p> 2. Create Umbrella  Policy
 * <p> 3. Set Do Not Renew for Policy
 * <p> 4. Verify Policy status is 'Policy Active'
 * <p> 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * <p> 6. Remove Do Not Renew for Policy
 * <p> 7. Verify Policy status is 'Policy Active'
 * <p> 8. Verify 'Do Not Renew' flag isn't displayed in the policy overview header
 *
 */

public class TestPolicyDoNotRenewAddRemove extends PolicyDoNotRenewAddRemove {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP)
	public void testPolicyDoNotRenewAddRemove(@Optional("") String state) {

		testPolicyDoNotRenewAddRemove();
	}
}
