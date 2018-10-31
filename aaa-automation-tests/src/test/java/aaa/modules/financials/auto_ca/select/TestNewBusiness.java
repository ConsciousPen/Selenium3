package aaa.modules.financials.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.financials.FinancialsBaseTest;
import aaa.modules.financials.template.TestNewBusinessTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestNewBusiness extends TestNewBusinessTemplate {

	@Override
	public PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @scenario
	 * 1. Create new policy WITHOUT employee benefit
	 * 2. Advance time one week
	 * 3. Perform endorsement resulting in additional premium (add vehicle for for CA Auto)
	 * 4. Advance time one week
	 * 5. Cancel policy
	 * 6. Reinstate policy with no lapse (reinstatement eff. date same as cancellation date)
	 * @details NBZ-01, PMT-01, END-01, CNL-01, RST-01, PMT-06, PMT-19, FEE-15, FEE-06
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testNewBusinessScenario_1(@Optional("CA") String state) {
		testNewBusinessScenario_1();
	}

	/**
	 * @scenario
	 * 1. Create new policy with effective date three weeks from now, WITHOUT employee benefit
	 * 2. Advance time 3 days
	 * 3. Perform endorsement resulting in reduction (return) of premium (add vehicle for for CA Auto)
	 * 4. Advance time 4 days
	 * 5. Cancel policy
	 * 6. Advance time one week
	 * 6. Reinstate policy with lapse
	 * 7. Remove reinstatement lapse
	 * @details NBZ-03, PMT-04, END-02, CNL-06, PMT-05, RST-02, RST-09
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.TIMEPOINT, Groups.CFT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void testNewBusinessScenario_2(@Optional("CA") String state) {
		testNewBusinessScenario_2();

	}

}
