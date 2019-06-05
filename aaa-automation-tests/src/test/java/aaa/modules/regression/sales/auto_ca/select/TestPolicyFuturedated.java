package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.PolicyFuturedated;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * <b> Test futuredated policy </b>
 */

public class TestPolicyFuturedated extends PolicyFuturedated {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Parameters({"state"})
	@StateList(states = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT )
	public void testPolicyFuturedated(@Optional("CA") String state) {

		testPolicyFuturedated();
		
	}

}
