package aaa.modules.regression.sales.home_ss.ho4.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInsuranceScoreNBTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestInsuranceScoreNB extends TestInsuranceScoreNBTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test insurance score reported ordering for New Business
	 * *@scenario
	 * 1. Create Customer
	 * 2. Initiate Quote creation
	 * 3. Add 3 insureds: 1 primary insured, 1 spouse, 1 child
	 * 4. Go to Reports tab
	 * 5. Check that only primary insured and spouse are available to order insurance score
	 * 6. Order insurance score for 1 primary insured
	 * 7. Check that insurance score override table shows up only 1 primary insured
	 * 8. Fill the rest of the quote until bind tab
	 * 9. Try binding
	 * 10. Check that error page has error "All named insureds must have an insurance score ordered."
	 * 11. Go back to Reports Tab
	 * 12. Order report for spouse
	 * 13. Check that insurance score override table shows up primary insured and spouse
	 * 14. Proceed to bind a policy
	 * 15. Check that no error message appears.
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA, Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-24967")
	public void pas24967_testInsuranceScoreNB(@Optional("") String state) {
		testInsuranceScoreNB();
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test insurance score reported ordering for New Business
	 * *@scenario
	 * 1. Create Customer
	 * 2. Initiate Quote creation
	 * 3. Go to Reports tab
	 * 4. Check that Insurance score section is not available for MD state
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-24967")
	public void pas24967_testInsuranceScoreNBMD(@Optional("MD") String state) {
		testInsuranceScoreNBMD();
	}
}