package aaa.modules.regression.service.auto_ca.select.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelper;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelperCA;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestMiniServicesVehicles extends TestMiniServicesVehiclesHelperCA {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-10449", "PAS-12244", "PAS-12246", "PAS-14593", "PAS-25265", "PAS-15401"})
	public void pas10449_ViewVehicleServiceOrderOfVehicle(@Optional("CA") String state) {

		pas10449_ViewVehicleServiceCheckOrderOfVehicle(getPolicyType(), state);
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-7082", "PAS-7145", "PAS-11621", "PAS-25265", "PAS-15401"})
	public void pas7082_AddVehicle(@Optional("CA") String state) {

		pas7082_AddVehicle(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite, Maris Strazds
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-502", "PAS-11005", "PAS-25265", "PAS-15401"})
	public void pas502_DuplicateVinAddVehicleService(@Optional("CA") String state) {

		pas502_CheckDuplicateVinAddVehicleService(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite, Maris Strazds
	 * @name Check if only active Vehicles are allowed using DXP
	 * @scenario 1. Create policy with two vehicles.
	 * 2. Check if the same vehicles are displayed in dxp server.
	 * 3. Initiate endorsement, and change VIN for one of the vehicles. Don't bind.
	 * 4. Check if the new vehicle, which wad added during endorsement is not displayed in dxp server.
	 * 5. Bind the endorsement.
	 * 6. Check if new vehicle is displayed, and the old one is not displayed anymore.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-8273", "PAS-7145", "PAS-11622", "PAS-25265", "PAS-15401"})
	public void pas8273_OnlyActiveVehiclesAreAllowed(@Optional("CA") String state) {
		assertSoftly(softly ->
				pas8273_CheckIfOnlyActiveVehiclesAreAllowed(softly, getPolicyType())
		);
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-9610", "PAS-25267"})
	public void pas9610_UpdateVehicleService(@Optional("CA") String state) {
		pas9610_UpdateVehicleService();
	}

	/**
	 * @author Oleg Stasyuk, Maris Strazds
	 * @name Validation on Update/Rate/Bind for vehicle use = Business, AND also if registeredOwner = false
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-7147"})
	public void pas7147_VehicleUpdateBusiness(@Optional("CA") String state) {

//		pas7147_VehicleUpdateBusinessBody(); //TODO-mstrazds: finish when there is story for it
	}

	/**
	 * @author Oleg Stasyuk, Maris Strazds
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-11618", "PAS-25267"})
	public void pas11618_UpdateVehicleLeasedInfo(@Optional("CA") String state) {
		//assertSoftly(softly ->
				//pas11618_UpdateVehicleLeasedFinancedInfoBody(softly, "LSD")//TODO-mstrazds: PAS-28718 PAS-28718 Garaging Address and Ownership - Let's make sure it works for CA Select
		//);
	}

	/**
	 * @author Oleg Stasyuk, Maris Strazds
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
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-11618", "PAS-25267"})
	public void pas11618_UpdateVehicleFinancedInfo(@Optional("CA") String state) {
		//assertSoftly(softly ->
				//pas11618_UpdateVehicleLeasedFinancedInfoBody(softly, "FNC")//TODO-mstrazds: PAS-28718 PAS-28718 Garaging Address and Ownership - Let's make sure it works for CA Select
		//);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-13252", "PAS-25267"})
	public void pas13252_UpdateVehicleGaragingAddressProblem(@Optional("CA") String state) {
		//pas13252_UpdateVehicleGaragingAddressProblemBody();//TODO-mstrazds: PAS-28718 Garaging Address and Ownership - Let's make sure it works for CA Select
	}


}
