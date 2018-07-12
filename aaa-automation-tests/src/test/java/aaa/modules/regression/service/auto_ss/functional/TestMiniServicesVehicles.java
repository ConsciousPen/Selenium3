package aaa.modules.regression.service.auto_ss.functional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelper;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMiniServicesVehicles extends TestMiniServicesVehiclesHelper {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Email change through service
	 * @scenario 1. Create customer
	 * 2. Create a policy with a vehicle with UBI
	 * 3. Check Green Button endorsement is not allowed. There is a VehicleRules error about UBI
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-8275"})
	public void pas8275_vinValidate(@Optional("") String state) {
		assertSoftly(softly ->
				pas8275_vinValidateCheck(softly, getPolicyType())
		);
	}

	/**
	 * @author Megha Gubbala
	 * @name Check dxp server To add vehicle.
	 * Create a Policy
	 * Create a pended endorsement
	 * Hit "add-vehicle" dxp server.
	 * Pass Pearches date and VIN to the service
	 * Go to pas open pended endorsement and go to vehicle tab
	 * Check the new vehicle is added with the vin number.
	 * @scenario
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7082", "PAS-7145", "PAS-11621"})
	public void pas7082_AddVehicle(@Optional("AZ") String state) {

		pas7082_AddVehicle(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check Vehicle status using view vehicle service/ check endorsement rate service
	 * @scenario 1. Create active policy with one vehicle.
	 * 2. Create Endorsement using dxp server.
	 * 3. Hit rate endorsement service.
	 * 4. Check premium amount in service and UI, check the endorsement status.
	 * 5. Add new vehicle.
	 * 6. Hit the view vehicle service.
	 * 7. Check the vehicles status.
	 * 8. Edit endorsement, add usage for new vehicle. Save it.
	 * 9. Hit rating service.
	 * 10. Check if premium amount from ui and from service is the same. Check endorsement status again.
	 * 11. Bind pended endorsement.
	 * 12. Hit the view vehicle service again.
	 * 13. Check if Pended vehicle status was changed.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9490", "PAS-479"})
	public void pas9490_ViewVehicleServiceCheckVehiclesStatus(@Optional("AZ") String state) {

		pas9490_ViewVehicleServiceCheckVehiclesStatus();
	}

	/**
	 * @author Megha Gubbala
	 * @name Check Vehicle vehicle service
	 * @scenario 1.Create a policy with 4 vehicles (1.PPA 2.PPA 3. Conversion Van 4. Trailer )
	 * 2.hit view vehicle service
	 * 3.get a response in right sequence
	 * 4.perform endorsement
	 * 5.add new vehicle (that will be pending)
	 * 6.hit view vehicle service
	 * 7.validate response shows pending vehicle first.
	 * Added Pas 12244
	 * Add 2 PPA vehicle
	 * hit view vehicle service on pended endorsement
	 * verify order of vehicle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-10449", "PAS-12244", "PAS-14593"})
	public void pas10449_ViewVehicleServiceOrderOfVehicle(@Optional("VA") String state) {

		pas10449_ViewVehicleServiceCheckOrderOfVehicle(getPolicyType(), state);
	}

	/**
	 * @author Megha Gubbala
	 * @name Verify update vehicle service
	 * @scenario 1. Create active policy with one vehicle.
	 * 2. hit view vehicle service.
	 * 3. get OID from view vehicle service.
	 * 4. hit update vehicle service.
	 * 5. verify on Pas ui that vehicle updated with the provided information
	 * 6. hit view vehicle service again to verify vehicle information is updated.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9610"})
	public void pas9610_UpdateVehicleService(@Optional("VA") String state) {

		pas9610_UpdateVehicleService();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Business
	 * @scenario 1. Create active policy
	 * 2. Add a vehicle
	 * 3. Update vehicle, set usage = Business
	 * Error expected
	 * 4. Rate the endorsement
	 * Error expected
	 * 5. Bind the endorsement
	 * Error expected
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7147"})
	public void pas7147_VehicleUpdateBusiness(@Optional("VA") String state) {

		pas7147_VehicleUpdateBusinessBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Registered Owner
	 * @scenario 1. Create active policy
	 * 2. Add a vehicle
	 * 3. Update vehicle, set usage = Business
	 * Error expected
	 * 4. Rate the endorsement
	 * Error expected
	 * 5. Bind the endorsement
	 * Error expected
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-7147"})
	public void pas7147_VehicleUpdateRegisteredOwner(@Optional("VA") String state) {

		pas7147_VehicleUpdateRegisteredOwnerBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Registered Owner
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle with Ownership info = Leased or Financed
	 * 3.1) check field values in ui, check address is validated
	 * 5) rate
	 * 6) bind
	 * 7) do an endorsement in PAS, rate bind
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11618"})
	public void pas11618_UpdateVehicleLeasedInfo(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas11618_UpdateVehicleLeasedFinancedInfoBody(softly, "LSD")
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Registered Owner
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle
	 * 4) update vehicle adding Ownership info = Leased or Financed
	 * 4.1) check field values in ui, check address is validated
	 * 5) rate
	 * 6) bind
	 * 7) do an endorsement in PAS, rate bind
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-11618"})
	public void pas11618_UpdateVehicleFinancedInfo(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas11618_UpdateVehicleLeasedFinancedInfoBody(softly, "FNC")
		);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13252"})
	public void pas13252_UpdateVehicleGaragingAddressProblem(@Optional("VA") String state) {
		pas13252_UpdateVehicleGaragingAddressProblemBody();
	}

	/**
	 * @author Megha Gubbala
	 * 1. create a policy with 2 ppa,1 conversion-van and 1 motor vehicle
	 * 2. create pended endorsement.
	 * 2. run view vehicle service and get OID of PPA vehicle
	 * 3. run delete vehicle service and delete vehicle using Oid
	 * 4. verify response status should be pending removal
	 * 5. rate endorsement
	 * 6. bind endorsement
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-488"})
	public void pas488_VehicleDelete(@Optional("VA") String state) {

		pas488_VehicleDeleteBody(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check Duplicate VINS and the Add Vehicle Service
	 * @scenario 1. Create policy with Two vehicles.
	 * 2. Create endorsement outside of PAS.
	 * 3. Try Add Vehicle with the same VIN witch already exist in the policy.
	 * 4. Check Error about duplicate VIN.
	 * 5. Add new vehicle with new VIN. Check the status.
	 * 6. Try add the same vehicle one more time.
	 * 7. Check if error is displaying.
	 * Start PAS-11005
	 * 8. Try add to expensive vehicle.
	 * 9. Check if error is displaying.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-502", "PAS-11005"})
	public void pas502_DuplicateVinAddVehicleService(@Optional("VA") String state) {

		pas502_CheckDuplicateVinAddVehicleService(getPolicyType());
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15483"})
	public void pas15483_deleteOriginalVehicle(@Optional("VA") String state) {
		pas15483_deleteOriginalVehicleBody();
	}

	/**
	 * @author Megha Gubbala
	 * 1. create a policy with 2 ppa,1 conversion-van and 1 motor vehicle
	 * 2. hit view vehicle servise to get order of all active vehicles
	 * 3. create pended endorsement.
	 * 2. add  one PPA vehicle through DXP servise.
	 * 3. run delete vehicle service and delete vehicle using Oid
	 * 4. verify response status should be pending removal
	 * 5. then hit view vehicle servise again to get proper order of vehicle
	 * 6. pending removal vehicle should be the 1st and then pending added vehicle and then rest of order will be same.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12246"})
	public void pas12246_ViewVehiclePendingRemoval(@Optional("AZ") String state) {

		pas12246_ViewVehiclePendingRemovalService(getPolicyType());
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation of E2E flow in DXP
	 * @scenario 1. Create a policy
	 * 2. Add maximum number of vehicle
	 * 3. Check when the last vehicle is added, the response contains canAddVehicle = false
	 * 4. Try to add 1 more vehicle
	 * 5. Check there is an error message
	 * 6. Rate, Bind
	 * 7. Start new endorsement
	 * 8. add 1 more vehicle - check error
	 * 9. remove a vehicle
	 * 10. add a vehicle
	 * 11. Check when the last vehicle is added, the response contains canAddVehicle = false
	 * 12. rate, bind
	 * 13. Create 1 more endorsement in UI
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9546"})
	public void pas9546_maxVehicles(@Optional("VA") String state) {
		pas9546_maxVehiclesBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name no error about garaging address being different when binding endorsement
	 * @scenario 1. see script body
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14501"})
	public void pas14501_garagingDifferent(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14501_garagingDifferentBody(state, softly)
		);
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14952"})
	public void pas14952_EndorsementStatusResetForVehRatingFactors(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14952_EndorsementStatusResetForVehRatingFactorsBody(state, softly)
		);
	}

	@Parameters({"state"})
	//@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14952", "PAS-15152"})
	public void pas14952_StatusResetsForNewlyAddedVehicle(@Optional("VA") String state) {
		pas14952_StatusResetsForNewlyAddedVehicleBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transaction Information For Endorsements outside of PAS - AddVehicle
	 * @scenario 1. Create policy.
	 * 2. Start do endorsement outside of PAS.
	 * 3. Hit "Transaction History Service". Check if response is empty.
	 * 4. Add Vehicle.
	 * 5. Hit "Transaction History Service". Check new vehicle info.
	 * 6. Update "Usage".
	 * 7. Rate endorsement
	 * 8. Add one more vehicle.
	 * 9. Hit "Transaction History Service". Check new vehicle info.
	 * 10. Bind endorsement.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9493"})
	public void pas9493_TransactionInformationForEndorsementsAddVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas9493_TransactionInformationForEndorsementsAddVehicleBody(getPolicyType())
		);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle coverages after replacing the vehicle
	 * @scenario 1.Create a policy with 1 leased vehicle, 1 New Car coverage and 2 drivers
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicles
	 * 4.check coverages:
	 * loan/lease coverage = No Coverage, not shown, cant edit
	 * new car coverage = No Coverage, not shown, cant edit
	 * the rest coverages have the same values as the original vehicle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleKeepAssignmentsKeepCoverages(@Optional("VA") String state) {

		pas13920_ReplaceVehicleKeepAssignmentsKeepCoveragesBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle coverages after replacing the vehicle
	 * @scenario 1.Create a policy with 1 leased vehicle, 1 New Car coverage and 2 drivers
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicles with KeepAssignments, Keep Coverages
	 * 4.check coverages:
	 * loan/lease coverage = No Coverage, not shown, cant edit
	 * new car coverage = No Coverage, not shown, cant edit
	 * the rest coverages have the same values as the original vehicle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleNoAssignmentsKeepCoverages(@Optional("VA") String state) {

		pas13920_ReplaceVehicleNoAssignmentsKeepCoveragesBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle coverages after replacing the vehicle
	 * @scenario 1.Create a policy with 1 leased vehicle, 1 New Car coverage and 2 drivers
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicles with KeepAssignments, Don't Keep Coverages
	 * 4.check coverages:
	 * loan/lease coverage = No Coverage, not shown, cant edit
	 * new car coverage = No Coverage, not shown, cant edit
	 * the rest coverages are set to default values as if it were new car
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleKeepAssignmentsNoCoverages(@Optional("VA") String state) {

		pas13920_ReplaceVehicleKeepAssignmentsNoCoveragesBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle Assignments after replacing the vehicle
	 * @scenario 1.Create a policy with 1 leased vehicle, 1 New Car coverage and one driver
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicles with KeepAssignments, Don't Keep Coverages
	 * 4.check Assignments
	 * one of vehicles is Primary, the other one is Occasional
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleKeepAssignmentsOneDriver(@Optional("VA") String state) {

		pas13920_ReplaceVehicleKeepAssignmentsOneDriverBody(true);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle Assignments after replacing the vehicle
	 * @scenario 1.Create a policy with 1 leased vehicle, 1 New Car coverage and one driver
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicles with Don't KeepAssignments, Don't Keep Coverages
	 * 4.check Assignments
	 * one of vehicles is Primary, the other one is Occasional
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleDontKeepAssignmentsOneDriver(@Optional("VA") String state) {

		pas13920_ReplaceVehicleKeepAssignmentsOneDriverBody(false);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check replace with KeepAssignments is allowed in the state with no Assignments
	 * @scenario 1.Create a policy with 1 vehicle and one driver
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicles with KeepAssignments, Don't Keep Coverages
	 * ??????????? - result is not clear. Maybe this test can be removed.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleKeepAssignmentsOneDriverAz(@Optional("AZ") String state) {
		//BUG PAS-16113 Replace Vehicle and Driver Assignment - when a state doesn't have driver assignment
		pas13920_ReplaceVehicleKeepAssignmentsOneDriverAzBody(true);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check replace with Don't KeepAssignments is allowed in the state with no Assignments
	 * @scenario 1.Create a policy with 1 vehicle and one driver
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicles with KeepAssignments, Don't Keep Coverages
	 * 4. check replacement is successful
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleDontKeepAssignmentsOneDriverAz(@Optional("AZ") String state) {

		pas13920_ReplaceVehicleKeepAssignmentsOneDriverAzBody(false);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle coverages after replacing the vehicle
	 * @scenario 1.Create a policy with 1 leased vehicle, 1 drivers
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicle with Don't KeepCoverages
	 * 4.check coverages:
	 * loan/lease coverage = No Coverage, not shown, cant edit
	 * new car coverage = No Coverage, not shown, cant edit
	 * the rest coverages are defaulted
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleDontKeepCoveragesOneDriverOneVehicle(@Optional("VA") String state) {

		pas13920_ReplaceVehicleDontKeepCoveragesOneDriverOneVehicleBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle coverages after replacing the vehicle
	 * @scenario 1.Create a policy with 1 leased vehicle, 1 drivers
	 * 2.Check coverages
	 * 3.Start an endorsement, Replace vehicle with KeepCoverages
	 * 4.check coverages:
	 * loan/lease coverage = No Coverage, not shown, cant edit
	 * new car coverage = No Coverage, not shown, cant edit
	 * the rest coverages have the same values as the original vehicle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleKeepCoveragesOneDriverOneVehicle(@Optional("VA") String state) {

		pas13920_ReplaceVehicleKeepCoveragesOneDriverOneVehicleBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle available actions for vehicle
	 * @scenario 1. the only PPA vehicle can be only replaced
	 * 2. NON-PPA vehicle can be only removed
	 * 3. PendingAdd vehicle has no available actions
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12175"})
	public void pas12175_RemoveReplaceAllVehicles(@Optional("VA") String state) {

		pas12175_RemoveReplaceAllVehiclesBody();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Check Vehicle available actions for vehicle
	 * @scenario 1. Waive Liability vehicle can only be removed
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12175"})
	public void pas12175_RemoveReplaceWaiveLiability(@Optional("VA") String state) {

		pas12175_RemoveReplaceWaiveLiabilityBody();
	}
}


