package aaa.modules.regression.service.helper;

import aaa.common.enums.Constants;
import aaa.helpers.TestDataManager;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.AssignmentTab;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestMiniServicesAssignmentsCAHelper extends TestMiniServicesAssignmentsHelper {

	protected void pas15195_DriverAssignmentAutoAssign1DMVBody() {
		TestData td = createPolicyWithMoreThanOneVehicle("TestData_1D2V");
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_1D2V")).resolveLinks();
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		addVehicle(policyNumber, "2012-02-21", "1HGEM21504L055795");
		ViewDriverAssignmentResponse driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse1.unassignedDrivers).isEmpty();
			softly.assertThat(driverAssignmentResponse1.unassignedVehicles).isEmpty();
			softly.assertThat(driverAssignmentResponse1.driverVehicleAssignments.size()).isEqualTo(3);
			softly.assertThat(driverAssignmentResponse1.assignableDrivers.size()).isEqualTo(1);
			String driverOid = driverAssignmentResponse1.assignableDrivers.iterator().next();
			driverAssignmentResponse1.driverVehicleAssignments.forEach(assignment -> softly.assertThat(assignment.driverOid).isEqualTo(driverOid));
		});

		addVehicle(policyNumber, "2012-02-21", "1G8AZ54F531234567");
		ViewDriverAssignmentResponse driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse2.unassignedDrivers).isEmpty();
			softly.assertThat(driverAssignmentResponse2.unassignedVehicles).isEmpty();
			softly.assertThat(driverAssignmentResponse2.driverVehicleAssignments.size()).isEqualTo(4);
			softly.assertThat(driverAssignmentResponse2.assignableDrivers.size()).isEqualTo(1);
			String driverOid = driverAssignmentResponse2.assignableDrivers.iterator().next();
			driverAssignmentResponse2.driverVehicleAssignments.forEach(assignment -> softly.assertThat(assignment.driverOid).isEqualTo(driverOid));
		});
	}

	protected void pas15195_DriverAssignmentAutoAssign1D1VBody() {
		TestData td = createPolicyWithMoreThanOneDriver("TestData_2D1V");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		DriversDto driverNotFNI = getTestMiniServicesDriversHelper().getAnyNotNIActiveDriver(policyNumber);
		DriversDto driverFNI = getTestMiniServicesDriversHelper().getFNIDriver(policyNumber);
		HelperCommon.removeDriver(policyNumber, driverNotFNI.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
		ViewDriverAssignmentResponse driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse1.unassignedDrivers).isEmpty();
			softly.assertThat(driverAssignmentResponse1.unassignedVehicles).isEmpty();
			softly.assertThat(driverAssignmentResponse1.driverVehicleAssignments.size()).isEqualTo(1);
			softly.assertThat(driverAssignmentResponse1.assignableDrivers.size()).isEqualTo(1);
			String driverOid = driverAssignmentResponse1.assignableDrivers.iterator().next();
			driverAssignmentResponse1.driverVehicleAssignments.forEach(assignment -> softly.assertThat(assignment.driverOid).isEqualTo(driverFNI.oid));
		});
	}

	protected void pas15412_DriverAssignmentAddDriverBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_2D2V", "TestData_2D2V", "TestData_2D2V");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		String newDriverOid = addRegularDriver(policyNumber, "Connemara", "123456852");
		ViewDriverAssignmentResponse driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse1.unassignedDrivers).isEmpty();
			softly.assertThat(driverAssignmentResponse1.unassignedVehicles.size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse1.driverVehicleAssignments).isEmpty();
			softly.assertThat(driverAssignmentResponse1.assignableDrivers.size()).isEqualTo(3);
			softly.assertThat(driverAssignmentResponse1.assignableVehicles.size()).isEqualTo(2);
		});
		driverAssignmentResponse1.unassignedVehicles.stream().forEach(vehicleOid -> HelperCommon.updateDriverAssignment(policyNumber, vehicleOid, Collections.singletonList(newDriverOid)));
		ViewDriverAssignmentResponse driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse2.unassignedDrivers).isEmpty();
			softly.assertThat(driverAssignmentResponse2.unassignedVehicles).isEmpty();
			softly.assertThat(driverAssignmentResponse2.driverVehicleAssignments.size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse2.assignableDrivers.size()).isEqualTo(3);
			softly.assertThat(driverAssignmentResponse2.assignableVehicles.size()).isEqualTo(2);
			driverAssignmentResponse2.driverVehicleAssignments.forEach(assignment -> softly.assertThat(assignment.driverOid).isEqualTo(newDriverOid));
		});
	}

	protected void pas15412_DriverAssignmentAddVehicleBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_2D2V", "TestData_2D2V", "TestData_2D2V");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		DriversDto driverFNI = getTestMiniServicesDriversHelper().getFNIDriver(policyNumber);
		String newVehicleOid = addVehicle(policyNumber, "2012-02-21", "1HGEM21504L055795");
		ViewDriverAssignmentResponse driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse1.unassignedDrivers).isEmpty();
			softly.assertThat(driverAssignmentResponse1.unassignedVehicles.size()).isEqualTo(1);
			softly.assertThat(driverAssignmentResponse1.unassignedVehicles.contains(newVehicleOid)).isTrue();
			softly.assertThat(driverAssignmentResponse1.driverVehicleAssignments.size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse1.assignableDrivers.size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse1.assignableVehicles.size()).isEqualTo(3);
		});
		ViewDriverAssignmentResponse updateAssignmentResponse = HelperCommon.updateDriverAssignment(policyNumber, newVehicleOid, new ArrayList<>(driverAssignmentResponse1.assignableDrivers));
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse1.unassignedDrivers).isEmpty();
			softly.assertThat(updateAssignmentResponse.unassignedVehicles.size()).isEqualTo(0);
			softly.assertThat(updateAssignmentResponse.driverVehicleAssignments.size()).isEqualTo(4);
			softly.assertThat(updateAssignmentResponse.vehiclesWithTooManyDrivers.size()).isEqualTo(1);
			softly.assertThat(updateAssignmentResponse.vehiclesWithTooManyDrivers).contains(newVehicleOid);
			softly.assertThat(updateAssignmentResponse.assignableDrivers.size()).isEqualTo(2);
			softly.assertThat(updateAssignmentResponse.assignableVehicles.size()).isEqualTo(3);
		});
		ViewDriverAssignmentResponse updateAssignmentResponse2 = HelperCommon.updateDriverAssignment(policyNumber, newVehicleOid, Collections.singletonList(driverFNI.oid));
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse1.unassignedDrivers).isEmpty();
			softly.assertThat(updateAssignmentResponse2.unassignedVehicles.size()).isEqualTo(0);
			softly.assertThat(updateAssignmentResponse2.driverVehicleAssignments.size()).isEqualTo(3);
			softly.assertThat(updateAssignmentResponse2.vehiclesWithTooManyDrivers.size()).isEqualTo(0);
			softly.assertThat(updateAssignmentResponse2.assignableDrivers.size()).isEqualTo(2);
			softly.assertThat(updateAssignmentResponse2.assignableVehicles.size()).isEqualTo(3);
			softly.assertThat(updateAssignmentResponse2.driverVehicleAssignments.stream().anyMatch(assignment -> assignment.vehicleOid.equals(newVehicleOid) && assignment.driverOid.equals(driverFNI.oid))).isTrue();
		});
	}

	protected void pas15412_DriverAssignmentRemoveDriver_CurrentlyAssignedBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_3D3V", "TestData_3D3V", "TestData_3D3V");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		DriversDto driverNotFNI = getTestMiniServicesDriversHelper().getAnyNotNIActiveDriver(policyNumber);
		DriversDto driverFNI = getTestMiniServicesDriversHelper().getFNIDriver(policyNumber);
		HelperCommon.removeDriver(policyNumber, driverNotFNI.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
		ViewDriverAssignmentResponse driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse1.unassignedDrivers).isEmpty();
			softly.assertThat(driverAssignmentResponse1.unassignedVehicles.size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse1.driverVehicleAssignments.size()).isEqualTo(1);
			softly.assertThat(driverAssignmentResponse1.assignableDrivers.size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse1.assignableVehicles.size()).isEqualTo(3);
			driverAssignmentResponse1.driverVehicleAssignments.forEach(assignment -> softly.assertThat(assignment.driverOid).isEqualTo(driverFNI));
		});
	}

	protected void pas15412_DriverAssignmentRemoveDriver_CurrentlyUnassignedBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_3D3V", "TestData_3D3V", "TestData_3D3V");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		DriversDto driverNotFNI = getTestMiniServicesDriversHelper().getAnyNotNIActiveDriver(policyNumber);
		DriversDto driverFNI = getTestMiniServicesDriversHelper().getFNIDriver(policyNumber);
		HelperCommon.removeDriver(policyNumber, driverNotFNI.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
		getHelperMiniServices().endorsementRateAndBind(policyNumber);
	}

	protected void pas15412_DriverAssignmentRemoveVehicleBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_2D3V", "TestData_2D3V", "TestData_2D3V");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, "1GAZG1FG7D1145543");
		HelperCommon.deleteVehicle(policyNumber, vehicle1.oid, VehicleUpdateResponseDto.class, Response.Status.OK.getStatusCode());
		ViewDriverAssignmentResponse driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignmentResponse1.unassignedDrivers).isEmpty();
			softly.assertThat(driverAssignmentResponse1.unassignedVehicles.size()).isEqualTo(0);
			softly.assertThat(driverAssignmentResponse1.driverVehicleAssignments.size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse1.assignableDrivers.size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse1.assignableVehicles.size()).isEqualTo(2);
		});
	}

	protected void pas15412_DriverAssignmentReplaceVehicleBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_2D3V", "TestData_2D3V", "TestData_2D3V");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, "1GAZG1FG7D1145543");
		ViewDriverAssignmentResponse driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		List<DriverAssignment> driverAssignments = driverAssignmentResponse1.driverVehicleAssignments.stream().filter(assignment -> assignment.vehicleOid.equals(vehicle1.oid)).collect(Collectors.toList());
		String replacedVeh1 = replaceVehicleWithUpdates(policyNumber, vehicle1.oid, "1HGFA16526L081415", true, false);
		ViewDriverAssignmentResponse driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments(policyNumber);
		driverAssignments.forEach(previousAssignment -> {
			assertSoftly(softly -> {
				softly.assertThat(driverAssignmentResponse2.driverVehicleAssignments.stream().anyMatch(currentAssignment -> currentAssignment.driverOid.equals(previousAssignment.driverOid) && currentAssignment.vehicleOid.equals(replacedVeh1))).isTrue();
				softly.assertThat(driverAssignmentResponse2.unassignedVehicles.size()).isEqualTo(0);
				softly.assertThat(driverAssignmentResponse2.unassignedDrivers).isEmpty();
			});
		});
	}

	protected void pas15412_DriverAssignmentReplaceVehicle1DMVBody() {
		TestData td = createPolicyWithMoreThanOneVehicle("TestData_1D2V");
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_1D2V")).resolveLinks();
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, "1GAZG1FG7D1145543");
		ViewDriverAssignmentResponse driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		List<DriverAssignment> driverAssignments = driverAssignmentResponse1.driverVehicleAssignments.stream().filter(assignment -> assignment.vehicleOid.equals(vehicle1.oid)).collect(Collectors.toList());
		String replacedVeh1 = replaceVehicleWithUpdates(policyNumber, vehicle1.oid, "1HGFA16526L081415", true, false);
		ViewDriverAssignmentResponse driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments(policyNumber);
		driverAssignments.forEach(previousAssignment -> {
			assertSoftly(softly -> {
				softly.assertThat(driverAssignmentResponse2.driverVehicleAssignments.stream().anyMatch(currentAssignment -> currentAssignment.driverOid.equals(previousAssignment.driverOid) && currentAssignment.vehicleOid.equals(replacedVeh1))).isTrue();
				softly.assertThat(driverAssignmentResponse2.unassignedVehicles.size()).isEqualTo(0);
				softly.assertThat(driverAssignmentResponse2.unassignedDrivers).isEmpty();
			});
		});
	}

	protected void pas29163_DriverAssignmentAndOddBallsBody(PolicyType policyType) {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_VehicleOtherTypesForOddBalls", "TestData_VehicleOtherTypesForOddBalls", "TestData_VehicleOtherTypesForOddBalls");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);

		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		ViewVehicleResponse viewPolicyVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		Vehicle vehicle1 = findVehicle(viewPolicyVehicleResponse, "1GAZG1FG7D1145543");
		Vehicle vehicle2 = findVehicle(viewPolicyVehicleResponse, "WDCYC7BB0B6729451");
		Vehicle vehicle3 = findVehicle(viewPolicyVehicleResponse, "DDFDFCCVCVCV");

		ViewDriversResponse viewDriverResponse = HelperCommon.viewPolicyDrivers(policyNumber);
		DriversDto driver1 = findDriverByFirstName("Nicolas", viewDriverResponse);
		DriversDto driver2 = findDriverByFirstName("Vincent", viewDriverResponse);
		//Verify  Assignments
		verifyDriverAssignment(policyNumber, vehicle1, vehicle2, driver1, driver2, 2, 2);
		//Delete trailer
		HelperCommon.deleteVehicle(policyNumber, vehicle3.oid, VehicleUpdateResponseDto.class, Response.Status.OK.getStatusCode());
		//Verify no impact on DA
		verifyDriverAssignment(policyNumber, vehicle1, vehicle2, driver1, driver2, 2, 2);

		// remove driver 2
		HelperCommon.removeDriver(policyNumber, driver2.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
		//verify vehicle 1 is assigned to driver 1
		ViewDriverAssignmentResponse endorsementDriverAssignmentsResponse3 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(endorsementDriverAssignmentsResponse3.assignableDrivers.size()).isEqualTo(1);
			softly.assertThat(endorsementDriverAssignmentsResponse3.assignableVehicles.size()).isEqualTo(2);

			softly.assertThat(assignmentExistsForDriverVehicle(driver1.oid, vehicle2.oid, endorsementDriverAssignmentsResponse3.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver1.oid, vehicle1.oid, endorsementDriverAssignmentsResponse3.driverVehicleAssignments)).isTrue();
		});
		getHelperMiniServices().endorsementRateAndBind(policyNumber);
	}

	private void verifyDriverAssignment(String policyNumber, Vehicle vehicle1, Vehicle vehicle2, DriversDto driver1, DriversDto driver2, Integer driverCt, Integer vehicleCt) {
		ViewDriverAssignmentResponse endorsementDriverAssignmentsResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.size()).isEqualTo(driverCt);
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableVehicles.size()).isEqualTo(vehicleCt);

			softly.assertThat(assignmentExistsForDriverVehicle(driver1.oid, vehicle2.oid, endorsementDriverAssignmentsResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver2.oid, vehicle1.oid, endorsementDriverAssignmentsResponse.driverVehicleAssignments)).isTrue();

		});
	}

	protected void pas15412_DriverAssignmentViewAssignmentsBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_7D6V", "TestData_7D6V", "TestData_7D6V");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		td = td.adjust(AutoCaMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy(td);
		ViewVehicleResponse viewPolicyVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		Vehicle vehicle1 = findVehicle(viewPolicyVehicleResponse, "WDCYC7BB0B6729451");
		Vehicle vehicle2 = findVehicle(viewPolicyVehicleResponse, "1GAZG1FG7D1145543");
		Vehicle vehicle3 = findVehicle(viewPolicyVehicleResponse, "WA1BNAFY6J2156797");
		Vehicle vehicle4 = findVehicle(viewPolicyVehicleResponse, "SAJWA1CZ1F8V88395");
		Vehicle vehicle5 = findVehicle(viewPolicyVehicleResponse, "WBA8B9G34HNU57200");
		Vehicle vehicle6 = findVehicle(viewPolicyVehicleResponse, "4JGDA5JB1GA688408");

		ViewDriversResponse viewDriverResponse = HelperCommon.viewPolicyDrivers(policyNumber);
		DriversDto driver1 = findDriverByFirstName("Nicolas", viewDriverResponse);
		DriversDto driver2 = findDriverByFirstName("Vincent", viewDriverResponse);
		DriversDto driver3 = findDriverByFirstName("Iseult", viewDriverResponse);
		DriversDto driver4 = findDriverByFirstName("Gray", viewDriverResponse);
		DriversDto driver5 = findDriverByFirstName("Christopher", viewDriverResponse);
		DriversDto driver6 = findDriverByFirstName("Orlaith", viewDriverResponse);
		DriversDto driver7 = findDriverByFirstName("Iona", viewDriverResponse);
		ViewDriverAssignmentResponse policyDriverAssignmentResponse = HelperCommon.viewPolicyAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(policyDriverAssignmentResponse.unassignedDrivers).isEmpty();
			softly.assertThat(policyDriverAssignmentResponse.unassignedVehicles).isEmpty();
			softly.assertThat(policyDriverAssignmentResponse.driverVehicleAssignments.size()).isEqualTo(6);
			softly.assertThat(policyDriverAssignmentResponse.assignableDrivers.size()).isEqualTo(5);
			softly.assertThat(policyDriverAssignmentResponse.assignableVehicles.size()).isEqualTo(6);
			softly.assertThat(policyDriverAssignmentResponse.assignableDrivers.contains(driver1.oid)).isTrue();
			softly.assertThat(policyDriverAssignmentResponse.assignableDrivers.contains(driver2.oid)).isTrue();
			softly.assertThat(policyDriverAssignmentResponse.assignableDrivers.contains(driver3.oid)).isTrue();
			softly.assertThat(policyDriverAssignmentResponse.assignableDrivers.contains(driver4.oid)).isTrue();
			softly.assertThat(policyDriverAssignmentResponse.assignableDrivers.contains(driver5.oid)).isTrue();
			softly.assertThat(policyDriverAssignmentResponse.assignableDrivers.contains(driver6.oid)).isFalse();
			softly.assertThat(policyDriverAssignmentResponse.assignableDrivers.contains(driver7.oid)).isFalse();
			softly.assertThat(assignmentExistsForDriverVehicle(driver1.oid, vehicle1.oid, policyDriverAssignmentResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver2.oid, vehicle2.oid, policyDriverAssignmentResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver2.oid, vehicle3.oid, policyDriverAssignmentResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver3.oid, vehicle4.oid, policyDriverAssignmentResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver3.oid, vehicle5.oid, policyDriverAssignmentResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver3.oid, vehicle6.oid, policyDriverAssignmentResponse.driverVehicleAssignments)).isTrue();
		});
		getHelperMiniServices().createEndorsementWithCheck(policyNumber);
		ViewDriverAssignmentResponse endorsementDriverAssignmentsResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(endorsementDriverAssignmentsResponse.unassignedDrivers).isEmpty();
			softly.assertThat(endorsementDriverAssignmentsResponse.unassignedVehicles).isEmpty();
			softly.assertThat(endorsementDriverAssignmentsResponse.driverVehicleAssignments.size()).isEqualTo(6);
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.size()).isEqualTo(5);
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableVehicles.size()).isEqualTo(6);
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.contains(driver1.oid)).isTrue();
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.contains(driver2.oid)).isTrue();
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.contains(driver3.oid)).isTrue();
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.contains(driver4.oid)).isTrue();
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.contains(driver5.oid)).isTrue();
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.contains(driver6.oid)).isFalse();
			softly.assertThat(endorsementDriverAssignmentsResponse.assignableDrivers.contains(driver7.oid)).isFalse();
			softly.assertThat(assignmentExistsForDriverVehicle(driver1.oid, vehicle1.oid, endorsementDriverAssignmentsResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver2.oid, vehicle2.oid, endorsementDriverAssignmentsResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver2.oid, vehicle3.oid, endorsementDriverAssignmentsResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver3.oid, vehicle4.oid, endorsementDriverAssignmentsResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver3.oid, vehicle5.oid, endorsementDriverAssignmentsResponse.driverVehicleAssignments)).isTrue();
			softly.assertThat(assignmentExistsForDriverVehicle(driver3.oid, vehicle6.oid, endorsementDriverAssignmentsResponse.driverVehicleAssignments)).isTrue();
		});
	}

	/**Assigns any driver to all vehicles for CA Policy*/
	public static void makeAssignmentsForCA(String policyNumber) {
		if (getState().equals(Constants.States.CA)) {
			ViewDriverAssignmentResponse viewDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
			DriversDto driverToAssign = HelperCommon.viewEndorsementDrivers(policyNumber).driverList.get(0);
			for (String vehicleOid : viewDriverAssignmentResponse.unassignedVehicles) {
				HelperCommon.updateDriverAssignment(policyNumber, vehicleOid, Collections.singletonList(driverToAssign.oid));
			}
		}
	}
}
