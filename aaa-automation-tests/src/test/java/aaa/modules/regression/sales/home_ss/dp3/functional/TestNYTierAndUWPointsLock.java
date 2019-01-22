package aaa.modules.regression.sales.home_ss.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestNYPropertyTierAndUWPointsLock;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.NY)
public class TestNYTierAndUWPointsLock extends TestNYPropertyTierAndUWPointsLock {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test NY Lock UW points and Market Tier - View Rating Detail. Renewal
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
	 * 11. Calculate And Save Premium. Change Cov E for bigger premium
	 * 12. Calculate Premium
	 * 13. Check that Premium is bigger than previously one
	 * 14. Open VRD
	 * 15. Check that Market tier is the saved value in step 5
	 * 16. Check that Total UW points are the same value saved in step 5
	 * 17. Validate new Market tier message for renewals
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "NY Tier And UW points lock - UI Change : View Rating Details screen. Renewal")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-14030, PAS-14045")
	public void pas14030_testNYViewRatingDetailsRenewal_DP3(@Optional("NY") String state) {

		pas14030_TestNYViewRatingDetailsRenewal();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test NY Lock UW points and Market Tier - View Rating Detail. Renewal with Flat Endorsement
	 * @scenario
	 * 1. Create NY DP3 Policy
	 * 2. Issue Policy
	 * 3. Create and Issue Renewal
	 * 4. Flat Endorse Renewal
	 * 5. Navigate to P&C
	 * 6. Calculate Premium Open VRD
	 * 7. Save Total UW Points Value and Market Tier
	 * 8. Change FR Score
	 * 9. Calculate Premium
	 * 10. Open VRD
	 * 11. Check that Market tier is the saved value in step 5
	 * 12. Check that Total UW points are the same value saved in step 5
	 * 13. Validate new Market tier message for renewals
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "NY Tier And UW points lock - UI Change : View Rating Details screen. Renewal with Flat Endorsement")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-14030, PAS-14045")
	public void pas14030_testNYViewRatingDetailsRenewalFlatEndorsement_DP3(@Optional("NY") String state) {

		pas14030_TestNYViewRatingDetailsRenewalFlatEndorsement();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test NY Lock UW points and Market Tier - View Rating Detail. Mid Term Endorsement
	 * @scenario
	 * 1. Create NY DP3 Policy
	 * 2. Fill All required fields and Calculate Premium
	 * 3. View Rating Details
	 * 4. Check that Market tier value is between A and J
	 * 5. Save Total UW Points Value and Market Tier
	 * 6. Issue Policy
	 * 7. Initiate Mid Term Endorsement
	 * 8. Navigate to Reports Tab
	 * 9. Change FR Score
	 * 10. Navigate to P&C
	 * 11. Calculate And Save Premium. Change Cov E for bigger premium
	 * 12. Calculate Premium
	 * 13. Check that Premium is bigger than previously one
	 * 14. Open VRD
	 * 15. Check that Market tier is the saved value in step 5
	 * 16. Check that Total UW points are the same value saved in step 5
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "NY Tier And UW points lock - UI Change : View Rating Details screen. Mid Term Endorsement")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-14030")
	public void pas14030_testNYViewRatingDetailsMidTermEndorsement_DP3(@Optional("NY") String state) {

		pas14030_TestNYViewRatingDetailsMidTermEndorsement();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Test NY Lock UW points and Market Tier - View Rating Detail. Endorsement
	 * @scenario
	 * 1. Create NY DP3 Policy
	 * 2. Fill All required fields and Calculate Premium
	 * 3. View Rating Details
	 * 4. Check that Market tier value is between A and J
	 * 5. Save Total UW Points Value and Market Tier
	 * 6. Issue Policy
	 * 7. Initiate Endorsement with effective date = to policy effective date
	 * 8. Navigate to Reports Tab
	 * 9. Change FR Score
	 * 10. Navigate to P&C
	 * 11. Calculate And Save Premium. Change Cov E for bigger premium
	 * 12. Calculate Premium
	 * 13. Check that Premium is bigger than previously one
	 * 14. Open VRD
	 * 15. Check that Market tier is recalculated
	 * 16. Check that Total UW points are recalculated
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "NY Tier And UW points lock - UI Change : View Rating Details screen. Endorsement")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-14030")
	public void pas14030_testNYViewRatingDetailsEndorsement_DP3(@Optional("NY") String state) {

		pas14030_TestNYViewRatingDetailsEndorsement();
	}
}