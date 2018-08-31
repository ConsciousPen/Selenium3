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
	 * @name Test Claims points refresh on VRD page for SS HO3 policies during NB
	 * @scenario
	 * 1.  Create customer
	 * 2.  Initiate SS HO3 quote
	 * 3.  Fill quote with 4 claims:
	 * 		a. Fire, $500, Closed, date of loss = 12 months ago, AAA Claim = 'Yes', Catastrophe = 'No'
	 * 		b. Water, $5000, Closed, date of loss = 10 months ago, AAA Claim = 'Yes', Catastrophe = 'Yes'
	 * 		c. Theft, $2500, Closed, date of loss = 8 months ago,  AAA Claim = 'Yes', Catastrophe = 'No'
	 * 		d. Liability, $7000, Closed, date of loss = 6 months ago,  AAA Claim = 'Yes', Catastrophe = 'No'
	 * 4.  Navigate to P & C tab and calculate premium
	 * 5.  Validate only theft & liability claims display on VRD page with correct points
	 * 6.  Navigate to Property Info tab
	 * 7.  Adjust date of loss for liability claim to 37 months ago; water claim to catastrophe = 'No'
	 * 9.  Navigate to P & C tab and calculate premium
	 * 10. Validate only water & theft claims display on VRD page with correct points
	 * 10. Navigate to Property Info tab
	 * 11. Adjust liability claim date of loss to 2 years ago
	 * 12. Navigate to P & C tab and calculate premium
	 * 13. Validate all 4 claims display on VRD page with correct points
	 * 14. Navigate to Property Info tab
	 * 15. Adjust liability claim date of loss back to 6 months ago; all claims to AAA Claim = 'No'
	 * 17. Navigate to P & C tab and calculate premium
	 * 18. Validate all 4 claims display on VRD page with correct points under 'Prior Claims Points' rows
	 * 19. Navigate to Property Info tab
	 * 20. Adjust fire & water claims back to AAA Claim = 'Yes'; fire claim amount to $1001; theft claim to catastrophe = 'Yes'
	 * 21. Navigate to P & C tab and calculate premium
	 * 22. Validate liability claim only displays on VRD page with correct points under 'Prior Claims Points' rows
	 * 23. Validate fire & water claims display on VRD page with correct points under 'AAA Claims Points' rows
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Service.HOME_SS_HO3, testCaseId = "PAS-17772")
	public void pas17772_testAAAClaimPointsVRDPageNB(@Optional("") String state) {

		pas17772_testAAAClaimPointsVRDPageNB_SS();

	}

}
