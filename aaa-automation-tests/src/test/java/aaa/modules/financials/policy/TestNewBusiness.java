package aaa.modules.financials.policy;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.financials.FinancialsBaseTest;

public class TestNewBusiness extends FinancialsBaseTest {

	/**
	 * @author Josh Carpenter
	 * @name Test new business process number NBZ-01
	 * @scenario NBZ-01
	 * @details New business, bound ON or AFTER policy effective date, WITHOUT employee benefit.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FINANCE})
	public void testNBZ_01(@Optional("") String state) {
		mainApp().open();
		if (getState().equals(Constants.States.CA)) {
			for (PolicyType type : getCAPolicyTypes()) {
				createCustomerIndividual();
				type.get().createPolicy(getStateTestData(testDataManager.policy.get(type), "DataGather", "TestData"));
			}
		} else {
			for (PolicyType type : getSSPolicyTypes()) {
				createCustomerIndividual();
				type.get().createPolicy(getStateTestData(testDataManager.policy.get(type), "DataGather", "TestData"));
			}
		}
		createCustomerIndividual();
		PolicyType.PUP.get().createPolicy(getStateTestData(testDataManager.policy.get(PolicyType.PUP), "DataGather", "TestData"));
	}


}
