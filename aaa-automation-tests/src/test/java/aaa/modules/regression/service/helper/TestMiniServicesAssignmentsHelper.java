package aaa.modules.regression.service.helper;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.PRIMARY_OPERATOR;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestMiniServicesAssignmentsHelper extends PolicyBaseTest {

	private AssignmentTab assignmentTab = new AssignmentTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private VehicleTab vehicleTab = new VehicleTab();
	private TestMiniServicesDriversHelper testMiniServicesDriversHelper= new TestMiniServicesDriversHelper();
	protected void pas10484_ViewDriverAssignmentService() {
		TestData td = createPolicyWithMoreThanOneDriver( "TestData_TwoDrivers");
		String policyNumber = openAppAndCreatePolicy(td);

		//Create a pended Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		List<TestData> assignmentsPrimary = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue();
		String driverAssignment1 = assignmentsPrimary.get(0).toString();
		String driverAssignment2 = assignmentsPrimary.get(1).toString();
		String driverNameFromAssignment1 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(0).getValue("Driver");
		String driverNameFromAssignment2 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(1).getValue("Driver");
		ViewDriversResponse responseViewDriver = HelperCommon.viewPolicyDrivers(policyNumber);
		String driverOid1 = responseViewDriver.driverList.stream().filter(driver -> driverNameFromAssignment1.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		String driverOid2 = responseViewDriver.driverList.stream().filter(driver -> driverNameFromAssignment2.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;

		ViewDriverAssignmentResponse response1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment1).contains(response1.driverVehicleAssignments.get(0).vehicleDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment1).contains(response1.driverVehicleAssignments.get(0).driverDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(0).driverOid).isEqualTo(driverOid1);
			softly.assertThat(response1.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment2).contains(response1.driverVehicleAssignments.get(1).vehicleDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(1).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment2).contains(response1.driverVehicleAssignments.get(1).driverDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(1).driverOid).isEqualTo(driverOid2);
			softly.assertThat(response1.driverVehicleAssignments.get(1).relationshipType).isEqualTo("primary");

			softly.assertThat(response1.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(response1.assignableDrivers.contains(driverOid2)).isTrue();

			softly.assertThat(response1.assignableVehicles.isEmpty()).isFalse();
			softly.assertThat(response1.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(response1.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(response1.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(response1.maxOneDriverPerVehicle).isEqualTo(false);
		});
		assignmentTab.saveAndExit();

		//add vehicle
		String newVin = "4S2CK58W8X4307498";
		String newVehOid = addVehicle(policyNumber, "2012-02-21", newVin);

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		String driverAssignment3 = assignmentsPrimary.get(0).toString();
		String driverAssignment4 = assignmentsPrimary.get(1).toString();
		String driverNameFromAssignment3 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(0).getValue("Driver");
		String driverNameFromAssignment4 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(1).getValue("Driver");

		ViewDriversResponse responseViewDriver2 = HelperCommon.viewPolicyDrivers(policyNumber);
		String driverOid3 = responseViewDriver2.driverList.stream().filter(driver -> driverNameFromAssignment3.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		String driverOid4 = responseViewDriver2.driverList.stream().filter(driver -> driverNameFromAssignment4.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;

		assertThat(driverOid1).isEqualTo(driverOid3);
		assertThat(driverOid2).isEqualTo(driverOid4);

		ViewDriverAssignmentResponse response2 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {

			softly.assertThat(driverAssignment3).contains(response2.driverVehicleAssignments.get(0).vehicleDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3).contains(response2.driverVehicleAssignments.get(0).driverDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(0).driverOid).isEqualTo(driverOid3);
			softly.assertThat(response2.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4).contains(response2.driverVehicleAssignments.get(1).vehicleDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(1).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4).contains(response2.driverVehicleAssignments.get(1).driverDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(1).driverOid).isEqualTo(driverOid4);
			softly.assertThat(response2.driverVehicleAssignments.get(1).relationshipType).isEqualTo("primary");

			softly.assertThat(response2.assignableDrivers.contains(driverOid3)).isTrue();
			softly.assertThat(response2.assignableDrivers.contains(driverOid4)).isTrue();

			softly.assertThat(response2.assignableVehicles.size()).isEqualTo(2);
			softly.assertThat(response2.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(response2.unassignedVehicles.size()).isEqualTo(1);
			softly.assertThat(response2.vehiclesWithTooManyDrivers.size()).isEqualTo(1);
			softly.assertThat(response2.maxOneDriverPerVehicle).isEqualTo(true);

		});
		assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getTable().getRow(1).getCell("Select Driver").controls.comboBoxes.getFirst()
				.setValue(driverNameFromAssignment3);
		Tab.buttonTopSave.click();

		List<TestData> assignmentOccasional = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment6 = assignmentOccasional.get(0).toString();

		ViewDriverAssignmentResponse response3 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {

			softly.assertThat(driverAssignment3).contains(response3.driverVehicleAssignments.get(0).vehicleDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3).contains(response3.driverVehicleAssignments.get(0).driverDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(0).driverOid).isEqualTo(driverOid3);
			softly.assertThat(response3.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4).contains(response3.driverVehicleAssignments.get(1).vehicleDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(1).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4).contains(response3.driverVehicleAssignments.get(1).driverDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(1).driverOid).isEqualTo(driverOid4);
			softly.assertThat(response3.driverVehicleAssignments.get(1).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment6).contains(response3.driverVehicleAssignments.get(2).vehicleDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(2).vehicleOid).isEqualTo(newVehOid);
			softly.assertThat(driverAssignment6).contains(response3.driverVehicleAssignments.get(2).driverDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(2).driverOid).isEqualTo(driverOid3);
			softly.assertThat(response3.driverVehicleAssignments.get(2).relationshipType).isEqualTo("occasional");

			softly.assertThat(response3.assignableDrivers.contains(driverOid3)).isTrue();
			softly.assertThat(response3.assignableDrivers.contains(driverOid4)).isTrue();

			softly.assertThat(response2.assignableVehicles.size()).isEqualTo(2);
			softly.assertThat(response2.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(response2.unassignedVehicles.isEmpty()).isFalse();
			softly.assertThat(response2.vehiclesWithTooManyDrivers.size()).isEqualTo(1);
			softly.assertThat(response2.maxOneDriverPerVehicle).isEqualTo(true);
		});

		//Check that View Vehicle service returns the same veh OID, that was added
		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);
		List<Vehicle> sortedVehicles = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		assertThat(viewEndorsementVehicleResponse2.vehicleList).containsAll(sortedVehicles);
		Vehicle newVehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> newVehOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle1.vehIdentificationNo).isEqualTo(newVin);
	}

	protected void pas14477_ViewDriverAssignment_NewDriver_Body() {
		TestData td = createPolicyWithMoreThanOneDriver("TestData_TwoDrivers");
		String policyNumber = openAppAndCreatePolicy(td);
		SearchPage.openPolicy(policyNumber);

		//Create a pended Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add new driver for PAS-14477
		String newDriverOid = addRegularDriver(policyNumber, "Justinio", "888963147");

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		List<TestData> assignmentsPrimary = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue();
		String driverAssignment1 = assignmentsPrimary.get(0).toString();
		String driverAssignment2 = assignmentsPrimary.get(1).toString();
		String driverAssignmentNewDriver = assignmentsPrimary.get(2).toString();
		String driverNameFromAssignment1 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(0).getValue("Driver");
		String driverNameFromAssignment2 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(1).getValue("Driver");
		ViewDriversResponse responseViewDriver = HelperCommon.viewPolicyDrivers(policyNumber);
		String driverOid1 = responseViewDriver.driverList.stream().filter(driver -> driverNameFromAssignment1.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		String driverOid2 = responseViewDriver.driverList.stream().filter(driver -> driverNameFromAssignment2.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;

		ViewDriverAssignmentResponse response1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment1).contains(response1.driverVehicleAssignments.get(0).vehicleDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment1).contains(response1.driverVehicleAssignments.get(0).driverDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(0).driverOid).isEqualTo(driverOid1);
			softly.assertThat(response1.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment2).contains(response1.driverVehicleAssignments.get(1).vehicleDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(1).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment2).contains(response1.driverVehicleAssignments.get(1).driverDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(1).driverOid).isEqualTo(driverOid2);
			softly.assertThat(response1.driverVehicleAssignments.get(1).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignmentNewDriver).contains(response1.driverVehicleAssignments.get(2).vehicleDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(2).vehicleOid).isNotNull();
			softly.assertThat(driverAssignmentNewDriver).contains(response1.driverVehicleAssignments.get(2).driverDisplayValue);
			softly.assertThat(response1.driverVehicleAssignments.get(2).driverOid).isEqualTo(newDriverOid);
			softly.assertThat(response1.driverVehicleAssignments.get(2).relationshipType).isEqualTo("primary");

			softly.assertThat(response1.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(response1.assignableDrivers.contains(driverOid2)).isTrue();
			softly.assertThat(response1.assignableDrivers.contains(newDriverOid)).isTrue();

			softly.assertThat(response1.assignableVehicles.isEmpty()).isFalse();
			softly.assertThat(response1.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(response1.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(response1.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(response1.maxOneDriverPerVehicle).isEqualTo(false);
		});
		assignmentTab.saveAndExit();

		String newVin = "4S2CK58W8X4307498";
		//add vehicle
		String newVehOid = addVehicle(policyNumber, "2012-02-21", "4S2CK58W8X4307498");

		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		String driverAssignment3 = assignmentsPrimary.get(0).toString();
		String driverAssignment4 = assignmentsPrimary.get(1).toString();
		String driverAssignment5 = assignmentsPrimary.get(2).toString();
		String driverNameFromAssignment3 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(0).getValue("Driver");
		String driverNameFromAssignment4 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(1).getValue("Driver");
		String driverNameFromAssignment5 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(2).getValue("Driver");
		ViewDriversResponse responseViewDriver2 = HelperCommon.viewPolicyDrivers(policyNumber);
		String driverOid3 = responseViewDriver2.driverList.stream().filter(driver -> driverNameFromAssignment3.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		String driverOid4 = responseViewDriver2.driverList.stream().filter(driver -> driverNameFromAssignment4.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;
		ViewDriversResponse responseViewDriver2Endorsement = HelperCommon.viewEndorsementDrivers(policyNumber);
		String driverOid5 = responseViewDriver2Endorsement.driverList.stream().filter(driver -> driverNameFromAssignment5.equals(driver.firstName + " " + driver.lastName)).findFirst().orElse(null).oid;

		assertThat(driverOid1).isEqualTo(driverOid3);
		assertThat(driverOid2).isEqualTo(driverOid4);
		assertThat(newDriverOid).isEqualTo(driverOid5);

		ViewDriverAssignmentResponse response2 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {

			softly.assertThat(driverAssignment3).contains(response2.driverVehicleAssignments.get(0).vehicleDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3).contains(response2.driverVehicleAssignments.get(0).driverDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(0).driverOid).isEqualTo(driverOid3);
			softly.assertThat(response2.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4).contains(response2.driverVehicleAssignments.get(1).vehicleDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(1).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4).contains(response2.driverVehicleAssignments.get(1).driverDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(1).driverOid).isEqualTo(driverOid4);
			softly.assertThat(response2.driverVehicleAssignments.get(1).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment5).contains(response2.driverVehicleAssignments.get(2).vehicleDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(2).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment5).contains(response2.driverVehicleAssignments.get(2).driverDisplayValue);
			softly.assertThat(response2.driverVehicleAssignments.get(2).driverOid).isEqualTo(driverOid5);
			softly.assertThat(response2.driverVehicleAssignments.get(2).relationshipType).isEqualTo("primary");

			softly.assertThat(response2.assignableDrivers.contains(driverOid3)).isTrue();
			softly.assertThat(response2.assignableDrivers.contains(driverOid4)).isTrue();
			softly.assertThat(response2.assignableDrivers.contains(driverOid5)).isTrue();

			softly.assertThat(response2.assignableVehicles.size()).isEqualTo(2);
			softly.assertThat(response2.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(response2.unassignedVehicles.size()).isEqualTo(1);
			softly.assertThat(response2.vehiclesWithTooManyDrivers.size()).isEqualTo(0);
			softly.assertThat(response2.maxOneDriverPerVehicle).isEqualTo(false);

		});
		assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getTable().getRow(1).getCell("Select Driver").controls.comboBoxes.getFirst()
				.setValue(driverNameFromAssignment3);
		Tab.buttonTopSave.click();

		List<TestData> assignmentOccasional = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment6 = assignmentOccasional.get(0).toString();

		ViewDriverAssignmentResponse response3 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {

			softly.assertThat(driverAssignment3).contains(response3.driverVehicleAssignments.get(0).vehicleDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3).contains(response3.driverVehicleAssignments.get(0).driverDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(0).driverOid).isEqualTo(driverOid3);
			softly.assertThat(response3.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4).contains(response3.driverVehicleAssignments.get(1).vehicleDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(1).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4).contains(response3.driverVehicleAssignments.get(1).driverDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(1).driverOid).isEqualTo(driverOid4);
			softly.assertThat(response3.driverVehicleAssignments.get(1).relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment6).contains(response3.driverVehicleAssignments.get(3).vehicleDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(3).vehicleOid).isEqualTo(newVehOid);
			softly.assertThat(driverAssignment6).contains(response3.driverVehicleAssignments.get(3).driverDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(3).driverOid).isEqualTo(driverOid3);
			softly.assertThat(response3.driverVehicleAssignments.get(3).relationshipType).isEqualTo("occasional");

			softly.assertThat(driverAssignment5).contains(response3.driverVehicleAssignments.get(2).vehicleDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(2).vehicleOid).isNotNull();
			softly.assertThat(driverAssignment5).contains(response3.driverVehicleAssignments.get(2).driverDisplayValue);
			softly.assertThat(response3.driverVehicleAssignments.get(2).driverOid).isEqualTo(driverOid5);
			softly.assertThat(response3.driverVehicleAssignments.get(2).relationshipType).isEqualTo("primary");

			softly.assertThat(response3.assignableDrivers.contains(driverOid3)).isTrue();
			softly.assertThat(response3.assignableDrivers.contains(driverOid4)).isTrue();
			softly.assertThat(response3.assignableDrivers.contains(driverOid5)).isTrue();

			softly.assertThat(response2.assignableVehicles.size()).isEqualTo(2);
			softly.assertThat(response2.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(response2.unassignedVehicles.isEmpty()).isFalse();
			softly.assertThat(response2.vehiclesWithTooManyDrivers.size()).isEqualTo(0);
			softly.assertThat(response2.maxOneDriverPerVehicle).isEqualTo(false);
		});

		//Check that View Vehicle service returns the same veh OID, that was added
		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);
		List<Vehicle> sortedVehicles = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		assertThat(viewEndorsementVehicleResponse2.vehicleList).containsAll(sortedVehicles);
		Vehicle newVehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> newVehOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle1.vehIdentificationNo).isEqualTo(newVin);
	}

	protected void pas11633_ViewDriverAssignmentAutoAssignService() {
		String policyNumber = openAppAndCreatePolicy();

		//Create a pended Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//add vehicle
		addVehicle(policyNumber, "2012-02-21", "4S2CK58W8X4307498");

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());

		String vehicle1 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getTable().getRow(1).getCell("Select Vehicle").controls.comboBoxes.getFirst()
				.getValue();
		List<TestData> assignmentsPrimary = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue();
		String driver1 = assignmentsPrimary.get(0).getValue("Driver");
		List<TestData> assignmentsPrimary1 = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String vehicle2 = assignmentsPrimary1.get(0).getValue("Excess Vehicle(s)");

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);
		List<Vehicle> sortedVehicles1 = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles1.sort(Vehicle.PENDING_ENDORSEMENT_COMPARATOR);
		String vehicleOid1 = viewEndorsementVehicleResponse2.vehicleList.get(0).oid;
		String vehicleOid2 = viewEndorsementVehicleResponse2.vehicleList.get(1).oid;

		ViewDriverAssignmentResponse response = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			assertThat(response.driverVehicleAssignments.get(0).vehicleDisplayValue).isEqualTo(vehicle1);
			assertThat(response.driverVehicleAssignments.get(0).vehicleOid).isEqualTo(vehicleOid2);
			assertThat(response.driverVehicleAssignments.get(0).driverOid).isNotEmpty();
			assertThat(response.driverVehicleAssignments.get(0).driverDisplayValue).isEqualTo(driver1);
			assertThat(response.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");

			assertThat(response.driverVehicleAssignments.get(1).vehicleDisplayValue).isEqualTo(vehicle2);
			assertThat(response.driverVehicleAssignments.get(1).vehicleOid).isEqualTo(vehicleOid1);
			assertThat(response.driverVehicleAssignments.get(1).driverOid).isNotEmpty();
			assertThat(response.driverVehicleAssignments.get(1).driverDisplayValue).isEqualTo(driver1);
			assertThat(response.driverVehicleAssignments.get(1).relationshipType).isEqualTo("occasional");

			softly.assertThat(response.assignableDrivers.size()).isEqualTo(1);
			softly.assertThat(response.assignableVehicles.size()).isEqualTo(2);
			softly.assertThat(response.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(response.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(response.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(response.maxOneDriverPerVehicle).isEqualTo(true);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule1Body1(PolicyType policyType) {
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		TestData td = createPolicyWithMoreThanOneDriver( "TestData_TwoDrivers");

		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));

			//Get Drivers oid
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");

			//add V2
			String vehicleOid2 = addVehicle(policyNumber, "2012-02-21", "1HGEM21504L055795");

			//Update: V2-->D1 ,Check V2-->D1, V1-->D2
			DriverAssignments updDriverAssignee1 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid2, Arrays.asList(driver1.oid));
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicleOid2);
			softly.assertThat(updDriverAssignee1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");

			//check the last part of the response
			softly.assertThat(updDriverAssignee1.getAssignableDrivers().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee1.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Update: V1-->D1, D2, Check V1-->D1,D2, V2-->Unn
			DriverAssignments updDriverAssignee2 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle1.oid, Arrays.asList(driver1.oid, driver2.oid));
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle1.oid)
					.addUnassignedVehicles(vehicleOid2);
			softly.assertThat(updDriverAssignee2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedVehicles");
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedVehicles");

			//check the last part of the response
			softly.assertThat(updDriverAssignee2.getAssignableDrivers().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee2.getAssignableVehicles().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee2.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.getUnassignedVehicles().contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee2.getVehiclesWithTooManyDrivers().contains(vehicle1.oid)).isTrue();
			softly.assertThat(updDriverAssignee2.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getCode(), ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getMessage());

			//Update: V2-->D2, V1-->D1, V2-->D2
			DriverAssignments updDriverAssignee3 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid2, Arrays.asList(driver2.oid));
			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicleOid2);
			softly.assertThat(updDriverAssignee3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments");
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments");

			//check the last part of the response
			softly.assertThat(updDriverAssignee3.getAssignableDrivers().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee3.getAssignableVehicles().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee3.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas15529_UpdateDriverAssignmentServiceRule1Body2(PolicyType policyType) {
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_FourDrivers", "TestData_TwoVehicles", "AssignmentTabFourDrivers");

		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Vehicle Oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));

			//add V3 and V4
			String vehicleOid3 = addVehicle(policyNumber, "2012-02-21", "1NXBR32E53Z168489");
			String vehicleOid4 = addVehicle(policyNumber, "2012-02-21", "1G2NE52T9XM924276");

			//Get Drivers oid
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");
			DriversDto driver3 = findDriver(td, dResponse, 2, "DriverTab");
			DriversDto driver4 = findDriver(td, dResponse, 3, "DriverTab");

			//Update: V3-->D1, Check V1-->D2 V2-->D3,D4, V3-->D1, V4-->Unn
			DriverAssignments updDriverAssignee1 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid3, Arrays.asList(driver1.oid));
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driver3.oid, vehicle2.oid)
					.addAssignment(driver4.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicleOid3)
					.addUnassignedVehicles(vehicleOid4);
			softly.assertThat(updDriverAssignee1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments", "unassignedVehicles");
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments", "unassignedVehicles");

			//check the last part of the response
			softly.assertThat(updDriverAssignee1.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee1.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.getUnassignedVehicles().size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee1.getVehiclesWithTooManyDrivers().contains(vehicle2.oid)).isTrue();
			softly.assertThat(updDriverAssignee1.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getCode(), ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getMessage());

			//Update: V4-->D2, Check V1-->Unn, V2-->D4,D3 V3-->D1, V4-->D2
			DriverAssignments updDriverAssignee2 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid4, Arrays.asList(driver2.oid));
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicleOid3)
					.addAssignment(driver2.oid, vehicleOid4)
					.addAssignment(driver4.oid, vehicle2.oid)
					.addAssignment(driver3.oid, vehicle2.oid)
					.addUnassignedVehicles(vehicle1.oid);
			softly.assertThat(updDriverAssignee2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedVehicles");
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedVehicles");

			//check the last part of the response
			softly.assertThat(updDriverAssignee2.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee2.getAssignableVehicles().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee2.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.getUnassignedVehicles().size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee2.getVehiclesWithTooManyDrivers().contains(vehicle2.oid)).isTrue();
			softly.assertThat(updDriverAssignee2.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getCode(), ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getMessage());

			//Update: V2-->D3, Check V1-->Unn, V2-->D3, V3-->D1, V4-->D2, D4-->Unn
			DriverAssignments updDriverAssignee3 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle2.oid, Arrays.asList(driver3.oid));
			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver3.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicleOid3)
					.addAssignment(driver2.oid, vehicleOid4)
					.addUnassignedDrivers(driver4.oid)
					.addUnassignedVehicles(vehicle1.oid);
			softly.assertThat(updDriverAssignee3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedVehicles", "unassignedDrivers");
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedVehicles", "unassignedDrivers");

			//check the last part of the response
			softly.assertThat(updDriverAssignee3.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee3.getAssignableVehicles().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee3.getUnassignedDrivers().size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee3.getUnassignedVehicles().size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee3.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage());

			//Update: V1-->D3, Check V1-->D3, V2-->Unn, V3-->D1, V4-->D2, D4-->Unn
			DriverAssignments updDriverAssignee4 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle1.oid, Arrays.asList(driver3.oid));
			DriverAssignments driverAssignmentResponse4 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch4 = new DriverAssignments()
					.addAssignment(driver3.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicleOid3)
					.addAssignment(driver2.oid, vehicleOid4)
					.addUnassignedDrivers(driver4.oid)
					.addUnassignedVehicles(vehicle2.oid);
			softly.assertThat(updDriverAssignee4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments", "unassignedVehicles", "unassignedDrivers");
			softly.assertThat(driverAssignmentResponse4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments", "unassignedVehicles", "unassignedDrivers");

			//check the last part of the response
			softly.assertThat(updDriverAssignee4.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee4.getAssignableVehicles().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee4.getUnassignedDrivers().size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee4.getUnassignedVehicles().size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee4.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage());

			//Update: V2-->D4, Check V1-->D3, V2-->D4, V3-->D1, V4-->D2
			DriverAssignments updDriverAssignee5 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle2.oid, Arrays.asList(driver4.oid));
			DriverAssignments driverAssignmentResponse5 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch5 = new DriverAssignments()
					.addAssignment(driver3.oid, vehicle1.oid)
					.addAssignment(driver4.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicleOid3)
					.addAssignment(driver2.oid, vehicleOid4);
			softly.assertThat(updDriverAssignee5).isEqualToComparingOnlyGivenFields(toMatch5, "driverVehicleAssignments");
			softly.assertThat(driverAssignmentResponse5).isEqualToComparingOnlyGivenFields(toMatch5, "driverVehicleAssignments");

			//check the last part of the response
			softly.assertThat(updDriverAssignee5.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee5.getAssignableVehicles().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee5.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule2Body1(PolicyType policyType) {
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_TwoDrivers", "TestData_ThreeVehicles", "AssignmentTab");

		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Vehicle Oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));
			Vehicle vehicle3 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(2).getValue("VIN"));

			//add V4
			String vehicleOid4 = addVehicle(policyNumber, "2012-05-11", "1NXBR32E53Z168489");
			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA.getMessage());

			//Get Drivers oid
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");

			//Update: V4-->D1, Check V1-->D1, V2-->D2, V3-->D1, V4-->D1
			DriverAssignments updDriverAssignee1 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid4, Arrays.asList(driver1.oid));
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch1 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicle3.oid)
					.addAssignment(driver1.oid, vehicleOid4);
			softly.assertThat(updDriverAssignee1).isEqualToComparingOnlyGivenFields(toMatch1, "driverVehicleAssignments");
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch1, "driverVehicleAssignments");

			//Check the second part of the response
			softly.assertThat(updDriverAssignee1.getAssignableDrivers().contains(driver1.oid)).isTrue();
			softly.assertThat(updDriverAssignee1.getAssignableDrivers().contains(driver2.oid)).isTrue();
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().contains(vehicle1.oid)).isTrue();
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().contains(vehicle2.oid)).isTrue();
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().contains(vehicle3.oid)).isTrue();
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().contains(vehicleOid4)).isTrue();
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee1.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.isMaxOneDriverPerVehicle()).isEqualTo(true);

			//Update V2-->D1 Check V1-->D1, V2-->D1, V3-->D1, V4-->D1
			DriverAssignments updDriverAssignee2 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle2.oid, Arrays.asList(driver1.oid));
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicle3.oid)
					.addAssignment(driver1.oid, vehicleOid4)
					.addUnassignedDrivers(driver2.oid);
			softly.assertThat(updDriverAssignee2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers");
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers");

			//Check the second part of the response
			softly.assertThat(updDriverAssignee2.getAssignableVehicles().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee2.getAssignableDrivers().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee2.getUnassignedDrivers().size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee2.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage());

			//Update V2-->D2, Check V1-->D1, V2-->D2, V3-->D1, V4-->D1
			DriverAssignments updDriverAssignee3 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle2.oid, Arrays.asList(driver2.oid));
			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicle3.oid)
					.addAssignment(driver1.oid, vehicleOid4);
			softly.assertThat(updDriverAssignee3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments");
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments");

			//Check the second part of the response
			softly.assertThat(updDriverAssignee3.getAssignableVehicles().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee3.getAssignableDrivers().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee3.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.isMaxOneDriverPerVehicle()).isTrue();

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule2Body2(PolicyType policyType) {
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle( "TestData_ThreeDrivers", "TestData_ThreeVehicles", "AssignmentTabThreeDrivers");

		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Vehicle Oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));
			Vehicle vehicle3 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(2).getValue("VIN"));

			//add V4 and V5
			String vehicleOid4 = addVehicle(policyNumber, "2012-02-21", "1NXBR32E53Z168489");
			String vehicleOid5 = addVehicle(policyNumber, "2015-01-11", "JTDKN3DU0E0356920");

			//Get Drivers oid
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");
			DriversDto driver3 = findDriver(td, dResponse, 2, "DriverTab");

			//Update V4-->D1 AND V5-->D2 (V1-->D1, V2-->D2, V3-->D3, V4-->D1, V5-->D2)
			HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid5, Arrays.asList(driver2.oid));
			DriverAssignments updDriverAssignee1 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid4, Arrays.asList(driver1.oid));
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver3.oid, vehicle3.oid)
					.addAssignment(driver1.oid, vehicleOid4)
					.addAssignment(driver2.oid, vehicleOid5);
			softly.assertThat(updDriverAssignee1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");

			//check the last part of the response
			softly.assertThat(updDriverAssignee1.getAssignableDrivers().size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().size()).isEqualTo(5);
			softly.assertThat(updDriverAssignee1.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Update D1-->All V, (V1-->D1, V2-->D1,  V3-->D1, V4-->D1, V5-->D1, D2,D3-->Unn)
			HelperCommon.updateDriverAssignment2(policyNumber, vehicle2.oid, Arrays.asList(driver1.oid));
			HelperCommon.updateDriverAssignment2(policyNumber, vehicle3.oid, Arrays.asList(driver1.oid));
			HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid4, Arrays.asList(driver1.oid));
			DriverAssignments updDriverAssignee2 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid5, Arrays.asList(driver1.oid));
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicle3.oid)
					.addAssignment(driver1.oid, vehicleOid4)
					.addAssignment(driver1.oid, vehicleOid5)
					.addUnassignedDrivers(driver2.oid, driver3.oid);
			softly.assertThat(updDriverAssignee2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers");
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers");

			//Check the second part of the response
			softly.assertThat(updDriverAssignee2.getAssignableDrivers().size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee2.getAssignableVehicles().size()).isEqualTo(5);
			softly.assertThat(updDriverAssignee2.getUnassignedDrivers().size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee2.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage());

			//Update V1,V2-->D2, V3,V4,V5-->D3, (V1-->D2, V2-->D2,  V3-->D3, V4-->D3, V5-->D3, D1 -->Unn)
			HelperCommon.updateDriverAssignment2(policyNumber, vehicle1.oid, Arrays.asList(driver2.oid));
			HelperCommon.updateDriverAssignment2(policyNumber, vehicle2.oid, Arrays.asList(driver2.oid));
			HelperCommon.updateDriverAssignment2(policyNumber, vehicle3.oid, Arrays.asList(driver3.oid));
			HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid4, Arrays.asList(driver3.oid));
			DriverAssignments updDriverAssignee3 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid5, Arrays.asList(driver3.oid));

			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver3.oid, vehicle3.oid)
					.addAssignment(driver3.oid, vehicleOid4)
					.addAssignment(driver3.oid, vehicleOid5)
					.addUnassignedDrivers(driver1.oid);
			softly.assertThat(updDriverAssignee3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedDrivers");
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedDrivers");

			//Check the second part of the response
			softly.assertThat(updDriverAssignee3.getAssignableDrivers().size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee3.getAssignableVehicles().size()).isEqualTo(5);
			softly.assertThat(updDriverAssignee3.getUnassignedDrivers().size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee3.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage());

			//Update V5-->D1, (V1-->D2, V2-->D2,  V3-->D3, V4-->D3, V5-->D1)
			DriverAssignments updDriverAssignee4 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid5, Arrays.asList(driver1.oid));
			DriverAssignments driverAssignmentResponse4 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch4 = new DriverAssignments()
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver3.oid, vehicle3.oid)
					.addAssignment(driver3.oid, vehicleOid4)
					.addAssignment(driver1.oid, vehicleOid5);
			softly.assertThat(updDriverAssignee4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments");
			softly.assertThat(driverAssignmentResponse4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments");

			//Check the second part of the response
			softly.assertThat(updDriverAssignee4.getAssignableDrivers().size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee4.getAssignableVehicles().size()).isEqualTo(5);
			softly.assertThat(updDriverAssignee4.getUnassignedDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.getUnassignedVehicles().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.isMaxOneDriverPerVehicle()).isEqualTo(true);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule3Body() {
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_FourDrivers", "TestData_TwoVehicles", "AssignmentTabFourDrivers");

		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);
			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Add V3
			String vehicleOid3 = addVehicle(policyNumber, "2012-02-21", "1NXBR32E53Z168489");

			//Get Drivers oid
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");
			DriversDto driver3 = findDriver(td, dResponse, 2, "DriverTab");
			DriversDto driver4 = findDriver(td, dResponse, 3, "DriverTab");

			//Vehicle Oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));

			//Update and check DA: V3-->D1, Check V1-->D2, V2-->D3, D4, V3-->D1
			DriverAssignments updDriverAssignee1 = HelperCommon.updateDriverAssignment2(policyNumber, vehicleOid3, Arrays.asList(driver1.oid));
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver1.oid, vehicleOid3)
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driver3.oid, vehicle2.oid)
					.addAssignment(driver4.oid, vehicle2.oid);
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");
			softly.assertThat(updDriverAssignee1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");

			//check the last part of the response
			softly.assertThat(updDriverAssignee1.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee1.getAssignableVehicles().size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee1.getUnassignedDrivers().isEmpty());
			softly.assertThat(updDriverAssignee1.getUnassignedVehicles().isEmpty());
			softly.assertThat(updDriverAssignee1.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.isMaxOneDriverPerVehicle()).isEqualTo(false);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Update: V2-->D2, Check V1-->Unn, V2-->D2, V3-->D1, D3,D4-->Unn
			DriverAssignments updDriverAssignee2 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle2.oid, Arrays.asList(driver2.oid));
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicleOid3)
					.addUnassignedDrivers(driver3.oid, driver4.oid)
					.addUnassignedVehicles(vehicle1.oid);
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers", "unassignedVehicles");
			softly.assertThat(updDriverAssignee2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers", "unassignedVehicles");

			//check the last part of the response
			softly.assertThat(driverAssignmentResponse2.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(driverAssignmentResponse2.getAssignableVehicles().size()).isEqualTo(3);
			softly.assertThat(driverAssignmentResponse2.getUnassignedDrivers().size()).isEqualTo(2);
			softly.assertThat(driverAssignmentResponse2.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(driverAssignmentResponse2.isMaxOneDriverPerVehicle()).isEqualTo(false);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());

			//Update: V1-->D3,D4,D2 Check V2-->Unn, V3-->D1, V1-->D3,D4,D2
			DriverAssignments updDriverAssignee3 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle1.oid, Arrays.asList(driver3.oid, driver4.oid, driver2.oid));
			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver3.oid, vehicle1.oid)
					.addAssignment(driver4.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicleOid3)
					.addUnassignedVehicles(vehicle2.oid);
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedVehicles");
			softly.assertThat(updDriverAssignee3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedVehicles");

			//check the last part of the response
			softly.assertThat(updDriverAssignee3.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee3.getAssignableVehicles().size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee3.getUnassignedDrivers().isEmpty());
			softly.assertThat(updDriverAssignee3.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.isMaxOneDriverPerVehicle()).isEqualTo(false);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());

			//Update: V2-->D3 Check V2-->D3, V3-->D1, V1-->D4,D2
			DriverAssignments updDriverAssignee4 = HelperCommon.updateDriverAssignment2(policyNumber, vehicle2.oid, Arrays.asList(driver3.oid));
			DriverAssignments driverAssignmentResponse4 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch4 = new DriverAssignments()
					.addAssignment(driver4.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driver3.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicleOid3);
			softly.assertThat(driverAssignmentResponse4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments");
			softly.assertThat(updDriverAssignee4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments");

			//check the last part of the response
			softly.assertThat(updDriverAssignee4.getAssignableDrivers().size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee4.getAssignableVehicles().size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee4.getUnassignedDrivers().isEmpty());
			softly.assertThat(updDriverAssignee4.getUnassignedVehicles().isEmpty());
			softly.assertThat(updDriverAssignee4.getVehiclesWithTooManyDrivers().isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.isMaxOneDriverPerVehicle()).isEqualTo(false);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas11684_DriverAssignmentExistsForStateBody(String state, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//View driver assignment if VA
		if ("VA".equals(state) || "CA".equals(state) || "NY".equals(state)) {
			ViewDriverAssignmentResponse responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");
		} else {
			ErrorResponseDto responseDriverAssignment = HelperCommon.viewEndorsementAssignmentsError(policyNumber, 422);
			softly.assertThat(responseDriverAssignment.errorCode).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getCode());
			softly.assertThat(responseDriverAssignment.message).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getMessage());
		}
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas14539_transactionInfoUpdateDriverAssignmentBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_TwoDrivers", "TestData_TwoVehicles", "AssignmentTab_Two_Driver");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);

		String policyNumber = openAppAndCreatePolicy(td);
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//get vehicles oid's
		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
		Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));

		//get drivers oid's
		ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
		DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");

		//Update: V2-->D1 ,Check V2-->D1, V1-->D2
		HelperCommon.updateDriverAssignment(policyNumber, vehicle2.oid, Collections.singletonList(driver1.oid));
		HelperCommon.updateDriverAssignment(policyNumber, vehicle1.oid, Collections.singletonList(driver2.oid));

		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh1 = policyResponse.vehicles.get(vehicle1.oid);
		ComparableVehicle veh2 = policyResponse.vehicles.get(vehicle2.oid);

		checkEndorsementChangeLogForVehicle(veh1, driver1.oid, "REMOVED", "primary", driver1.firstName);
		checkEndorsementChangeLogForVehicle(veh1, driver2.oid, "ADDED", "primary", driver2.firstName);
		checkEndorsementChangeLogForVehicle(veh2, driver1.oid, "ADDED", "primary", driver1.firstName);
		checkEndorsementChangeLogForVehicle(veh2, driver2.oid, "REMOVED", "primary", driver2.firstName);
	}

	protected void pas14539_transactionInfoDriverAssignmentBody() {
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		String policyNumber = openAppAndCreatePolicy();

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//add vehicle
		String vehicleOid1 = addVehicle(policyNumber, "2012-02-21", "4S2CK58W8X4307498" );

		ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);

		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh1 = policyResponse.vehicles.get(vehicleOid1);
		checkEndorsementChangeLogForVehicle(veh1, driver1.oid, "ADDED", "occasional", driver1.firstName);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas15540_RemoveDriverAssignedToTrailerBody() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_TwoDrivers", "TestData_VehicleTrailer", null);
		//adjust test data to override errors for NJ and NY
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		if (Constants.States.NJ.contains(getState()) || Constants.States.NY.contains(getState())) {
			td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		}
		String policyNumber = openAppAndCreatePolicy(td);

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		DriversDto driverNotFNI = testMiniServicesDriversHelper.getAnyNotNIActiveDriver(policyNumber);
		DriversDto driverFNI = testMiniServicesDriversHelper.getFNIDriver(policyNumber);
		//Validate that Trailer, Motor Home, Golf Cart are not assigned to FNI before removal of driver (precondition)
		validateVehicleTab_pas15540(driverFNI, false);

		HelperCommon.removeDriver(policyNumber, driverNotFNI.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
		SearchPage.openPolicy(policyNumber);
		validateVehicleTab_pas15540(driverFNI, true);
		vehicleTab.cancel();
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas21199_ViewDriverAssignmentAddRemoveActionsRule1Body(){
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle( "TestData_ThreeDrivers", "TestData_ThreeVehicles", "AssignmentTabThreeDrivers");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");
			DriversDto driver3 = findDriver(td, dResponse, 2, "DriverTab");

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));
			Vehicle vehicle3 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(2).getValue("VIN"));

			//Remove V1, V2, D3
			HelperCommon.deleteVehicle(policyNumber, vehicle1.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			HelperCommon.deleteVehicle(policyNumber, vehicle2.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			HelperCommon.removeDriver(policyNumber, driver3.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));

			//Check DA: D1-->V3, D2-->V3
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle3.oid)
					.addAssignment(driver2.oid, vehicle3.oid);
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//TC2
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Delete D2, D3
			HelperCommon.removeDriver(policyNumber, driver2.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
			HelperCommon.removeDriver(policyNumber, driver3.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));

			//Check DA: D1-->V1, V2, V3 and rate.
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicle3.oid);
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Prepare for other TC when we have 2V and 2D
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			HelperCommon.removeDriver(policyNumber, driver3.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
			HelperCommon.deleteVehicle(policyNumber, vehicle3.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			helperMiniServices. endorsementRateAndBind(policyNumber);

			//TC3
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Add D3, V3
			String driverOid3 = addRegularDriver(policyNumber, "Jovita", "123456852");
			String vehicleOid3 = addVehicle(policyNumber, "2013-02-22", "5YMGY0C57C1661237");

			//Check D1-->V1, D2-->V2, D3, V3-->Unn
			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addUnassignedDrivers(driverOid3)
					.addUnassignedVehicles(vehicleOid3);
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedDrivers", "unassignedVehicles");

			//Update D3-->V3
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid3, Arrays.asList(driverOid3));
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//TC4
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			String replacedVeh1 = replaceVehicleWithUpdates(policyNumber, vehicle1.oid, "1HGFA16526L081415", true, true);

			//Check D1-->V1, D2-->V2
			DriverAssignments driverAssignmentResponse4 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch4 = new DriverAssignments()
					.addAssignment(driver1.oid, replacedVeh1)
					.addAssignment(driver2.oid, vehicle2.oid);
			softly.assertThat(driverAssignmentResponse4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
		});
	}

	protected void pas21199_ViewDriverAssignmentAddRemoveActionsRule3Part1Body() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_TwoDrivers", "TestData_FourVehicles", "AssignmentTabFourDriversTwoVehicles");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));
			Vehicle vehicle3 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(2).getValue("VIN"));
			Vehicle vehicle4 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(3).getValue("VIN"));

			//Remove V2
			HelperCommon.deleteVehicle(policyNumber, vehicle2.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			//Check DA: D1-->V1, D2-->V3,V4
			DriverAssignments driverAssignmentResponse = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle3.oid)
					.addAssignment(driver2.oid, vehicle4.oid);
			softly.assertThat(driverAssignmentResponse).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Remove D2
			HelperCommon.removeDriver(policyNumber, driver2.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));

			//Check D1-->All
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicle3.oid)
					.addAssignment(driver1.oid, vehicle4.oid);
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Replace V1, delete V2, add D2
			String replacedVehicle1 = replaceVehicleWithUpdates(policyNumber, vehicle1.oid, "1C3EL55R85N699121",  true, true);
			HelperCommon.deleteVehicle(policyNumber, vehicle2.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			String driverOid3 = addRegularDriver(policyNumber, "Maria", "888456321");

			//Check D1-->V1, D2-->V3,V4, D3-->Unn
			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver1.oid, replacedVehicle1)
					.addAssignment(driver2.oid, vehicle3.oid)
					.addAssignment(driver2.oid, vehicle4.oid)
					.addUnassignedDrivers(driverOid3);
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedDrivers");

			//Update D3-->V4
			HelperCommon.updateDriverAssignment(policyNumber, vehicle4.oid, Arrays.asList(driverOid3));
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Prepare for next TC: D1-->V1,V2,V3, D2-->V4,V5,V6
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			String vehicleOid5 = addVehicle(policyNumber, "2016-05-22", "5YMGY0C57C1661237");
			String vehicleOid6 = addVehicle(policyNumber, "2017-05-10", "1HGFA16526L081415");

			HelperCommon.updateDriverAssignment(policyNumber, vehicle3.oid, Arrays.asList(driver1.oid));
			HelperCommon.updateDriverAssignment(policyNumber, vehicle4.oid, Arrays.asList(driver2.oid));
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid5, Arrays.asList(driver2.oid));
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid6, Arrays.asList(driver2.oid));
			helperMiniServices.endorsementRateAndBind(policyNumber);

			//TC2
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Remove D2, V2, V3
			HelperCommon.removeDriver(policyNumber, driver2.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
			HelperCommon.deleteVehicle(policyNumber, vehicle2.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			HelperCommon.deleteVehicle(policyNumber, vehicle3.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			//Check: D1-->V1,V4,V5,V6
			DriverAssignments driverAssignmentResponse4 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch4 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicle4.oid)
					.addAssignment(driver1.oid, vehicleOid5)
					.addAssignment(driver1.oid, vehicleOid6);
			softly.assertThat(driverAssignmentResponse4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments");
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas21199_ViewDriverAssignmentAddRemoveActionsRule3Part2Body() {
		TestData td = createPolicyWithMoreThanOneVehicle( "TestData_TwoVehicles");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));

			//Replace V1, V2, and after add V3
			String replacedVehicle1 = replaceVehicleWithUpdates(policyNumber, vehicle1.oid, "1C3EL55R85N699121",  true, true);
			String replacedVehicle2 = replaceVehicleWithUpdates(policyNumber, vehicle2.oid, "3VWRC29M6YM079860",  true, true);
			String vehicleOidSt3 = addVehicle(policyNumber, "2014-05-16", "2GTEC19V531282646");

			//Check DA: D1-->V1,V2,V3
			DriverAssignments driverAssignmentResponse = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver1.oid, replacedVehicle1)
					.addAssignment(driver1.oid, replacedVehicle2)
					.addAssignment(driver1.oid, vehicleOidSt3);
			softly.assertThat(driverAssignmentResponse).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			String vehicleOidNd3 = addVehicle(policyNumber, "2014-05-16", "2GTEC19V531282646");
			String driverOid2 = addRegularDriver(policyNumber, "Mia", "111658745");

			//Check D1-->V2,V3; D2-->Unn
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicleOidNd3)
					.addUnassignedDrivers(driverOid2);
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers");

			//Update D2-->V3
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOidNd3, Arrays.asList(driverOid2));
			HelperCommon.orderReports(policyNumber, driverOid2, OrderReportsResponse.class, 200);
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas21199_ViewDriverAssignmentAddRemoveActionsRule3Part3Body() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_ThreeDrivers", "TestData_FourVehicles", "AssignmentTabThreeDriversFourVehicles");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");
			DriversDto driver3 = findDriver(td, dResponse, 2, "DriverTab");

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));
			Vehicle vehicle3 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(2).getValue("VIN"));
			Vehicle vehicle4 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(3).getValue("VIN"));

			//Remove V1
			HelperCommon.deleteVehicle(policyNumber, vehicle1.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			//Check: V2-->D2, V3-->D3, V4-->D1
			DriverAssignments driverAssignmentResponse = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle4.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver3.oid, vehicle3.oid);
			softly.assertThat(driverAssignmentResponse).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Remove V2
			HelperCommon.deleteVehicle(policyNumber, vehicle2.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			//Check: V1-->D1, V3-->D3, V4-->D1, D2-->Unn
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver3.oid, vehicle3.oid)
					.addAssignment(driver1.oid, vehicle4.oid)
					.addUnassignedDrivers(driver2.oid);
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers");

			//Update D2-->V4
			HelperCommon.updateDriverAssignment(policyNumber, vehicle4.oid, Arrays.asList(driver2.oid));
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Remove V3
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			HelperCommon.deleteVehicle(policyNumber, vehicle3.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			//Check: V1-->D1, V2-->D2, V4-->D1, D3-->Unn
			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver1.oid, vehicle4.oid)
					.addUnassignedDrivers(driver3.oid);
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments", "unassignedDrivers");

			//Update D3-->V4
			HelperCommon.updateDriverAssignment(policyNumber, vehicle4.oid, Arrays.asList(driver3.oid));
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Remove V4
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			HelperCommon.deleteVehicle(policyNumber, vehicle4.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			//Check: V1-->D1, V2-->D2, V3-->D3
			DriverAssignments driverAssignmentResponse4 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch4 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addAssignment(driver3.oid, vehicle3.oid);
			softly.assertThat(driverAssignmentResponse4).isEqualToComparingOnlyGivenFields(toMatch4, "driverVehicleAssignments");

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas21199_ViewDriverAssignmentAddRemoveActionsRule2Part1Body(){
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_FourDrivers", "TestData_ThreeVehicles", "AssignmentTabFourDriversThreeVehicles");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");
			DriversDto driver3 = findDriver(td, dResponse, 2, "DriverTab");
			DriversDto driver4 = findDriver(td, dResponse, 3, "DriverTab");

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));
			Vehicle vehicle3 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(2).getValue("VIN"));

			//Remove D2
			HelperCommon.removeDriver(policyNumber, driver2.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));

			//Check D1-->V1, D3-->V2, D4-->V3
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver3.oid, vehicle2.oid)
					.addAssignment(driver4.oid, vehicle3.oid);
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch, "driverVehicleAssignments");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//TC2
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Delete V2 and after D3
			HelperCommon.deleteVehicle(policyNumber, vehicle2.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			HelperCommon.removeDriver(policyNumber, driver3.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));

			//Check D1-->V1, D2-->Unn, D4-->V3
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver4.oid, vehicle3.oid)
					.addUnassignedDrivers(driver2.oid);
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments", "unassignedDrivers");

			//Update D2-->V1
			HelperCommon.updateDriverAssignment(policyNumber, vehicle1.oid, Arrays.asList(driver2.oid, driver1.oid));
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//TC3
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Delete V2, V3
			HelperCommon.deleteVehicle(policyNumber, vehicle2.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			HelperCommon.deleteVehicle(policyNumber, vehicle3.oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			//Check V1-->All D
			DriverAssignments driverAssignmentResponse3 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch3 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driver3.oid, vehicle1.oid)
					.addAssignment(driver4.oid, vehicle1.oid);
			softly.assertThat(driverAssignmentResponse3).isEqualToComparingOnlyGivenFields(toMatch3, "driverVehicleAssignments");
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas21199_ViewDriverAssignmentAddRemoveActionsRule2Part2Body() {
		TestData td = createPolicyWithMoreThanOneDriverAndVehicle("TestData_ThreeDrivers", "TestData_TwoVehicles", "AssignmentTabThreeDriversTwoVehicles");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");
			DriversDto driver3 = findDriver(td, dResponse, 2, "DriverTab");

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));
			Vehicle vehicle2 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(1).getValue("VIN"));

			//Remove D3 add V3
			HelperCommon.removeDriver(policyNumber, driver3.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
			String vehicleOid3 = addVehicle(policyNumber, "2016-02-22", "5YMGY0C57C1661237");

			//Check D1-->V1, D2-->V2, V3-->Unn
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch1 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle2.oid)
					.addUnassignedVehicles(vehicleOid3);
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch1, "driverVehicleAssignments", "unassignedVehicles");

			//Update D2-->V3
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid3, Arrays.asList(driver2.oid));
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//TC2 need policy with 2V and 5D
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			String driverOid4 = addRegularDriver(policyNumber, "Saule", "888963147");
			String driverOid5 = addRegularDriver(policyNumber, "Megha", "811163147");

			//Update V1-->D1,D2,D3, V2-->D4,D5
			HelperCommon.updateDriverAssignment(policyNumber, vehicle1.oid, Arrays.asList(driver1.oid, driver2.oid, driver3.oid));
			HelperCommon.updateDriverAssignment(policyNumber, vehicle2.oid, Arrays.asList(driverOid4, driverOid5));
			HelperCommon.orderReports(policyNumber, driverOid4, OrderReportsResponse.class, 200);
			HelperCommon.orderReports(policyNumber, driverOid5, OrderReportsResponse.class, 200);
			helperMiniServices.endorsementRateAndBind(policyNumber);

			//Start TC2
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Remove D2, D3
			HelperCommon.removeDriver(policyNumber, driver2.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
			HelperCommon.removeDriver(policyNumber, driverOid4, DXPRequestFactory.createRemoveDriverRequest("RD1001"));

			//Check V1-->D2,D3 V2-->D5
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver3.oid, vehicle1.oid)
					.addAssignment(driverOid5, vehicle2.oid);
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments");
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas21199_ViewDriverAssignmentAddRemoveActionsRule2Part3Body() {
		TestData td = createPolicyWithMoreThanOneDriver("TestData_TwoDrivers");
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			String policyNumber = openAppAndCreatePolicy(td);

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = findDriver(getStateTestData(customerData, "DataGather", "TestData"), dResponse, 0, "GeneralTab", true);
			DriversDto driver2 = findDriver(td, dResponse, 1, "DriverTab");

			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, td.getTestDataList("VehicleTab").get(0).getValue("VIN"));

			//Add D3, D4, V2
			String driverOid3 = addRegularDriver(policyNumber, "Maja", "000213654");
			String driverOid4 = addRegularDriver(policyNumber, "Cingingy", "000454554");
			String vehicleOidSt2 = addVehicle(policyNumber, "2017-02-22", "4S2CK58W8X4307498");

			//Check D1-->V1, D2-->V1, D3-->V1, D4-->V1, V2-->Unn
			DriverAssignments driverAssignmentResponse1 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch1 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver2.oid, vehicle1.oid)
					.addAssignment(driverOid3, vehicle1.oid)
					.addAssignment(driverOid4, vehicle1.oid)
					.addUnassignedVehicles(vehicleOidSt2);
			softly.assertThat(driverAssignmentResponse1).isEqualToComparingOnlyGivenFields(toMatch1, "driverVehicleAssignments", "unassignedVehicles");

			//Update D2-->V2, rate.
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOidSt2, Arrays.asList(driver2.oid));
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//TC2
			//Create Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Delete D2 and add V2
			HelperCommon.removeDriver(policyNumber, driver2.oid, DXPRequestFactory.createRemoveDriverRequest("RD1001"));
			String vehicleOidNd2 = addVehicle(policyNumber, "2015-01-10", "4S2CK58W8X4307498");

			//Check D1-->V1,V2
			DriverAssignments driverAssignmentResponse2 = HelperCommon.viewEndorsementAssignments2(policyNumber);
			DriverAssignments toMatch2 = new DriverAssignments()
					.addAssignment(driver1.oid, vehicle1.oid)
					.addAssignment(driver1.oid, vehicleOidNd2);
			softly.assertThat(driverAssignmentResponse2).isEqualToComparingOnlyGivenFields(toMatch2, "driverVehicleAssignments");
			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	private void checkAssignButtonInUiRateAndBind(String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		AssignmentTab assignmentTab = new AssignmentTab();
		assertThat(assignmentTab.btnAssign.isEnabled()).isEqualTo(false);
		assignmentTab.saveAndExit();
		helperMiniServices.bindEndorsementWithCheck(policyNumber);
	}

	protected boolean assignmentExistsForDriverVehicle(String driverOid, String vehicleOid, List<DriverAssignment> assignments) {
		return assignments.stream().anyMatch(assignment -> assignment.driverOid.equals(driverOid) && assignment.vehicleOid.equals(vehicleOid));
	}

	protected DriversDto findDriverByFirstName(String firstName, ViewDriversResponse dResponse) {
		return dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName)).findFirst().orElse(null);
	}

	private DriversDto findDriver(TestData testData, ViewDriversResponse dResponse, int driverIndex, String tabName) {
		return findDriver(testData, dResponse, driverIndex, tabName, false);
	}

	private DriversDto findDriver(TestData testData, ViewDriversResponse dResponse, int driverIndex, String tabName, boolean trimRandomLetters) {
		//Drivers info from testData
		String firstNameFromTd = testData.getTestDataList(tabName).get(driverIndex).getValue("First Name");
		String firstName = formatFirstName(firstNameFromTd, trimRandomLetters);
		DriversDto driverDto = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName)).findFirst().orElse(null);
		assertThat(driverDto).isNotNull();
		return driverDto;
	}

	private String formatFirstName(String firstName, boolean trimRandomLetters) {
		if(trimRandomLetters) {
			firstName = firstName.substring(0, firstName.length() - 5);
		}
		return firstName;
	}

	private void checkEndorsementChangeLogForVehicle (ComparableVehicle vehicleResponse, String driverOid, String changeType, String relationshipType, String firstName){
		assertSoftly(softly -> {
			softly.assertThat(vehicleResponse.driverAssignments.get(driverOid).changeType).isEqualTo(changeType);
			softly.assertThat(vehicleResponse.driverAssignments.get(driverOid).data.driverOid).isEqualTo(driverOid);
			softly.assertThat(vehicleResponse.driverAssignments.get(driverOid).data.driverDisplayValue.contains(firstName)).isTrue();
			softly.assertThat(vehicleResponse.driverAssignments.get(driverOid).data.relationshipType).isEqualTo(relationshipType);
		});
	}

	protected Vehicle findVehicle(ViewVehicleResponse viewVehicleResponse, String vin) {
		Vehicle vehicle = viewVehicleResponse.vehicleList.stream().filter(veh -> vin.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
		assertThat(vehicle).isNotNull();
		return vehicle;
	}

	protected String addRegularDriver(String policyNumber, String driverName, String licenseNumber) {
		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest(driverName, null, "Pukenaite", "1984-02-08", "II");
		DriversDto addedDriverResponse = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", licenseNumber, 18, "VA", "CH", "MSS");
		HelperCommon.updateDriver(policyNumber, addedDriverResponse.oid, updateDriverRequest);
		return addedDriverResponse.oid;
	}

	protected String replaceVehicleWithUpdates(String policyNumber, String vehicleToReplaceOid, String replacedVehicleVin, boolean keepAssignments, boolean keepCoverages) {
		printToLog("policyNumber: " + policyNumber + ", vehicleToReplaceOid: " + vehicleToReplaceOid + ", replacedVehicleVin: " + replacedVehicleVin);
		printToLog("keepAssignments: "+ keepAssignments + ", keepCoverages: "+ keepCoverages);
		ReplaceVehicleRequest replaceVehicleRequest = DXPRequestFactory.createReplaceVehicleRequest(replacedVehicleVin, "2013-03-31", keepAssignments, keepCoverages);
		VehicleUpdateResponseDto replaceVehicleResponse = HelperCommon.replaceVehicle(policyNumber, vehicleToReplaceOid, replaceVehicleRequest,VehicleUpdateResponseDto.class, Response.Status.OK.getStatusCode());
		String replaceVehicleOid = replaceVehicleResponse.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleOid);
		return replaceVehicleOid;
	}

	protected String addVehicle(String policyNumber, String purchaseDate, String vin) {
		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);
		return newVehicleOid;
	}

	protected TestData createPolicyWithMoreThanOneVehicle(String vehicleTestData){
			TestData td = getPolicyDefaultTD();
			td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD(vehicleTestData).getTestDataList("VehicleTab")).resolveLinks();
			return td;
		}

	protected TestData createPolicyWithMoreThanOneDriver(String driverTestData){
			TestData td = getPolicyDefaultTD();
			td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD(driverTestData).getTestDataList("DriverTab")).resolveLinks();
			return td;
		}

	protected TestData createPolicyWithMoreThanOneDriverAndVehicle(String driverTestData, String vehicleTestData, String assignmentTestData){
			TestData td = getPolicyDefaultTD();
			td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD(driverTestData).getTestDataList("DriverTab")).resolveLinks();
			td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD(vehicleTestData).getTestDataList("VehicleTab")).resolveLinks();
			if (assignmentTestData != null) {
				td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD(assignmentTestData)).resolveLinks();
			}
			return td;
		}

	private void validateVehicleTab_pas15540(DriversDto driverFNI, boolean primaryOperatorFNIExpected) {
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		validatePrimaryOperator_pas15540(driverFNI.firstName, 2, primaryOperatorFNIExpected);
		validatePrimaryOperator_pas15540(driverFNI.firstName, 3, primaryOperatorFNIExpected);
		if (Constants.States.AZ.equals(getState())) {
			validatePrimaryOperator_pas15540(driverFNI.firstName, 4, primaryOperatorFNIExpected);
		}
	}

	private void validatePrimaryOperator_pas15540(String driverFirstName, int vehicleNumber, boolean primaryOperatorFNIExpected) {
		VehicleTab.tableVehicleList.selectRow(vehicleNumber);
		if (primaryOperatorFNIExpected) {
			assertThat(vehicleTab.getInquiryAssetList().getStaticElement(PRIMARY_OPERATOR).getValue()).as("Vehicle Primary operator should be FNI.").contains(driverFirstName);
		} else {
			assertThat(vehicleTab.getInquiryAssetList().getStaticElement(PRIMARY_OPERATOR).getValue()).doesNotContain(driverFirstName);
		}
	}

	public HelperMiniServices getHelperMiniServices() {
		return helperMiniServices;
	}

	public TestMiniServicesDriversHelper getTestMiniServicesDriversHelper() {
		return testMiniServicesDriversHelper;
	}
}



