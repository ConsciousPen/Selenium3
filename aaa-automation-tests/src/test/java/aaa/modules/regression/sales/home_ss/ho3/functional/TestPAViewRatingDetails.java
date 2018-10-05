package aaa.modules.regression.sales.home_ss.ho3.functional;


import aaa.main.modules.policy.PolicyType;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.template.functional.RevisedHomeTierPATemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.PA)
public class TestPAViewRatingDetails extends RevisedHomeTierPATemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test PA Revised Home Tier UI Change View Rating Detail
	 * @scenario
	 * 1. Create PA HO3 Policy
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
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6676, PAS-7025, PAS-7024")
	public void pas6676_testPAViewRatingDetails(@Optional("PA") String state) {

		pas6676_TestPAViewRatingDetails();
	}
}