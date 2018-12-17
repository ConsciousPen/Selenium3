package aaa.modules.regression.service.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesCoveragesHelper;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11741", "PAS-11852", "PAS-12601", "PAS-17732"})
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
	@StateList(states = {Constants.States.AZ, Constants.States.ID, Constants.States.KY, Constants.States.PA, Constants.States.UT, Constants.States.WV, Constants.States.MT, //applicable states for PAS-15254
			Constants.States.VA, Constants.States.DE, Constants.States.IN, Constants.States.KS, Constants.States.MD, Constants.States.NV, Constants.States.NJ, Constants.States.OH, Constants.States.OR}) //applicable states for PAS-14733
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15254", "PAS-14733"})
	public void pas15254_14733_UpdateCoveragesBI_UM_UIM(@Optional("VA") String state) {
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13353", "PAS-17740"})
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
	 * 3. Add vehicle.
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
	@StateList(states = {Constants.States.AZ, Constants.States.DC, Constants.States.ID, Constants.States.KY, Constants.States.PA,
			Constants.States.SD, Constants.States.MT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15228"})
	public void pas15228_UmUimDelimiter(@Optional("ID") String state) {
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
	public void pas15325_UmpdNotExist(@Optional("MT") String state) {
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

	/**
	 * @author Maris Strazds
	 * @name Trailers - Coverages that do not apply
	 * @scenario
	 * 1. Create a policy in PAS with with more than 1 vehicle (one of them must be Trailer)
	 * 2. Run View Coverages service (Policy)
	 * 3. Validate that View coverages Service response contains only  one instance of Policy level coverages
	 * 4. Run View Coverages service (Policy) for Trailer
	 * 5. Validate that View Coverages service response should contain  all vehicle level coverages
	 *      AND only comp and coll should have CanChangecoverage = true
	 *      AND CustomerDisplay = true
	 * 6. Create an endorsement through service
	 * 7. Run View Coverages service (Endorsement)
	 * 8. Validate that View coverages Service response contains only  one instance of Policy level coverages
	 * 9. Run View Coverages service (Endorsement) for Trailer
	 * 10. Validate that View Coverages service response should contain  all vehicle level coverages
	 *      AND only comp and coll should have CanChangecoverage = true
	 *      AND CustomerDisplay = true
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14680"})
	public void pas14680_TrailersCoveragesThatDoNotApply(@Optional("SD") String state) {
		pas14680_TrailersCoveragesThatDoNotApplyBody(getPolicyType());

	}

	/**`
	 * @author Megha Gubbala :verify view and update IL and Medical Expences coverage
	 * 1. Create a active policy in the pas
	 * 2.Create an endorsement.
	 * 3.View Existing IL and Medical Expences coverage
	 * 4.Verify Available limits for both coverages
	 * Scenario 1
	 * run update coverage service
	 * set MEDPM Coverage 10000 and verify response.
	 * Scenario 2
	 * run update coverage service to set IL to no coverage and verify response
	 * again run View coverage service and verify if coverage in updated on PAS
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14734"})
	public void pas14734_UpdateViewCoverageILAndMedical(@Optional("AZ") String state) {
		pas14734_UpdateViewCoverageILAndMedicalBody(getPolicyType());

	}

	/**`
	 * @author Megha Gubbala : Update Coverages - UMPD - PD and UMPD rule
	 * 1.Create a active policy in the pas
	 * 2.Create an endorsement.
	 * 3. run view coverage service get PD and UMPD
	 * 4. Update PD coverage
	 * 5. Verify available limits
	 * 6. Verify UMPD it should match PD
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14730"})
	public void pas14730_UpdateCoverageUMPDAndPD(@Optional("MD") String state) {
		pas14730_UpdateCoverageUMPDAndPDBody(getPolicyType());

	}

	/**
	 * @author MeghaGubbala
	 * @name update and view coverage UMUIM and BI
	 * Create a policy in pas Verify BI = UmUIM
	 * update BI to 25/50000 verify UMUIM is the same
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-17629", "PAS-17958"})
	public void pas17629_Umuim_Update_coverage(@Optional("IN") String state) {
		pas17629_Umuim_Update_coverageBody(getPolicyType());
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
	 * @name Verify Policy and Vehicle level coverages Order + Driver level coverages order for states where we have requirements
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.IN, Constants.States.KS,
			Constants.States.MD, Constants.States.NV, Constants.States.NJ, Constants.States.OH, Constants.States.OR, Constants.States.CT, Constants.States.KY, Constants.States.SD, Constants.States.KS,
			Constants.States.CT, Constants.States.WV, Constants.States.UT, Constants.States.NV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-17646", "PAS-19013", "PAS-19042", "PAS-19016", "PAS-19024", "PAS-19044", "PAS-18202", "PAS-19055", "PAS-19052"})
	public void pas17646_OrderOfCoverage(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas17646_OrderOfCoverageBody(softly)
		);
	}

	/**
	 * @author Maris Strazds
	 * @scenario validate "Verify PUP Policy" error.
	 * 1. Create Auto SS policy with companion PUP policy
	 * 2. Create endorsement through service
	 * 3. validate for all available BI limits, that error "Verify PUP Policy" is displayed if limit is lower than 500000/500000
	 * 4. Set BI limit to the higher one so that all PD limits are available
	 * 5. Validate for all available PD limits, that error "Verify PUP Policy" is displayed if limit is lower than 100000
	 * 6. Set BI and PD so that the error is displayed, rate and bind the endorsement. (successfully)
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15379"})
	public void pas15379_ValidatePUPErrorRelatedWithBiPdLimits(@Optional("VA") String state) {
		pas15379_ValidatePUPErrorRelatedWithBiPdLimitsBody();
		//NOTE: This test forks for ALL states. (Except CA)
	}

	/**
	 * @author Maris Strazds
	 * @name Customized Equipment (CUSTEQUIP)
	 * @scenario for VA
	 * 1. Create a policy in PAS with one regular vehicle, one VANS/PICKUP without CUSTEQUIP coverage and one VANS/PICKUP with CUSTEQUIP coverage
	 * 2. Create endorsement through service
	 * 3. Run viewEndorsementCoverages, viewPolicyCoverages, viewEndorsementCoveragesByVehicle, viewPolicyCoveragesByVehicle services
	 * 4. validate that responses contains Customized Equipment coverage (CUSTEQUIP)
	 *    AND canChange = false
	 *    and customerDisplay = true
	 *    and value is as per the UI
	 *    and the coverage is displayed after Collision.
	 * @scenario for states other than VA (states without CUSTEQUIP)
	 * 1. Create a policy in PAS with one regular vehicle and two VAN/PICKUP (CUSTEQUIP coverage is applicable only to VA)
	 * 2. Create endorsement through service
	 * 3. Run viewEndorsementCoverages, viewPolicyCoverages, viewEndorsementCoveragesByVehicle, viewPolicyCoveragesByVehicle services
	 * 4. Validate that responses don't contain Customized Equipment coverage (CUSTEQUIP)
	 *
	 * @author Jovita Pukenaite
	 * @name Add/remove Comp, check CUSTEQUIP
	 * @scenario for VA only!
	 * 1. One vehicle should have CUSTEQUIP
	 * 2. Remove Comp coverage (-1)
	 * 3. Check coverages, rate.
	 * 4. Return back Comp coverage
	 * 5. Check coverages again, rate.
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18624", "PAS-19834"})
	public void pas18624_CustomisedEquipment(@Optional("VA") String state) {
		pas18624_CustomisedEquipmentBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name View_Coverage_Update_Coverage UMPD
	 * @scenario for IN
	 * @details
	 * @scenario1:
	 * 1.Create an IN policy
	 * 2.run View Coverages Service
	 * 3.Verify the delimiter is Per Accident
	 * 4.Verify the canChangeCoverage = false
	 * 5.Verify canViewCoverage = true
	 *  * @scenario2
	 *  1.update my PD Limit
	 *  2.my UMPD Limit is set to the same as my PD Limit
	 * **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas16035"})
	public void pas16035_ViewCoverageUpdateCoverage(@Optional("IN") String state) {
		pas16035ViewCoverageUpdateCoverageBody(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * @name View Coverages/Update Coverages - UMPD Deductible - Indiana
	 * @scenario for IN
	 * * @details
	 * Scenario1:
	 *  1.Create an IN policy
	 *  2.run View Coverages Service
	 *  3.verify Uninsured Motorist Property Damage Deductible+ is vehicle level and the canChangeCoverage = true and canViewCoverage = true and available limits are provided
	 *  @Scenario 2:
	 *  1.go to pas update UMPD = no coverage
	 *  2. update PD Limit
	 *  3. verify UMPD Limit is set to the same as my PD Limit and UMPD Deductible is not updated
	 * @Scenario 3 :
	 * 1. update UMPD limit is other than No Coverage
	 * 2. update my PD Limit
	 * 3. verify  UMPD Limit is set to the same as my PD Limit
	 * 4. verify UMPD Deductible is not updated
	 * @Scenario 4:
	 *1.Add new vehicle
	 * 2. Update UMPDDED for new vehicle 0
	 * 3. verify UMPDDED is updated
	 * 4. And UMPDDED for existing vehicle is 250
	 * 	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas17628"})
	public void pas17628_ViewCoverageUpdateCoverageUmpdDeductible(@Optional("IN") String state) {
		pas17628_pas17628_ViewCoverageUpdateCoverageUmpdDeductibleBody(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * @name View Coverages/Update Coverages - EUIM - MD
	 * @scenario for MD
	 * * @details
	 * 1. Create a MD policy
	 * 2. run view coverage service  see the Enhanced UIM coverage selection :canChangeCoverage = true and customerView = true
	 * 3. Update EUIM select coverage and verify response.
	 * 4. Update EUIM remove coverage and verify response.
	 * 	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas11654", "PAS-22550"})
	public void pas11654_MDEnhancedUIMBICoverage(@Optional("MD") String state) {
		assertSoftly(softly ->
				pas11654_MDEnhancedUIMBICoverageBody(softly, getPolicyType())
		);
	}

	/**
	 * @author Megha Gubbala
	 * @name Tort Coverage - Kentucky
	 * @scenario for KY
	 * * @details
	 * 1. Create a KY policy with afr, nafr and excluded driver.
	 * 2. run view coverage service and see can view Driver Rejects Limitation on Right To Sue for
	 * 3. Update Tort Coverage yes for all AFRD and verify
	 * 4. Update Tort Coverage no for all AFRD and verify
	 * 	 **/

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas11654, pas18202"})
	public void pas20675_TortCoverage(@Optional("KY") String state) {
		assertSoftly(softly ->
				pas20675_TortCoverageBody(softly, getPolicyType())
		);
	}
	/**
	 * @author Maris Strazds
	 * @scenario validate Property Damage Liability available limits if BI is selected as the lowest available coverage
	 * 1. Create a policy in PAS with BI that are NOT the lowest BI limits available
	 * 2. Create endorsement through service
	 * 3. Update my BI coverages to be BI limits that are the lowest BI limits available through service
	 * 4. Validate that the delimiter for Bodily Injury Liability shows as Per Person/Per Accident
	 * AND the delimiter for Property Damage Liability shows as Per Accident
	 * AND the only values available for Property Damage are those that are less than or equal to the Per Accident amount for Bodily Injury
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@StateList(states = {Constants.States.AZ, Constants.States.NV, Constants.States.SD, Constants.States.UT, Constants.States.WY})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15788"})
	public void pas15788_PDAvailableLimitsWhenBIisTheLowestAvailable(@Optional("WY") String state) {
		pas15788_PDAvailableLimitsWhenBIisTheLowestAvailableBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name UM/UIM required to have UMPD (Update Comp/Coll)
	 * @scenario1
	 * 1. Create policy. (I have UM/UIM other than no coverage)
	 * 2. Create endorsement outside of PAS.
	 * 3. Run update coverage service: remove Collision Coverage from PPA vehicle.
	 * 4. Check UMPD coverage.
	 * 5. Delete endorsement, create new one.
	 * 6. Run update coverage service: remove Comp Coverage from PPA vehicle.
	 * 7. Check UMPD coverage.
	 * @scenario2
	 * 1. The same like TC1, but Policy should not have UM/UIM coverage.
	 * @scenario3
	 * 1. Prepare policy: UM/UIM other than no coverage,  COMP/COLL = No coverage)
	 * 2. Create endorsement outside of PAS.
	 * 3. Run update coverage service:  Add COMP/COLL coverage.
	 * 4. Check UMPD coverage.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15255"})
	public void pas15255_UpdateCompCollCoveragesCheckUmpd(@Optional("OH") String state) {

		pas15255_UpdateCompCollCoveragesCheckUmpdBody(state);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name View Coverages - UMPD (Update Comp/Coll)
	 * @scenario1
	 * 1. Create policy: UM/UIM other than no coverage, Comp/Coll- both = no coverage
	 * 2. Run view coverages service. Check UMBI
	 * 3. Create endorsement outside of PAS.
	 * 4. Update Coverage: Coll =  No coverages, bind endorsement.
	 * 5. Run view coverages service. Check UMBI
	 * @scenario2
	 * 1. Create policy: UM/UIM other than no coverage, Comp/Coll = both have other than no coverage.
	 * 2. Run view coverages service. Check UMBI
	 * @scenario3
	 * 1. Create policy: UM/UIM - No Coverage, Comp, Comp/Coll- both = no coverage
	 * 2. Run view coverages service. Check UMBI
	 * 3. Create endorsement outside of PAS.
	 * 4. Update Coverage: Coll =  No coverages, bind endorsement.
	 * Note: Repeat with MotorHome
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15496"})
	public void pas15496_viewCoveragesUmpdWhenYouDontHaveCompColl(@Optional("OH") String state) {

		pas15496_viewCoveragesUmpdWhenYouDontHaveCompCollBody(state, getPolicyType(), false);
		pas15496_viewCoveragesUmpdWhenYouDontHaveCompCollBody(state, getPolicyType(),true);
	}

	/**
	 * @author Megha Gubbala
	 * @name Update Coverage - ADB
	 * @scenario1
	 * 1. Create policy With multiple drivers
	 * 2. Verify ADB coverage showing only for AFR drivers.
	 * 3. Run View Premium service save the Premium
	 * 4. Update Coverage Service add adb coverage to the AFR driver verify Premium should increased
	 * 5. Update Coverage Service add adb coverage for 2 AFR driver verify Premium should increased
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas17642"})
	public void pas17642_UpdateCoverageADB(@Optional("AZ") String state) {
		pas17642_UpdateCoverageADBBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name UM and UIM Coverage - Kentucky
	 * @scenario1
	 * 1. Create policy for Ky
	 * 2. Run view coverages service. check  UM and UIM coverages are separate
	 * and UM Coverage has customerDisplayed = true
	 * and UIM Coverage has canChangeCoverage = false
	 * 3. run update coverage service update BI limit to "25000/50000";
	 * 4. verify UIM UMUIM is same.
	 * 5. verify available limits according to following table.

	||BI Limit ||UIM/UIM Limit
	||25/50	   ||No Coverage,25/50
	||50/100   ||	Above plus 50/100
	||100/300  ||	Above plus 100/300
	||250/500  ||	Above plus 250/500
	||300/500  ||	Above plus 300/500
	||500/500  ||	Above plus 500/500
	||500/1000 ||	Above plus 500/1000
	||1000/1000||	Above plus 1000/1000
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas19627"})
	public void pas19627_UMAndUimCoverage(@Optional("KY") String state) {

		pas19627_UMAndUimCoverageBody(getPolicyType());
	}

	/**
	 * @author Megha Gubbala
	 * @name South Dakota - BI and UM and UIM Coverage Rules
	 * @scenario1
	 * 1. Create policy for SD
	 * 2. Run view coverages service. check  UM and UIM coverages are separate
	 * and UM Coverage has cusstomerDisplayed = true
	 * and UIM Coverage has canChangeCoverage = false
	 * 3.update  bi BI is <= 100/300 verify  UM and UIM have canChangeCoverage = false
	 * 4.update  bi BI is > 100/300 verify  UM and UIM have canChangeCoverage = true
	 * 5.verify Available limits according to table.

	||BI Limit	||UM Limit	           ||UIM Limit	        ||canChange
	||25/50	    ||25/50	               ||25/50	            ||No
	||50/100	||50/100	           ||50/100             ||No
	||100/300	||100/300	           ||100/300	        ||No
	||250/500	||100/300, 250/500     ||100/300, 250/500   ||	Yes
	||300/500	||Above plus 300/500   ||Above plus 300/500	||Yes
	||500/500	||Above plus 500/500   ||Above plus 500/500	||Yes
	||500/1000	||Above plus 500/1000  ||Above plus 500/1000||Yes
	||1000/1000	||Above plus 1000/1000||Above plus 1000/1000||Yes
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-19626"})
	public void pas19626_biAndUMAndUIMCoverageSD(@Optional("SD") String state) {

		pas19626_biAndUMAndUIMCoverageSDBody(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @scenario
	 * 1. Create Auto SS policy with 2 AFR, 1 NAFR and 1 Excluded driver (all applicable drivers should have TORT coverage)
	 * 2. Create endorsement through service
	 * 3. Run view coverages and validate that PIP coverages are the same as in PAS UI
	 * 4. Update Basic PIP to No Coverage through service and validate PIP in response. Run viewEndorsement coverages and validate PIP in response.
	 * 5. Update Basic PIP to $10,000 through service and validate PIP in response. Run viewEndorsement coverages and validate PIP in response.
	 * 6. Update one or more drivers to be Reject Limit to Sue = No through service and validate PIP in response. Run viiewEndorsement coverages and validate PIP in response.
	 * 7. Update endorsement coverages (ADDPIP) to other than 0 through service and validate PIP in response. Run viiewEndorsement coverages and validate PIP in response.
	 * 8. Update endorsement coverages (ADDPIP) back to 0 through service and validate PIP in response. Run viewEndorsement coverages and validate PIP in response.
	 * 9. Update endorsement coverages (ADDPIP) back to other than 0 through service and validate PIP in response. Run viiewEndorsement coverages and validate PIP in response.
	 * 10. Update endorsement coverages (PIPDED) to other than 0 through service and validate PIP in response. Run viiewEndorsement coverages and validate PIP in response.
	 * 11. Update endorsement coverages (PIPDED) back to 0 through service and validate PIP in response. Run viewEndorsement coverages and validate PIP in response.
	 * 12. Update endorsement coverages (PIPDED) back to other than 0 through service and validate PIP in response. Run viiewEndorsement coverages and validate PIP in response.
	 * 13. Update all drivers to be Reject Limit to Sue = YES through service and validate PIP in response. Run viewEndorsement coverages and validate PIP in response.
	 * 14. Update BPIP to no coverage and then update one or more drivers to be Reject Limit to Sue = No through service and validate PIP in response. Run viewEndorsement coverages and validate PIP in response.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.KY})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-19195", "PAS-19194"})
	public void pas19195_viewUpdatePIPCoverage_KY(@Optional("KY") String state) {
		pas19195_viewUpdatePIPCoverage_KYBody();
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update KS PIP Coverages
	 * @scenario
	 * 1. Create KS Auto SS policy in PAS
	 * 2. Run vieEndorsementCoverages service and validate response and compare it with PAS UI
	 * 3. Update PIP by updating MEDEXP coverage to 10000 and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 * 4. Update PIP by updating MEDEXP coverage to 25000 and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 * 5. Update PIP by updating MEDEXP coverage to 4500 and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.KS})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15358", "PAS-15359"})
	public void pas15358_viewUpdatePIPCoverage_KS(@Optional("KS") String state) {
		pas15358_viewUpdatePIPCoverage_KSBody();
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update OR PIP Coverages
	 * @scenario
	 * 1. Create OR Auto SS policy in PAS
	 * 2. Run vieEndorsementCoverages service and validate response and compare it with PAS UI
	 * 3. Update PIP by updating MEDEXP coverage and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 * 4. Update PIPDED by updating MEDEXP coverage and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.OR})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15365", "PAS-15366"})
	public void pas15365_viewUpdatePIPCoverage_OR(@Optional("OR") String state) {
		pas15365_viewUpdatePIPCoverage_ORBody();
	}

	/**
	 * @author Maris Strazds
	 * @name View/Update UT PIP Coverages
	 * @scenario
	 * 1. Create OR Auto SS policy in PAS
	 * 2. Run vieEndorsementCoverages service and validate response and compare it with PAS UI
	 * 3. Update PIP by updating MEDEXP coverage while "Rejection of Work Loss Benefit" is "false" and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 * 4. Update "Rejection of Work Loss Benefit" to "true" and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 * 5 Update PIP by updating MEDEXP coverage while "Rejection of Work Loss Benefit" is "true" and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 * 6. Update "Rejection of Work Loss Benefit" to "false" and validate response and compare it with PAS UI. Validate that updateCoverage response is the same as viewCoverages response.
	 * Note: validate subCoverages in changeLog after each update
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.UT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15368", "PAS-21918"})
	public void pas15368_viewUpdatePIPCoverage_UT(@Optional("UT") String state) {
		pas15368_viewUpdatePIPCoverage_UTBody();
	}

	/**
	 * @author Sabra Domeika
	 * @name Let's Check for Wrong Values - Coverages
	 * @scenario1
	 * 1. Create a policy for a given state.
	 * 2. Create an endorsement for the policy.
	 * 3. Run the View Coverage service and retrieve the details for the coverages of that state.
	 * 4. For each coverage that can be changed, attempt to update the coverage with a bogus coverage limit. Validate
	 * that an error is returned.
	 * 5. For each coverage that cannot be changed, attempt to update the coverage with a valid coverage limit.
	 * Validate that an error is returned.
	 * 6. Try to update a coverage that doesn't exist and validate that an error is returned.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16984"})
	public void pas16984_validateCoverageConstraints(@Optional("VA") String state) {
		pas16984_validateCoverageConstraints(getPolicyType());
	}

	/**
	 * @author Maris Strazds
	 * @name
	 * @scenario1
	 * 1. Create CT Auto policy in PAS with 'Underinsured Motorist Conversion Coverage' (UIMCONV) = no
	 * 2. Create endorsement through service
	 * 3. Run viewEndorsement coverages service
	 * 4. Validate 'Underinsured Motorist Conversion Coverage'(UIMCONV) and 'Uninsured/Underinsured Motorist Bodily Injury' (UIMB) coverage
	 * @scenario2
	 * 1. Create CT Auto policy in PAS with 'Underinsured Motorist Conversion Coverage' (UIMCONV) = yes
	 * 2. Create endorsement through service
	 * 3. Run viewEndorsement coverages service
	 * 4. Validate 'Underinsured Motorist Conversion Coverage'(UIMCONV) and 'ninsured/Underinsured Motorist Bodily Injury With UIM Conversion Coverage' (UIMB) coverage
	 *
	 * @NOTE: Probably should be able to update these tests with Update story when it will be in sprint. Probably both tests then could be consolidated in one.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15265"})
	public void pas15265_WithOutUnderInsuredConversionCoverageCT(@Optional("CT") String state) {
		pas15265_UnderInsuredConversionCoverageCTBody(false);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15265"})
	public void pas15265_WithUnderInsuredConversionCoverageCT(@Optional("CT") String state) {
		pas15265_UnderInsuredConversionCoverageCTBody(true);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name View Coverages - PD Limits
	 * @scenario
	 * 1. Initiate quote creation. The effective date of the policy is
	 * TC1: 12/8/18 or LATER;
	 * TC2: 12/7/2018
	 * 2. Check PD limits in P&C page.
	 * 3. Bind the policy.
	 * 4. Create endorsement outside of PAS. The effective is 12/8/18 or LATER
	 * 5. Hit view coverages service, and check PD limits.
	 * 6. Go to the PAS, open endorsement data gather mode, and check if the PD limits the same.
	 * 7. Rate and Bind.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.IN, Constants.States.KS})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-20818", "PAS-20819"})
	public void pas20818_ViewPdCoverageLimits(@Optional("KS") String state) {

		pas20818_ViewPdCoverageLimitsBody("12/7/2018", true); //this is the date when we had more PD limits
		pas20818_ViewPdCoverageLimitsBody("12/8/2018", false); //that date or later, two PD limits should be not displaying anymore
	}

	/**
	 * @author Jovita Pukenaite
	 * @name View Coverage - UMPD and UIMPD (WV)
	 * @scenario
	 * 1. Create policy.
	 * 2. Hit view policy coverages service.
	 * 3. Check UMPD and UIMPD coverages
	 * 4. Create endorsement outside of PAS
	 * 5. Hit view endorsement coverages service
	 * 6. Check the same coverages again.
	 * 7. Update PD coverage.
	 * 8. Check the response again.
	 * 9. Rate and bind.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-17083"})
	public void pas17083_ViewUmpdAndUimpdCoverages(@Optional("WV") String state) {

		pas17083_ViewUmpdAndUimpdCoveragesBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name Maryland and Enhanced Coverage - Give me a label, don't let me edit
	 * @scenario1
	 * 1. Create a policy for MD.
	 * 2. Create an endorsement for the policy.
	 * 3. my policy has Enhanced UIM = False/No
	 * 3. Run the View Coverage service my label for UMBI is "Standard Uninsured/Underinsured Motorist Bodily Injury" and canChangeCoverage = false .
	 * 4. Go to Pas change my policy Enhanced UIM = True/Yes.
	 * 5. Run the View Coverage service my label for UMBI is "Standard Uninsured/Underinsured Motorist Bodily Injury"and canChangeCoverage = false .
	 * 6. and my label for UMPD is "Standard Uninsured Motorist Property Damage"
	 * 7.Update any Coverage and verify if update showing same label
	 * 8. PAS-22550 - check that EUIM description is as expected in transaction history
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-20835", "PAS-22550"})
	public void pas20835_mdAndEnhancedCoverage(@Optional("MD") String state) {
		pas20835_mdAndEnhancedCoverageBody(getPolicyType());
	}

/**
 * @author Megha Gubbala
 * @name Maryland and Enhanced Coverage - Give me a label, don't let me edit
 * @scenario1:
 * 1.create WV policy and updated BI
 * 2.verify  UM and UIM limits match my BI Limit
 * 3.updated BI so that my current PD LImit now exceeds my BI per accident limit
 * 4.Verify my PD Limit is updated to match my new BI per accident limit.
 * 5.have updated BI verify  updated PD  UMPD and UIMPD limits match my PD limit
 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-20292"})
	public void pas20292_updateCoverageBIPDWv(@Optional("WV") String state) {
		pas20292_updateCoverageBIPDWvBody(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update BI coverage (SD state only)
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Update BI, check the response.
	 * Note: All TC in the table:
	 *
	 * ||BI/UM/UIM  	||Update BI 	 || Expected UM/UIM||Av Limits UM/UIM||canChange UM/UIM
	 * ||25000/50000	||50000/100000   || 50000/100000   ||50000/100000	 ||FALSE
	 * ||50000/100000	||25000/50000    || 25000/50000    ||25000/50000 	 ||FALSE
	 * ||100000/300000  ||25000/50000    || 25000/50000    ||25000/50000 	 ||FALSE
	 * ||100000/300000	||300000/500000  || 100000/300000  ||100000/300000 + ||TRUE
	 * ||300000/500000	||250000/500000  || 250000/500000  ||100000/300000 + ||TRUE
	 * ||300000/500000	||1000000/1000000|| 300000/500000  ||100000/300000 + ||TRUE
	 * ||1000000/1000000||25000/50000    || 25000/50000	   ||25000/50000	 ||FALSE
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.SD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-22037"})
	public void pas22037_updateBiCoverage(@Optional("SD") String state) {

		pas22037_updateBiCoverageBody();
	}

	/**
	 * @author Bob Van
	 * @name View Coverages - UMPD (Update Comp/Coll)
	 * @scenario1
	 * 1. Create policy: UMBI/UIMBI, COMP, COLL other than no coverage
	 * 2. Create endorsement outside of PAS.
	 * 3. DXP Update Coverage: remove COLL
	 * 4. verify response: has UMPD, canChangeCoverage & customerDisplayed true, check availLimits
	 * 5. Run viewEndorsementCoverages service and validate that response is the same as updateCoverage response
	 * 6. Run viewChangeLog service and validate that response contains updated UMPD
	 * @scenario2
	 * 1. Create policy: UMBI/UIMBI, COMP, COLL other than no coverage
	 * 2. Create endorsement outside of PAS.
	 * 3. DXP Update Coverage: remove COLL
	 * 4. verify response: UMPD canChangeCoverage & customerDisplayed false, check availLimits
	 * 5. Run viewEndorsementCoverages service and validate that response is the same as updateCoverage response
	 * 6. Run viewChangeLog service and validate that response contains updated UMPD
	 * @scenario3
	 * 1. Create policy: UMBI/UIMBI, COMP other than no coverage, COLL no coverage
	 * 2. Create endorsement outside of PAS.
	 * 3. DXP Update Coverage: COLL =  500
	 * 4. verify response: has UMPD, canChangeCoverage & customerDisplayed false, check availLimits
	 * 5. Run viewEndorsementCoverages service and validate that response is the same as updateCoverage response
	 * 6. Run viewChangeLog service and validate that response contains updated UMPD
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.UT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-20306", "PAS-20305"})
	public void pas20306_viewUpdateCoveragesUmpdCompColl(@Optional("UT") String state) {
		pas20306_viewUpdateCoveragesUmpdCompCollBody(state, getPolicyType());
	}

	/**
	 * @author Sabra Domeika
	 * @name View Coverages Update coverage  - UMPD (Update Comp/Coll)
	 * @scenario1 Create policy in PAS
	 * 1. Create endorsement through service
	 * 2. Update UMPD through service and check response
	 * 3. Open PAS UI and validate Coverage tab
	 * 4. Update BI to 100000/300000 and COLLDED to -1 through service and check response
	 * 5. Update UMPD to 3500 and check response
	 * */
	@Parameters({"state"})
	@StateList(states = {Constants.States.NV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15344", "PAS-18198"})
	public void pas15344_ViewUpdateUmpdNV(@Optional("NV") String state) {
		pas15344_ViewUpdateUMPD_NV();
	}

	/**
	 * @author Megha Gubbala
	 * @name View Coverages Update coverage  - UMPD (Update Comp/Coll)
	 * @scenario1
	 * 1. Create policy with trailer Motor home and ppa vehicle
	 * 2. Create endorsement outside of PAS.
	 * 3. DXP View  Coverage: PPA and motor home should have customerDisplayed canChangeCoverage true
	 * 4. And Trailer customerDisplayed canChangeCoverage false
	 * */

	@Parameters({"state"})
	@StateList(states = {Constants.States.OR})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16112"})
	public void pas16112_umpdOregonViewCoverage(@Optional("OR") String state) {
		assertSoftly(softly ->
				pas16112_umpdOregonViewCoverageBody(softly, getPolicyType())
		);
	}

	/**
	 * @author Megha Gubbala
	 * @name View Coverages
	 * @scenario for AZ
	 * * @details
	 * 1. Create a AZ policy with trailer, Motorhome,golfcart
	 * 2. run view coverage service.
	 * 3. Verify can change coverage and customer display is false for coverage other than Comp and Coll
	 * */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-20344"})
	public void pas20344_trailerMotorHomeAndGolfCartViewCoverage(@Optional("AZ") String state) {
		assertSoftly(softly ->
				pas20344_trailerMotorHomeAndGolfCartViewCoverageBody(softly, getPolicyType())
		);
	}

	/**
	 * @author Maris Strazds
	 * @name Total Disability - South Dakota
	 * @scenario
	 * 1. Create policy with FNI, NI, NAFR Driver, Spouse (not NI), other driver than Spouse (not NI)
	 * 2. Create endorsement through service
	 * 3. Add another spouse through service
	 * 4. Run viewEndorsementCoverages service
	 * 5. Assert that Total Disability (TD) is available for all NIs and Spouse
	 * 6. Update TD for all available drivers and assert that it is updated
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.SD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-19625"})
	public void pas19625_TotalDisabilitySD(@Optional("SD") String state) {
		pas19625_TotalDisabilitySDBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Test BI and UMBI update when canChangeCoverage = TRUE for UMBI
	 * @NOTE FOR THIS TEST ANY STATE WHERE canChangeCoverage = TRUE for UMBI COULD BE USED. Test can be adapted to any state where UMBI is single coverage (not 2 separate)
	 *  ans state must have BI available limits be the same as UMBI available limits.
	 * @scenario
	 * 1. Create policy in PAS
	 * 2.Create endorsement through service
	 * 3. Update BI from higher Limit to lower limit (go through all available limits) ---> BI and UMBI is updated, UMBI availableLimits are not greater than BI limit
	 * 4. Update BI from lower Limit to higher limit (go through all available limits) ---> BI and UMBI is updated, UMBI availableLimits are not greater than BI limit
	 * 5. Update UMBI limit ---> UMBI is updated, BI limit is not updated
	 * 6. Check in PAS UI that limits are updated
	 * 7. Check transaction change log
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21363"})
	public void pas21363_BIAndUMBIAndCanChangeTrue(@Optional("VA") String state) {
		pas21363_BIAndUMBIAndCanChangeTrueBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Test BI and UMBI update when canChangeCoverage = FALSE for UMBI
	 * @scenario
	 * @NOTE FOR THIS TEST ANY STATE WHERE canChangeCoverage = FALSE for UMBI COULD BE USED. Test can be adapted to any state where UMBI is single coverage (not 2 separate)
	 *  ans state must have BI available limits be the same as UMBI available limits.
	 * 1. Create policy in PAS
	 * 2. Create endorsement through service
	 * 3. Update BI from higher Limit to lower limit (go through all available limits) ---> BI and UMBI is updated, UMBI availableLimits are not greater than BI limit
	 * 4. Update BI from lower Limit to higher limit (go through all available limits) ---> BI and UMBI is updated, UMBI availableLimits are not greater than BI limit
	 * 5. Update UMBI limit ---> UMBI is not updated, BI limit is not updated
	 * 6. Check in PAS UI that limits are/are not updated
	 * 7. Check transaction change log
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.KS})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21363"})
	public void pas21363_BIAndUMBIAndCanChangeFalse(@Optional("KS") String state) {
		pas21363_BIAndUMBIAndCanChangeFalseBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Test PD and UMPD update when canChangeCoverage = TRUE for UMPD
	 * @NOTE FOR THIS TEST ANY STATE WHERE canChangeCoverage = FALSE for UMPD COULD BE USED. Test can be adapted to any state where PD available limits are the same as UMPD available limits.
	 * @scenario
	 * 1. Create policy in PAS
	 * 2.Create endorsement through service
	 * 3. Update PD from higher Limit to lower limit (go through all available limits) ---> PD and UMPD is updated, UMPD availableLimits are not greater than PD limit
	 * 4. Update PD from lower Limit to higher limit (go through all available limits) ---> PD and UMPD is updated, UMPD availableLimits are not greater than PD limit
	 * 5. Update UMPD limit ---> UMPD is updated, PD limit is not updated
	 * 6. Update BI to lower limit so that PD limit and available limits also are updated ---> PD is updated, PD availableLimits are updated, UMPD is updated. UMPD available limits are updated.
	 * 7. Update BI to higher limit so that PD limit and available limits also are updated ---> PD is not updated, PD availableLimits are updated, UMPD is not updated. UMPD available limits are not updated.
	 * 8. Check in PAS UI that limits are updated
	 * 9. Check transaction change log
	 * @NOTE: functionality related with pas15824_UmpdDelimiter (needed to update)
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21364"})
	public void pas21364_PDAndUMPDandCanChangeTrue(@Optional("VA") String state) {
		pas21364_PDAndUMPDAndCanChangeTrueBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Test PD and UMPD update when canChangeCoverage = FALSE for UMPD
	 * @scenario
	 * @NOTE FOR THIS TEST ANY STATE WHERE canChangeCoverage = FALSE for UMPD COULD BE USED. Test can be adapted to any state where PD available limits are the same as UMPD available limits.
	 * 1. Create policy in PAS
	 * 2.Create endorsement through service
	 * 3. Update PD from higher Limit to lower limit (go through all available limits) ---> PD and UMPD is updated, UMPD availableLimits are not greater than PD limit
	 * 4. Update PD from lower Limit to higher limit (go through all available limits) ---> PD and UMPD is updated, UMPD availableLimits are not greater than PD limit
	 * 5. Update UMPD limit ---> UMPD is not updated, PD limit is not updated
	 * 6. Update BI to lower limit so that PD limit and available limits also are updated ---> PD is updated, PD availableLimits are updated, UMPD is updated. UMPD available limits are updated.
	 * 7. Update BI to higher limit so that PD limit and available limits also are updated ---> PD is not updated, PD availableLimits are updated, UMPD is not updated. UMPD available limits are not updated.
	 * 8. Check in PAS UI that limits are updated
	 * 9. Check transaction change log
	 * @NOTE: functionality related with pas20292_updateCoverageBIPDWv (needed to update)
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21364"})
	public void pas21364_PDAndUMPDAndCanChangeFalse(@Optional("WV") String state) {
		pas21364_PDAndUMPDAndCanChangeFalseBody();
	}

	/**
	 * @author Maris Strazds
	 * @name Test PD and UMPD update when canChangeCoverage = TRUE for UMPD and UIMPD
	 * @NOTE FOR THIS TEST ANY STATE WHERE canChangeCoverage = FALSE for UMPD COULD BE USED. Test can be adapted to any state where PD available limits are the same as UMPD available limits.
	 * @scenario
	 * 1. Create policy in PAS
	 * 2.Create endorsement through service
	 * 3. Update PD from higher Limit to lower limit (go through all available limits) ---> PD and UMPD is updated, UMPD availableLimits are not greater than PD limit
	 * 4. Update PD from lower Limit to higher limit (go through all available limits) ---> PD and UMPD is updated, UMPD availableLimits are not greater than PD limit
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.DC})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15281"})
	public void pas15281_UMPDAndUIMPDAndCanChangeTrue(@Optional("DC") String state) {
		pas15281_UMPDAndUIMPDAndCanChangeTrueBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Update coverage - UM and UIM - DC
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS.
	 * 3. Hit view coverage service.
	 * 4. Update BI from higher Limit to lower limit (go through all available limits)
	 * 5. Check UMBI and UIMBI available limits.
	 * 6. Update BI from higher Limit to lower limit (go through all available limits)
	 * 7. Check UMBI and UIMBI available limits.
	 * 8. Update UMBI limit to be less than my BI limit.
	 * 9. Check rate service, if any error is not displaying.
	 * 10. Check if UM and UIM were updated with BI
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.DC})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15313"})
	public void pas15313_updateBiCoverageCheckUMandUIM(@Optional("DC") String state) {

		pas15313_updateBiCoverageCheckUMandUIMbody();
	}

	/**
	 * @author RVanover
	 * @name View/Update PIP Coverage
	 * @scenario for DC
	 * * @details
	 * 1. Create a DC endorsement outside PAS.
	 * 2. Run DXP view coverage service.
	 * 3. Verify PIP coverage criteria.
	 * 4. Update PIP coverages from DXP.
	 * 5. Verify updates to PIP coverages in DXP, PAS UI & change log.
	 * */
	@Parameters({"state"})
	@StateList(states = {Constants.States.DC})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15288"})
	public void pas15288_ViewUpdateCoveragePIPCoverage(@Optional("DC") String state) {
		pas15288_ViewUpdateCoveragePIPCoverageBody();
	}

}

