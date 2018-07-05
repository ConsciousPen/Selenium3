package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.modules.regression.sales.template.functional.TestNYPropertyTierAndUWPointsLock;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.NY)
public class TestNYTierAndUWPointsLock extends HomeSSDP3BaseTest {

	private TestNYPropertyTierAndUWPointsLock template = new TestNYPropertyTierAndUWPointsLock();

	/**
	 * @author Dominykas Razgunas
	 * @name Test NY Lock UW points and Market Tier - View Rating Detail
	 * @scenario
	 * 1. Create NY DP3 Policy
	 * 2. Fill All required fields and Calculate Premium
	 * 3. View Rating Details
	 * 4. Check that Market tier value is between A and J
	 * 5. Save Total UW Points Value and Market Tier
	 * 6. Issue Policy
	 * 7. Initiate renewal
	 * 8. Navigate to Reports Tab
	 * 9. Change FR Score
	 * 10. Navigate to P&C
	 * 11. Calculate And Save Premium. Change Payment Plan to Quarterly
	 * 12. Calculate Premium
	 * 13. Check that Premium is bigger than previously one
	 * 14. Open VRD
	 * 15. Check that Market tier is the saved value in step 5
	 * 16. Check that Total UW points is the same value saved in step 5
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "NY Tier And UW points lock - UI Change : View Rating Details screen")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-14030")
	public void pas14030_testNYViewRatingDetailsRenewal(@Optional("NY") String state) {

		template.pas14030_TestNYViewRatingDetailsRenewal(getPolicyType());
	}
}