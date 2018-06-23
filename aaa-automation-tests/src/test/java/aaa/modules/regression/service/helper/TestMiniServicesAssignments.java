package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.TestData;

public class TestMiniServicesAssignments extends PolicyBaseTest {

	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private ErrorTab errorTab = new ErrorTab();
	private AssignmentTab assignmentTab = new AssignmentTab();
	private VehicleTab vehicleTab = new VehicleTab();
	private DriverTab driverTab = new DriverTab();
	private GeneralTab generalTab = new GeneralTab();
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

		DriverAssignmentDto[] response = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment1).contains(response[0].vehicleDisplayValue);
			softly.assertThat(response[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment1).contains(response[0].driverDisplayValue);
			softly.assertThat(response[0].driverOid).isEqualTo(driverOid1);
			softly.assertThat(response[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment2).contains(response[1].vehicleDisplayValue);
			softly.assertThat(response[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment2).contains(response[1].driverDisplayValue);
			softly.assertThat(response[1].driverOid).isEqualTo(driverOid2);
			softly.assertThat(response[1].relationshipType).isEqualTo("primary");
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

		List<TestData> assignmentsUnassigned = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment5 = assignmentsUnassigned.get(0).toString();

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment3).contains(driverAssignmentAfterAddingVehicleResponse[0].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3).contains(driverAssignmentAfterAddingVehicleResponse[0].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[0].driverOid).isEqualTo(driverOid3);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4).contains(driverAssignmentAfterAddingVehicleResponse[1].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4).contains(driverAssignmentAfterAddingVehicleResponse[1].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[1].driverOid).isEqualTo(driverOid4);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[1].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment5).contains(driverAssignmentAfterAddingVehicleResponse[2].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[2].vehicleOid).isEqualTo(newVehOid);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[2].driverDisplayValue).isEqualTo("unassigned");
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[2].driverOid).isEqualTo("unassigned");
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse[2].relationshipType).isEqualTo("unassigned");
		});
		assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getTable().getRow(1).getCell("Select Driver").controls.comboBoxes.getFirst()
				.setValue(driverNameFromAssignment3);
		Tab.buttonTopSave.click();

		List<TestData> assignmentOccasional = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue();
		String driverAssignment6 = assignmentOccasional.get(0).toString();

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse1 = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(driverAssignment3).contains(driverAssignmentAfterAddingVehicleResponse1[0].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment3).contains(driverAssignmentAfterAddingVehicleResponse1[0].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].driverOid).isEqualTo(driverOid3);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[0].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment4).contains(driverAssignmentAfterAddingVehicleResponse1[1].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].vehicleOid).isNotNull();
			softly.assertThat(driverAssignment4).contains(driverAssignmentAfterAddingVehicleResponse1[1].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].driverOid).isEqualTo(driverOid4);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[1].relationshipType).isEqualTo("primary");

			softly.assertThat(driverAssignment6).contains(driverAssignmentAfterAddingVehicleResponse1[2].vehicleDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].vehicleOid).isEqualTo(newVehOid);
			softly.assertThat(driverAssignment6).contains(driverAssignmentAfterAddingVehicleResponse1[2].driverDisplayValue);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].driverOid).isEqualTo(driverOid3);
			softly.assertThat(driverAssignmentAfterAddingVehicleResponse1[2].relationshipType).isEqualTo("occasional");
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

		DriverAssignmentDto[] driverAssignmentAfterAddingVehicleResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			assertThat(driverAssignmentAfterAddingVehicleResponse[0].vehicleDisplayValue).isEqualTo(vehicle1);
			assertThat(driverAssignmentAfterAddingVehicleResponse[0].vehicleOid).isEqualTo(vehicleOid2);
			assertThat(driverAssignmentAfterAddingVehicleResponse[0].driverDisplayValue).isEqualTo(driver1);
			assertThat(driverAssignmentAfterAddingVehicleResponse[0].relationshipType).isEqualTo("primary");

			assertThat(driverAssignmentAfterAddingVehicleResponse[1].vehicleDisplayValue).isEqualTo(vehicle2);
			assertThat(driverAssignmentAfterAddingVehicleResponse[1].vehicleOid).isEqualTo(vehicleOid1);
			assertThat(driverAssignmentAfterAddingVehicleResponse[1].driverDisplayValue).isEqualTo(driver1);
			assertThat(driverAssignmentAfterAddingVehicleResponse[1].relationshipType).isEqualTo("occasional");
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule1Body(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData_VA");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();

		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();

			policyType.get().createPolicy(testData);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Create a pended Endorsement
			AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

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
			DriverAssignmentDto[] updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, driverOid1);
			List<DriverAssignmentDto> v1ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v2ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Update: V1-->D1, Check V1-->D1,D2, V2-->Unn
			DriverAssignmentDto[] updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid1, driverOid1);
			v1ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> "unassigned".equals(veh.driverOid))).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode());
			softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());
			softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("attributeForRules");
			softly.assertThat(rateResponse.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getCode());
			softly.assertThat(rateResponse.errors.get(1).message).contains(ErrorDxpEnum.Errors.DRIVERS_MUST_BE_ASSIGNED_A_UNIQUE_VEHICLE.getMessage());
			softly.assertThat(rateResponse.errors.get(1).field).isEqualTo("attributeForRules");

			//Update V2-->D2 (V1-->D1, V2-->D2)
			DriverAssignmentDto[] updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, driverOid2);
			v1ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
		});
	}

	protected void pas13994_UpdateDriverAssignmentServiceRule2Body(PolicyType policyType) {
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

			ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode());
			softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage());
			softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("attributeForRules");
			softly.assertThat(rateResponse.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA.getCode());
			softly.assertThat(rateResponse.errors.get(1).message).contains(ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION_VA.getMessage());
			softly.assertThat(rateResponse.errors.get(1).field).isEqualTo("attributeForRules");

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

			//Update: V4-->D1, Check D1-->V1,V3,V4, D2-->V2
			DriverAssignmentDto[] updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid4, driverOid2);
			List<DriverAssignmentDto> v1ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v2ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v3ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v4ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//TODO-jpukenaite It will be fixed with PAS-14699
			//Update: V4-->D2, Check D1-->V1,V3, D2-->V2, V4
			//			DriverAssignmentDto[] updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber,vehicleOid4,driverOid1);
			//			v1ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			//			v2ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			//			v3ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());
			//			v4ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid4.equals(veh.vehicleOid)).collect(Collectors.toList());
			//
			//			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			//			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			//			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			//			softly.assertThat(v4ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
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
			DriverAssignmentDto[] updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid3, driverOid1);
			List<DriverAssignmentDto> v1ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v2ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			List<DriverAssignmentDto> v3ByOid = Arrays.stream(updDriverAssignee1).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Update: V2-->D2, Check V1-->Unn, V2-->D3,D4,D2, V3-->D1
			DriverAssignmentDto[] updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, driverOid2);
			v1ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = Arrays.stream(updDriverAssignee2).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> "unassigned".equals(veh.driverOid))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");

			//Update: V2-->D1, Check V1-->Unn, V2-->D3,D4,D2,D1 V3-->Unn
			DriverAssignmentDto[] updDriverAssignee3 = HelperCommon.updateDriverAssignment(policyNumber, vehicleOid2, driverOid1);
			v1ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid1.equals(veh.vehicleOid)).collect(Collectors.toList());
			v2ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid2.equals(veh.vehicleOid)).collect(Collectors.toList());
			v3ByOid = Arrays.stream(updDriverAssignee3).filter(veh -> vehicleOid3.equals(veh.vehicleOid)).collect(Collectors.toList());

			softly.assertThat(v1ByOid.stream().anyMatch(veh -> "unassigned".equals(veh.driverOid))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid3))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid4))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid2))).isEqualTo(true);
			softly.assertThat(v2ByOid.stream().anyMatch(veh -> veh.driverOid.equals(driverOid1))).isEqualTo(true);
			softly.assertThat(v3ByOid.stream().anyMatch(veh -> "unassigned".equals(veh.driverOid))).isEqualTo(true);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getCode(), ErrorDxpEnum.Errors.INCOMPLETE_OR_UNACCEPTABLE_SELECTION.getMessage(), "attributeForRules");
		});
	}

	protected void pas11684_DriverAssignmentExistsForStateBody(String state, SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//View driver assignment if VA
		if ("VA".equals(state) || "CA".equals(state) || "NY".equals(state)) {
			DriverAssignmentDto[] responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment[0].vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment[0].driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment[0].relationshipType).isEqualTo("primary");
		} else {
			ErrorResponseDto responseDriverAssignment = HelperCommon.viewEndorsementAssignmentsError(policyNumber, 422);
			softly.assertThat(responseDriverAssignment.errorCode).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getCode());
			softly.assertThat(responseDriverAssignment.message).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getMessage());
		}

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}
}



