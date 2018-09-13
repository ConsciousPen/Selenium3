package aaa.modules.financials.home_ss.ho3;

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

public class TestNewBusiness extends TestNewBusinessTemplate {

	@Override
	public PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	/**
	 * @author Josh Carpenter
	 * @name Test new business process number NBZ-01
	 * @scenario NBZ-01
	 * @details New business, bound ON or AFTER policy effective date, WITHOUT employee benefit.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FINANCE})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testNBZ_01(@Optional("") String state) {
		testNBZ_01();
	}

	/**
	 * @author Josh Carpenter
	 * @name Test new business process number NBZ-03
	 * @scenario NBZ-03
	 * @details New business, bound BEFORE policy effective date, WITHOUT employee benefit. (Including entries recorded via batch job at effective date.)
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FINANCE})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testNBZ_03(@Optional("") String state) {
		testNBZ_03();
	}

}
