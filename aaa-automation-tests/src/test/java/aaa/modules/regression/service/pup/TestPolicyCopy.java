package aaa.modules.regression.service.pup;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCopy;

public class TestPolicyCopy  extends PolicyCopy {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	/**
    * @author Ryan Yu
    * @name Test Policy Copy
    * @scenario
    * 1. Create new or open existent Customer;
    * 2. Create PUP Policy or open existent;
    * 3. Select "CopyFromPolicy" action and click Go button. Fill Copy From Policy tab and click Ok button. Confirm Copy action.;
    * 4. Select "Data Gather" action and click Go button. Fill all mandatory fields and issue quote. Verify new policy is in Active satus. Verify new generated policy number.;
    * 
    **/
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
    public void testPolicyCopy(String state) {
		super.testPolicyCopy();
    }
}
