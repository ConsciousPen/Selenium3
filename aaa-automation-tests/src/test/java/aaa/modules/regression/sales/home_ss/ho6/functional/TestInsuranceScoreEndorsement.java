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
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("Yes", "No", false);
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
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("Yes", "Yes", false);
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
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("Decline", "No", false);
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
		testQualifiedNamedInsuredAddedOnMidTermEndorsement("Yes", "No", true);
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test order insurance score on Renewal then Qualified named insured used in rating on NB is deleted during midterm
	 * *@scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 2.1. Add Primary Insured, Spouse, Child, Parent (Spouse has highest insurance score).
	 * 2.2. Check Scores used in rating
	 * 2.3. Finish policy creation
	 * 3. Do Endorsement - policy effective date +1 day atleast
	 * 4. Navigate to Applicant tab
	 * 5. Remove Qualified named insured with highest Insurance score (spouse)
	 * 6. Navigate to reports tab
	 * 7. Check that only Primary Insured is displayed in reports section
	 * 8. Calculate Premium
	 * 9. Open View Rating Details page
	 * 10. Check that spouses score is still used during midterm endorsement
	 * 11. Finish Endorsement
	 * 12. Do Renewal
	 * 13. Navigate to Reports tab
	 * 14. Check that new Insurance Score Report was ordered
	 * 15. Calculate premium
	 * 16. Open View Rating Details page
	 * 17. Check that the best score of Primary Insured is used in rating
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA, Constants.States.MD, Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-24664", "PAS-29054", "PAS-25628"})
	public void pas24664_Endorsement_DeletingQualifiedNamedInsured_SpouseUsedInRating(@Optional("") String state) {
		testQualifiedNamedInsuredDeletedOnMidTermEndorsement("ApplicantTab_SpouseIsHigher",
				500, 600, 650);
	}

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test order insurance score on Renewal then Qualified named insured NOT used in rating on NB is deleted during midterm
	 * *@scenario
	 * 1. Create Customer
	 * 2. Create Policy
	 * 2.1. Add Primary Insured, Spouse, Child, Parent (Primary Insured has highest insurance score).
	 * 2.2. Check Scores used in rating
	 * 2.3. Finish policy creation
	 * 3. Do Endorsement - policy effective date +1 day atleast
	 * 4. Navigate to Applicant tab
	 * 5. Remove Qualified named insured with NOT highest Insurance score (Spouse)
	 * 6. Navigate to reports tab
	 * 7. Check that only Primary Insured is displayed in reports section
	 * 8. Calculate Premium
	 * 9. Open View Rating Details page
	 * 10. Check that spouses score is still used during midterm endorsement
	 * 11. Finish Endorsement
	 * 12. Do Renewal
	 * 13. Navigate to Reports tab
	 * 14. Check that new Insurance Score Report was ordered
	 * 15. Calculate premium
	 * 16. Open View Rating Details page
	 * 17. Check that the best score of Primary Insured is used in rating
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA, Constants.States.MD, Constants.States.PA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-24664", "PAS-29054", "PAS-25628"})
	public void pas24664_Endorsement_DeletingQualifiedNamedInsured_PrimaryInsuredUsedInRating(@Optional("") String state) {
		testQualifiedNamedInsuredDeletedOnMidTermEndorsement("ApplicantTab_SpouseIsLower",
				650, 700, 500);
	}
}