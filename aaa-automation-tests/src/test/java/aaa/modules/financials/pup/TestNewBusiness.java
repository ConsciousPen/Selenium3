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
	 * @details NBZ-01, PMT-01, END-01, CNL-01, RST-01, PMT-06, PMT-19
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.PUP)
	public void testNewBusinessScenario_1(@Optional("") String state) {
		testNewBusinessScenario_1();
	}

}
