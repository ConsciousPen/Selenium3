package aaa.modules.regression.sales.home_ss.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.modules.regression.sales.home_ss.helper.HelperRevisedHomeTierPA;
import toolkit.utils.TestInfo;


public class TestPAViewRatingDetails extends HomeSSDP3BaseTest {

	private HelperRevisedHomeTierPA helper = new HelperRevisedHomeTierPA();

	/**
	 * @author Dominykas Razgunas
	 * @name Test PA Revised Home Tier UI Change View Rating Detail
	 * @scenario
	 * 1. Create PA DP3 Policy
	 * 2. Fill All required fields and Calculate Premium
	 * 3. View Rating Details
	 * 4. Check that Auto tier value is between 1 and 16
	 * 5. Check that Market tier value is between A and J
	 * 6. Check that Persistency, Age, Reinstatements points value are displayed
	 * 7. Issue Policy
	 * 8. Initiate renewal
	 * 9. Calculate Premium
	 * 10. Check that Auto tier value is between 1 and 16
	 * 11. Check that Market tier value is between A and J
	 * 12. Check that Persistency, Age, Reinstatements points value are displayed
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PA Revised Home Tier  - UI Change : View Rating Details screen")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-6676, PAS-7025, PAS-7024")
	public void pas6676_testPAViewRatingDetails(@Optional("PA") String state) {

		helper.pas6676_TestPAViewRatingDetails(getPolicyType());

	}
}