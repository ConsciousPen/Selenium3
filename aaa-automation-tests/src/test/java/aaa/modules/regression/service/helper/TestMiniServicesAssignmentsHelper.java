package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.SoftAssertions;
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
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.TestData;

public class TestMiniServicesAssignmentsHelper extends PolicyBaseTest {

	private AssignmentTab assignmentTab = new AssignmentTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();


protected void pas10484_ViewDriverAssignmentService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Create a pended Endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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

	protected void pas11633_ViewDriverAssignmentAutoAssignService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
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

	protected void pas11684_DriverAssignmentExistsForStateBody(String state, SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

//		//View driver assignment if VA
//		if ("VA".equals(state) || "CA".equals(state) || "NY".equals(state)) {
//			DriverAssignment[] responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
//			softly.assertThat(responseDriverAssignment[0].vehicleOid).isNotNull();
//			softly.assertThat(responseDriverAssignment[0].driverOid).isNotNull();
//			softly.assertThat(responseDriverAssignment[0].relationshipType).isEqualTo("primary");
//		} else {
//			ErrorResponseDto responseDriverAssignment = HelperCommon.viewEndorsementAssignmentsError(policyNumber, 422);
//			softly.assertThat(responseDriverAssignment.errorCode).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getCode());
//			softly.assertThat(responseDriverAssignment.message).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getMessage());
//		}

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}
}



