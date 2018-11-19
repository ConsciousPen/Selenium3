package aaa.modules.financials.home_ca.ho6;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.financials.template.TestNewBusinessTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestNewBusiness extends TestNewBusinessTemplate {

	@Override
	public PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO6;
	}


	@Parameters({"state"})
	@Test(groups = {Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6)
	public void testNewBusinessScenario_1(@Optional("CA") String state) {
		testNewBusinessScenario_1();
	}


	@Parameters({"state"})
	@Test(groups = {Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6)
	public void testNewBusinessScenario_2(@Optional("") String state) {
		testNewBusinessScenario_2();
	}

}
