package aaa.modules.regression.sales.home_ca.ho3.functional;

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

@StateList(states = {Constants.States.CA})
public class TestFireline extends TestFirelineTemplate {

	@Override
	protected PolicyType getPolicyType() { return PolicyType.HOME_CA_HO3; }

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - CA
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
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-18914")
	public void pas18914_firelineRuleForWoodShingleRoof_trigger_privileged(@Optional("CA") String state) {
		pas18914_CA_firelineRuleForWoodShingleRoof("93737", "265 CHIPMAN AVE", 3, PrivilegeEnum.Privilege.L41, false);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - CA
	 * @scenario
	 * 1. Open application with F35
	 * 2. Create Customer
	 * 3. Initiate Policy creation
	 * 4. In Applicant tab fill specific address to get fireline score of 4 (found in a RetrievePropertyClassificationMockData)
	 * 5. Check that fireline score from report is 4 in Reports tab
	 * 6. Try Purchase policy
	 * 7. Check that Error tab appeared
	 * 8. Check that F35 user can NOT override the error
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fire rule for roof type - wood shingle/wood shake for fireline score 3, F35 user- SS")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-18914")
	public void pas18914_firelineRuleForWoodShingleRoofs_trigger_NONprivileged(@Optional("CA") String state) {
		pas18914_CA_firelineRuleForWoodShingleRoof("93711", "3680 W Shaw Ave", 4, PrivilegeEnum.Privilege.F35, false);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - CA
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
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-18914")
	public void pas18914_firelineRuleForWoodShingleRoofs_noTrigger_privileged(@Optional("CA") String state) {
		pas18914_CA_firelineRuleForWoodShingleRoof("92899", "265 CHIPMAN AVE", 2, PrivilegeEnum.Privilege.L41, false);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - CA
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
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-18914")
	public void pas18914_firelineRuleForWoodShingleRoofs_noTrigger_NOTprivileged(@Optional("CA") String state) {
		pas18914_CA_firelineRuleForWoodShingleRoof("92899", "265 CHIPMAN AVE", 2, PrivilegeEnum.Privilege.A30, false);
	}

	/**
	 * @author Parth Varmora
	 * @name test: Fire rule for roof type - wood shingle/wood shake for 3 and above - CA
	 * @scenario
	 * 1. Open application with L41
	 * 2. Create Customer
	 * 3. Initiate Policy creation
	 * 4. In Applicant tab fill specific address to get fireline score of 5 (found in a RetrievePropertyClassificationMockData)
	 * 5. Check that fireline score from report is 5 in Reports tab
	 * 6. Try Purchase policy
	 * 7. Check that Error tab appeared
	 * 8. Check that L41 user can override the error
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Fire rule for roof type - wood shingle/wood shake for fireline score 5, L41 user- SS")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-18914")
	public void pas18914_firelineRuleForWoodShingleRoof_trigger_NON_privileged(@Optional("CA") String state) {
		pas18914_CA_firelineRuleForWoodShingleRoof("90004", "252 CHIPMAN AVE", 5, PrivilegeEnum.Privilege.L41, false);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test: FAIRplan endorsement ignores fire rule for roof type - wood shingle/wood shake for 3 and above - CA
	 * @scenario
	 * 1. Open application with L41
	 * 2. Create Customer
	 * 3. Initiate Policy creation
	 * 4. In Applicant tab fill specific address to get fireline score of 5 (found in a RetrievePropertyClassificationMockData)
	 * 5. Check that fireline score from report is 5 in Reports tab
	 * 6. Add 'FAIRplan endorsement'
	 * 7. Try Purchase policy
	 * 8. Check that Error tab did not appeared (FAIRplan should make it not appear)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "FAIRplan endorsement ignores fire rule for roof type - wood shingle/wood shake for fireline score 5")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-22224")
	public void pas18914_FAIRplanEndorsement(@Optional("CA") String state) {
		pas18914_CA_firelineRuleForWoodShingleRoof("91016", "265 CHIPMAN AVE", 5, PrivilegeEnum.Privilege.L41, true);
	}

	/**
	 * @author Rokas Lazdauskas
	 * @name test: Revise CA Automated Exception model to remove AE#5 rule
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Policy creation
	 * 3. Fill information which should satisfy AE#5 rule for CA
	 * 3.1. Have underlying AAA Auto policy (which has Auto policy BI limit != $25.000/$50.000)
	 * 3.2. Applicant must be a member
	 * 3.3. Fireline score should be 5 or 6
	 * 3.4. Coverage A must be less than a million dollars
	 * 4. Try Purchase policy
	 * 5. Check that Error tab appeared and fireline rule ERROR_AAA_HO_Fireline_CA02122017 appeared
	 * // Note: This was automated exception rule that if quote meets all requirments from step 3. error wouldn't be fire and quote was eligible to purchase)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-22425")
	public void pas18296_AE5RuleNotTriggering(@Optional("CA") String state) {
		pas18296_AE5RuleNotTriggering("91016", "265 CHIPMAN AVE", 5);
	}
}