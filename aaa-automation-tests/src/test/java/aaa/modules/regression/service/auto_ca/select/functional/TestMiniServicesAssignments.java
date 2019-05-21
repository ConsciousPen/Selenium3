/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ca.select.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

import aaa.modules.regression.service.helper.TestMiniServicesAssignmentsCAHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestMiniServicesAssignments extends TestMiniServicesAssignmentsCAHelper {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-11684"})
	public void pas11684_DriverAssignmentExistsForState(@Optional("CA") String state) {
		assertSoftly(softly ->
				pas11684_DriverAssignmentExistsForStateBody(state, softly)
		);
	}

	/**
	 * @author Sabra Domeika
	 * 1. Create a policy with 1D/2V.
	 * 2. Create an endorsement through DXP.
	 * 3. Add a new vehicle through DXP.
	 * 4. Validate that the driver is auto-assigned to the newly added vehicle.
	 * 5. Add another vehicle through DXP.
	 * 6. Validate that the driver is auto-assigned to the newly added vehicle.
	 * 7. Rate and bind the policy.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15195"})
	public void pas15195_DriverAssignmentAutoAssign1DMV(@Optional("CA") String state) {
		pas15195_DriverAssignmentAutoAssign1DMVBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Create a policy with 2D/1V. Assign driver to vehicle.
	 * 2. Create an endorsement through DXP.
	 * 3. Remove the currently assigned driver from the policy.
	 * 4. Validate that the remaining driver is auto-assigned to the existing vehicle.
	 * 5. Rate and bind the policy.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15195"})
	public void pas15195_DriverAssignmentAutoAssign1D1V(@Optional("CA") String state) {
		pas15195_DriverAssignmentAutoAssign1D1VBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Create a policy with 2D/2V. Assign one driver to each vehicle.
	 * 2. Create an endorsement through DXP.
	 * 3. Add a new driver through DXP.
	 * 4. Validate that the vehicles on the policy are all unassigned.
	 * 5. Assign the new driver to both vehicles.
	 * 6. Validate that the vehicles on the policy are assigned to the new driver and no failures are shown.
	 * 7. Assign the original drivers to both vehicles.
	 * 8. Validate that the vehicles on the policy are assigned back to their original drivers and no failures are shown.
	 * 9. Rate and bind the policy.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15195"})
	public void pas15412_DriverAssignmentAddDriver(@Optional("CA") String state) {
		pas15412_DriverAssignmentAddDriverBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Craete a policy with 2D/2V. Assign one driver to each vehicle.
	 * 2. Create an endorsement through DXP.
	 * 3. Add a new vehicle through DXP.
	 * 4. Validate that both of the existing assignments remain the same.
	 * 5. Assign both of the drivers to the new vehicle.
	 * 6. Validate that the response includes vehicles with too many drivers assigned for the vehicle.
	 * 7. Try to rate. Validate that the error was thrown.
	 * 8. Update the vehicle to only have one driver.
	 * 9. Rate and bind successfully.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15412"})
	public void pas15412_DriverAssignmentAddVehicle(@Optional("CA") String state) {
		pas15412_DriverAssignmentAddVehicleBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Craete a policy with 3D/3V. Assign one driver to one vehicle, one driver to the other two. Leave one driver
	 * not assigned to any vehicle.
	 * 2. Create an endorsement through DXP.
	 * 3. Remove the driver with two assignments through DXP.
	 * 4. Validate that the vehicle assigned to the driver still on the policy is still assigned.
	 * 5. Validate that the other vehicles are unassigned.
	 * 6. Attempt to rate. Validate that error is received.
	 * 7. Assign unassigned driver to both vehicles.
	 * 8. Rate and bind successfully.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15412"})
	public void pas15412_DriverAssignmentRemoveDriver_CurrentlyAssigned(@Optional("CA") String state) {
		pas15412_DriverAssignmentRemoveDriver_CurrentlyAssignedBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Create a policy with 3D/3V. Assign one driver to one vehicle, one driver to the other two. Leave one driver
	 * not assigned to any vehicle.
	 * 2. Create an endorsement through DXP.
	 * 3. Remove the driver with no assignments through DXP.
	 * 4. Validate that the existing assignments are not impacted.
	 * 5. Rate and bind successfully.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15412"})
	public void pas15412_DriverAssignmentRemoveDriver_CurrentlyUnassigned(@Optional("CA") String state) {
		pas15412_DriverAssignmentRemoveDriver_CurrentlyUnassignedBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Create a policy with 2D/3V. Assign one driver to one vehicle, one driver to the other two.
	 * 2. Create an endorsement through DXP.
	 * 3. Remove one of the vehicles.
	 * 4. Validate that the existing assignments remain the same as before.
	 * 5. Rate and bind successfully.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15412"})
	public void pas15412_DriverAssignmentRemoveVehicle(@Optional("CA") String state) {
		pas15412_DriverAssignmentRemoveVehicleBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Create a policy with 2D/3V. Assign one driver to one vehicle, one driver to the other two.
	 * 2. Create an endorsement through DXP.
	 * 3. Replace one of the vehicles.
	 * 4. Validate that the driver assigned to the previous vehicle is assigned to the new one.
	 * 5. Rate and bind successfully.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15412"})
	public void pas15412_DriverAssignmentReplaceVehicle(@Optional("CA") String state) {
		pas15412_DriverAssignmentReplaceVehicleBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Create a policy with 1D/2V.
	 * 2. Create an endorsement through DXP.
	 * 3. Replace one of the vehicles.
	 * 4. Validate that the driver assigned to the previous vehicle is assigned to the new one.
	 * 5. Rate and bind successfully.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-29131"})
	public void pas15412_DriverAssignmentReplaceVehicle1DMV(@Optional("CA") String state) {
		pas15412_DriverAssignmentReplaceVehicle1DMVBody();
	}

	/**
	 * @author Sabra Domeika
	 * 1. Create a policy with 7D/6V. Assign one driver to one vehicle, one driver to two, one driver to three. Leave
	 * all other drivers as not assigned. Mark one of the vehicles as manually rated against one of the non-assgined
	 * drivers. Make one driver an NAFR and another one an excluded driver.
	 * 2. Validate the assignments through DXP. Make sure that all assignments match the PAS UI. Make sure the manually
	 * rated driver is NOT displayed as the driver on the relationship with that vehicle.
	 * 3. Create an endorsement through DXP.
	 * 4. Validate the assignments through DXP. Make sure that all assignments match the PAS UI. Make sure the manually
	 * rated driver is NOT displayed as the driver on the relationship with that vehicle.
	 * 5. Rate and bind successfully.
	 */
	@Parameters({"state"})
	@StateList(states = Constants.States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15412"})
	public void pas15412_DriverAssignmentViewAssignments(@Optional("CA") String state) {
		pas15412_DriverAssignmentViewAssignmentsBody();
	}

	/**
	 * @author Megha Gubbala
	 * 1. Create a policy with 2D and 6 vehicles (2 PPA ,trailer ,camper,AntiqueClassic,motorhome)
	 *2. FNI is assigned to 1 PPA vehicle Driver 2 assigned to all other vehicle
	 * 3. Verify Driver assignemt shpould have 2 assignable driver 2 assignable vehicle D1 assigned to vehicle 1 nd driver 2 assigned to vehicle 2
	 * 4. remove vehicle Trailer Verify no impact on DA
	 * 5.remove driver 2 And verify vehicle to automatically assigned to D1
	 * 6.rate and bind
	 */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-29163"})
	public void pas29163_DriverAssignmentAndOddBalls(@Optional("CA") String state) {
		pas29163_DriverAssignmentAndOddBallsBody(getPolicyType());
	}

}