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

	/**
	 * @author Josh Carpenter
	 * @name Financial Scenario 1
	 * @scenario
	 * 1. Create new policy WITHOUT employee benefit, monthly payment plan
	 * 2. Advance time one week
	 * 3. Perform endorsement resulting in additional premium
	 * 4. Advance time one week
	 * 5. Cancel policy
	 * 6. Reinstate policy with no lapse (reinstatement eff. date same as cancellation date)
	 * @details NBZ-01, PMT-01, END-01, CNL-01, RST-01, PMT-06, PMT-19, FEE-15, FEE-04
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FINANCE})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6)
	public void testNewBusinessScenario_1(@Optional("CA") String state) {
		testNewBusinessScenario_1();
	}

}
