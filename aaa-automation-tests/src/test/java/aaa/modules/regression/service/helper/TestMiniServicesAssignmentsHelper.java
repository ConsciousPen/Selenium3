package aaa.modules.regression.service.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.PRIMARY_OPERATOR;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.enums.ProductConstants;
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
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestMiniServicesAssignmentsHelper extends PolicyBaseTest {

	private AssignmentTab assignmentTab = new AssignmentTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private RemoveDriverRequest removeDriverRequest = new RemoveDriverRequest();
	private VehicleTab vehicleTab = new VehicleTab();
	private TestMiniServicesDriversHelper testMiniServicesDriversHelper= new TestMiniServicesDriversHelper();
	protected void pas10484_ViewDriverAssignmentService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Create a pended Endorsement
		PolicySummary endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

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
		String purchaseDate = "2012-02-21";
		String newVin = "4S2CK58W8X4307498";

		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, newVin);
		assertSoftly(softly ->
				softly.assertThat(addVehicle.oid).isNotEmpty()
		);
		String newVehOid = addVehicle.oid;

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

	protected void pas14477_ViewDriverAssignment_NewDriver_Body(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();
		String policyNumber = createPolicy(testData);
		SearchPage.openPolicy(policyNumber);

		//Create a pended Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add new driver for PAS-14477
		AddDriverRequest addDriverRequest = new AddDriverRequest();
		addDriverRequest.firstName = "Justinio";
		addDriverRequest.middleName = "Doc";
		addDriverRequest.lastName = "Jill";
		addDriverRequest.birthDate = "1960-02-08";
		addDriverRequest.suffix = "III";
		DriversDto addDriverResponse = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String newDriverOid = addDriverResponse.oid;

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

		//add vehicle
		String purchaseDate = "2012-02-21";
		String newVin = "4S2CK58W8X4307498";

		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, newVin);
		assertSoftly(softly ->
				softly.assertThat(addVehicle.oid).isNotEmpty()
		);
		String newVehOid = addVehicle.oid;

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

	protected void pas11633_ViewDriverAssignmentAutoAssignService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

		//Create a pended Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin2 = "4S2CK58W8X4307498";

		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin2);
		assertSoftly(softly ->
				softly.assertThat(addVehicle.oid).isNotEmpty()
		);

		mainApp().open();
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
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();

			policyType.get().createPolicy(testData);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//V1 vin from testData
			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");

			//add V2
			String purchaseDate = "2012-02-21";
			String vin2 = "1HGEM21504L055795";
			Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin2);
			assertThat(addVehicle.oid).isNotEmpty();
			String newVehicleOid = addVehicle.oid;
			printToLog("newVehicleOid: " + newVehicleOid);

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

			//Drivers info from testData
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");

			String firstName2 = testData.getTestDataList("DriverTab").get(1).getValue("First Name");
			String lastName2 = testData.getTestDataList("DriverTab").get(1).getValue("Last Name");

			//get drivers oid's
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.lastName).isEqualTo(lastName1);
			String driverOid1 = driver1.oid;
			softly.assertThat(driver2.lastName).isEqualTo(lastName2);
			String driverOid2 = driver2.oid;

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
			String vehicleOid1 = vehicle1.oid;
			softly.assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
			String vehicleOid2 = vehicle2.oid;

			//Update: V2-->D1 ,Check V2-->D1, V1-->D2
			ViewDriverAssignmentResponse updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid1));
			List<DriverAssignment> v1ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v2ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee1.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableDrivers.contains(driverOid2)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee1.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.maxOneDriverPerVehicle).isEqualTo(true);
			softly.assertThat(updDriverAssignee1.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Update: V1-->D1, D2, Check V1-->D1,D2, V2-->Unn
			ViewDriverAssignmentResponse updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid1, Arrays.asList(driverOid1, driverOid2));
			v1ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee2.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee2.assignableDrivers.contains(driverOid2)).isTrue();
			softly.assertThat(updDriverAssignee2.assignableVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee2.assignableVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee2.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.unassignedVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee2.vehiclesWithTooManyDrivers.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee2.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getCode(), ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getMessage(), "attributeForRules");

			//Update: V2-->D2, V1-->D1, V2-->D2
			ViewDriverAssignmentResponse updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid2));
			v1ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee3.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee3.assignableDrivers.contains(driverOid2)).isTrue();
			softly.assertThat(updDriverAssignee3.assignableVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee3.assignableVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee3.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas15529_UpdateDriverAssignmentServiceRule1Body2(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData_VA");
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_FourDrivers").getTestDataList("DriverTab")).resolveLinks();
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_TwoVehicles").getTestDataList("VehicleTab")).resolveLinks();
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTabFourDrivers")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
			String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

			//add V3
			String purchaseDate = "2012-02-21";
			String vin3 = "1NXBR32E53Z168489";
			Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin3);
			assertThat(addVehicle.oid).isNotEmpty();
			String newVehicleOid = addVehicle.oid;
			printToLog("newVehicleOid: " + newVehicleOid);

			//add V4
			String vin4 = "1G2NE52T9XM924276";
			Vehicle addVehicle2 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin4);
			assertThat(addVehicle2.oid).isNotEmpty();
			String newVehicleOid2 = addVehicle2.oid;

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid2);

			//Drivers info from testData
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");
			String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
			String lastName2 = td.getTestDataList("DriverTab").get(1).getValue("Last Name");
			String firstName3 = td.getTestDataList("DriverTab").get(2).getValue("First Name");
			String lastName3 = td.getTestDataList("DriverTab").get(2).getValue("Last Name");
			String firstName4 = td.getTestDataList("DriverTab").get(3).getValue("First Name");
			String lastName4 = td.getTestDataList("DriverTab").get(3).getValue("Last Name");

			//get drivers oid's
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driver3 = dResponse.driverList.stream().filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driver4 = dResponse.driverList.stream().filter(driver -> firstName4.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.lastName).isEqualTo(lastName1).isEqualTo(lastName1);
			String driverOid1 = driver1.oid;
			softly.assertThat(driver2.lastName).isEqualTo(lastName2).isEqualTo(lastName2);
			String driverOid2 = driver2.oid;
			softly.assertThat(driver3.lastName).isEqualTo(lastName3).isEqualTo(lastName3);
			String driverOid3 = driver3.oid;
			softly.assertThat(driver4.lastName).isEqualTo(lastName4).isEqualTo(lastName4);
			String driverOid4 = driver4.oid;

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle2 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle3 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle4 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
			String vehicleOid1 = vehicle1.oid;
			softly.assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
			String vehicleOid2 = vehicle2.oid;
			softly.assertThat(vehicle3.vehIdentificationNo).isEqualTo(vin3);
			String vehicleOid3 = vehicle3.oid;
			softly.assertThat(vehicle4.vehIdentificationNo).isEqualTo(vin4);
			String vehicleOid4 = vehicle4.oid;

			//Update: V3-->D1, Check V1-->D2 V2-->D3,D4, V3-->D1, V4-->Unn
			ViewDriverAssignmentResponse updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid3, Arrays.asList(driverOid1));
			List<DriverAssignment> v1ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v2ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v3ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee1.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee1.assignableVehicles.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee1.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.unassignedVehicles.contains(vehicleOid4)).isTrue();
			softly.assertThat(updDriverAssignee1.vehiclesWithTooManyDrivers.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee1.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getCode(), ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getMessage(), "attributeForRules");

			//Update: V4-->D2, Check V1-->Unn, V2-->D4,D3 V3-->D1, V4-->D2
			ViewDriverAssignmentResponse updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid4, Arrays.asList(driverOid2));
			v2ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v4ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee2.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee2.assignableVehicles.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee2.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.unassignedVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee2.vehiclesWithTooManyDrivers.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee2.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getCode(), ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getMessage(), "attributeForRules");

			//Update: V2-->D3, Check V1-->Unn, V2-->D3, V3-->D1, V4-->D2, D4-->Unn
			ViewDriverAssignmentResponse updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid3));
			v2ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			v4ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee3.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee3.assignableVehicles.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee3.unassignedDrivers.contains(driverOid4)).isTrue();
			softly.assertThat(updDriverAssignee3.unassignedVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee3.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage(), "attributeForRules");

			//Update: V1-->D3, Check V1-->D3, V2-->Unn, V3-->D1, V4-->D2, D4-->Unn
			ViewDriverAssignmentResponse updDriverAssignee4 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid1, Arrays.asList(driverOid3));
			v1ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			v4ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee4.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee4.assignableVehicles.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee4.unassignedDrivers.contains(driverOid4)).isTrue();
			softly.assertThat(updDriverAssignee4.unassignedVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee4.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage(), "attributeForRules");

			//Update: V2-->D4, Check V1-->D3, V2-->D4, V3-->D1, V4-->D2
			ViewDriverAssignmentResponse updDriverAssignee5 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid4));
			v1ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			v4ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee5.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee5.assignableVehicles.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee5.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule2Body1(PolicyType policyType) {

		TestData td = getPolicyTD("DataGather", "TestData_VA");
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_ThreeVehicles").getTestDataList("VehicleTab")).resolveLinks();
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
			String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
			String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");

			//add V4
			String purchaseDate = "2012-02-21";
			String vin4 = "1NXBR32E53Z168489";
			Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin4);
			assertThat(addVehicle.oid).isNotEmpty();
			String newVehicleOid = addVehicle.oid;
			printToLog("newVehicleOid: " + newVehicleOid);

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);
			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA.getMessage(), "attributeForRules");

			//Drivers info from testData
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");

			String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
			String lastName2 = td.getTestDataList("DriverTab").get(1).getValue("Last Name");

			//get drivers oid's
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.lastName).isEqualTo(lastName1);
			String driverOid1 = driver1.oid;
			softly.assertThat(driver2.lastName).isEqualTo(lastName2);
			String driverOid2 = driver2.oid;

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle3 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle4 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
			String vehicleOid1 = vehicle1.oid;
			softly.assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
			String vehicleOid2 = vehicle2.oid;
			softly.assertThat(vehicle3.vehIdentificationNo).isEqualTo(vin3);
			String vehicleOid3 = vehicle3.oid;
			softly.assertThat(vehicle4.vehIdentificationNo).isEqualTo(vin4);
			String vehicleOid4 = vehicle4.oid;

			//Update: V4-->D1, Check V1-->D1, V2-->D2, V3-->D1, V4-->D1
			ViewDriverAssignmentResponse updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid4, Arrays.asList(driverOid1));
			List<DriverAssignment> v1ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v2ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v3ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v4ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee1.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableDrivers.contains(driverOid2)).isTrue();

			softly.assertThat(updDriverAssignee1.assignableVehicles.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid3)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid4)).isTrue();

			softly.assertThat(updDriverAssignee1.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.maxOneDriverPerVehicle).isEqualTo(true);

			//Update V2-->D1 Check V1-->D1, V2-->D1, V3-->D1, V4-->D1
			ViewDriverAssignmentResponse updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid1));
			v1ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			v4ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee2.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee2.assignableDrivers.contains(driverOid2)).isTrue();

			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid3)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.contains(vehicleOid4)).isTrue();

			softly.assertThat(updDriverAssignee2.unassignedDrivers.contains(driverOid2)).isTrue();
			softly.assertThat(updDriverAssignee2.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage(), "attributeForRules");

			//Update V2-->D2, Check V1-->D1, V2-->D2, V3-->D1, V4-->D1
			ViewDriverAssignmentResponse updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid2));
			v1ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			v4ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee3.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee3.assignableDrivers.contains(driverOid2)).isTrue();

			softly.assertThat(updDriverAssignee3.assignableVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee3.assignableVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee3.assignableVehicles.contains(vehicleOid3)).isTrue();
			softly.assertThat(updDriverAssignee3.assignableVehicles.contains(vehicleOid4)).isTrue();

			softly.assertThat(updDriverAssignee3.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule2Body2(PolicyType policyType) {

		TestData td = getPolicyTD("DataGather", "TestData_VA");
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_ThreeDrivers").getTestDataList("DriverTab")).resolveLinks();
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_ThreeVehicles").getTestDataList("VehicleTab")).resolveLinks();
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTabThreeDrivers")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Create a pended Endorsement
			PolicySummary endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
			String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
			String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");

			//add V4
			String purchaseDate1 = "2012-02-21";
			String vin4 = "1NXBR32E53Z168489";
			Vehicle addVehicle1 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate1, vin4);
			assertThat(addVehicle1.oid).isNotEmpty();
			String newVehicleOid = addVehicle1.oid;
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

			//add V5
			String purchaseDate2 = "2015-01-11";
			String vin5 = "JTDKN3DU0E0356920";
			Vehicle addVehicle2 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate2, vin5);
			assertThat(addVehicle2.oid).isNotEmpty();
			String newVehicleOid2 = addVehicle2.oid;
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid2);

			//Drivers info from testData
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");
			String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
			String lastName2 = td.getTestDataList("DriverTab").get(1).getValue("Last Name");
			String firstName3 = td.getTestDataList("DriverTab").get(2).getValue("First Name");
			String lastName3 = td.getTestDataList("DriverTab").get(2).getValue("Last Name");

			//get drivers oid's
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driver3 = dResponse.driverList.stream().filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.lastName).isEqualTo(lastName1).isEqualTo(lastName1);
			String driverOid1 = driver1.oid;
			softly.assertThat(driver2.lastName).isEqualTo(lastName2).isEqualTo(lastName2);
			String driverOid2 = driver2.oid;
			softly.assertThat(driver3.lastName).isEqualTo(lastName3).isEqualTo(lastName3);
			String driverOid3 = driver3.oid;

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle2 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle3 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle4 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle5 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin5.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
			String vehicleOid1 = vehicle1.oid;
			softly.assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
			String vehicleOid2 = vehicle2.oid;
			softly.assertThat(vehicle3.vehIdentificationNo).isEqualTo(vin3);
			String vehicleOid3 = vehicle3.oid;
			softly.assertThat(vehicle4.vehIdentificationNo).isEqualTo(vin4);
			String vehicleOid4 = vehicle4.oid;
			softly.assertThat(vehicle5.vehIdentificationNo).isEqualTo(vin5);
			String vehicleOid5 = vehicle5.oid;

			//Update V4-->D1 AND V5-->D2 (V1-->D1, V2-->D2, V3-->D3, V4-->D1, V5-->D2)
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid4, Arrays.asList(driverOid1));
			ViewDriverAssignmentResponse updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid5, Arrays.asList(driverOid2));

			List<DriverAssignment> v1ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v2ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v3ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v4ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v5ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid5.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v5ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee1.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableDrivers.contains(driverOid2)).isTrue();
			softly.assertThat(updDriverAssignee1.assignableVehicles.size()).isEqualTo(5);
			softly.assertThat(updDriverAssignee1.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Update D1-->All V, (V1-->D1, V2-->D1,  V3-->D1, V4-->D1, V5-->D1, D2,D3-->Unn)
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid1, Arrays.asList(driverOid1));
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid1));
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid3, Arrays.asList(driverOid1));
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid4, Arrays.asList(driverOid1));
			ViewDriverAssignmentResponse updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid5, Arrays.asList(driverOid1));

			v1ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			v4ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());
			v5ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid5.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v5ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee3.assignableDrivers.size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee3.assignableVehicles.size()).isEqualTo(5);
			softly.assertThat(updDriverAssignee3.unassignedDrivers.size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee3.unassignedDrivers.contains(driverOid2)).isTrue();
			softly.assertThat(updDriverAssignee3.unassignedDrivers.contains(driverOid3)).isTrue();
			softly.assertThat(updDriverAssignee3.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage(), "attributeForRules");

			//Update V1,V2-->D2, V3,V4,V5-->D3, (V1-->D2, V2-->D2,  V3-->D3, V4-->D3, V5-->D3, D1 -->Unn)
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid1, Arrays.asList(driverOid2));
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid2));
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid3, Arrays.asList(driverOid3));
			HelperCommon.updateDriverAssignment(policyNumber, vehicleOid4, Arrays.asList(driverOid3));
			ViewDriverAssignmentResponse updDriverAssignee4 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid5, Arrays.asList(driverOid3));

			v1ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			v4ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());
			v5ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid5.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v5ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee4.assignableDrivers.size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee4.assignableVehicles.size()).isEqualTo(5);
			softly.assertThat(updDriverAssignee4.unassignedDrivers.size()).isEqualTo(1);
			softly.assertThat(updDriverAssignee4.unassignedDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee4.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA_2.getMessage(), "attributeForRules");

			//Update V5-->D1, (V1-->D2, V2-->D2,  V3-->D3, V4-->D3, V5-->D1)
			ViewDriverAssignmentResponse updDriverAssignee5 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid5, Arrays.asList(driverOid1));
			v1ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			v4ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());
			v5ByOid = updDriverAssignee5.driverVehicleAssignments.stream().filter(veh -> vehicleOid5.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v5ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee5.assignableDrivers.contains(driverOid1)).isTrue();
			softly.assertThat(updDriverAssignee5.assignableDrivers.contains(driverOid2)).isTrue();
			softly.assertThat(updDriverAssignee5.assignableVehicles.size()).isEqualTo(5);
			softly.assertThat(updDriverAssignee5.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee5.maxOneDriverPerVehicle).isEqualTo(true);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			checkAssignButtonInUiRateAndBind(policyNumber);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule3Body(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData_VA");
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_FourDrivers").getTestDataList("DriverTab")).resolveLinks();
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_TwoVehicles").getTestDataList("VehicleTab")).resolveLinks();
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTabFourDrivers")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
			String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

			//add V3
			String purchaseDate = "2012-02-21";
			String vin3 = "1NXBR32E53Z168489";
			Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin3);
			assertThat(addVehicle.oid).isNotEmpty();
			String newVehicleOid = addVehicle.oid;
			printToLog("newVehicleOid: " + newVehicleOid);

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

			//Drivers info from testData
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");
			String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
			String lastName2 = td.getTestDataList("DriverTab").get(1).getValue("Last Name");
			String firstName3 = td.getTestDataList("DriverTab").get(2).getValue("First Name");
			String lastName3 = td.getTestDataList("DriverTab").get(2).getValue("Last Name");
			String firstName4 = td.getTestDataList("DriverTab").get(3).getValue("First Name");
			String lastName4 = td.getTestDataList("DriverTab").get(3).getValue("Last Name");

			//get drivers oid's
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driver3 = dResponse.driverList.stream().filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driver4 = dResponse.driverList.stream().filter(driver -> firstName4.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.lastName).isEqualTo(lastName1).isEqualTo(lastName1);
			String driverOid1 = driver1.oid;
			softly.assertThat(driver2.lastName).isEqualTo(lastName2).isEqualTo(lastName2);
			String driverOid2 = driver2.oid;
			softly.assertThat(driver3.lastName).isEqualTo(lastName3).isEqualTo(lastName3);
			String driverOid3 = driver3.oid;
			softly.assertThat(driver4.lastName).isEqualTo(lastName4).isEqualTo(lastName4);
			String driverOid4 = driver4.oid;

			//get vehicles oid's
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle vehicle1 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle2 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			Vehicle vehicle3 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
			String vehicleOid1 = vehicle1.oid;
			softly.assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
			String vehicleOid2 = vehicle2.oid;
			softly.assertThat(vehicle3.vehIdentificationNo).isEqualTo(vin3);
			String vehicleOid3 = vehicle3.oid;

			//Update: V3-->D1, Check V1-->D2, V2-->D3, D4, V3-->D1
			ViewDriverAssignmentResponse updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid3, Arrays.asList(driverOid1));
			List<DriverAssignment> v1ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v2ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignment> v3ByOid = updDriverAssignee1.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee1.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee1.assignableVehicles.size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee1.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee1.maxOneDriverPerVehicle).isEqualTo(false);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Update: V2-->D2, Check V1-->Unn, V2-->D2, V3-->D1, D3,D4-->Unn
			ViewDriverAssignmentResponse updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid2));
			v2ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee2.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee2.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee2.assignableVehicles.size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee2.unassignedDrivers.size()).isEqualTo(2);
			softly.assertThat(updDriverAssignee2.unassignedDrivers.contains(driverOid3)).isTrue();
			softly.assertThat(updDriverAssignee2.unassignedDrivers.contains(driverOid4)).isTrue();
			softly.assertThat(updDriverAssignee2.unassignedVehicles.contains(vehicleOid1)).isTrue();
			softly.assertThat(updDriverAssignee2.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee2.maxOneDriverPerVehicle).isEqualTo(false);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");

			//Update: V1-->D3,D4,D2 Check V2-->Unn, V3-->D1, V1-->D3,D4,D2
			ViewDriverAssignmentResponse updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid1, Arrays.asList(driverOid3, driverOid4, driverOid2));
			v1ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee3.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee3.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee3.assignableVehicles.size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee3.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.unassignedVehicles.contains(vehicleOid2)).isTrue();
			softly.assertThat(updDriverAssignee3.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee3.maxOneDriverPerVehicle).isEqualTo(false);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");

			//Update: V2-->D3 Check V2-->D3, V3-->D1, V1-->D4,D2
			ViewDriverAssignmentResponse updDriverAssignee4 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Arrays.asList(driverOid3));
			v1ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = updDriverAssignee4.driverVehicleAssignments.stream().filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			//Check the second part of the response
			softly.assertThat(updDriverAssignee4.assignableDrivers.size()).isEqualTo(4);
			softly.assertThat(updDriverAssignee4.assignableVehicles.size()).isEqualTo(3);
			softly.assertThat(updDriverAssignee4.unassignedDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.unassignedVehicles.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.vehiclesWithTooManyDrivers.isEmpty()).isTrue();
			softly.assertThat(updDriverAssignee4.maxOneDriverPerVehicle).isEqualTo(false);

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

	private void checkAssignButtonInUiRateAndBind(String policyNumber) {
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		AssignmentTab assignmentTab = new AssignmentTab();
		assertThat(assignmentTab.btnAssign.isEnabled()).isEqualTo(false);
		assignmentTab.saveAndExit();
		helperMiniServices.bindEndorsementWithCheck(policyNumber);
	}

	protected void pas14539_transactionInfoUpdateDriverAssignmentBody(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData_VA");
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_TwoVehicles").getTestDataList("VehicleTab")).resolveLinks();
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab_Two_Driver")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(td);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

		String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
		String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);

		String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//get vehicles oid's
		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);

		Vehicle vehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicle2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

		assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
		String vehicleOid1 = vehicle1.oid;
		assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
		String vehicleOid2 = vehicle2.oid;

		//get drivers oid's
		ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
		DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

		String driverOid1 = driver1.oid;

		String driverOid2 = driver2.oid;

		//Update: V2-->D1 ,Check V2-->D1, V1-->D2
		HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, Collections.singletonList(driverOid1));

		HelperCommon.updateDriverAssignment(policyNumber, vehicleOid1, Collections.singletonList(driverOid2));

		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh1 = policyResponse.vehicles.get(vehicleOid1);
		ComparableVehicle veh2 = policyResponse.vehicles.get(vehicleOid2);
		assertSoftly(softly -> {
			softly.assertThat(veh1.driverAssignments.get(driverOid1).changeType).isEqualTo("REMOVED");
			softly.assertThat(veh1.driverAssignments.get(driverOid1).data.driverOid).isEqualTo(driverOid1);
			softly.assertThat(veh1.driverAssignments.get(driverOid1).data.driverDisplayValue.contains(firstName1)).isTrue();
			softly.assertThat(veh1.driverAssignments.get(driverOid1).data.relationshipType).isEqualTo("primary");

			softly.assertThat(veh1.driverAssignments.get(driverOid2).changeType).isEqualTo("ADDED");
			softly.assertThat(veh1.driverAssignments.get(driverOid2).data.driverOid).isEqualTo(driverOid2);
			softly.assertThat(veh1.driverAssignments.get(driverOid2).data.driverDisplayValue).isEqualTo("John Smith");
			softly.assertThat(veh1.driverAssignments.get(driverOid2).data.relationshipType).isEqualTo("primary");

			softly.assertThat(veh2.driverAssignments.get(driverOid1).changeType).isEqualTo("ADDED");
			softly.assertThat(veh2.driverAssignments.get(driverOid1).data.driverOid).isEqualTo(driverOid1);
			softly.assertThat(veh2.driverAssignments.get(driverOid1).data.driverDisplayValue.contains(firstName1)).isTrue();
			softly.assertThat(veh2.driverAssignments.get(driverOid1).data.relationshipType).isEqualTo("primary");

			softly.assertThat(veh2.driverAssignments.get(driverOid2).changeType).isEqualTo("REMOVED");
			softly.assertThat(veh2.driverAssignments.get(driverOid2).data.driverOid).isEqualTo(driverOid2);
			softly.assertThat(veh2.driverAssignments.get(driverOid2).data.driverDisplayValue).isEqualTo("John Smith");
			softly.assertThat(veh2.driverAssignments.get(driverOid2).data.relationshipType).isEqualTo("primary");
		});
	}

	protected void pas14539_transactionInfoDriverAssignmentBody() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD());

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vin = "4S2CK58W8X4307498";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		String newVehicleOid = addVehicle.oid;
		assertThat(addVehicle.oid).isNotEmpty();

		//Update Vehicle with proper Usage and Registered Owner
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		ViewDriversResponse response = HelperCommon.viewPolicyDrivers(policyNumber);
		String dOid = response.driverList.get(0).oid;

		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh1 = policyResponse.vehicles.get(newVehicleOid);
		assertSoftly(softly -> {
			softly.assertThat(veh1.driverAssignments.get(dOid).changeType).isEqualTo("ADDED");
			softly.assertThat(veh1.driverAssignments.get(dOid).data.driverOid).isEqualTo(dOid);
			softly.assertThat(veh1.driverAssignments.get(dOid).data.driverDisplayValue.contains("Ben")).isTrue();
			softly.assertThat(veh1.driverAssignments.get(dOid).data.relationshipType).isEqualTo("occasional");
		});

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas15505_RemoveDriverAssignedToTrailerMotorHomeGolfCartBody(PolicyType policyType) {
		TestData td = getPolicyDefaultTD();
		//adjust Driver Tab to have 1 driver from policy default TD and one driver from custom TD
		List<TestData> testDataDriverData = new ArrayList<>();// Merged driver tab with 2 drivers
		testDataDriverData.add(td.getTestData("DriverTab"));
		testDataDriverData.addAll(getTestSpecificTD("TestData_oneAdditionalDriver").resolveLinks().getTestDataList("DriverTab"));
		td = td.adjust("DriverTab", testDataDriverData);
		//Adjust Vehicle Tab
		td = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleTrailer").getTestDataList("VehicleTab")).resolveLinks();
		//adjust test data to override errors for NJ and NY
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		if ("NJ, NY".contains(getState())) {
			td = td.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError).resolveLinks();
		}

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(td);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		//String policyNumber = "NYSS952918643";
		//SearchPage.openPolicy(policyNumber);

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		DriversDto driverNotFNI = testMiniServicesDriversHelper.getAnyNotNIActiveDriver(policyNumber);
		DriversDto driverFNI = testMiniServicesDriversHelper.getFNIDriver(policyNumber);
		//Validate that Trailer, Motor Home, Golf Cart are not assigned to FNI before removal of driver (precondition)
		validateVehicleTab_pas15505(driverFNI, false);

		removeDriverRequest.removalReasonCode = "RD1001";
		HelperCommon.removeDriver(policyNumber, driverNotFNI.oid, removeDriverRequest);
		SearchPage.openPolicy(policyNumber);
		validateVehicleTab_pas15505(driverFNI, true);
		vehicleTab.cancel();
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	private void validateVehicleTab_pas15505(DriversDto driverFNI, boolean primaryOperatorFNIExpected) {
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		validatePrimaryOperator_pas15505(driverFNI.firstName, 2, primaryOperatorFNIExpected);
		validatePrimaryOperator_pas15505(driverFNI.firstName, 3, primaryOperatorFNIExpected);
		if ("AZ".equals(getState())) {
			validatePrimaryOperator_pas15505(driverFNI.firstName, 4, primaryOperatorFNIExpected);
		}
	}

	private void validatePrimaryOperator_pas15505(String driverFirstName, int vehicleNumber, boolean primaryOperatorFNIExpected) {
		VehicleTab.tableVehicleList.selectRow(vehicleNumber);
		if (primaryOperatorFNIExpected) {
			assertThat(vehicleTab.getInquiryAssetList().getStaticElement(PRIMARY_OPERATOR).getValue()).as("Vehicle Primary operator should be FNI.").contains(driverFirstName);
		} else {
			assertThat(vehicleTab.getInquiryAssetList().getStaticElement(PRIMARY_OPERATOR).getValue()).doesNotContain(driverFirstName);
		}

	}
}



