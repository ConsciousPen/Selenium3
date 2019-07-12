package aaa.modules.financials.auto_ss;

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

@StateList(statesExcept = Constants.States.CA)
public class TestNewBusiness extends TestNewBusinessTemplate {

	@Override
	public PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testNewBusinessScenario_1(@Optional("") String state) {
		testNewBusinessScenario_1();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testNewBusinessScenario_2(@Optional("") String state) {
		testNewBusinessScenario_2();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testNewBusinessScenario_3(@Optional("") String state) {
		testNewBusinessScenario_3();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testNewBusinessScenario_4(@Optional("") String state) {
		testNewBusinessScenario_4();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testNewBusinessScenario_5(@Optional("") String state) {
		testNewBusinessScenario_5();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testNewBusinessScenario_8(@Optional("") String state) {
		testNewBusinessScenario_8();
	}
}
