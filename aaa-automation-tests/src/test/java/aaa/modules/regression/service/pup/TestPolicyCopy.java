package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCopy;
import toolkit.utils.TestInfo;

public class TestPolicyCopy  extends PolicyCopy {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	/**
    * @author Ryan Yu
	 * <b> Test Policy Copy </b>
	 * <p> Steps:
	 * <p> 1. Create new or open existent Customer;
	 * <p> 2. Create PUP Policy or open existent;
	 * <p> 3. Select "CopyFromPolicy" action and click Go button. Fill Copy From Policy tab and click Ok button. Confirm Copy action.;
	 * <p> 4. Select "Data Gather" action and click Go button. Fill all mandatory fields and issue quote. Verify new policy is in Active satus. Verify new generated policy number.;
	 * <p>
    **/
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
    public void testPolicyCopy(@Optional("") String state) {
		testPolicyCopy();
    }
}
