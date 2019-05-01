package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoCaMetaData.VehicleTab.VIN;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.BooleanUtils;

public class TestMiniServicesVehiclesHelperCA extends PolicyBaseTest {

	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private GeneralTab generalTab = new GeneralTab();
	private VehicleTab vehicleTab = new VehicleTab();
	private AssignmentTab assignmentTab = new AssignmentTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestMiniServicesGeneralHelper testMiniServicesGeneralHelper = new TestMiniServicesGeneralHelper();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

	protected void pas10449_ViewVehicleServiceCheckOrderOfVehicle(PolicyType policyType, String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestData("AssignmentTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Get VIN's
		String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
		String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");
		String vin4 = td.getTestDataList("VehicleTab").get(3).getValue("VIN");
		String vin7 = td.getTestDataList("VehicleTab").get(4).getValue("VIN");
		String vin8 = td.getTestDataList("VehicleTab").get(5).getValue("VIN");

		//hit view vehicle service to get Vehicle order
		ViewVehicleResponse viewVehicleResponse1 = HelperCommon.viewPolicyVehicles(policyNumber);
		assertThat(viewVehicleResponse1.canAddVehicle).isEqualTo(true);
		List<Vehicle> originalOrderingFromResponse = ImmutableList.copyOf(viewVehicleResponse1.vehicleList);
		List<Vehicle> sortedVehicles = viewVehicleResponse1.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		String oidForVin3 = viewVehicleResponse1.vehicleList.stream().filter(vehicle -> vin3.equals(vehicle.vehIdentificationNo)).findFirst().map(vehicle -> vehicle.oid).orElse(null);

		assertSoftly(softly -> {
			assertThat(originalOrderingFromResponse).containsAll(sortedVehicles);

			Vehicle vehicle1 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("Regular");
			softly.assertThat(vehicle1.availableActions).containsExactly("replace", "remove");

			Vehicle vehicle2 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("Regular");
			softly.assertThat(vehicle2.availableActions).containsExactly("replace", "remove");

			Vehicle vehicle3 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");
			softly.assertThat(vehicle3.availableActions).containsExactly("remove");

			Vehicle vehicle4 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Camper");
			softly.assertThat(vehicle4.availableActions).containsExactly("remove");

			Vehicle vehicle7 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin7.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle7).isNotNull();
			softly.assertThat(vehicle7.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle7.vehTypeCd).isEqualTo("Antique");
			softly.assertThat(vehicle7.availableActions).containsExactly("remove");

			Vehicle vehicle8 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin8.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle8).isNotNull();
			softly.assertThat(vehicle8.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle8.vehTypeCd).isEqualTo("Trailer");
			softly.assertThat(vehicle8.availableActions).containsExactly("remove");
		});

		// Perform endorsement
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Add new vehicle to have pending vehicle
		String purchaseDate = "2013-02-22";
		String vin5 = "1HGFA16526L081415";
		Vehicle addVehicleResponse =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin5, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicleResponse.oid).isNotEmpty();

		ViewVehicleResponse viewEndorsementVehicleResponse1 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse1.canAddVehicle).isEqualTo(true);

		String vin6 = "2GTEC19K8S1525936";
		Vehicle addVehicleResponse2 =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin6, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicleResponse2.oid).isNotEmpty();

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(false);//because max count of vehicles

		//run delete vehicle service
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oidForVin3, VehicleUpdateResponseDto.class, Response.Status.OK.getStatusCode());

		assertSoftly(softly -> {
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oidForVin3);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			softly.assertThat(deleteVehicleResponse.availableActions).containsExactly("revert");
			assertThat(deleteVehicleResponse.validations).isEqualTo(null);
		});

		ViewVehicleResponse viewEndorsementVehicleResponse3 = HelperCommon.viewEndorsementVehicles(policyNumber);
		List<Vehicle> originalOrderingFromResponse2 = ImmutableList.copyOf(viewEndorsementVehicleResponse3.vehicleList);
		List<Vehicle> sortedVehicles1 = viewEndorsementVehicleResponse3.vehicleList;
		sortedVehicles1.sort(Vehicle.PENDING_ENDORSEMENT_COMPARATOR);
		assertSoftly(softly -> {
			softly.assertThat(originalOrderingFromResponse2).isEqualTo(sortedVehicles1);

			Vehicle vehicle6 = viewEndorsementVehicleResponse3.vehicleList.stream().filter(veh -> vin6.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle6).isNotNull();
			softly.assertThat(vehicle6.vehicleStatus).isEqualTo("pending");
			softly.assertThat(vehicle6.vehTypeCd).isEqualTo("Regular");
			softly.assertThat(vehicle6.availableActions).containsExactly("remove");

			Vehicle vehicle5 = viewEndorsementVehicleResponse3.vehicleList.stream().filter(veh -> vin5.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle5).isNotNull();
			softly.assertThat(vehicle5.vehicleStatus).isEqualTo("pending");
			softly.assertThat(vehicle5.vehTypeCd).isEqualTo("Regular");
			softly.assertThat(vehicle5.availableActions).containsExactly("remove");

			Vehicle vehicle1 = viewEndorsementVehicleResponse3.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.oid).isEqualTo(oidForVin3);
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("pendingRemoval");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("Regular");
			softly.assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin3);
			//softly.assertThat(vehicle1.availableActions).containsExactly("revert");//TODO-mstrazds: in revert vehicle story

			Vehicle vehicle2 = viewEndorsementVehicleResponse3.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("Regular");
			softly.assertThat(vehicle2.availableActions).containsExactly("replace", "remove");

			Vehicle vehicle3 = viewEndorsementVehicleResponse3.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");
			softly.assertThat(vehicle3.availableActions).containsExactly("remove");

			Vehicle vehicle4 = viewEndorsementVehicleResponse3.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Camper");
			softly.assertThat(vehicle4.availableActions).containsExactly("remove");

			Vehicle vehicle7 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin7.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle7).isNotNull();
			softly.assertThat(vehicle7.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle7.vehTypeCd).isEqualTo("Antique");
			softly.assertThat(vehicle7.availableActions).containsExactly("remove");

			Vehicle vehicle8 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin8.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle8).isNotNull();
			softly.assertThat(vehicle8.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle8.vehTypeCd).isEqualTo("Trailer");
			softly.assertThat(vehicle8.availableActions).containsExactly("remove");
		});
	}

	protected void pas7082_AddVehicle(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		policy.policyInquiry().start();
		String zipCodeDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE).getValue();
		String addressDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1).getValue();
		String cityDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoCaMetaData.GeneralTab.NamedInsuredInformation.CITY).getValue();
		String stateDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoCaMetaData.GeneralTab.NamedInsuredInformation.STATE).getValue();

		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		String vin1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN).getValue();
		mainApp().close();

		//Create pended endorsement
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Add new vehicle
		String purchaseDate = "2012-02-21";
		String vin2 = "1HGEM21504L055795";

		Vehicle response1 =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin2, purchaseDate), Vehicle.class, 201);

		assertSoftly(softly -> {
					softly.assertThat(response1.modelYear).isEqualTo("2004");
					softly.assertThat(response1.manufacturer).isEqualTo("HONDA");
					softly.assertThat(response1.series).isEqualTo("CIVIC LX");
					softly.assertThat(response1.model).isEqualTo("CIVIC");
					softly.assertThat(response1.bodyStyle).isEqualTo("COUPE");
					softly.assertThat(response1.oid).isNotNull();
					softly.assertThat(response1.vehIdentificationNo).isEqualTo(vin2);
					softly.assertThat(response1.garagingDifferent).isEqualTo(false);
					softly.assertThat(response1.vehTypeCd).isEqualTo("Regular");
					softly.assertThat(response1.odometerReadingDate).isEqualTo(endorsementDate);
					softly.assertThat(response1.declaredAnnualMiles).isEqualTo("13000");
					softly.assertThat(response1.garagingAddress.postalCode).isEqualTo(zipCodeDefault);
					softly.assertThat(response1.garagingAddress.addressLine1).isEqualTo(addressDefault);
					softly.assertThat(response1.garagingAddress.city).isEqualTo(cityDefault);
					softly.assertThat(response1.garagingAddress.stateProvCd).isEqualTo(stateDefault);
					softly.assertThat(response1.vehicleStatus).isEqualTo("pending");
				}
		);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.PRIMARY_USE).setValueContains("Pleasure");
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.ODOMETER_READING).setValue("15000");
		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());
		assignmentTab.getAssetList().getAsset(AutoCaMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getTable()
				.getRow(2).getCell(PolicyConstants.AssignmentTabTable.PRIMARY_DRIVER).controls.comboBoxes.getFirst().setValueByIndex(1);
		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		//BUG PAS-11468 When endorsement is started through service, issueing it triggers Members Last Name error
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		//View added vehicle in view vehicle service
		ViewVehicleResponse response3 = HelperCommon.viewPolicyVehicles(policyNumber);
		if (response3.vehicleList.get(0).vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response3.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response3.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin2);
			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response3.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response3.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin1);
			});
		}
	}

	protected void pas502_CheckDuplicateVinAddVehicleService(PolicyType policyType) {
		assertSoftly(softly -> {

			mainApp().open();
			createCustomerIndividual();
			String policyNumber = createPolicy();

			TestData vehicleData = new TestDataManager().policy.get(policyType);
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//add vehicle
			String purchaseDate1 = "2012-02-21";
			String vin1 = getStateTestData(vehicleData, "DataGather", "TestData").getTestDataList("VehicleTab").get(0).getValue("VIN");

			ErrorResponseDto errorResponse =
					HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate1), ErrorResponseDto.class, 422);

			helperMiniServices.validateUniqueVinError(errorResponse, softly);
			String purchaseDate2 = "2015-02-11";
			String vin2 = "9BWFL61J244023215";

			//add vehicle
			helperMiniServices.addVehicleWithChecks(policyNumber, purchaseDate2, vin2, true);

			//try add the same vehicle one more time
			ErrorResponseDto errorResponse2 =
					HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin2, purchaseDate2), ErrorResponseDto.class, 422);

			helperMiniServices.validateUniqueVinError(errorResponse2, softly);

			//Start PAS-11005
			String purchaseDate3 = "2015-02-11";
			String vin3 = "ZFFCW56A830133118";

			//try add to expensive vehicle
			helperMiniServices.addVehicleWithChecks(policyNumber, purchaseDate3, vin3, true);
			//helperMiniServices.endorsementRateAndBind(policyNumber);//TODO-mstrazds: can not rate becuase of assignments. Uncomment when PAS-15195 is done.
		});
	}

	protected void pas8273_CheckIfOnlyActiveVehiclesAreAllowed(ETCSCoreSoftAssertions softly, PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehiclesGaragingAddress").getTestDataList("VehicleTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_VehiclesGaragingAddress").getTestData("AssignmentTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		policy.policyInquiry().start();
		//All info about first vehicle
		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		String modelYear1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.YEAR).getValue();
		String manufacturer1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MAKE).getValue();
		String series1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.SERIES).getValue();
		String model1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MODEL).getValue();
		String bodyStyle1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.BODY_STYLE).getValue();
		String vehIdentificationNo1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN).getValue();
		String ownership1 = vehicleTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.VehicleTab.OWNERSHIP)
				.getStaticElement(AutoCaMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE).getValue().replace("Owned", "OWN");
		String usage1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.PRIMARY_USE).getValue().replace("Commute (to/from work and school)", "WC");
		String garagingDifferent1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL).getValue().toLowerCase();
		String antiTheft1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ANTI_THEFT).getValue().toUpperCase().replace("UNKNOWN", "UNK");
		String vehType1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.TYPE).getValue().replace("Regular", "Regular");
		//Garaging address for first vehicle
		String zipCode1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ZIP_CODE).getValue();
		String address1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ADDRESS_LINE_1).getValue();
		String city1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.CITY).getValue();
		String state1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.STATE).getValue();
		//CA specific fields
		String distanceOneWayToWork1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL).getValue();
		String odometerReading1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ODOMETER_READING).getValue();
		String declaredAnnualMiles1 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.CUSTOMER_DECLARED_ANNUAL_MILES).getValue();
		VehicleTab.tableVehicleList.selectRow(2);

		//Get all info about second vehicle
		String modelYear2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.YEAR).getValue();
		String manufacturer2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MAKE).getValue();
		String series2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.SERIES).getValue();
		String model2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MODEL).getValue();
		String bodyStyle2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.BODY_STYLE).getValue();
		String vehIdentificationNo2 = vehicleTab.getInquiryAssetList().getStaticElement(VIN).getValue();
		String ownership2 = vehicleTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.VehicleTab.OWNERSHIP)
				.getStaticElement(AutoCaMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE).getValue().replace("Owned", "OWN");
		String usage2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.PRIMARY_USE).getValue().replace("Commute (to/from work and school)", "WC");
		String garagingDifferent2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL).getValue().toLowerCase();
		String antiTheft2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ANTI_THEFT).getValue().toUpperCase().replace("D-IMMOBILIZER/KEYLSS ENTRY/ALARM", "STD");
		String vehType2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.TYPE).getValue().replace("Regular", "Regular");
		//Get garaging address for second vehicle
		String zipCode2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ZIP_CODE).getValue();
		String address2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ADDRESS_LINE_1).getValue();
		String city2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.CITY).getValue();
		String state2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.STATE).getValue();
		//CA specific fields
		String distanceOneWayToWork2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL).getValue();
		String odometerReading2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ODOMETER_READING).getValue();
		String declaredAnnualMiles2 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.CUSTOMER_DECLARED_ANNUAL_MILES).getValue();

		ViewVehicleResponse response = HelperCommon.viewPolicyVehicles(policyNumber);
		Vehicle vehicleSt = response.vehicleList.stream().filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleNd = response.vehicleList.stream().filter(vehicle -> vehIdentificationNo2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt).isNotNull();
		softly.assertThat(vehicleSt.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt.series).isEqualTo(series1);
		softly.assertThat(vehicleSt.model).isEqualTo(model1);
		softly.assertThat(vehicleSt.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt.vehicleOwnership.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt.garagingDifferent)).isEqualTo(garagingDifferent1);
		softly.assertThat(vehicleSt.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt.oid).isNotEmpty();
		softly.assertThat(vehicleSt.garagingAddress.postalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt.garagingAddress.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt.garagingAddress.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt.garagingAddress.city).isEqualTo(city1);
		//CA Specific fields
		softly.assertThat(vehicleSt.distanceOneWayToWork).isEqualTo(distanceOneWayToWork1);
		softly.assertThat(vehicleSt.odometerReading).isEqualTo(odometerReading1);
		softly.assertThat(vehicleSt.declaredAnnualMiles).isEqualTo(declaredAnnualMiles1);

		softly.assertThat(vehicleNd).isNotNull();
		softly.assertThat(vehicleNd.modelYear).isEqualTo(modelYear2);
		softly.assertThat(vehicleNd.manufacturer).isEqualTo(manufacturer2);
		softly.assertThat(vehicleNd.series).isEqualTo(series2);
		softly.assertThat(vehicleNd.model).isEqualTo(model2);
		softly.assertThat(vehicleNd.bodyStyle).isEqualTo(bodyStyle2);
		softly.assertThat(vehicleNd.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleNd.vehicleOwnership.ownership).isEqualTo(ownership2);
		softly.assertThat(vehicleNd.usage).isEqualTo(usage2);
		softly.assertThat(vehicleNd.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleNd.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleNd.antiTheft).isEqualTo(antiTheft2);
		softly.assertThat(vehicleNd.vehTypeCd).isEqualTo(vehType2);
		softly.assertThat(vehicleNd.oid).isNotEmpty();
		softly.assertThat(vehicleNd.garagingAddress.postalCode).isEqualTo(zipCode2);
		softly.assertThat(vehicleNd.garagingAddress.addressLine1).isEqualTo(address2);
		softly.assertThat(vehicleNd.garagingAddress.stateProvCd).isEqualTo(state2);
		softly.assertThat(vehicleNd.garagingAddress.city).isEqualTo(city2);
		//CA specific fields
		softly.assertThat(vehicleNd.distanceOneWayToWork).isEqualTo(distanceOneWayToWork2);
		softly.assertThat(vehicleNd.odometerReading).isEqualTo(odometerReading2);
		softly.assertThat(vehicleNd.declaredAnnualMiles).isEqualTo(declaredAnnualMiles2);

		VehicleTab.buttonCancel.click();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(VIN).setValue("1FMEU15H7KLB19840");
		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		ViewVehicleResponse response1 = HelperCommon.viewPolicyVehicles(policyNumber);
		Vehicle vehicleSt1 = response1.vehicleList.stream().filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleNd1 = response1.vehicleList.stream().filter(vehicle -> vehIdentificationNo2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt1).isNotNull();
		softly.assertThat(vehicleSt1.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt1.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt1.series).isEqualTo(series1);
		softly.assertThat(vehicleSt1.model).isEqualTo(model1);
		softly.assertThat(vehicleSt1.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt1.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt1.vehicleOwnership.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt1.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt1.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt1.garagingDifferent)).isEqualTo(garagingDifferent1);
		softly.assertThat(vehicleSt1.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt1.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt1.oid).isNotEmpty();
		softly.assertThat(vehicleSt1.garagingAddress.postalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt1.garagingAddress.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt1.garagingAddress.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt1.garagingAddress.city).isEqualTo(city1);
		//CA specific fields
		softly.assertThat(vehicleSt1.distanceOneWayToWork).isEqualTo(distanceOneWayToWork1);
		softly.assertThat(vehicleSt1.odometerReading).isEqualTo(odometerReading1);
		softly.assertThat(vehicleSt1.declaredAnnualMiles).isEqualTo(declaredAnnualMiles1);

		softly.assertThat(vehicleNd1).isNotNull();
		softly.assertThat(vehicleNd1.modelYear).isEqualTo(modelYear2);
		softly.assertThat(vehicleNd1.manufacturer).isEqualTo(manufacturer2);
		softly.assertThat(vehicleNd1.series).isEqualTo(series2);
		softly.assertThat(vehicleNd1.model).isEqualTo(model2);
		softly.assertThat(vehicleNd1.bodyStyle).isEqualTo(bodyStyle2);
		softly.assertThat(vehicleNd1.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleNd1.vehicleOwnership.ownership).isEqualTo(ownership2);
		softly.assertThat(vehicleNd1.usage).isEqualTo(usage2);
		softly.assertThat(vehicleNd1.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleNd1.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleNd1.antiTheft).isEqualTo(antiTheft2);
		softly.assertThat(vehicleNd1.vehTypeCd).isEqualTo(vehType2);
		softly.assertThat(vehicleNd1.oid).isNotEmpty();
		softly.assertThat(vehicleNd1.garagingAddress.postalCode).isEqualTo(zipCode2);
		softly.assertThat(vehicleNd1.garagingAddress.addressLine1).isEqualTo(address2);
		softly.assertThat(vehicleNd1.garagingAddress.stateProvCd).isEqualTo(state2);
		softly.assertThat(vehicleNd1.garagingAddress.city).isEqualTo(city2);
		//CA specific fields
		softly.assertThat(vehicleNd1.distanceOneWayToWork).isEqualTo(distanceOneWayToWork2);
		softly.assertThat(vehicleNd1.odometerReading).isEqualTo(odometerReading2);
		softly.assertThat(vehicleNd1.declaredAnnualMiles).isEqualTo(declaredAnnualMiles2);

		testEValueDiscount.simplifiedPendedEndorsementIssue();

		policy.policyInquiry().start();
		//Gel all info about third vehicle
		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		String modelYear3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.YEAR).getValue();
		String manufacturer3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MAKE).getValue();
		String series3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.SERIES).getValue();
		String model3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MODEL).getValue();
		String bodyStyle3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.BODY_STYLE).getValue();
		String vehIdentificationNo3 = vehicleTab.getInquiryAssetList().getStaticElement(VIN).getValue();
		String ownership3 = vehicleTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.VehicleTab.OWNERSHIP)
				.getStaticElement(AutoCaMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE).getValue().replace("Owned", "OWN");
		String usage3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.PRIMARY_USE).getValue().replace("Commute (to/from work and school)", "WC");
		String garagingDifferent3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL).getValue().toLowerCase();
		String antiTheft3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ANTI_THEFT).getValue().toUpperCase().replace("N-NONE", "NONE");
		String vehType3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.TYPE).getValue().replace("Regular", "Regular");
		//Garaging address for third vehicle
		String zipCode3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ZIP_CODE).getValue();
		String address3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ADDRESS_LINE_1).getValue();
		String city3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.CITY).getValue();
		String state3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.STATE).getValue();
		//CA specific fields
		String distanceOneWayToWork3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL).getValue();
		String odometerReading3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.ODOMETER_READING).getValue();
		String declaredAnnualMiles3 = vehicleTab.getInquiryAssetList().getStaticElement(AutoCaMetaData.VehicleTab.CUSTOMER_DECLARED_ANNUAL_MILES).getValue();

		ViewVehicleResponse response2 = HelperCommon.viewPolicyVehicles(policyNumber);
		Vehicle vehicleSt2 = response2.vehicleList.stream().filter(vehicle -> vehIdentificationNo1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicleRd2 = response2.vehicleList.stream().filter(vehicle -> vehIdentificationNo3.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(vehicleSt2).isNotNull();
		softly.assertThat(vehicleSt2.modelYear).isEqualTo(modelYear1);
		softly.assertThat(vehicleSt2.manufacturer).isEqualTo(manufacturer1);
		softly.assertThat(vehicleSt2.series).isEqualTo(series1);
		softly.assertThat(vehicleSt2.model).isEqualTo(model1);
		softly.assertThat(vehicleSt2.bodyStyle).isEqualTo(bodyStyle1);
		softly.assertThat(vehicleSt2.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleSt2.vehicleOwnership.ownership).isEqualTo(ownership1);
		softly.assertThat(vehicleSt2.usage).isEqualTo(usage1);
		softly.assertThat(vehicleSt2.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleSt2.garagingDifferent)).isEqualTo(garagingDifferent2);
		softly.assertThat(vehicleSt2.antiTheft).isEqualTo(antiTheft1);
		softly.assertThat(vehicleSt2.vehTypeCd).isEqualTo(vehType1);
		softly.assertThat(vehicleSt2.oid).isNotEmpty();
		softly.assertThat(vehicleSt2.garagingAddress.postalCode).isEqualTo(zipCode1);
		softly.assertThat(vehicleSt2.garagingAddress.addressLine1).isEqualTo(address1);
		softly.assertThat(vehicleSt2.garagingAddress.stateProvCd).isEqualTo(state1);
		softly.assertThat(vehicleSt2.garagingAddress.city).isEqualTo(city1);
		//CA specific fields
		softly.assertThat(vehicleSt2.distanceOneWayToWork).isEqualTo(distanceOneWayToWork1);
		softly.assertThat(vehicleSt2.odometerReading).isEqualTo(odometerReading1);
		softly.assertThat(vehicleSt2.declaredAnnualMiles).isEqualTo(declaredAnnualMiles1);

		softly.assertThat(vehicleRd2).isNotNull();
		softly.assertThat(vehicleRd2.modelYear).isEqualTo(modelYear3);
		softly.assertThat(vehicleRd2.manufacturer).isEqualTo(manufacturer3);
		softly.assertThat(vehicleRd2.series).isEqualTo(series3);
		softly.assertThat(vehicleRd2.model).isEqualTo(model3);
		softly.assertThat(vehicleRd2.bodyStyle).isEqualTo(bodyStyle3);
		softly.assertThat(vehicleRd2.vehicleStatus).isEqualTo("active");
		softly.assertThat(vehicleRd2.vehicleOwnership.ownership).isEqualTo(ownership3);
		softly.assertThat(vehicleRd2.usage).isEqualTo(usage3);
		softly.assertThat(vehicleRd2.salvaged).isEqualTo(false);
		softly.assertThat(BooleanUtils.toStringYesNo(vehicleRd2.garagingDifferent)).isEqualTo(garagingDifferent3);
		softly.assertThat(vehicleRd2.antiTheft).isEqualTo(antiTheft3);
		softly.assertThat(vehicleRd2.vehTypeCd).isEqualTo(vehType3);
		softly.assertThat(vehicleRd2.oid).isNotEmpty();
		softly.assertThat(vehicleRd2.garagingAddress.postalCode).isEqualTo(zipCode3);
		softly.assertThat(vehicleRd2.garagingAddress.addressLine1).isEqualTo(address3);
		softly.assertThat(vehicleRd2.garagingAddress.stateProvCd).isEqualTo(state3);
		softly.assertThat(vehicleRd2.garagingAddress.city).isEqualTo(city3);
		//CA specific fields
		softly.assertThat(vehicleRd2.distanceOneWayToWork).isEqualTo(distanceOneWayToWork3);
		softly.assertThat(vehicleRd2.odometerReading).isEqualTo(odometerReading3);
		softly.assertThat(vehicleRd2.declaredAnnualMiles).isEqualTo(declaredAnnualMiles3);
	}

	protected void pas9610_UpdateVehicleService() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		SearchPage.openPolicy(policyNumber);

		//Get garage address from UI
		policy.policyInquiry().start();
		String zipCodeDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE).getValue();
		String addressDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1).getValue();
		String cityDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoCaMetaData.GeneralTab.NamedInsuredInformation.CITY).getValue();
		String stateDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoCaMetaData.GeneralTab.NamedInsuredInformation.STATE).getValue();
		GeneralTab.buttonCancel.click();

		//Create pended endorsement
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		PolicySummary endorsementResponse = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//Get OID from View vehicle
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		//send request to update vehicle service
		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.vehicleOwnership = new VehicleOwnership();
		updateVehicleRequest.vehicleOwnership.ownership = "OWN";
		updateVehicleRequest.usage = "FM";
		updateVehicleRequest.salvaged = false;
		updateVehicleRequest.garagingDifferent = false;
		updateVehicleRequest.antiTheft = "STD";
		updateVehicleRequest.registeredOwner = false;
		//CA specific fields
		updateVehicleRequest.distanceOneWayToWork = "21";
		updateVehicleRequest.odometerReading = "34000";
		updateVehicleRequest.declaredAnnualMiles ="31500";

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(updateVehicleResponse.usage).isEqualTo("FM");
			softly.assertThat(updateVehicleResponse.salvaged).isEqualTo(false);
			softly.assertThat(updateVehicleResponse.garagingDifferent).isEqualTo(false);
			softly.assertThat(updateVehicleResponse.antiTheft).isEqualTo("STD");
			softly.assertThat(updateVehicleResponse.registeredOwner).isEqualTo(false);
			//CA specific fields
			softly.assertThat(updateVehicleResponse.distanceOneWayToWork).isEqualTo("21");
			softly.assertThat(updateVehicleResponse.odometerReading).isEqualTo("34000");
			softly.assertThat(updateVehicleResponse.declaredAnnualMiles).isEqualTo("31500");

			//verify updated information with viewEndorsementVehicles
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).modelYear).isEqualTo(updateVehicleResponse.modelYear);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).manufacturer).isEqualTo(updateVehicleResponse.manufacturer);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).series).isEqualTo(updateVehicleResponse.series);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).model).isEqualTo(updateVehicleResponse.model);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).bodyStyle).isEqualTo(updateVehicleResponse.bodyStyle);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehIdentificationNo).isEqualTo(updateVehicleResponse.vehIdentificationNo);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).usage).isEqualTo("FM");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).salvaged).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingDifferent).isEqualTo(false);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).antiTheft).isEqualTo("STD");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).registeredOwner).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.postalCode).isEqualTo(zipCodeDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.addressLine1).isEqualTo(addressDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.stateProvCd).isEqualTo(stateDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.city).isEqualTo(cityDefault);

			//CA specific fields
			softly.assertThat(updateVehicleResponse.distanceOneWayToWork).isEqualTo("21");
			softly.assertThat(updateVehicleResponse.odometerReading).isEqualTo("34000");
			softly.assertThat(updateVehicleResponse.odometerReadingDate).isEqualTo(endorsementDate);
			softly.assertThat(updateVehicleResponse.declaredAnnualMiles).isEqualTo("31500");
		});

		//PAS-7145 Start
		//Check vehicle update service when  garage address is different
		String zipCodeGarage = "23703";
		String addressGarage = "4112 FORREST HILLS DR";
		String cityGarage = "PORTSMOUTH";
		String stateGarage = "VA";

		//send request to update vehicle service
		VehicleUpdateDto updateGaragingAddressVehicleRequest = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest.vehicleOwnership = new VehicleOwnership();
		updateGaragingAddressVehicleRequest.vehicleOwnership.ownership = "OWN";
		updateGaragingAddressVehicleRequest.usage = "FM";
		updateGaragingAddressVehicleRequest.salvaged = false;
		updateGaragingAddressVehicleRequest.garagingDifferent = false;
		updateGaragingAddressVehicleRequest.antiTheft = "STD";
		updateGaragingAddressVehicleRequest.registeredOwner = false;
		updateGaragingAddressVehicleRequest.garagingDifferent = true;
		updateGaragingAddressVehicleRequest.garagingAddress = new Address();
		updateGaragingAddressVehicleRequest.garagingAddress.postalCode = zipCodeGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.addressLine1 = addressGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.city = cityGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.stateProvCd = stateGarage;

		Vehicle updateVehicleResponseGaragingAddress = HelperCommon.updateVehicle(policyNumber, oid, updateGaragingAddressVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponseGaragingAddress.vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(updateVehicleResponseGaragingAddress.usage).isEqualTo("FM");
			softly.assertThat(updateVehicleResponseGaragingAddress.salvaged).isEqualTo(false);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingDifferent).isEqualTo(true);
			softly.assertThat(updateVehicleResponseGaragingAddress.antiTheft).isEqualTo("STD");
			softly.assertThat(updateVehicleResponseGaragingAddress.registeredOwner).isEqualTo(false);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddress.postalCode).isEqualTo(zipCodeGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddress.addressLine1).isEqualTo(addressGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddress.city).isEqualTo(cityGarage);
			softly.assertThat(updateVehicleResponseGaragingAddress.garagingAddress.stateProvCd).isEqualTo(stateGarage);

			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).modelYear).isEqualTo(updateVehicleResponse.modelYear);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).manufacturer).isEqualTo(updateVehicleResponse.manufacturer);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).series).isEqualTo(updateVehicleResponse.series);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).model).isEqualTo(updateVehicleResponse.model);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).bodyStyle).isEqualTo(updateVehicleResponse.bodyStyle);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehIdentificationNo).isEqualTo(updateVehicleResponse.vehIdentificationNo);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).usage).isEqualTo("FM");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).salvaged).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingDifferent).isEqualTo(true);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).antiTheft).isEqualTo("STD");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).registeredOwner).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.postalCode).isEqualTo(zipCodeGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.addressLine1).isEqualTo(addressGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.stateProvCd).isEqualTo(stateGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.city).isEqualTo(cityGarage);

			//CA specific fields
			softly.assertThat(updateVehicleResponse.distanceOneWayToWork).isEqualTo("21");
			softly.assertThat(updateVehicleResponse.odometerReading).isEqualTo("34000");
			softly.assertThat(updateVehicleResponse.odometerReadingDate).isEqualTo(endorsementDate);
			softly.assertThat(updateVehicleResponse.declaredAnnualMiles).isEqualTo("31500");
		});
	}

	protected void pas7147_VehicleUpdateBusinessBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);

		assertThat(responseAddVehicle.oid).isNotEmpty();
		String oid = responseAddVehicle.oid;
		printToLog("oid: " + oid);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.usage = "BU";
		updateVehicleRequest.registeredOwner = false;
		updateVehicleRequest.odometerReading = "22000";

		VehicleUpdateResponseDto updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.usage).isEqualTo("BU");
			assertThat(updateVehicleResponse.validations.get(0).message).startsWith("Usage is Business");
		});

		ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(helperMiniServices.hasError(rateResponse, ErrorDxpEnum.Errors.REGISTERED_OWNERS)).isTrue();
			softly.assertThat(helperMiniServices.hasError(rateResponse, ErrorDxpEnum.Errors.USAGE_IS_BUSINESS)).isTrue();
		});
	}

	protected void pas11618_UpdateVehicleLeasedFinancedInfoBody(ETCSCoreSoftAssertions softly, String ownershipType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Add vehicle with specific info
		Vehicle vehicleAddRequest = new Vehicle();
		vehicleAddRequest.purchaseDate = purchaseDate;
		vehicleAddRequest.vehIdentificationNo = vin;
		String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		String zipCode = "23703";
		String addressLine1 = "4112 FORREST HILLS DR";
		String addressLine2 = "Apt. 202";
		String city = "PORTSMOUTH";
		String state = "VA";
		String otherName = "other name";
		String secondName = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2;
		updateVehicleLeasedFinanced.vehicleOwnership.city = city;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = state;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCode;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = ownershipType;
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherName;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondName;
		//CA specific
		updateVehicleLeasedFinanced.odometerReading = "22000";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		if ("LSD".equals(ownershipType)) {
			assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE)).hasValue("Leased");
		}
		if ("FNC".equals(ownershipType)) {
			assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE)).hasValue("Financed");
		}
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.FIRST_NAME)).hasValue("Other");
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.OWNER_NO_LABEL)).hasValue(otherName); //can't take the value of the field with no label
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.SECOND_NAME)).hasValue(secondName);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.ADDRESS_LINE_1)).hasValue(addressLine1);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.ADDRESS_LINE_2)).hasValue(addressLine2);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.CITY)).hasValue(city);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.STATE)).hasValue(state);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.ZIP_CODE)).hasValue(zipCode);
		mainApp().close();

		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse.canAddVehicle).isEqualTo(true);
		List<Vehicle> sortedVehicles = viewEndorsementVehicleResponse.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		assertThat(viewEndorsementVehicleResponse.vehicleList).containsAll(sortedVehicles);
		Vehicle newVehicle1 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> newVehicleOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle1.vehIdentificationNo).isEqualTo(vin);
		assertThat(newVehicle1).isNotNull();
		assertThat(newVehicle1.oid).isEqualTo(newVehicleOid);
		softly.assertThat(newVehicle1.vehicleOwnership.ownership).isEqualTo(ownershipType);
		softly.assertThat(newVehicle1.vehicleOwnership.addressLine1).isEqualTo(addressLine1);
		softly.assertThat(newVehicle1.vehicleOwnership.addressLine2).isEqualTo(addressLine2);
		softly.assertThat(newVehicle1.vehicleOwnership.city).isEqualTo(city);
		softly.assertThat(newVehicle1.vehicleOwnership.stateProvCd).isEqualTo(state);
		softly.assertThat(newVehicle1.vehicleOwnership.postalCode).isEqualTo(zipCode);
		softly.assertThat(newVehicle1.vehicleOwnership.name).isEqualTo(otherName);
		softly.assertThat(newVehicle1.vehicleOwnership.secondName).isEqualTo(secondName);

		AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsementVehiclesMetaData(policyNumber, newVehicleOid);
		AttributeMetadata metaDataFieldResponse = testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.ownership", true, true, false, null, "String");
		softly.assertThat(metaDataFieldResponse.valueRange.get("OWN")).isEqualTo("Owned");
		softly.assertThat(metaDataFieldResponse.valueRange.get("FNC")).isEqualTo("Financed");
		softly.assertThat(metaDataFieldResponse.valueRange.get("LSD")).isEqualTo("Leased");

		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.name", true, true, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.secondName", true, true, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.postalCode", true, true, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine1", true, true, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine2", true, true, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.city", true, true, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.stateProvCd", true, true, false, null, "String");

		ViewVehicleResponse policyValidateVehicleInfoResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oldVehicleOid = policyValidateVehicleInfoResponse.vehicleList.get(0).oid;
		AttributeMetadata[] metaDataResponseOwned = HelperCommon.viewEndorsementVehiclesMetaData(policyNumber, oldVehicleOid);
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.ownership", true, true, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.name", false, false, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.secondName", false, false, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.postalCode", false, false, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.addressLine1", false, false, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.addressLine2", false, false, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.city", false, false, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.stateProvCd", false, false, false, null, "String");

		//helperMiniServices.endorsementRateAndBind(policyNumber); //TODO-mstrazds: can be updated to bind here when assignments are implemented

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		setAssignmentsInPASUI();//TODO-mstrazds: can be removed when Driver assignments are implemented
		helperMiniServices.endorsementRateAndBind(policyNumber);

		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		VehicleUpdateDto updateVehicleOwned = new VehicleUpdateDto();
		updateVehicleOwned.vehicleOwnership = new VehicleOwnership();
		updateVehicleOwned.vehicleOwnership.ownership = "OWN";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleOwned);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		//BUG PAS-14395 Update Vehicle service failed to update ownership
		softly.assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE).getValue()).isEqualTo("Owned");
		mainApp().close();

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.secondEndorsementIssueCheck();
	}

	protected void pas13252_UpdateVehicleGaragingAddressProblemBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		assertSoftly(softly -> {
			String purchaseDate = "2013-02-22";
			String vin = "1HGFA16526L081415";

			//Add vehicle with specific info
			Vehicle vehicleAddRequest = new Vehicle();
			vehicleAddRequest.purchaseDate = purchaseDate;
			vehicleAddRequest.vehIdentificationNo = vin;
			String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);

			String zipCode = "23703";
			String addressLine1 = "4112 FORREST HILLS DR";
			String city = "PORTSMOUTH";
			String state = "VA";
			//Update vehicle Garaging Info
			VehicleUpdateDto updateVehicleGaraging = new VehicleUpdateDto();
			updateVehicleGaraging.garagingDifferent = true;
			updateVehicleGaraging.garagingAddress = new Address();
			updateVehicleGaraging.garagingAddress.postalCode = zipCode;
			updateVehicleGaraging.garagingAddress.addressLine1 = addressLine1;
			updateVehicleGaraging.garagingAddress.city = city;
			updateVehicleGaraging.garagingAddress.stateProvCd = state;
			updateVehicleGaraging.odometerReading = "22000";
			HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleGaraging);

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.tableVehicleList.selectRow(2);
			assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL)).hasValue("Yes");
			assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.ZIP_CODE)).hasValue(zipCode);
			assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.ADDRESS_LINE_1)).hasValue(addressLine1);
			assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.CITY)).hasValue(city);
			assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.STATE)).hasValue(state);

			mainApp().close();
			//helperMiniServices.endorsementRateAndBind(policyNumber);//TODO-mstrazds: can be updated to bind through DXP when Driver assignments are implemented

			mainApp().open();
			SearchPage.openPolicy(policyNumber);

			setAssignmentsInPASUI();//TODO-mstrazds: can be removed/updated when assignments are implemented
			helperMiniServices.endorsementRateAndBind(policyNumber);
			testEValueDiscount.secondEndorsementIssueCheck();
		});
	}

	private void setAssignmentsInPASUI() {
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());
		new AssignmentTab().getAssetList().getAsset(AutoCaMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getTable()
				.getRow(2).getCell(PolicyConstants.AssignmentTabTable.PRIMARY_DRIVER).controls.comboBoxes.getFirst().setValueByIndex(1);
		assignmentTab.saveAndExit();
	}

}
