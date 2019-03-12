package aaa.modules.regression.sales.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestInsuranceScoreRenewalTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestInsuranceScoreRenewal extends TestInsuranceScoreRenewalTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
	}

	//	----------------------------------------- Ordering at renewal per customer request ----------------------------

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test insurance score reports ordering for Re-order at Renewal
	 * *@scenario
	 * 1. Create Customer
	 * 2. Initiate Quote Creation
	 * 3. Add 4 insureds: 1 Primary Insured, 1 Spouse, 2 others (child, parent, other)
	 * 4. Navigate to Reports tab
	 * 5. Check that only Primary Insured and Spouse are available in Insurance Score Report section
	 * 6. Order Insurance score for Primary Insured and Spouse
	 * 7. If 'reorderForPrimaryInsured' parameter is true = Select 're-order for renewal' for Primary Insured
	 * 8. If 'reorderForSpouse' parameter is true = Select 're-order for renewal' for Spouse
	 * 9. Navigate to Premium and Coverages Quote tab
	 * 10. Click 'View Rating details'
	 * 11. Check 'FR score' - insurance score which was used in rating
	 * Note:
	 * **For all states (except VA) it uses BEST score for ALL terms from all named insureds
	 * 	e.g. spouse had insurance score 650, Primary insured 500 - it will uses spouses score in rating (BEST)
	 *
	 * **VA- It will use only the NEWEST score from the LATEST term from all named insured which has LATEST term insurance score.
	 * 	e.g. spouse had insurance score 650, Primary insured 500 - it will uses spouses score  in rating (BEST)
	 * 	but if you reorder at renewal for both of them, it will use only renewal terms score.
	 * 	If you reorder for one of them (e.g. spouse), it will use only that score, even though it was worse or other named insured
	 * 	has better score on New business.
	 * 12. Finish creating Policy
	 * 13. Do Renewal
	 * 14. Navigate to Renewals Report tab
	 * 15. Check that only Primary Insured and Spouse are available in Insurance Score Report section
	 * 16. Check that Insurance Score reports were reordered if reorderForPrimaryInsured and / or reorderForSpouse was set to 'Yes'
	 * 17. Check Insurance score reports dates
	 * 18. Check Insurance score report numbers
	 * 	Note:
	 * 	**For all states (except VA) it uses BEST score for ALL terms from all named insureds
	 *  e.g. spouse had insurance score 650, Primary insured 500, and they both were selected to reorder at renewal to 'Yes' and
	 *  their new scores on Renewal are spouses: 600 and Primary Insured: 700
	 *  It should show BEST score in insurance score section, so 650 for spouse and 700 for Primary Insured
	 *  And It should use BEST score from ALL terms in rating - 700.
	 *
	 * **VA- It will use only the NEWEST score from the LATEST term from all named insured which has LATEST term insurance score.
	 *  e.g. spouse had insurance score 650, Primary insured 500, and they both were selected to reorder at renewal to 'Yes' and
	 *  their new scores on Renewal are spouses: 600 and Primary Insured: 400
	 *  It should show NEWEST score in insurance score section, so 600 for spouse and 400 for Primary Insured
	 *  And It should use BEST score from NEWEST terms in rating - 600.
	 * 19. Navigate to Premium and Coverages Quote tab
	 * 20. Click 'View Rating details'
	 * 21. Check 'FR score' - insurance score which was used in rating
	 * *@details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_SpouseScoreOnRenewalIsHigher(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_SpouseScoreOnRenewalIsHigher",
				500, 650, 600, 700,
				true, true, false);
	}

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_PrimaryInsuredScoreOnRenewalIsHigher(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_PrimaryInsuredScoreOnRenewalIsHigher",
				500, 650, 800, 700,
				true, true, false);
	}

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_PrimaryInsuredScoreOnRenewalIsHigher2(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_PrimaryInsuredScoreOnRenewalIsHigher2",
				500, 650, 800, 600,
				true, true, false);
	}

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_SpouseScoreFromIsHigher(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_SpouseScoreFromNBIsHigher",
				500, 650, 600, 600,
				true, true, false);
	}

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_notOrderingForSpouse_SpouseScoreOnRenewalIsHigher(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_SpouseScoreFromNBIsHigher",
				500, 650, 600, 650,
				true, false, false);
	}

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_notOrderingForSpouse_PrimaryInsuredScoreOnRenewalIsHigher(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_PrimaryInsuredScoreOnRenewalIsHigher2",
				500, 650, 800, 650,
				true, false, false);
	}

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_notOrderingForSpouseandPrimaryInsured(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_PrimaryInsuredScoreOnRenewalIsHigher2",
				500, 650, 500, 650,
				false, false, false);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_SpouseGotLowestOnRenewal(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_SpouseGotLowestOnRenewal",
				500, 650, 600, 400,
				true, true, false);
	}

//	----------------------------------------- Automatic reordering at 36 months ----------------------------

	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test insurance score automatic reports ordering at 36 months for named insured which was used in rating
	 * *@scenario
	 * 1. Create Customer
	 * 2. Initiate Quote Creation
	 * 3. Add 4 insureds: 1 Primary Insured, 1 Spouse, 2 others (child, parent, other)
	 * 4. Navigate to Reports tab
	 * 5. Check that only Primary Insured and Spouse are available in Insurance Score Report section
	 * 6. Order Insurance score for Primary Insured and Spouse
	 * 7. Navigate to Premium and Coverages Quote tab
	 * 8. Click 'View Rating details'
	 * 9. Check 'FR score' - insurance score which was used in rating
	 * Note:
	 * **For all states (except VA) it uses BEST score for ALL terms from all named insureds
	 * 	e.g. spouse had insurance score 650, Primary insured 500 - it will uses spouses score in rating (BEST)
	 *
	 * **VA- It will use only the NEWEST score from the LATEST term from all named insured which has LATEST term insurance score.
	 * 	e.g. spouse had insurance score 650, Primary insured 500 - it will uses spouses score  in rating (BEST)
	 * 	but if you reorder at renewal for both of them, it will use only renewal terms score.
	 * 	If you reorder for one of them (e.g. spouse), it will use only that score, even though it was worse or other named insured
	 * 	has better score on New business.
	 * 10. Finish creating Policy
	 * 11. Do Renewal 3 times
	 * 12. Navigate to 3rd Renewals Report tab
	 * 13. Check that only Primary Insured and Spouse are available in Insurance Score Report section
	 * 14. Check that Insurance Score reports were reordered for named insured which was used in rating on LAST term
	 * 17. Check Insurance score reports dates
	 * 18. Check Insurance score report numbers
	 * 19. Navigate to Premium and Coverages Quote tab
	 * 20. Click 'View Rating details'
	 * 21. Check 'FR score' - insurance score which was used in rating
	 * *@details
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_automaticReorderingAt36Months_SpouseScoreOnRenewalIsHigher(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_SpouseScoreOnRenewalIsHigher",
				500, 650, 600, 700,
				false, false, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_automaticReorderingAt36Months_SpouseGotLowestOnRenewal(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_SpouseGotLowestOnRenewal",
				500, 650, 600, 400,
				false, false, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_automaticReorderingAt36Months_SpouseScoreOnRenewalIsLower(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_PrimaryInsuredScoreOnRenewalIsHigher2",
				500, 650, 800, 600,
				false, false, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_automaticReorderingAt36Months_NBisEqual_takesOlder(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_NBisEqual",
				650, 650, 650, 700,
				false, false, true);
	}

	//	-------------------- Ordering at renewal per customer request + Automatic reordering at 36 months ----------------------------
	/**
	 * *@author Rokas Lazdauskas
	 * *@name Test insurance score reports ordering for Re-order at Renewal + automatic reordering at 36 months
	 * *@scenario
	 * 1. Create Customer
	 * 2. Initiate Quote Creation
	 * 3. Add 4 insureds: 1 Primary Insured, 1 Spouse, 2 others (child, parent, other)
	 * 4. Navigate to Reports tab
	 * 5. Check that only Primary Insured and Spouse are available in Insurance Score Report section
	 * 6. Order Insurance score for Primary Insured and Spouse
	 * 7. If 'reorderForPrimaryInsured' parameter is true = Select 're-order for renewal' for Primary Insured
	 * 8. If 'reorderForSpouse' parameter is true = Select 're-order for renewal' for Spouse
	 * 9. Navigate to Premium and Coverages Quote tab
	 * 10. Click 'View Rating details'
	 * 11. Check 'FR score' - insurance score which was used in rating
	 * Note:
	 * **For all states (except VA) it uses BEST score for ALL terms from all named insureds
	 * 	e.g. spouse had insurance score 650, Primary insured 500 - it will uses spouses score in rating (BEST)
	 *
	 * **VA- It will use only the NEWEST score from the LATEST term from all named insured which has LATEST term insurance score.
	 * 	e.g. spouse had insurance score 650, Primary insured 500 - it will uses spouses score  in rating (BEST)
	 * 	but if you reorder at renewal for both of them, it will use only renewal terms score.
	 * 	If you reorder for one of them (e.g. spouse), it will use only that score, even though it was worse or other named insured
	 * 	has better score on New business.
	 * 12. Finish creating Policy
	 * 13. Do 3 Renewals
	 * 14. Navigate to 3rd Renewals Report tab
	 * 15. Check that only Primary Insured and Spouse are available in Insurance Score Report section
	 * 16. Check that Insurance Score reports were reordered for named insured which was used in rating on LAST term
	 * Note:
	 * 	If attributes 'reorderForPrimaryInsured' and / or 'reorderForSpouse' was set to 'Yes'
	 * 	it should automatically reorder at first renewal and then default to 'No'.
	 * 	Then system uses logic to determine which score to pick up for rating (check details in previous notes).
	 * 	And then it goes to 36 months mark (automatic reordering), it picks up user which was used in rating and reorders report for him.
	 * 17. Check Insurance score reports dates
	 * 18. Check Insurance score report numbers
	 * 	Note:
	 * 	**For all states (except VA) it uses BEST score for ALL terms from all named insureds
	 *  e.g. spouse had insurance score 650, Primary insured 500, and they both were selected to reorder at renewal to 'Yes' and
	 *  their new scores on Renewal are spouses: 600 and Primary Insured: 700
	 *  It should show BEST score in insurance score section, so 650 for spouse and 700 for Primary Insured
	 *  And It should use BEST score from ALL terms in rating - 700.
	 *
	 * **VA- It will use only the NEWEST score from the LATEST term from all named insured which has LATEST term insurance score.
	 *  e.g. spouse had insurance score 650, Primary insured 500, and they both were selected to reorder at renewal to 'Yes' and
	 *  their new scores on Renewal are spouses: 600 and Primary Insured: 400
	 *  It should show NEWEST score in insurance score section, so 600 for spouse and 400 for Primary Insured
	 *  And It should use BEST score from NEWEST terms in rating - 600.
	 * 19. Navigate to Premium and Coverages Quote tab
	 * 20. Click 'View Rating details'
	 * 21. Check 'FR score' - insurance score which was used in rating
	 * *@details
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_ReorderAtRenewal_ReorderAt36Months_SpouseHigherOnRenewal(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_SpouseScoreOnRenewalIsHigher",
				500, 650, 600, 700,
				true, true, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_ReorderAtRenewal_ReorderAt36Months_OnlyReorderForPrimaryInsured(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_SpouseScoreOnRenewalIsHigher",
				500, 650, 600, 700,
				true, false, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_ReorderAtRenewal_ReorderAt36Months_PrimaryInsuredScoreOnRenewalIsHigher(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_PrimaryInsuredScoreOnRenewalIsHigher",
				500, 650, 800, 700,
				true, true, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_BothEqualOnRenewal(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_BothEqualOnRenewal",
				500, 650, 650, 600,
				true, true, true);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CO, Constants.States.MT, Constants.States.NV, Constants.States.OK, Constants.States.VA, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6, testCaseId = {"PAS-25569", "PAS-25573", "PAS-23089", "PAS-26025"})
	public void pas25569_ReorderAtRenewal__automaticReorderingAt36Months_SpouseScoreOnRenewalIsLower(@Optional("") String state) {
		testInsuranceScoreThenReorderedAtRenewal("ApplicantTab_PrimaryInsuredScoreOnRenewalIsHigher2",
				500, 650, 800, 600,
				true, true, true);
	}

}