package aaa.modules.regression.service.auto_ss.functional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesCoveragesHelper;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMiniServicesCoverages extends TestMiniServicesCoveragesHelper {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Loan Lease coverage check for veh equal to 3 years old
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle Older than 3 years with Ownership info = Owned
	 * 5) rate
	 * 6) check Loan Lease coverage is not available
	 * 7) Add Ownership Leased or Financed
	 * 8) rate
	 * 9) check Loan Lease coverage is available and defaulted to No Coverage
	 * 10) set value of the coverage to Yes
	 * 11) rate
	 * 12) check Loan Lease coverage is available and defaulted to No Coverage
	 * 13) Issue
	 * 14) set Ownership to Owned
	 * 15) rate
	 * 16) check Loan Lease is not available
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14316"})
	public void pas14316_LoanLeasedCovForFinancedNewVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14316_LoanLeasedCovForFinancedNewVehicleBody(softly, "FNC")
		);
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2018 vehicle
	 * Get vehicle coverages from Pas
	 * run Dxp ViewManageVehicleLevelCoverages
	 * verify coverages are same like pas coverages
	 * calculate premium save and exit
	 * run ViewManageVehicleLevelCoverages for endorsemnt
	 * validate coverages are matching with pas and all delimators .
	 * Go to pas and change all the coverages for vehicle
	 * calculate premium save.
	 * open pended endorsement go to P and C page.
	 * get all vehicle coverages save them
	 * hit view manage vehicle level coverages dxp
	 * validate response aginst pas vehicle coverages.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11741", "PAS-11852", "PAS-12601"})
	public void pas11741_ManageVehicleLevelCoverages(@Optional("VA") String state) {

		pas11741_ViewManageVehicleLevelCoverages(getPolicyType());
	}

	//Scenario 2
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11741", "PAS-11852", "PAS-12601"})
	public void pas11741_ManageVehicleLevelCoveragesOtherThanVA(@Optional("AZ") String state) {

		pas11741_ViewManageVehicleLevelCoveragesForAZ(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2018 vehicle
	 * Create an endorsement.
	 * add new vehicle
	 * Update newly added vehicle to ownership LSD
	 * Get all the vehicle level coverages from pas
	 * request update vehicle level coverage with Oid policy number and coverage name and value
	 * Scanario 1.
	 * Change all the coverages using service
	 * remove one by one coverage and veryfi dependent coverages
	 * change all the coverages back
	 * verify if all other coverages changed
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10352"})
	public void pas10352_ManageVehicleCoverageUpdateCoverage(@Optional("VA") String state) {

		pas10352_ManageVehicleCoverageUpdateCoverage(getPolicyType());

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10352"})
	public void pas10352_ManageVehicleCoverageUpdateCoverageOtherThanVa(@Optional("AZ") String state) {

		pas10352_ManageVehicleCoverageUpdateCoverageOtherState(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * 1. Create a active policy with 2018 vehicle
	 * 2. Create an endorsement.
	 * 3.add new vehicle
	 * 4. get a default coverage for rental Reimbursement
	 * 5. Change RREIM to 30/900 and validate available coverages only no coverage and 30/900
	 * 6.Change RREIM to 0/0 and verify if we are getting all coverages in available limit
	 * scenario 2:
	 * 1.take COMPDED off and verify  RentalReimbursement shows all available limits
	 * 2. collded make it 500 and verify  RentalReimbursement shows all available limits
	 * 3.update rreim 50/1500 and verify limits
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14693"})
	public void pas14693_viewCoverageAndUpdateCoverageRentalReimbursement(@Optional("AZ") String state) {
		assertSoftly(softly ->

				pas14693_viewCoverageAndUpdateCoverageRentalReimbursement(getPolicyType(), softly)
		);
	}

	//scenario 2
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14693"})
	public void pas14693_updateCoverageRentalReimbursement(@Optional("AZ") String state) {
		assertSoftly(softly ->

				pas14693_updateCoverageRentalReimbursementBody(getPolicyType(), softly)
		);
	}

	/**`
	 * @author Megha Gubbala
	 * 1. Create a active policy in the pas
	 * 2.Create an endorsement.
	 * Scenario 1
	 * run update coverage service
	 * set BI Coverage 50000/100000 and verify response and check BI limit updated PD available limit should be up to upper limit of BI.
	 * Scenario 2
	 * run update coverage service to set PD 3000000
	 * Verify since PD can not exceed BI so PD limit should set to 100000
	 * Update set BI Coverage 500000/500000 and verify response PD available limit should be up to upper limit of BI
	 * PD limit should not change.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14648"})
	public void pas14721_UpdateCoveragesBI_PD(@Optional("AZ") String state) {

		pas14721_UpdateCoveragesServiceBIPD(getPolicyType(), state);
	}

	/**
	 * @author Maris Strazds
	 * @name UM, UIM, UM/UIM rule dependency on UI
	 * @scenario
	 * 1. Create a active policy in the pas
	 * 2. Create an endorsement.
	 * 3. Run update coverage service
	 * 4. Set BI Coverage and verify that UM and UIM (or UM/UIM where it is not separate coverages) response limit is set to the same limit as updated BI limit
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@StateList(states = {Constants.States.AZ, Constants.States.ID, Constants.States.KY, Constants.States.PA, Constants.States.SD, Constants.States.UT, Constants.States.WV, //applicable states for PAS-15254
			Constants.States.VA, Constants.States.DE, Constants.States.IN, Constants.States.KS, Constants.States.MD, Constants.States.NV, Constants.States.NJ, Constants.States.OH, Constants.States.OR}) //applicable states for PAS-14733
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15254", "PAS-14733"})
	public void pas15254_14733_UpdateCoveragesBI_UM_UIM(@Optional("") String state) {
		pas15254_14733_UpdateCoveragesUM_UIM_Body(getPolicyType(), getState());
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2018 and leased  vehicle
	 * Create an endorsement.
	 * run viewCoverageInfo service
	 * verify loan and leas coverage
	 * verify customerDisplayed canChangeCoverage is true
	 * update vehicle using DXP change ownership to own
	 * verify customerDisplayed canChangeCoverage is false
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13353"})
	public void pas13353_LoanLeaseCoverage(@Optional("VA") String state) {

		pas13353_LoanLeaseCoverage(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * Create a active policy with 2018 vehicle
	 * add pending vehicle on policy
	 * update pending vehicle usage as pleasure and ownership as leased
	 * Get vehicle coverages from Pas for both the vehicles using policy number and oid
	 * run Dxp ViewManageVehicleLevelCoveragesone vehicle
	 * verify coverages are same like pas coverages
	 * calculate premium save and exit
	 * run ViewManageVehicleLevelCoverages for endorsemnt
	 * validate coverages are matching with pas and all delimators .
	 * Go to pas and change all the coverages for vehicle
	 * calculate premium save.
	 * open pended endorsement go to P and C page.
	 * get all vehicle coverages save them
	 * hit view manage vehicle level coverages dxp
	 * validate response aginst pas vehicle coverages.
	 * bind the endorsement
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12769"})
	public void pas12769_ViewVehicleLevelCoveragesOneVehicle(@Optional("VA") String state) {

		pas11741_ViewVehicleLevelCoverages(getPolicyType());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14648"})
	public void pas14645_ViewPolicyLevelCoverages(@Optional("VA") String state) {

		pas14645_ViewCoveragesBiPd(getPolicyType());
	}

	/**
	 * 	 * @author Oleg Stasyuk
	 * 	 * @name Loan Lease coverage check for veh older than 3 years
	 * 	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle Older than 3 years with Ownership info = Owned
	 * 5) rate
	 * 6) check Loan Lease coverage is not available
	 * 6) Add Ownership Leased or Financed
	 * 7) rate
	 * 8) check coverage is not available
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14316"})
	public void pas14316_LoanLeasedCovForLeasedOldVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14316_LoanLeasedCovForLeasedOldVehicleBody(softly, "LSD")
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Add new vehicle when you had Vehicle, without Transportation Expense before.
	 * @scenario 1. Create policy with one vehicle,
	 * without Transportation Expense. (
	 * 2. Start endorsement outside of PAS.
	 * 3. Add vehicle.
	 * 4. Hit View Coverage service.
	 * 5. Check coverages for new vehicle:
	 * Transportation Expense, Comprehensive Coverage
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14536"})
	public void pas14536_TransportationExpensePart1(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14536_TransportationExpensePart1Body(getPolicyType(), softly)
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transportation Expense remains the limit I chose
	 * @scenario 1. Create policy with one vehicle,
	 * with Transportation Expense 1.200$
	 * 2. Start endorsement outside of PAS.
	 * 	 * 3. Add vehicle.
	 * 4. Remove "COMPDED" coverage from my newly added vehicle.
	 * 5. Hit View Coverage service.
	 * 6. Check if Transportation Expense remains the limit I chose.
	 * 7. Update: add "COMPDED" coverage again.
	 * 8. Hit View Coverage service.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14536"})
	public void pas14536_TransportationExpensePart2(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14536_TransportationExpensePart2Body(getPolicyType(), softly)
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transportation Expense is defaulted
	 * @scenario 1. Create policy with one vehicle,
	 * with Transportation Expense 600$
	 * 2. Start endorsement outside of PAS.
	 * 3. Add vehicle.
	 * 4. Remove "COMPDED" coverage from my newly added vehicle.
	 * 5. Hit View Coverage service.
	 * 6. Update: add "COMPDED" coverage again.
	 * 7. Hit View Coverage service.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14536"})
	public void pas14536_TransportationExpensePart3(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14536_TransportationExpensePart3Body(getPolicyType(), softly)
		);
	}

	/**
	 * @author gzkvano
	 * @name Check UIM delimiter
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.IN, Constants.States.KS,
			Constants.States.MD, Constants.States.NV, Constants.States.NJ, Constants.States.OH, Constants.States.OR, Constants.States.CT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14646"})
	public void pas14646_UimDelimiter(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14646_UimDelimiter(state, softly)
		);
	}

	/**
	 * @author mstrazds
	 * @name Check UM/UIM delimiter
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.DC, Constants.States.ID, Constants.States.KY, Constants.States.PA, Constants.States.SD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15228"})
	public void pas15228_UmUimDelimiter(@Optional("DC") String state) {
		pas15228_UmUimDelimiterBody();
	}

	/**
	 * @author mstrazds
	 * @name Check UMPD delimiter
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.DE, Constants.States.MD, Constants.States.VA, Constants.States.NJ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15824"})
	public void pas15824_UmpdDelimiter(@Optional("NJ") String state) {
		pas15824_UmpdDelimiterBody();
	}

	/**
	 * @author mstrazds
	 * @name Check UMPD doesn't exit
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.CT, Constants.States.KS, Constants.States.KY, Constants.States.MT, Constants.States.NY,
			Constants.States.OK, Constants.States.PA, Constants.States.SD, Constants.States.WY, Constants.States.ID})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15325"})
	public void pas15325_UmpdNotExist(@Optional("AZ") String state) {
		pas15325_UmpdNotExistBody();
	}

	/**
	 * @author gzkvano
	 * @name Check MEDPM delimiter
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14648"})
	public void pas14648_MedpmDelimiter(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14648_MedpmDelimiter(getPolicyType())
		);
	}

}


