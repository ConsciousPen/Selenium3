package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInsuranceScoreNBTemplate;
import aaa.modules.regression.sales.template.functional.TestInsuranceScoreRenewalTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestInsuranceScoreRenewal extends TestInsuranceScoreRenewalTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test insurance score reported ordering for New Business
	 * *@scenario
	 * 1. Create Customer
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-25569")
	public void pas25569_testReorderInsuranceScoreAt36Months(@Optional("") String state) {
		testInsuranceScoreRenewalReorder36Months();
	}

}