package aaa.modules.financials.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.financials.template.TestNewBusinessTemplate;
import toolkit.utils.TestInfo;

public class TestNewBusiness extends TestNewBusinessTemplate {

	@Override
	public PolicyType getPolicyType() {
		return PolicyType.PUP;
	}


	@Parameters({"state"})
	@Test(groups = {Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testNewBusinessScenario_1(@Optional("") String state) {
		testNewBusinessScenario_1();
	}

}
