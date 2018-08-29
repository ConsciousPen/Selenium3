package aaa.modules.regression.sales.home_ss.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestClaimPointsVRDPageTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestClaimPointsVRDPage extends TestClaimPointsVRDPageTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	/**
	 * @author Josh Carpenter
	 * @name Test Claims points refresh on VRD page for SS HO3 policies
	 * @scenario
	 * 1.  Create customer
	 * 2.  Initiate SS HO3 quote
	 * 3.  Fill quote with 4 claims included in rating (first one is above $1000)
	 * 4.  Validate all 4 claims display on VRD page
	 * 5.  Adjust claim amount of first claim to less than $1000
	 * 6.  Validate first claim is no longer displayed on VRD page (other 3 are) along with correct points
	 * 7.  Navigate back to Property Info tab and adjust date of 4th claim so it is now the first claim
	 * 8.  Navigate to P & C tab and calculate premium
	 * 9.  Validate that all 4 claims are again displaying on the VRD page with correct points
	 * 10. Navigate back to Property Info tab and adjust the first claim so that it is now > 3 years old
	 * 11. Navigate to P & C tab and calculate premium
	 * 12. Validate the first claim no is longer on the VRD page (and the other 3 do) with correct points
	 * 13. Navigate back to Property Info tab and adjust the 3rd claim so catastrophe = 'Yes'
	 * 14. Navigate to P & C tab and calculate premium
	 * 15. Validate the 3rd claim is no longer on the VRD page (and the other 3 are) with correct points
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3, testCaseId = "PAS-17772")
	public void pas17772_testClaimPointsVRDPageHO3(@Optional("") String state) {

		pas17772_testClaimPointsVRDPageSS();

	}

}
