package aaa.modules.regression.service.auto_ss.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
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
	@StateList(states = {Constants.States.AZ, Constants.States.ID, Constants.States.KY, Constants.States.PA, Constants.States.SD, Constants.States.UT, Constants.States.WV, Constants.States.MT, //applicable states for PAS-15254
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
	 * @author MeghaGubbala
	 * @name Verify Policy and Vehicle level coverages Order
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.IN, Constants.States.KS,
			Constants.States.MD, Constants.States.NV, Constants.States.NJ, Constants.States.OH, Constants.States.OR, Constants.States.CT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-17646"})
	public void pas17646_OrderOfCoverage(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas17646_OrderOfCoverageBody(state, softly)
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
	 * PAS:18202:
	 * 1.Verify order of coverages
	 * Bodily Injury
	 *  Property Damage
	 *  Uninsured/Underinsured Motorist Bodily Injury
	 *  Enhanced UM
	 *  Uninsured Motorist Property Damage
	 *  Medical Payments
	 *  Personal Injury Protection
	 * 	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas11654, pas18202"})
	public void pas11654_MDEnhancedUIMBICoverage(@Optional("MD") String state) {
		assertSoftly(softly ->
				pas11654_MDEnhancedUIMBICoverageBody(softly, getPolicyType())
		);
	}
}


