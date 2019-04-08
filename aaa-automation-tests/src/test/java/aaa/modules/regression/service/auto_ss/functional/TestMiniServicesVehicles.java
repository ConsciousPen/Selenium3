package aaa.modules.regression.service.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelper;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.util.Random;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

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
	 * @name Check dxp service To add vehicle.
	 * Create a Policy
	 * Create a pended endorsement
	 * Hit "add-vehicle" service.
	 * send purchase date and VIN to the service as a request
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
	 * @name Check View Vehicle service
	 * @scenario 1.Create a policy with 4 vehicles (1.PPA 2.PPA 3. Conversion Van 4. Trailer )
	 * 2.hit view vehicle service
	 * 3.get a response in correct order.
	 * 4. perform endorsement on the Policy
	 * 5.add new vehicle (that will be pending)
	 * 6.hit view vehicle service
	 * 7.validate response and response should have pending vehicle first.
	 * @megha Gubbala Added Pas 12244
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
	 * @author Maris Strazds
	 * @name Check Duplicate VINs when adding or replacing Vehicle with status "pendingRemove"
	 * @scenario
	 * 1. Create a policy in PAS
	 * 2. Create an endorsement through service
	 * 3. Run Remove Vehicle service for one of the Vehicles ---> Vehicle status is changed to "pendingRemove"
	 * 4. Run Add Vehicle Service with the same VIN as vehicle with ""pendingRemove"" status --->
	 *          Error ""Each vehicle must have a unique Vehicle Identification Number (200031)"" is provided
	 *          AND The vehicle is not added/replaced to the pended endorsement
	 * 5. Run View Endorsement Drivers service and validate that vehicle is not added
	 * 6. Run Replace Vehicle Service with the same VIN as vehicle with "pendingRemove" status --->
	 *          Error ""Each vehicle must have a unique Vehicle Identification Number (200031)"" is provided
	 *          AND The vehicle is not added/replaced to the pended endorsement
	 * 7. Run View Endorsement Drivers service and validate that vehicle is not replaced
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16577"})
	public void pas16577_DuplicateVinAddVehicleServicePendingRemove(@Optional("VA") String state) {
		pas16577_DuplicateVinAddVehicleServicePendingRemoveBody();
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
	 *
	 * @author Maris Strazds
	 * @name validate that revert option is available for removed vehicles ("PAS-18672", "PAS-18670")
	 * @scenario
	 * 1. Retrieve policy with 8 vehicles (max count)
	 * 2. Remove 1 vehicle and validate that there is 'revert' option in response
	 * 3. Run viewEndorsementVehicles and validate that there is 'revert' option for removed vehicle
	 * 4. Add vehicle
	 * 5. Run viewEndorsementVehicles and validate that there is NOT 'revert' option for removed vehicle as there already is max amount of vehicles
	 *    PAS-18670
	 * 6. Try to revert removed vehicle when there already is max count of vehicles ----> I receive error
	 * 	 *    PAS-18670
	 * 7. Try to revert Replaced vehicle when there already is max count of vehicles ----> Revert option is available and I do not receive error
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9546", "PAS-18672", "PAS-18670"})
	public void pas9546_maxVehicles(@Optional("VA") String state) {
		pas9546_maxVehiclesBody();
		mainApp().close();
		pas18672_vehiclesRevertOptionForDeleteBody();
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when vehicle is not updated before removal
	 * @scenario
	 *1. Create a policy in PAS with multiple vehicles
	 *2. Create endorsement through service
	 *3. Do not Update and then Remove 1 vehicle through service
	 *4. Run Cancel Remove Vehicle Transaction Service for 'pendingRemoval' vehicle
	 *5. Run viewVehicles service and validate response
	 *6. Retrieve endorsement in PAS and validate that vehicle is reverted ---> vehicle is reverted back to state as it was before removal
	 * NOTE: test additionally validates that Vehicle level coverages are not changed after revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18670"})
	public void pas18670_CancelRemoveVehicleWithoutChanges(@Optional("VA") String state) {
		pas18670_CancelRemoveVehicleBody(false, false, false);
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when vehicle is updated before removal
	 * @scenario
	 *1. Create a policy in PAS with multiple vehicles
	 *2. Create endorsement through service
	 *3. Update and then Remove 1 vehicle through service
	 *4. Run Cancel Remove Vehicle Transaction Service for 'pendingRemoval' vehicle
	 *5. Run viewVehicles service and validate response
	 *6. Retrieve endorsement in PAS and validate that vehicle is reverted ---> vehicle is reverted back to state as it was after update and before removal
	 * NOTE: test additionally validates that Vehicle level coverages are not changed after revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18670"})
	public void pas18670_CancelRemoveVehicleWithChanges(@Optional("VA") String state) {
		pas18670_CancelRemoveVehicleBody(true, true, false);
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when vehicle is not updated before replace
	 * @scenario
	 *1. Create a policy in PAS with multiple vehicles
	 *2. Create endorsement through service
	 *3. Do not update Vehicle and Vehicle Coverages, Run Replace Vehicle service for one of the Vehicles
	 *4. Run Cancel Remove Vehicle Transaction Service for replaced vehicle
	 *5. Run viewVehicles service and validate response
	 *6. Retrieve endorsement in PAS and validate that vehicle is reverted ---> vehicle is reverted back to state as it was before removal. Newly added vehicle is deleted.
	 *7. Run viewAssignments service and validate that Reverted Vehicle Assignment is not defaulted (if multiple drivers)
	 * NOTE: test additionally validates that Vehicle level coverages are not changed after revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18671"})
	public void pas18671_CancelReplaceVehicleWithoutChanges(@Optional("VA") String state) {
		pas18670_CancelRemoveVehicleBody(false, true, true);
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when vehicle is updated before replace
	 * @scenario
	 *1. Create a policy in PAS with multiple vehicles and drivers
	 *2. Create endorsement through service
	 *3. Update Vehicle and Vehicle Coverages, Run Replace Vehicle service for one of the Vehicles
	 *4. Run Cancel Remove Vehicle Transaction Service for replaced vehicle
	 *5. Run viewVehicles service and validate response
	 *6. Retrieve endorsement in PAS and validate that vehicle is reverted ---> vehicle is reverted back to state as it was after update and before removal. Newly added vehicle is deleted.
	 *7. Run viewAssignments service and validate that Reverted Vehicle Assignement is not defaulted (if multiple drivers)
	 * NOTE: test additionally validates that Vehicle level coverages are not changed after revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18671"})
	public void pas18671_CancelReplaceVehicleWithChanges(@Optional("VA") String state) {
		pas18670_CancelRemoveVehicleBody(true, true, true);
	}

	/**
	 * @author Maris Strazds
	 * @name validate revert option when vehicle is updated before replace
	 * @scenario
	 *1. Create a policy in PAS with multiple vehicles and 1 driver
	 *2. Create endorsement through service
	 *3. Run Replace Vehicle service for one of the Vehicles
	 *4. Run Cancel Remove Vehicle Transaction Service for replaced vehicle
	 *5. Run viewVehicles service and validate response
	 *6. Retrieve endorsement in PAS and validate that vehicle is reverted ---> vehicle is reverted back to state as it was before removal. Newly added vehicle is deleted.
	 *7. Run viewAssignments service and validate that Reverted Vehicle Assignement is defaulted (if 1 driver)
	 * NOTE: test additionally validates that Vehicle level coverages are not changed after revert
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18671"})
	public void pas18671_CancelReplaceVehicle_oneDriver(@Optional("VA") String state) {
		boolean testWithUpdates = new Random().nextBoolean();
		printToLog("testWithUpdates: " + testWithUpdates);
		pas18670_CancelRemoveVehicleBody(testWithUpdates, false, true);
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
	public void pas14952_StatusResetsForNewlyAddedVehicle(@Optional("NY") String state) {
		pas14952_StatusResetsForNewlyAddedVehicleBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transaction Information For Endorsements outside of PAS - AddVehicle/RemoveVehicle
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
	 * For Delete vehicle
	 * 11. Create new endorsement.
	 * 12. Delete V1
	 * 13. Delete V0
	 * 14. Hit "Transaction History Service".
	 * 15. Check info for deleted vehicles
	 * 16. Rate and bind endorsement.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9493", "PAS-14497"})
	public void pas9493_TransactionInformationForEndorsementsAddRemoveVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas9493_TransactionInformationForEndorsementsAddRemoveVehicleBody(getPolicyType(), softly)
		);
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Transaction Information For Endorsements outside of PAS - Replace vehicle
	 * @scenario 1. Create policy with three vehicles.
	 * 2. Start do endorsement outside of PAS.
	 * 3. Hit "Transaction History Service". Check if response is empty.
	 * 4. Replace V1 and V2.
	 * 5. Remove V3.
	 * 6. Hit "Transaction History Service". Check info.
	 * 7. Rate and Bind.
	 * 8. Create new endorsement outside of PAS.
	 * 9. Hit "Transaction History Service". Check if response is empty.
	 * 10. Replace Vehicle which was already replaced.
	 * 11. Hit "Transaction History Service". Check if only one vehicle exist in response.
	 * 12. Rate and Bind.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-14497"})
	public void pas14497_TransactionInformationForEndorsementsReplaceVehicle(@Optional("VA") String state) {
		assertSoftly(softly ->
				pas14497_TransactionInformationForEndorsementsReplaceVehicleBody(getPolicyType(), softly)
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
	 * @author Nauris Ivanans
	 * @name Check NY UI fields after adding and replacing vehicle
	 * @scenario 1. Create policy
	 * 2. Add one vehicle from DXP
	 * 3. Replace one vehicle from DXP
	 * 4. Check default "Is this a Replacement Vehicle?" and "Select the Replaced Vehicle" values on UI:
	 * For added vehicle "Is this a Replacement Vehicle?" = false
	 * For replaced vehicle "Is this a Replacement Vehicle?" = true
	 * and "Select the Replaced Vehicle" displays information about the vehicle that was replaced.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-24166"})
	public void pas24166_ReplaceVehicleAndCheckUIFields(@Optional("NY") String state) {
		pas24166_ReplaceVehicleAndCheckUIFields();
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
	@StateList(states = {Constants.States.VA})
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
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleKeepAssignmentsOneDriver(@Optional("AZ") String state) {

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
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-13920", "PAS-13320", "PAS-14680"})
	public void pas13920_ReplaceVehicleDontKeepAssignmentsOneDriver(@Optional("VA") String state) {

		pas13920_ReplaceVehicleKeepAssignmentsOneDriverBody(false);
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
	@StateList(states = {Constants.States.VA})
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
	@StateList(states = {Constants.States.VA})
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
	@StateList(states = {Constants.States.VA, Constants.States.NY})
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
	@StateList(states = {Constants.States.VA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12175"})
	public void pas12175_RemoveReplaceWaiveLiability(@Optional("VA") String state) {

		pas12175_RemoveReplaceWaiveLiabilityBody();
		pas12175_RemoveReplaceWaiveLiabilityBody();
	}

	/**
	 * @author Dakota Berg
	 * @name Check garaging address on DXP
	 * @scenario 1. Create a customer and policy
	 * 2. Initiate an endorsement
	 * 3. Update vehicle to have a different garaging address outside of PAS
	 * 4. Hit Meta Data Service and verify that the garaging address is different
	 * 5. Bind the endorsement and verify that the policy is active
	 * Pas-15269 : Megha Gubbala.
	 * 6. Run the test for CT and VA If CT verify county in meta data service
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.CT})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12942, Pas-15269"})
	public void pas12942_GaragingAddressConsistencyDXP(@Optional("CT") String state) {
		pas12942_GaragingAddressConsistencyDXPBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Check Vehicle Assignments after replacing the vehicle (only states without driver assignment)
	 * @scenario 1.Create a policy with 2 drivers and two vehicles
	 * 2.Create new endorsement outside of PAS
	 * 3.Replace one vehicle: KeepAssignment = true
	 * 4.Replace second vehicle: KeepAssignment = false
	 * 5.Rate and bind
	 * 6.Create new endorsement outside of PAS.
	 * 7.Add new vehicle, update, rate and bind.
	 * 8.Create new endorsement outside of PAS.
	 * 9.Delete one old vehicle.
	 * 10. Replace the newest vehicle: KeepAssignment = true
	 * 11. Issue and Bind.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.NV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-16113"})
	public void pas16113_ReplaceVehicleKeepAssignmentsForOtherStatesThanVa(@Optional("NV") String state) {

		pas16113_ReplaceVehicleKeepAssignmentsForOtherStatesThanVaBody();
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Add Vehicle Service - Blocking for Purchase Date
	 * @scenario 1. Create policy.
	 * 2. Create endorsement outside of PAS (date = today)
	 * 3. Add vehicle, purchase date: -5days
	 * 4. Check error and if vehicle wasn't added to the endorsement.
	 * 5. Try add another vehicle, purchase date = endorsement date.
	 * 6. Check if vehicle was added.
	 * 7. Add vehicle, purchase date: - 31days
	 * 8. Check if vehicle was added.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-9750"})
	public void pas9750_addVehicleServiceBlockingForPurchaseDate(@Optional("NV") String state) {

		pas9750_addVehicleServiceBlockingForPurchaseDateBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name viewPolicyRenewalSummary - See if county is there
	 * @scenario 1. Create policy CT.
	 * 2. run viewPolicyRenewalSummary verify county is there
	 * 3. Create endorsement outside of PAS (date = today)
	 * 4. run viewEndorsementVehicles verify county
	 * 5. createUpdateVehicleRequest update vehicle with garaging Address outside CT verify county is not there.
	 * 6. createUpdateVehicleRequest update vehicle with garaging Address that has only 1 county verify county is there .
	 * 7. createUpdateVehicleRequest update vehicle with garaging Address that has more than 1  county verify county is null .
	 * 8. again update vehicle with county verify county in response.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15269"})
	public void pas15269_ViewVehicleServiceAddTownship(@Optional("CT") String state) {
		assertSoftly(softly ->
				pas15269_ViewVehicleServiceAddTownshipBody(softly)
		);
	}

	/**
	 * @author Sabra Domeika
	 * @name Registered Owner -
	 * @scenario
	 * 1. Create policy CT.
	 * 2. Create new endorsement in DXP
	 * 3. Add a new vehicle.
	 * 4. Update the vehicle and registered owner to false.
	 * 5. Validate that an error is returned.
	 * 6. Navigate to the PAS UI. Validate that Registered Owner Different is set to Yes in the UI.
	 * 7. Attempt to rate in DXP. Validate that an error is returned.
	 * 8. Update the vehicle to registered owner to true.
	 * 9. Rate in DXP. Validate than no error is returned.
	 * 10. Bind in DXP. Validate that no error is returned.
	 */
	@Parameters({"state"})
	@StateList(states = {Constants.States.CT, Constants.States.NY, Constants.States.WV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15269"})
	public void pas15499_RegisteredOwnerDifferent(@Optional("CT") String state) {
		assertSoftly(softly -> pas15499_RegisteredOwners(softly));
	}

	/**
	 * @author Jovita Pukenaite
	 * @name Add Vehicle - check Anti-Theft Drop Down Values
	 * @scenario 1. Create policy.
	 * 2. Create endorsement.
	 * 3. Add vehicle, check Anti-Theft Drop Down Values.
	 * 4. Delete endorsement, create new one outside of PAS.
	 * 5. Add new vehicle.
	 * 6. Hit MetaData service, check the values there.
	 * 7. Update Anti-theft.
	 * 8. Check if value was updated.
	 * 9. Rate and Bind.
	 * 10. Check PAS side, if value was updated there too.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@StateList(states = {Constants.States.NJ, Constants.States.NY})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21597,PAS-7148"})
	public void pas21597_addVehicleCheckAntiTheft(@Optional("NJ") String state) {

		assertSoftly(softly -> pas21597_addVehicleCheckAntiTheftBody(softly));
	}

	/**
	 * @author Sabra Domeika
	 * @name New Jersey and the Less than 1000 miles question
	 * @scenario
	 * 1. Create a policy with one vehicle older than 7 years and one less than 7 years.
	 * 2. Run view vehicles and validate that the less than 1000 miles value is returned.
	 * 3. Start an endorsement in DXP.
	 * 4. Run the metadata service. Validate that for the older vehicle, the less than 1000 miles question is not
	 * listed as visible. validate that the for the newer vehicle, the less than 1000 miles question is visible.
	 * 5. For a vehicle less than 7 years, a vehicle equal to 7 years and a vehicle less than 7 years, do the following:
	 * 5a. Create a new endorsement in DXP.
	 * 5b. Add the vehicle. Validate that less than 1000 miles value is returned.
	 * 5c. Validate the metadata service shows the question as visible for the <= 7 vehicles and is shown as
	 * not visible for the > 7 vehicle.
	 * 5d. For the vehicles <= 7 years, update the field to Yes. Validate that the right value is returned in the
	 * response. Run the Change Log service and validate that the value is present there as well.
	 * 5e. Run the Rate service.
	 * 5f. Run the Bind service.
	 * @param state
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@StateList(states = {Constants.States.NJ, Constants.States.NY})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-18408"})
	public void pas18408_lessThan1000Miles(@Optional("NY") String state) {
		assertSoftly(this::pas18408_validateLessThan1000Miles);
	}

	/**
	 * @author Maris Strazds
	 * @name Check that deleted vehicle and change log contains fields Make, Model, Other Make, Other Model, Other Series, Other Body Style
	 * @scenario
	 * 1. Create policy in PAS with Make/Model = other for PPA, VAN, Motor Home, Trailer, Golf Cart, Limited/Antique
	 * 2. Create Endorsement through service
	 * 3. Check that fields Make, Model, Other Make, Other Model, Other Series, Other Body Style contains correct values for vehicles listed above
	 * 3. Remove above listed vehicles through service
	 * 4. Verify that change log contains fields Make, Model, Other Make, Other Model, Other Series, Other Body Style contains correct values for vehicles listed above with correct values
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@StateList(states = {Constants.States.AZ})//Should work also for other states if they have all specific Vehicle Types
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-25065"})
	public void pas25065_validateMakeModelOthersForRemovedVehicle(@Optional("AZ") String state) {
		pas25065_validateMakeModelOthersForRemovedVehicleBody();
	}

	/**
	 * @author Megha Gubbala
	 * @name View/Update Vehicle Service - Daytime Running, Antilock Brakes - New York
	 * * @scenario
	 * 1. Create policy in PAS
	 * 2. Create Endorsement through service
	 * 3. add vehicle from service and run view vehicle service verifi Daytime Running Lamp and Anti-Lock Brakes is there false
	 * 4 update vehicle with Daytime Running Lamp and Anti-Lock Brakes true verify response
	 * 5. Verify Meta deta service.
	 * */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-15334"})
	public void pas15334viewUpdateVehicleDaytimeRunningAntilockBrakes(@Optional("NY") String state) {
		assertSoftly(softly -> pas15334viewUpdateVehicleDaytimeRunningAntilockBrakesBody(softly));
	}
}



