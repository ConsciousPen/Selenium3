package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInsuranceScoreEndorsementTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestInsuranceScoreEndorsement extends TestInsuranceScoreEndorsementTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test order insurance score at renewal when Qualified named insured is added on midterm endorsement
	 * *@scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 3. Initiate Endorsement
	 * 3.1. Add Spouse
	 * 3.2. Check that "Order Insurance Score" and "Reorder at renewal" buttons are enabled for spouse and Primary Insured
	 * 3.3. Check "Order Insurance Score" button to 'Yes' for spouse
	 * 3.4. Check "Reorder at renewal" button to 'No' for spouse
	 * 3.5. Finish doing Endorsement
	 * 4. Do Renewal
	 * 5. Navigate to Reports tab
	 * 6. Check that NEW insurance score reports were ordered for all qualified named insureds (Primary insured and spouse).
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA, Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-24663"})
	public void pas24663_Endorsement_reorderAtRenewalNo(@Optional("") String state) {
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("TestData_EndorsementWithQualifiedNamedInsured",
				"Yes", "No", false);
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test order insurance score at renewal when Qualified named insured is added on midterm endorsement
	 * *@scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 3. Initiate Endorsement
	 * 3.1. Add Spouse
	 * 3.2. Check that "Order Insurance Score" and "Reorder at renewal" buttons are enabled for spouse and Primary Insured
	 * 3.3. Check "Order Insurance Score" button to 'Yes' for spouse
	 * 3.4. Check "Reorder at renewal" button to 'Yes' for spouse
	 * 3.5. Finish doing Endorsement
	 * 4. Do Renewal
	 * 5. Navigate to Reports tab
	 * 6. Check that NEW insurance score reports were ordered for all qualified named insureds (Primary insured and spouse).
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA, Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-24663"})
	public void pas24663_Endorsement_reorderAtRenewalYes(@Optional("") String state) {
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("TestData_EndorsementWithQualifiedNamedInsured",
				"Yes", "Yes", false);
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test order insurance score at renewal when Qualified named insured is added on midterm endorsement
	 * *@scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 3. Initiate Endorsement
	 * 3.1. Add Spouse
	 * 3.2. Check that "Order Insurance Score" and "Reorder at renewal" buttons are enabled for spouse and Primary Insured
	 * 3.3. Check "Order Insurance Score" button to 'Decline' for spouse
	 * 3.4. Finish doing Endorsement
	 * 4. Do Renewal
	 * 5. Navigate to Reports tab
	 * 6. Check that NEW insurance score reports were ordered for all qualified named insureds (Primary insured and spouse).
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA, Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-24663"})
	public void pas24663_Endorsement_OrderInsuranceScoreDecline(@Optional("") String state) {
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("TestData_EndorsementWithQualifiedNamedInsured",
				"Decline", "No", false);
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test order insurance score at renewal when Qualified named insured is added on midterm endorsement
	 * *@scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 3. Do 2 renewals
	 * 4. Initiate Endorsement after 2nd renewal
	 * 4.1. Add Spouse
	 * 4.2. Check that "Order Insurance Score" and "Reorder at renewal" buttons are enabled for spouse and Primary Insured
	 * 4.3. Check "Order Insurance Score" button to 'Yes' for spouse
	 * 4.4. Check "Reorder at renewal" button to 'No' for spouse
	 * 4.5. Finish doing Endorsement
	 * 5. Do Renewal
	 * 6. Navigate to Reports tab
	 * 7. Check that NEW insurance score reports were ordered for all qualified named insureds (Primary insured and spouse).
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-24663"})
	public void pas24663_Endorsement_AutomaticReorderingAt36months(@Optional("") String state) {
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("TestData_EndorsementWithQualifiedNamedInsured",
				"Yes", "No", true);
	}

}