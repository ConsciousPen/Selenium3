package aaa.modules.regression.sales.auto_ca.select;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import toolkit.utils.TestInfo;
import aaa.modules.regression.sales.template.PolicyFuturedated;

/**
 * @author Lina Li
 * @name Test futuredated policy
 */

public class TestPolicyFuturedated extends PolicyFuturedated {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
	
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT )
	public void testPolicyFuturedated(String state) {

		super.testPolicyFuturedated();
		
	}

}
