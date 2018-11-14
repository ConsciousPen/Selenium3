package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.PrivilegeEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestFirelineTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

//Fireline rules is applicable for those states, but for testing purposes as we need mock data, tests only testing AZ
@StateList(states = {Constants.States.AZ, Constants.States.CO, Constants.States.ID, Constants.States.NV, Constants.States.OR, Constants.States.UT})
public class TestFireline extends TestFirelineTemplate {

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_SS_HO3; }

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - SS
	 * @scenario
	 * 1. Open application with L41
	 * 2. Create Customer
	 * 3. Initiate Policy creation
	 * 4. In Applicant tab fill specific address to get fireline score of 3 (found in a RetrievePropertyClassificationMockData)
	 * 5. Check that fireline score from report is 3 in Reports tab
	 * 6. Try Purchase policy
	 * 7. Check that Error tab appeared
	 * 8. Check that L41 user can override the error
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fire rule for roof type - wood shingle/wood shake for fireline score 3, L41 user- SS")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-18302")
	public void pas18302_firelineRuleForWoodShingleRoof_trigger_privileged(@Optional("AZ") String state) {
		pas18302_firelineRuleForWoodShingleRoof("85031", "517 CLIFFWOOD DR", 3, PrivilegeEnum.Privilege.L41);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - SS
	 * @scenario
	 * 1. Open application with F35
	 * 2. Create Customer
	 * 3. Initiate Policy creation
	 * 4. In Applicant tab fill specific address to get fireline score of 3 (found in a RetrievePropertyClassificationMockData)
	 * 5. Check that fireline score from report is 3 in Reports tab
	 * 6. Try Purchase policy
	 * 7. Check that Error tab appeared
	 * 8. Check that F35 user can NOT override the error
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fire rule for roof type - wood shingle/wood shake for fireline score 3, F35 user- SS")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-18302")
	public void pas18302_firelineRuleForWoodShingleRoofs_trigger_NONprivileged(@Optional("AZ") String state) {
		pas18302_firelineRuleForWoodShingleRoof("85031", "517 CLIFFWOOD DR", 3, PrivilegeEnum.Privilege.F35);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - SS
	 * @scenario
	 * 1. Open application with L41
	 * 2. Create Customer
	 * 3. Initiate Policy creation
	 * 4. In Applicant tab fill specific address to get fireline score of 2 (found in a RetrievePropertyClassificationMockData)
	 * 5. Check that fireline score from report is 2 in Reports tab
	 * 6. Try Purchase policy
	 * 7. Check that Error tab did NOT appear
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fire rule for roof type - wood shingle/wood shake for fireline score 2, L41 user- SS")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-18302")
	public void pas18302_firelineRuleForWoodShingleRoofs_noTrigger_privileged(@Optional("AZ") String state) {
		pas18302_firelineRuleForWoodShingleRoof("85601", "586 EAGLE RD.", 2, PrivilegeEnum.Privilege.L41);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - SS
	 * @scenario
	 * 1. Open application with A30
	 * 2. Create Customer
	 * 3. Initiate Policy creation
	 * 4. In Applicant tab fill specific address to get fireline score of 2 (found in a RetrievePropertyClassificationMockData)
	 * 5. Check that fireline score from report is 2 in Reports tab
	 * 6. Try Purchase policy
	 * 7. Check that Error tab did NOT appear
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fire rule for roof type - wood shingle/wood shake for fireline score 2, A30 user- SS")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-18302")
	public void pas18302_firelineRuleForWoodShingleRoofs_noTrigger_NOTprivileged(@Optional("AZ") String state) {
		pas18302_firelineRuleForWoodShingleRoof("85601", "586 EAGLE RD.", 2, PrivilegeEnum.Privilege.A30);
	}
}