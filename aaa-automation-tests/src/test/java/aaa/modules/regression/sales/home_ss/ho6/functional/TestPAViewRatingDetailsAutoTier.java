package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSHO6BaseTest;
import aaa.modules.regression.sales.home_ss.helper.HelperRevisedHomeTierPA;
import toolkit.utils.TestInfo;


public class TestPAViewRatingDetailsAutoTier extends HomeSSHO6BaseTest {

	private HelperRevisedHomeTierPA helper = new HelperRevisedHomeTierPA();

	/**
	 * @author Dominykas Razgunas
	 * @name Test PA Revised Home Tier UI Change View Rating Detail
	 * @scenario
	 * 1. Create PA HO6 Policy
	 * 2. Fill All required fields and Calculate Premium
	 * 3. View Rating Details
	 * 4. Check that Auto tier value is between 1 and 16
	 * 5. Issue Policy
	 * 6. Initiate renewal
	 * 7. Calculate Premium
	 * 8. Check that Auto tier value is between 1 and 16
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PA Revised Home Tier  - UI Change : View Rating Details screen")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = "PAS-6676")
	public void pas6676_testPAViewRatingDetailsAutoTier(@Optional("PA") String state) {

		helper.pas6676_TestPAViewRatingDetailsAutoTier(getPolicyType());
	}
}