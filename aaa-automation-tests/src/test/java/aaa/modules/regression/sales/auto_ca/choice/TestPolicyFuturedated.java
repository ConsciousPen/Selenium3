package aaa.modules.regression.sales.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import toolkit.utils.TestInfo;
import aaa.modules.regression.sales.template.PolicyFuturedated;

/**
 * @author N. Belakova
 * @name Test futuredated policy
 */

public class TestPolicyFuturedated extends PolicyFuturedated {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}
	
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE )
	public void testPolicyFuturedated(@Optional("CA") String state) {

		super.testPolicyFuturedated();
		
	}

}
