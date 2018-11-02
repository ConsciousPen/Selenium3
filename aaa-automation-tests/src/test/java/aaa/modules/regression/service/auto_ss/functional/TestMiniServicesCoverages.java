package aaa.modules.regression.service.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.google.common.collect.ImmutableList;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.dtoDxp.Coverage;
import aaa.helpers.rest.dtoDxp.PolicyCoverageInfo;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.TestMiniServicesCoveragesHelper;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.main.enums.CoverageInfo;
import aaa.main.enums.CoverageLimits;

public class TestMiniServicesCoverages extends TestMiniServicesCoveragesHelper {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

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
	 * @author Megha Gubbala, Maris Strazds
	 * @name Verify Policy and Vehicle level coverages Order + Driver level coverages order for states where we have requirements
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.IN, Constants.States.KS,
			Constants.States.MD, Constants.States.NV, Constants.States.NJ, Constants.States.OH, Constants.States.OR, Constants.States.CT, Constants.States.KY, Constants.States.SD, Constants.States.KS,
			Constants.States.CT, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-17646", "PAS-19013", "PAS-19042", "PAS-19016", "PAS-19024", "PAS-19044", "PAS-18202"})
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
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"pas11654"})
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

		pas17642_UpdateCoverageADBBody(getPolicyType());

	}

	/**
	 * @author Megha Gubbala
	 * @name UM and UIM Coverage - Kentucky
	 * @scenario1
	 * 1. Create policy for Ky
	 * 2. Run view coverages service. check  UM and UIM coverages are separate
	 * and UM Coverage has cusstomerDisplayed = true
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
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		TestData td = getPolicyDefaultTD();
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_FNI_AFR_Excluded_NAFR").getTestDataList("DriverTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();

		td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
				, AutoSSMetaData.PremiumAndCoveragesTab.BASIC_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel()), "contains=$10,000")
				.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
						, AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_PERSONAL_INJURY_PROTECTION_COVERAGE.getLabel()), "contains=$30,000")
				.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName()
						, AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE.getLabel()), "contains=$500");

		String policyNumber = openAppAndCreatePolicy(td);

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//validate view endorsement coverages
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());

		assertSoftly(softly -> {

			Map<String, Coverage> mapPIPCoveragesActual = getPIPCoverages(viewEndorsementCoverages.policyCoverages);

			Map<String, Coverage> mapPIPCoveragesExpected = new LinkedHashMap<>();
			mapPIPCoveragesExpected.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP));
			mapPIPCoveragesExpected.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_30000));
			mapPIPCoveragesExpected.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).changeLimit(CoverageLimits.DED_500));
			mapPIPCoveragesExpected.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, mapPIPCoveragesExpected, mapPIPCoveragesActual, null);

			//get drivers with TORT coverage available
			String driverWithTORTOid1 = getCoverage(viewEndorsementCoverages.driverCoverages, "TORT").getAvailableDrivers().get(0);
			String driverWithTORTOid2 = getCoverage(viewEndorsementCoverages.driverCoverages, "TORT").getAvailableDrivers().get(1);
			assertThat(getCoverage(viewEndorsementCoverages.driverCoverages, "TORT").getAvailableDrivers().size()).as("In this test only 2 drivers are expected to have TORT available.").isEqualTo(2);

			//AC#1: update Basic PIP to No Coverage
			validateTORTPrecondition_pas19195(policyNumber, true);
			PolicyCoverageInfo updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.BPIP.getCode(), CoverageLimits.COV_0.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> mapPipNoCoverage = new LinkedHashMap<>();
			mapPipNoCoverage.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).changeLimit(CoverageLimits.COV_0));
			mapPipNoCoverage.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).disableCanChange().disableCustomerDisplay());
			mapPipNoCoverage.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).disableCanChange().disableCustomerDisplay());
			mapPipNoCoverage.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).changeLimit(CoverageLimits.COV_10000).disableCanChange());

			validatePIPCoverages_KY(softly, policyNumber, mapPipNoCoverage, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#2: update Basic PIP to $10,000
			validateTORTPrecondition_pas19195(policyNumber, true);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.BPIP.getCode(), CoverageLimits.COV_10000.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> mapPipToTenThous = new LinkedHashMap<>();
			mapPipToTenThous.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP));
			mapPipToTenThous.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP));
			mapPipToTenThous.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			mapPipToTenThous.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, mapPipToTenThous, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#5: update one or more drivers to be Reject Limit to Sue = No
			validateTORTPrecondition_pas19195(policyNumber, true);
			updateCoverageResponse = updateTORTCoverage(policyNumber, ImmutableList.of(driverWithTORTOid2));
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			//PIP doesn't change
			Map<String, Coverage> pipDoesntChange = new LinkedHashMap<>();
			pipDoesntChange.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipDoesntChange.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP));
			pipDoesntChange.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			pipDoesntChange.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());
			validateTORTPrecondition_pas19195(policyNumber, false);
			validatePIPCoverages_KY(softly, policyNumber, pipDoesntChange, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#3: validate update endorsement coverages (ADDPIP) to other than 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.ADDPIP.getCode(), CoverageLimits.COV_20000.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> addPipOtherThenZero = new LinkedHashMap<>();
			addPipOtherThenZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			addPipOtherThenZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_20000));
			addPipOtherThenZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			addPipOtherThenZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validateTORTPrecondition_pas19195(policyNumber, false);
			validatePIPCoverages_KY(softly, policyNumber, addPipOtherThenZero, mapPIPCoveragesActual, updateCoverageResponse);

			//Update back to 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.ADDPIP.getCode(), CoverageLimits.COV_0.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> addPipEqualToZero = new LinkedHashMap<>();
			addPipEqualToZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			addPipEqualToZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP));
			addPipEqualToZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			addPipEqualToZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());
			validatePIPCoverages_KY(softly, policyNumber, addPipEqualToZero, mapPIPCoveragesActual, updateCoverageResponse);

			//Update back to to other than 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.ADDPIP.getCode(), CoverageLimits.COV_40000.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> pipFortyThous = new LinkedHashMap<>();
			pipFortyThous.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipFortyThous.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			pipFortyThous.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			pipFortyThous.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());
			validatePIPCoverages_KY(softly, policyNumber, pipFortyThous, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#3: validate update endorsement coverages (PIPDED) to other than 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.PIPDED.getCode(), CoverageLimits.DED_250.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> pipdedMoreZero = new LinkedHashMap<>();
			pipdedMoreZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipdedMoreZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			pipdedMoreZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).changeLimit(CoverageLimits.DED_250));
			pipdedMoreZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, pipdedMoreZero, mapPIPCoveragesActual, updateCoverageResponse);

			//Update back to 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.PIPDED.getCode(), CoverageLimits.DED_0.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> pipdedBackToZero = new LinkedHashMap<>();
			pipdedBackToZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipdedBackToZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			pipdedBackToZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			pipdedBackToZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, pipdedBackToZero, mapPIPCoveragesActual, updateCoverageResponse);

			//Update back to other than 0
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateCoverage(policyNumber, CoverageInfo.PIPDED.getCode(), CoverageLimits.DED_1000.getLimit());
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> pipdedMoreThenZero = new LinkedHashMap<>();
			pipdedMoreThenZero.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			pipdedMoreThenZero.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			pipdedMoreThenZero.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).changeLimit(CoverageLimits.DED_1000));
			pipdedMoreThenZero.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, pipdedMoreThenZero, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#4: update all drivers to be Reject Limit to Sue = YES
			validateTORTPrecondition_pas19195(policyNumber, false);
			updateCoverageResponse = updateTORTCoverage(policyNumber, ImmutableList.of(driverWithTORTOid1, driverWithTORTOid2));
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			//values stays the same as above
			Map<String, Coverage> rejectLimitEqualYes = new LinkedHashMap<>();
			rejectLimitEqualYes.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP));
			rejectLimitEqualYes.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP).changeLimit(CoverageLimits.COV_40000));
			rejectLimitEqualYes.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED).changeLimit(CoverageLimits.DED_1000));
			rejectLimitEqualYes.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, rejectLimitEqualYes, mapPIPCoveragesActual, updateCoverageResponse);

			//AC#6: update one or more drivers to be Reject Limit to Sue = No
			//update BPIP to no coverage (this is AC#1 functionality again)
			updateCoverage(policyNumber, CoverageInfo.BPIP.getCode(), CoverageLimits.COV_0.getLimit());

			//update one or more drivers to be Reject Limit to Sue = No
			validateTORTPrecondition_pas19195(policyNumber, true);
			updateCoverageResponse = updateTORTCoverage(policyNumber, ImmutableList.of(driverWithTORTOid1));
			mapPIPCoveragesActual = getPIPCoverages(updateCoverageResponse.policyCoverages);

			Map<String, Coverage> bpipToNoCoverage = new LinkedHashMap<>();
			bpipToNoCoverage.put(CoverageInfo.BPIP.getCode(), Coverage.create(CoverageInfo.BPIP).removeAvailableLimit(CoverageLimits.COV_0).disableCanChange());
			bpipToNoCoverage.put(CoverageInfo.ADDPIP.getCode(), Coverage.create(CoverageInfo.ADDPIP));
			bpipToNoCoverage.put(CoverageInfo.PIPDED.getCode(), Coverage.create(CoverageInfo.PIPDED));
			bpipToNoCoverage.put(CoverageInfo.GPIP.getCode(), Coverage.create(CoverageInfo.GPIP).disableCanChange().disableCustomerDisplay());

			validatePIPCoverages_KY(softly, policyNumber, bpipToNoCoverage, mapPIPCoveragesActual, updateCoverageResponse);

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
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
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		//validate view endorsement coverages
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);

		Coverage pipExpected = Coverage.createWithCdAndDescriptionOnly(CoverageInfo.PIP_KS_4500);
		Coverage medexpExpected = Coverage.create(CoverageInfo.MEDEXP_KS);
		Coverage worklossExpected = Coverage.create(CoverageInfo.WORKLOSS_KS_4500).disableCanChange();

		assertSoftly(softly -> {
			Coverage pipCoverageActual = getCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIP_KS_4500.getCode());
			softly.assertThat(pipCoverageActual).isEqualToIgnoringGivenFields(pipExpected, "subCoverages");

			List<Coverage> pipSubCoveragesActual = getCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PIP_KS_4500.getCode()).getSubCoverages();
			softly.assertThat(getCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_KS.getCode())).isEqualToComparingFieldByField(medexpExpected);
			softly.assertThat(getCoverage(pipSubCoveragesActual, CoverageInfo.WORKLOSS_KS_4500.getCode())).isEqualToComparingFieldByField(worklossExpected);
			validatePIPInUI_pas15358(softly, getCoverage(pipSubCoveragesActual, CoverageInfo.MEDEXP_KS.getCode()));
			validatePIPSubCoveragesThatDoesntChange_pas15358(pipSubCoveragesActual);

			//Update PIP (MEDPIP) coverage to 10000
			pipSubCoveragesActual = validateUpdatePIP_pas15359(softly, policyNumber, CoverageLimits.COV_10000);
			validatePIPSubCoveragesThatDoesntChange_pas15358(pipSubCoveragesActual);

			//Update PIP (MEDPIP) coverage to 25000
			pipSubCoveragesActual = validateUpdatePIP_pas15359(softly, policyNumber, CoverageLimits.COV_25000);
			validatePIPSubCoveragesThatDoesntChange_pas15358(pipSubCoveragesActual);

			//Update PIP (MEDPIP) coverage to 4500
			pipSubCoveragesActual = validateUpdatePIP_pas15359(softly, policyNumber, CoverageLimits.COV_4500);
			validatePIPSubCoveragesThatDoesntChange_pas15358(pipSubCoveragesActual);
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
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
	public void pas15265_WithOutUnderInsuredConversionCoverageCTBody(@Optional("CT") String state) {
		pas15265_UnderInsuredConversionCoverageCTBody(false);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15265"})
	public void pas15265_WithUnderInsuredConversionCoverageCTBody(@Optional("CT") String state) {
		pas15265_UnderInsuredConversionCoverageCTBody(true);
	}
}


