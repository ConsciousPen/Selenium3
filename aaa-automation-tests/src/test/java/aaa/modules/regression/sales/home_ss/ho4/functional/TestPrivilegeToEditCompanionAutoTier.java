package aaa.modules.regression.sales.home_ss.ho4.functional;


import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSHO4BaseTest;
import aaa.modules.regression.sales.home_ss.helper.HelperRevisedHomeTierPA;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.PA)
public class TestPrivilegeToEditCompanionAutoTier extends HomeSSHO4BaseTest {

	private HelperRevisedHomeTierPA helper = new HelperRevisedHomeTierPA();

	/**
	 * @author Dominykas Razgunas
	 * @name Test PA Privilege to Edit Auto Companion(Added via Search) Tier
	 * @scenario
	 * 1. Log in with default User with privilege to edit policy tier
	 * 2. Initiate Home Policy and add Auto policy as a companion
	 * 3. Fill Property till Applicant Tab
	 * 4. Check if policy tier is enabled
	 * 5. Save quote number and close application
	 * 6. Log in with User with no privilege to edit policy tier
	 * 7. Search for the Quote and navigate to applicant tab
	 * 8. Check if policy tier is disabled
	 * 9. Issue Policy
	 * 10. Endorse Policy
	 * 11. Check if policy tier is disabled
	 * 12. Close App and log in with privileged user
	 * 13. Renew Policy and check if policy tier is enabled
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PA Revised Home Tier  - Privilege to Edit Auto Companion Tier")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-6829")
	public void pas6676_testPAViewRatingDetailsAutoTier(@Optional("PA") String state) {

		helper.pas6829_TestPrivelegeToEditCompanionAutoTier(getPolicyType());
	}
	/**
	 * @author Dominykas Razgunas
	 * @name Test PA Privilege to Edit Auto Companion(Added manually) Tier
	 * @scenario
	 * 1. Log in with default User with privilege to edit policy tier
	 * 2. Initiate Home Policy and add Auto policy as a companion
	 * 3. Fill Property till Applicant Tab
	 * 4. Check if policy tier is enabled
	 * 5. Save quote number and close application
	 * 6. Log in with User with no privilege to edit policy tier
	 * 7. Search for the Quote and navigate to applicant tab
	 * 8. Check if policy tier is disabled
	 * 9. Issue Policy
	 * 10. Endorse Policy
	 * 11. Check if policy tier is disabled
	 * 12. Close App and log in with privileged user
	 * 13. Renew Policy and check if policy tier is enabled
	 * @details
	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PA Revised Home Tier  - Privilege to Edit Auto Companion Tier")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4, testCaseId = "PAS-6829")
	public void pas6676_testPAViewRatingDetailsManualAutoTier(@Optional("PA") String state) {

		helper.pas6829_TestPrivelegeToEditManualCompanionAutoTier(getPolicyType());
	}
}