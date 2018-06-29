package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.RULE_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.ws.rs.core.Response;
import org.assertj.core.api.SoftAssertions;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.UpdateRulesOverrideActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.ComboBox;

public class TestMiniServicesVehiclesHelper extends PolicyBaseTest {

	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private VehicleTab vehicleTab = new VehicleTab();
	private GeneralTab generalTab = new GeneralTab();
	private AssignmentTab assignmentTab = new AssignmentTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestMiniServicesGeneralHelper testMiniServicesGeneralHelper = new TestMiniServicesGeneralHelper();
	private TestMiniServicesCoveragesHelper testMiniServicesCoveragesHelper = new TestMiniServicesCoveragesHelper();

	protected void pas8275_vinValidateCheck(SoftAssertions softly, PolicyType policyType) {
		String getAnyActivePolicy = "select ps.policyNumber, ps.POLICYSTATUSCD, ps.EFFECTIVE\n"
				+ "from policySummary ps\n"
				+ "where 1=1\n"
				+ "and ps.policyNumber not like 'Q%'\n"
				+ "and ps.policyNumber like '%SS%'\n"
				+ "and ps.POLICYSTATUSCD = 'issued'\n"
				+ "and to_char(ps.EFFECTIVE, 'yyyy-MM-dd') = to_char(sysdate, 'yyyy-MM-dd')\n"
				+ "and rownum = 1";
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		String policyNumber;
		if (DBService.get().getValue(getAnyActivePolicy).isPresent()) {
			policyNumber = DBService.get().getValue(getAnyActivePolicy).get();
		} else {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(getPolicyTD());
			policyNumber = PolicySummaryPage.getPolicyNumber();
		}

		String vin1 = "aaaa"; //VIN too short
		AAAVehicleVinInfoRestResponseWrapper response = HelperCommon.executeVinInfo(policyNumber, vin1, endorsementDate);
		softly.assertThat(response.vehicles).isEmpty();
		softly.assertThat(response.validationMessage).isEqualTo("Invalid VIN length");

		String vin2 = "12345678901234567890"; //VIN too long
		AAAVehicleVinInfoRestResponseWrapper response2 = HelperCommon.executeVinInfo(policyNumber, vin2, null);
		softly.assertThat(response2.vehicles).isEmpty();
		softly.assertThat(response2.validationMessage).isEqualTo("Invalid VIN length");

		String vin3 = "4T1BF1FK0H1234567"; //VIN check digit failed
		AAAVehicleVinInfoRestResponseWrapper response3 = HelperCommon.executeVinInfo(policyNumber, vin3, null);
		softly.assertThat(response3.vehicles).isEmpty();
		softly.assertThat(response3.validationMessage).isEqualTo("Check Digit is Incorrect");

		String vin4 = "4T1BF1FK0H"; //VIN from VIN table but too short
		AAAVehicleVinInfoRestResponseWrapper response4 = HelperCommon.executeVinInfo(policyNumber, vin4, null);
		softly.assertThat(response4.vehicles).isEmpty();
		softly.assertThat(response4.validationMessage).isEqualTo("Invalid VIN length");

		String vin5 = "1D30E42J451234567"; //VIN NOT from VIN table to Check VIN service
		AAAVehicleVinInfoRestResponseWrapper response5 = HelperCommon.executeVinInfo(policyNumber, vin5, null);
		softly.assertThat(response5.vehicles).isEmpty();
		softly.assertThat(response5.validationMessage).isEqualTo("VIN is not on AAA VIN Table");

		String vin0 = "4T1BF1FK0HU624693"; //VIN from VIN table
		AAAVehicleVinInfoRestResponseWrapper response0 = HelperCommon.executeVinInfo(policyNumber, vin0, endorsementDate);
		softly.assertThat(response0.vehicles.get(0).vin).isEqualTo(vin0);
		softly.assertThat(response0.vehicles.get(0).year.toString()).isNotEmpty();
		softly.assertThat(response0.vehicles.get(0).make).isNotEmpty();
		softly.assertThat(response0.vehicles.get(0).modelText).isNotEmpty();
		softly.assertThat(response0.vehicles.get(0).seriesText).isNotEmpty();
		softly.assertThat(response0.vehicles.get(0).bodyStyleCd).isNotEmpty();
		softly.assertThat(response0.validationMessage).isEmpty();
	}

	protected void pas7082_AddVehicle(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		policy.policyInquiry().start();
		String zipCodeDefault = generalTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
		String addressDefault = generalTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
		String cityDefault = generalTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
		String stateDefault = generalTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		String vin1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();
		mainApp().close();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Add new vehicle
		String purchaseDate = "2012-02-21";
		String vin2 = "1HGEM21504L055795";

		Vehicle response1 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin2);
		assertSoftly(softly -> {
					softly.assertThat(response1.modelYear).isEqualTo("2004");
					softly.assertThat(response1.manufacturer).isEqualTo("HONDA");
					softly.assertThat(response1.series).isEqualTo("CIVIC LX");
					softly.assertThat(response1.model).isEqualTo("CIVIC");
					softly.assertThat(response1.bodyStyle).isEqualTo("COUPE");
					softly.assertThat(response1.oid).isNotNull();
					softly.assertThat(response1.vehIdentificationNo).isEqualTo(vin2);
					softly.assertThat(response1.garagingDifferent).isEqualTo(false);
					softly.assertThat(response1.vehTypeCd).isEqualTo("PPA");
					softly.assertThat(response1.garagingAddress.postalCode).isEqualTo(zipCodeDefault);
					softly.assertThat(response1.garagingAddress.addressLine1).isEqualTo(addressDefault);
					softly.assertThat(response1.garagingAddress.city).isEqualTo(cityDefault);
					softly.assertThat(response1.garagingAddress.stateProvCd).isEqualTo(stateDefault);
				}
		);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		vehicleTab.getAssetList().getAsset(USAGE.getLabel(), ComboBox.class).setValue("Pleasure");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
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

	protected void pas7147_VehicleUpdateBusinessBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String oid = responseAddVehicle.oid;
		printToLog("oid: " + oid);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.usage = "Business";
		//TODO remove garaging address from code once it is not necessary to pass it
		//updateVehicleRequest.garagingDifferent = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.usage).isEqualTo("Business");
			assertThat(((VehicleUpdateResponseDto) updateVehicleResponse).ruleSets.get(0).errors.get(0)).contains("Usage is Business");
		});

		ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.REGISTERED_OWNERS.getCode());
			softly.assertThat(rateResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.REGISTERED_OWNERS.getMessage());
			softly.assertThat(rateResponse.errors.get(0).field).isEqualTo("vehOwnerInd");
			softly.assertThat(rateResponse.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.USAGE_IS_BUSINESS.getCode());
			softly.assertThat(rateResponse.errors.get(1).message).contains(ErrorDxpEnum.Errors.USAGE_IS_BUSINESS.getMessage());
			softly.assertThat(rateResponse.errors.get(1).field).isEqualTo("vehicleUsageCd");
		});

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "PAS-7147", 422);
		assertSoftly(softly -> {
			softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(bindResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getCode());
			softly.assertThat(bindResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getMessage());
		});
	}

	protected void pas7147_VehicleUpdateRegisteredOwnerBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String oid = responseAddVehicle.oid;
		printToLog("oid: " + oid);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.registeredOwner = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.registeredOwner).isEqualTo(false);
			assertThat(((VehicleUpdateResponseDto) updateVehicleResponse).ruleSets.get(0).errors.get(0)).contains("Registered Owners");
		});
		//TODO jpukenaite-issue or not, "Usage is Business" should not be displaying
		//Check premium after new vehicle was added
		//		HashMap<String, String> rateResponse = HelperCommon.endorsementRateError(policyNumber, 422);
		//		assertThat(rateResponse.entrySet().toString()).contains(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());

		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, "PAS-7147", 422);
		assertSoftly(softly -> {
			softly.assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(bindResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getCode());
			softly.assertThat(bindResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getMessage());
		});
	}

	protected void pas488_VehicleDeleteBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//run get vehicle information service.
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;
		String vin = viewVehicleResponse.vehicleList.get(0).vehIdentificationNo;

		//run delete vehicle service
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oid);
		assertSoftly(softly -> {
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oid);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			softly.assertThat(deleteVehicleResponse.vehIdentificationNo).isEqualTo(vin);
			assertThat(deleteVehicleResponse.ruleSets).isEqualTo(null);
		});

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertSoftly(softly -> {
			assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).oid).isEqualTo(oid);
			assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin);
			assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).vehicleStatus).isEqualTo("pendingRemoval");
		});

		//Rate policy
		PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
		});

		//Bind policy
		helperMiniServices.bindEndorsementWithCheck(policyNumber);
	}

	protected void pas502_CheckDuplicateVinAddVehicleService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		TestData vehicleData = new TestDataManager().policy.get(policyType);
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//add vehicle
		String purchaseDate1 = "2012-02-21";
		String vin1 = getStateTestData(vehicleData, "DataGather", "TestData").getTestDataList("VehicleTab").get(0).getValue("VIN");

		ErrorResponseDto errorResponse = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate1, vin1);
		assertSoftly(softly -> {
			softly.assertThat(errorResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.DUPLICATE_VIN.getCode());
			softly.assertThat(errorResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.DUPLICATE_VIN.getMessage());
			softly.assertThat(errorResponse.errors.get(0).field).isEqualTo("vehIdentificationNo");
		});
		String purchaseDate2 = "2015-02-11";
		String vin2 = "9BWFL61J244023215";

		//add vehicle
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate2, vin2);
		assertThat(responseAddVehicle.oid).isNotEmpty();

		//try add the same vehicle one more time
		ErrorResponseDto errorResponse2 = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate2, vin2);
		assertSoftly(softly -> {
			softly.assertThat(errorResponse2.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponse2.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponse2.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.DUPLICATE_VIN.getCode());
			softly.assertThat(errorResponse2.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.DUPLICATE_VIN.getMessage());
			softly.assertThat(errorResponse2.errors.get(0).field).isEqualTo("vehIdentificationNo");
		});

		//Start PAS-11005
		String purchaseDate3 = "2015-02-11";
		String vin3 = "ZFFCW56A830133118";

		//try add to expensive vehicle
		ErrorResponseDto errorResponse3 = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate3, vin3);
		assertSoftly(softly -> {
			softly.assertThat(errorResponse3.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponse3.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponse3.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.TOO_EXPENSIVE_VEHICLE.getCode());
			softly.assertThat(errorResponse3.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.TOO_EXPENSIVE_VEHICLE.getMessage());
			softly.assertThat(errorResponse3.errors.get(0).field).isEqualTo("vehIdentificationNo");
		});
	}

	protected void pas10449_ViewVehicleServiceCheckOrderOfVehicle(PolicyType policyType, String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Get VIN's
		String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
		String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");
		String vin4 = td.getTestDataList("VehicleTab").get(3).getValue("VIN");

		//hit view vehicle service to get Vehicle order
		ViewVehicleResponse viewVehicleResponse1 = HelperCommon.viewPolicyVehicles(policyNumber);
		assertThat(viewVehicleResponse1.canAddVehicle).isEqualTo(true);
		List<Vehicle> originalOrderingFromResponse = ImmutableList.copyOf(viewVehicleResponse1.vehicleList);
		List<Vehicle> sortedVehicles = viewVehicleResponse1.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);

		assertSoftly(softly -> {
			assertThat(originalOrderingFromResponse).containsAll(sortedVehicles);

			Vehicle vehicle1 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle2 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle3 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicle4 = viewVehicleResponse1.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Conversion");
		});

		// Perform endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);

		//Add new vehicle to have pending vehicle
		String purchaseDate = "2013-02-22";
		String vin5 = "1HGFA16526L081415";
		Vehicle addVehicleResponse = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin5);
		assertThat(addVehicleResponse.oid).isNotEmpty();

		String vin6 = "2GTEC19K8S1525936";
		Vehicle addVehicleResponse2 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin6);
		assertThat(addVehicleResponse2.oid).isNotEmpty();

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);

		List<Vehicle> originalOrderingFromResponse2 = ImmutableList.copyOf(viewEndorsementVehicleResponse2.vehicleList);
		List<Vehicle> sortedVehicles1 = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles1.sort(Vehicle.PENDING_ENDORSEMENT_COMPARATOR);
		assertSoftly(softly -> {
			softly.assertThat(originalOrderingFromResponse2).isEqualTo(sortedVehicles1);

			Vehicle vehicle6 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin6.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle6).isNotNull();
			softly.assertThat(vehicle6.vehicleStatus).isEqualTo("pending");
			softly.assertThat(vehicle6.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle5 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin5.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle5).isNotNull();
			softly.assertThat(vehicle5.vehicleStatus).isEqualTo("pending");
			softly.assertThat(vehicle5.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle1 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle3 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicle4 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Conversion");
		});
	}

	protected void pas9610_UpdateVehicleService() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Get garage address from UI
		policy.policyInquiry().start();
		String zipCodeDefault = generalTab.getInquiryAssetList().getStaticElement(ZIP_CODE.getLabel()).getValue();
		String addressDefault = generalTab.getInquiryAssetList().getStaticElement(ADDRESS_LINE_1.getLabel()).getValue();
		String cityDefault = generalTab.getInquiryAssetList().getStaticElement(CITY.getLabel()).getValue();
		String stateDefault = generalTab.getInquiryAssetList().getStaticElement(STATE.getLabel()).getValue();
		GeneralTab.buttonCancel.click();

		//Create pended endorsement
		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		//Get OID from View vehicle
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		//send request to update vehicle service
		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.vehicleOwnership = new VehicleOwnership();
		updateVehicleRequest.vehicleOwnership.ownership = "OWN";
		updateVehicleRequest.usage = "Pleasure";
		updateVehicleRequest.salvaged = false;
		updateVehicleRequest.garagingDifferent = false;
		updateVehicleRequest.antiTheft = "STD";
		updateVehicleRequest.registeredOwner = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(updateVehicleResponse.usage).isEqualTo("Pleasure");
			softly.assertThat(updateVehicleResponse.salvaged).isEqualTo(false);
			softly.assertThat(updateVehicleResponse.garagingDifferent).isEqualTo(false);
			softly.assertThat(updateVehicleResponse.antiTheft).isEqualTo("STD");
			softly.assertThat(updateVehicleResponse.registeredOwner).isEqualTo(false);

			//verify updated information with viewEndorsementVehicles
			ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).modelYear).isEqualTo(updateVehicleResponse.modelYear);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).manufacturer).isEqualTo(updateVehicleResponse.manufacturer);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).series).isEqualTo(updateVehicleResponse.series);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).model).isEqualTo(updateVehicleResponse.model);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).bodyStyle).isEqualTo(updateVehicleResponse.bodyStyle);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehIdentificationNo).isEqualTo(updateVehicleResponse.vehIdentificationNo);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).vehicleOwnership.ownership).isEqualTo("OWN");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).usage).isEqualTo("Pleasure");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).salvaged).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingDifferent).isEqualTo(false);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).antiTheft).isEqualTo("STD");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).registeredOwner).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.postalCode).isEqualTo(zipCodeDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.addressLine1).isEqualTo(addressDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.stateProvCd).isEqualTo(stateDefault);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.city).isEqualTo(cityDefault);
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
		updateGaragingAddressVehicleRequest.usage = "Pleasure";
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
			softly.assertThat(updateVehicleResponseGaragingAddress.usage).isEqualTo("Pleasure");
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
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).usage).isEqualTo("Pleasure");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).salvaged).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingDifferent).isEqualTo(true);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).antiTheft).isEqualTo("STD");
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).registeredOwner).isEqualTo(false);

			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.postalCode).isEqualTo(zipCodeGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.addressLine1).isEqualTo(addressGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.stateProvCd).isEqualTo(stateGarage);
			softly.assertThat(viewEndorsementVehicleResponse.vehicleList.get(0).garagingAddress.city).isEqualTo(cityGarage);
		});
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
			HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleGaraging);

			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.tableVehicleList.selectRow(2);
			vehicleTab.getAssetList().getAsset(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL).verify.value("Yes");
			vehicleTab.getAssetList().getAsset(ZIP_CODE).verify.value(zipCode);
			vehicleTab.getAssetList().getAsset(ADDRESS_LINE_1).verify.value(addressLine1);
			vehicleTab.getAssetList().getAsset(CITY).verify.value(city);
			vehicleTab.getAssetList().getAsset(STATE).verify.value(state);

			mainApp().close();
			helperMiniServices.endorsementRateAndBind(policyNumber);

			mainApp().open();
			SearchPage.openPolicy(policyNumber);
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

			mainApp().open();
			SearchPage.openPolicy(policyNumber);
			testEValueDiscount.secondEndorsementIssueCheck();
		});
	}

	@SuppressWarnings("unchecked")
	protected void pas9546_maxVehiclesBody() {
		mainApp().open();
		//Default policy has 1 vehicles
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			//Add 6 vehicles
			String purchaseDate2 = "2013-01-20";
			String vin2 = "1C4BJWDG0JL847133"; //jeep wrangler 2018
			addVehicleWithChecks(policyNumber, purchaseDate2, vin2, true);

			String purchaseDate3 = "2013-01-21";
			String vin3 = "JF1GJAH65EH007244"; //Subaru Impreza 2014
			addVehicleWithChecks(policyNumber, purchaseDate3, vin3, true);

			String purchaseDate4 = "2013-02-22";
			String vin4 = "3MZBN1M39JM170308"; //Mazda 3 2018
			addVehicleWithChecks(policyNumber, purchaseDate4, vin4, true);

			String purchaseDate5 = "2013-03-23";
			String vin5 = "5YFBURHE0HP576402"; // Toyota Corolla 2017
			addVehicleWithChecks(policyNumber, purchaseDate5, vin5, true);

			String purchaseDate6 = "2013-04-24";
			String vin6 = "JTDKBRFU2H3564115"; //Toyota Prius 2017
			addVehicleWithChecks(policyNumber, purchaseDate6, vin6, true);

			String purchaseDate7 = "2013-05-25";
			String vin7 = "JTHHP5AY5JA002692"; //Lexus LC 500 2018
			addVehicleWithChecks(policyNumber, purchaseDate7, vin7, true);

			String purchaseDate8 = "2013-06-26";
			String vin8 = "2HGFC2F70HH505174"; //2017 Honda Civic
			addVehicleWithChecks(policyNumber, purchaseDate8, vin8, false);

			//add the 9th vehicle, check error
			String purchaseDate9 = "2013-06-27";
			String vin9 = "19XFC1F39HE010621"; //2017 Honda Civic
			Vehicle request = new Vehicle();
			request.purchaseDate = purchaseDate9;
			request.vehIdentificationNo = vin9;

			ErrorResponseDto responseAddVehicleError = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate9, vin9);
			softly.assertThat(responseAddVehicleError.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(responseAddVehicleError.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(responseAddVehicleError.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getCode());
			softly.assertThat(responseAddVehicleError.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getMessage());

			//Rate endorsement
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			//Bind endorsement
			HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
			SearchPage.openPolicy(policyNumber);
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Add 6 vehicles
			request.purchaseDate = purchaseDate9;
			request.vehIdentificationNo = vin9;

			ErrorResponseDto responseAddVehicleError2 = HelperCommon.viewAddVehicleServiceErrors(policyNumber, purchaseDate9, vin9);
			softly.assertThat(responseAddVehicleError2.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(responseAddVehicleError2.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(responseAddVehicleError2.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getCode());
			softly.assertThat(responseAddVehicleError2.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getMessage());

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.tableVehicleList.removeRow(6);
			vehicleTab.saveAndExit();
			mainApp().close();

			addVehicleWithChecks(policyNumber, purchaseDate6, vin6, false);
			PolicyPremiumInfo[] endorsementRateResponse2 = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
			softly.assertThat(endorsementRateResponse2[0].actualAmt).isNotBlank();

			helperMiniServices.bindEndorsementWithCheck(policyNumber);

			testEValueDiscount.secondEndorsementIssueCheck();
		});
	}

	/**
	 * @author Oleg Stasyuk
	 * @name no error about garaging address being different when binding endorsement
	 * @scenario 1. Create a policy
	 * 2. Create endorsement through service, add vehicle
	 * 3. Set garaging address different than residential with ZIP code not equal to Residential address ZIP
	 * 4. Rate, bind - check no error message
	 * 5. Create endorsement in UI, rate bind
	 * 6. Check no overridden rule 200021 about the garaging zip code
	 */
	protected void pas14501_garagingDifferentBody(String state, SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//get all vehicles
		ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String oldVehicleOid = responseViewVehicles.vehicleList.get(0).oid;

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Validate
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		AAAVehicleVinInfoRestResponseWrapper vinValidateResponse = HelperCommon.executeVinInfo(policyNumber, vin, endorsementDate);
		softly.assertThat(vinValidateResponse.vehicles.get(0).vin).isEqualTo(vin);

		//Add new vehicle
		//BUG PAS-14688, PAS-14689, PAS-14690, PAS-14691 - Add Vehicle for DC, KS, NY, OR
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		SearchPage.openPolicy(policyNumber);

		//Update Vehicle with proper Usage and Registered Owner
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		//PAS-14501 start
		//Check vehicle update service when  garage address is different
		String zipCodeGarage = "23703";
		String addressGarage = "4112 FORREST HILLS DR";
		String cityGarage = "PORTSMOUTH";
		String stateGarage = "VA";
		VehicleUpdateDto updateGaragingAddressVehicleRequest = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest.garagingDifferent = true;
		updateGaragingAddressVehicleRequest.garagingAddress = new Address();
		updateGaragingAddressVehicleRequest.garagingAddress.postalCode = zipCodeGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.addressLine1 = addressGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.city = cityGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.stateProvCd = stateGarage;
		Vehicle updateVehicleGaragingAddressResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingDifferent).isEqualTo(true);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.postalCode).isEqualTo(zipCodeGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.addressLine1).isEqualTo(addressGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.city).isEqualTo(cityGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.stateProvCd).isEqualTo(stateGarage);
		//PAS-14501 end

		//Rate endorsement
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		if (!"VA".equals(state)) {
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getCode(), ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getMessage(), "attributeForRules");

			VehicleUpdateDto updateGaragingAddressVehicleRequest2 = new VehicleUpdateDto();
			updateGaragingAddressVehicleRequest2.garagingDifferent = false;
			Vehicle updateVehicleGaragingAddressResponse2 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest2);
			softly.assertThat(updateVehicleGaragingAddressResponse2.garagingDifferent).isEqualTo(false);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
		}
		//Bind endorsement
		helperMiniServices.bindEndorsementWithCheck(policyNumber);

		testEValueDiscount.secondEndorsementIssueCheck();

		policy.updateRulesOverride().start();
		CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200021").getCell(1)).isAbsent();
		UpdateRulesOverrideActionTab.btnCancel.click();
	}

	protected void pas12246_ViewVehiclePendingRemovalService(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Get VIN's
		String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
		String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");
		String vin4 = td.getTestDataList("VehicleTab").get(3).getValue("VIN");

		//run get vehicle information service.
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		List<Vehicle> originalOrderingFromResponse = ImmutableList.copyOf(viewVehicleResponse.vehicleList);
		List<Vehicle> sortedVehicles = viewVehicleResponse.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		assertSoftly(softly -> {

			assertThat(originalOrderingFromResponse).containsAll(sortedVehicles);

			Vehicle vehicle1 = viewVehicleResponse.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle1).isNotNull();
			softly.assertThat(vehicle1.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle1.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle2 = viewVehicleResponse.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle2).isNotNull();
			softly.assertThat(vehicle2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");
			softly.assertThat(vehicle2.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicle3 = viewVehicleResponse.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle3).isNotNull();
			softly.assertThat(vehicle3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle3.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicle4 = viewVehicleResponse.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicle4).isNotNull();
			softly.assertThat(vehicle4.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicle4.vehTypeCd).isEqualTo("Conversion");
		});

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//add vehicle
		String purchaseDate = "2012-02-21";
		String vinNew = "3FAFP31341R200709";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vinNew);
		assertThat(addVehicle.oid).isNotEmpty();

		//run delete vehicle service
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oid);
		assertSoftly(softly -> {
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oid);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			assertThat(deleteVehicleResponse.ruleSets).isEqualTo(null);
		});

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.canAddVehicle).isEqualTo(true);

		List<Vehicle> originalOrderingFromResponse2 = ImmutableList.copyOf(viewEndorsementVehicleResponse2.vehicleList);
		List<Vehicle> sortedVehicles1 = viewEndorsementVehicleResponse2.vehicleList;
		sortedVehicles1.sort(Vehicle.PENDING_ENDORSEMENT_COMPARATOR);
		assertSoftly(softly -> {
			softly.assertThat(originalOrderingFromResponse2).isEqualTo(sortedVehicles1);

			Vehicle vehiclePendingRemoval = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehiclePendingRemoval).isNotNull();
			softly.assertThat(vehiclePendingRemoval.oid).isEqualTo(oid);
			softly.assertThat(vehiclePendingRemoval.vehicleStatus).isEqualTo("pendingRemoval");
			softly.assertThat(vehiclePendingRemoval.vehTypeCd).isEqualTo("PPA");
			softly.assertThat(vehiclePendingRemoval.vehIdentificationNo).isEqualTo(vin1);

			Vehicle vehiclePending = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vinNew.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

			softly.assertThat(vehiclePending.vehIdentificationNo).isEqualTo(vinNew);
			softly.assertThat(vehiclePending.vehicleStatus).isEqualTo("pending");
			softly.assertThat(vehiclePending.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicleActive = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicleActive).isNotNull();
			softly.assertThat(vehicleActive.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicleActive.vehTypeCd).isEqualTo("PPA");

			Vehicle vehicleActive2 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin4.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicleActive2).isNotNull();
			softly.assertThat(vehicleActive2.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicleActive2.vehTypeCd).isEqualTo("Motor");

			Vehicle vehicleActive3 = viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
			softly.assertThat(vehicleActive3).isNotNull();
			softly.assertThat(vehicleActive3.vehicleStatus).isEqualTo("active");
			softly.assertThat(vehicleActive3.vehTypeCd).isEqualTo("Conversion");
		});
	}

	protected void pas11618_UpdateVehicleLeasedFinancedInfoBody(SoftAssertions softly, String ownershipType) {
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
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		if ("LSD".equals(ownershipType)) {
			CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE)).hasValue("Leased");
		}
		if ("FNC".equals(ownershipType)) {
			CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE)).hasValue("Financed");
		}
		CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.FIRST_NAME)).hasValue("Other");
		CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNER_NO_LABEL)).hasValue(otherName); //can't take the value of the field with no label
		CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.SECOND_NAME)).hasValue(secondName);
		CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.ADDRESS_LINE_1)).hasValue(addressLine1);
		CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.ADDRESS_LINE_2)).hasValue(addressLine2);
		CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.CITY)).hasValue(city);
		CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.STATE)).hasValue(state);
		CustomAssertions.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.ZIP_CODE)).hasValue(zipCode);
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

		AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsmentVehiclesMetaData(policyNumber, newVehicleOid);
		AttributeMetadata metaDataFieldResponse = testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.ownership", true, true, false, null, "String");
		softly.assertThat(metaDataFieldResponse.valueRange.get("OWN")).isEqualTo("Owned");
		softly.assertThat(metaDataFieldResponse.valueRange.get("FNC")).isEqualTo("Financed");
		softly.assertThat(metaDataFieldResponse.valueRange.get("LSD")).isEqualTo("Leased");

		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.name", true, true, false, "100", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.secondName", true, true, false, "100", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.postalCode", true, true, false, "10", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine1", true, true, false, "40", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine2", true, true, false, "40", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.city", true, true, false, "30", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.stateProvCd", true, true, false, null, "String");

		ViewVehicleResponse policyValidateVehicleInfoResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oldVehicleOid = policyValidateVehicleInfoResponse.vehicleList.get(0).oid;
		AttributeMetadata[] metaDataResponseOwned = HelperCommon.viewEndorsmentVehiclesMetaData(policyNumber, oldVehicleOid);
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.ownership", true, true, false, null, "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.name", false, false, false, "100", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.secondName", false, false, false, "100", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.postalCode", false, false, false, "10", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.addressLine1", false, false, false, "40", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.addressLine2", false, false, false, "40", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.city", false, false, false, "30", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponseOwned, "vehicleOwnership.stateProvCd", false, false, false, null, "String");

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
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
		softly.assertThat(vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE).getValue()).isEqualTo("Owned");
		mainApp().close();

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.secondEndorsementIssueCheck();
	}

	protected void pas9490_ViewVehicleServiceCheckVehiclesStatus() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		String vin1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN.getLabel()).getValue();
		VehicleTab.buttonCancel.click();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Start PAS-479
		//Check premium
		PolicyPremiumInfo[] rateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		assertSoftly(softly -> {
			softly.assertThat(rateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(rateResponse[0].premiumCode).isEqualTo("GWT");
		});
		Dollar dxpPremium = new Dollar(rateResponse[0].actualAmt);

		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		assertThat(new Dollar(PremiumAndCoveragesTab.getActualPremium())).isEqualTo(dxpPremium);
		PremiumAndCoveragesTab.buttonCancel.click();

		//Add new vehicle
		String purchaseDate = "2013-02-22";
		String vin2 = "1HGFA16526L081415";
		Vehicle response2 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin2);
		assertThat(response2.oid).isNotEmpty();
		String newVehicleOid = response2.oid;

		//View vehicles status
		ViewVehicleResponse response3 = HelperCommon.viewEndorsementVehicles(policyNumber);

		if (response3.vehicleList.get(0).vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response3.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response3.vehicleList.get(0).vehicleStatus).isEqualTo("active");
				softly.assertThat(response3.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response3.vehicleList.get(1).vehicleStatus).isEqualTo("pending");
			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response3.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response3.vehicleList.get(0).vehicleStatus).isEqualTo("pending");
				softly.assertThat(response3.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response3.vehicleList.get(1).vehicleStatus).isEqualTo("active");
			});
		}

		//Update Vehicle with proper Usage and Registered Owner
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		//Check premium after new vehicle was added
		PolicyPremiumInfo[] rateResponse2 = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		Dollar dxpPremium2 = new Dollar(rateResponse2[0].actualAmt);

		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		assertThat(new Dollar(PremiumAndCoveragesTab.getActualPremium())).isEqualTo(dxpPremium2);
		premiumAndCoveragesTab.cancel();
		//End PAS-479

		//Issue pended endorsement
		testEValueDiscount.simplifiedPendedEndorsementIssue();

		//View vehicles status after endorsement was bind
		ViewVehicleResponse response4 = HelperCommon.viewPolicyVehicles(policyNumber);

		if (response4.vehicleList.get(0).vehIdentificationNo.contains(vin1)) {
			assertSoftly(softly -> {
				softly.assertThat(response4.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response4.vehicleList.get(0).vehicleStatus).isEqualTo("active");
				softly.assertThat(response4.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response4.vehicleList.get(1).vehicleStatus).isEqualTo("active");
			});
		} else {
			assertSoftly(softly -> {
				softly.assertThat(response4.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin2);
				softly.assertThat(response4.vehicleList.get(0).vehicleStatus).isEqualTo("active");
				softly.assertThat(response4.vehicleList.get(1).vehIdentificationNo).isEqualTo(vin1);
				softly.assertThat(response4.vehicleList.get(1).vehicleStatus).isEqualTo("active");
			});
		}
	}

	protected void pas15483_deleteOriginalVehicleBody() {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();
			//String policyNumber = "VASS952918552";

			ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
			String originalVehicleOid = responseViewVehicles.vehicleList.get(0).oid;

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String purchaseDate = "2013-02-22";
			String vin = "1HGFA16526L081415";

			//Add vehicle with specific info
			Vehicle vehicleAddRequest = new Vehicle();
			vehicleAddRequest.purchaseDate = purchaseDate;
			vehicleAddRequest.vehIdentificationNo = vin;
			String newVehicleOid = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest);
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

			String purchaseDate2 = "2013-02-22";
			String vin2 = "WAUKJAFM8C6314628";

			//Add vehicle with specific info
			Vehicle vehicleAddRequest2 = new Vehicle();
			vehicleAddRequest2.purchaseDate = purchaseDate2;
			vehicleAddRequest2.vehIdentificationNo = vin2;
			String newVehicleOid2 = helperMiniServices.vehicleAddRequestWithCheck(policyNumber, vehicleAddRequest2);
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid2);

			helperMiniServices.rateEndorsementWithCheck(policyNumber);
			//BUG PAS-15483 Delete Vehicle doesnt return response in some cases
			VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, originalVehicleOid);
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(originalVehicleOid);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");

			VehicleUpdateResponseDto deleteVehicleResponse2 = HelperCommon.deleteVehicle(policyNumber, newVehicleOid);
			softly.assertThat(deleteVehicleResponse2.oid).isNull();
			softly.assertThat(deleteVehicleResponse2.vehicleStatus).isNull();

			VehicleUpdateResponseDto deleteVehicleResponse3 = HelperCommon.deleteVehicle(policyNumber, newVehicleOid2);
			softly.assertThat(deleteVehicleResponse3.oid).isNull();
			softly.assertThat(deleteVehicleResponse3.vehicleStatus).isNull();
		});
	}

	protected void pas14952_EndorsementStatusResetForVehRatingFactorsBody(String state, SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Add new vehicle
		//BUG PAS-14688, PAS-14689, PAS-14690, PAS-14691 - Add Vehicle for DC, KS, NY, OR
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		SearchPage.openPolicy(policyNumber);

		//Update Vehicle with proper Usage and Registered Owner
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		//Check vehicle update service when  garage address is different
		String zipCodeGarage = "23703";
		String addressGarage = "4112 FORREST HILLS DR";
		String cityGarage = "PORTSMOUTH";
		String stateGarage = "VA";
		VehicleUpdateDto updateGaragingAddressVehicleRequest = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest.garagingDifferent = true;
		updateGaragingAddressVehicleRequest.garagingAddress = new Address();
		updateGaragingAddressVehicleRequest.garagingAddress.postalCode = zipCodeGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.addressLine1 = addressGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.city = cityGarage;
		updateGaragingAddressVehicleRequest.garagingAddress.stateProvCd = stateGarage;
		Vehicle updateVehicleGaragingAddressResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingDifferent).isEqualTo(true);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.postalCode).isEqualTo(zipCodeGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.addressLine1).isEqualTo(addressGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.city).isEqualTo(cityGarage);
		softly.assertThat(updateVehicleGaragingAddressResponse.garagingAddress.stateProvCd).isEqualTo(stateGarage);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		String zipCodeOwnership = "23703";
		String addressLine1Ownership = "4112 FORREST HILLS DR";
		String addressLine2Ownership = "Apt. 202";
		String cityOwnership = "PORTSMOUTH";
		String stateOwnership = "VA";
		String otherNameOwnership = "other name";
		String secondNameOwnership = "Second Name";

		//Update vehicle Leased Financed Info
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = addressLine1Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = addressLine2Ownership;
		updateVehicleLeasedFinanced.vehicleOwnership.city = cityOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = stateOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = zipCodeOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "LSD";
		updateVehicleLeasedFinanced.vehicleOwnership.name = otherNameOwnership;
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = secondNameOwnership;
		VehicleUpdateResponseDto ownershipUpdateResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse.vehicleOwnership.ownership).isEqualTo("LSD");

		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.usage = "Business";
		updateVehicleUsageRequest.registeredOwner = false;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Business");
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

		ErrorResponseDto rateOwnershipUsageResponse = HelperCommon.endorsementRateError(policyNumber);
		softly.assertThat(rateOwnershipUsageResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(rateOwnershipUsageResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(rateOwnershipUsageResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.REGISTERED_OWNERS.getCode());
		softly.assertThat(rateOwnershipUsageResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.REGISTERED_OWNERS.getMessage());
		softly.assertThat(rateOwnershipUsageResponse.errors.get(0).field).isEqualTo("vehOwnerInd");
		softly.assertThat(rateOwnershipUsageResponse.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.USAGE_IS_BUSINESS.getCode());
		softly.assertThat(rateOwnershipUsageResponse.errors.get(1).message).contains(ErrorDxpEnum.Errors.USAGE_IS_BUSINESS.getMessage());
		softly.assertThat(rateOwnershipUsageResponse.errors.get(1).field).isEqualTo("vehicleUsageCd");

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

		VehicleUpdateDto updateVehicleAntitheftRequest = new VehicleUpdateDto();
		updateVehicleAntitheftRequest.antiTheft = "STD";
		Vehicle updateVehicleAntitheftResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleAntitheftRequest);
		assertThat(updateVehicleAntitheftResponse.antiTheft).isEqualTo("STD");
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

		VehicleUpdateDto updateVehicleSalvagedRequest = new VehicleUpdateDto();
		updateVehicleSalvagedRequest.salvaged = true;
		Vehicle updateVehicleSalvagedResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleSalvagedRequest);
		assertThat(updateVehicleSalvagedResponse.salvaged).isEqualTo(true);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

		VehicleUpdateDto updateVehiclePurchaseDateRequest = new VehicleUpdateDto();
		updateVehiclePurchaseDateRequest.purchaseDate = "2018-02-28";
		Vehicle updateVehiclePurchaseDateResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehiclePurchaseDateRequest);
		assertThat(updateVehiclePurchaseDateResponse.purchaseDate.replace("T00:00:00Z", "")).isEqualTo("2018-02-28");
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");

		//Bind endorsement
		HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		testEValueDiscount.secondEndorsementIssueCheck();

		policy.updateRulesOverride().start();
		CustomAssertions.assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200021").getCell(1)).isAbsent();
		UpdateRulesOverrideActionTab.btnCancel.click();
	}

	protected void pas14952_StatusResetsForNewlyAddedVehicleBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		assertSoftly(softly -> {
			String purchaseDate = "2013-02-22";
			String vin = "1HGFA16526L081415";

			//Validate VIN
			String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			AAAVehicleVinInfoRestResponseWrapper vinValidateResponse = HelperCommon.executeVinInfo(policyNumber, vin, endorsementDate);
			softly.assertThat(vinValidateResponse.vehicles.get(0).vin).isEqualTo(vin);

			//Add new vehicle
			Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
			assertThat(responseAddVehicle.oid).isNotEmpty();
			String newVehicleOid = responseAddVehicle.oid;
			printToLog("newVehicleOid: " + newVehicleOid);
			SearchPage.openPolicy(policyNumber);

			//Update Vehicle with proper Usage and Registered Owner
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);
			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
			helperMiniServices.rateEndorsementWithCheck(policyNumber);

			helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Premium Calculated");
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			assertThat(Tab.labelStatus.getValue()).isEqualTo("Premium Calculated");
			VehicleTab.tableVehicleList.selectRow(2);
			//BUG PAS-15396 Endorsement status is reset when navigating to a vehicle added through service after it was rated through service
			assertThat(Tab.labelStatus.getValue()).isEqualTo("Premium Calculated");
		});
	}

	protected void pas9493_TransactionInformationForEndorsementsAddVehicleBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		ComparablePolicy response1 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		assertThat(response1.vehicles).isEqualTo(null);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin1);
		assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		//Check first vehicle
		ComparablePolicy response2 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		assertThat(response2.vehicles.get(oid1).changeType).isEqualTo("ADDED");
		assertThat(response2.vehicles.get(oid1).data.modelYear).isEqualTo("2006");
		assertThat(response2.vehicles.get(oid1).data.manufacturer).isEqualTo("HONDA");
		assertThat(response2.vehicles.get(oid1).data.series).isEqualTo("CIVIC LX");
		assertThat(response2.vehicles.get(oid1).data.model).isEqualTo("CIVIC");
		assertThat(response2.vehicles.get(oid1).data.bodyStyle).isEqualTo("SEDAN 4 DOOR");
		assertThat(response2.vehicles.get(oid1).data.oid).isEqualTo(oid1);
		assertThat(response2.vehicles.get(oid1).data.purchaseDate).startsWith(purchaseDate);
		assertThat(response2.vehicles.get(oid1).data.vehIdentificationNo).isEqualTo(vin1);
		assertThat(response2.vehicles.get(oid1).data.vehicleStatus).isEqualTo("pending");
		assertThat(response2.vehicles.get(oid1).data.usage).isEqualTo("Pleasure");
		assertThat(response2.vehicles.get(oid1).data.salvaged).isEqualTo(false);
		assertThat(response2.vehicles.get(oid1).data.garagingDifferent).isEqualTo(false);
		assertThat(response2.vehicles.get(oid1).data.antiTheft).isEqualTo("NONE");
		assertThat(response2.vehicles.get(oid1).data.registeredOwner).isEqualTo(true);
		assertThat(response2.vehicles.get(oid1).data.vehTypeCd).isEqualTo("PPA");
		//Garaging Address
		assertThat(response2.vehicles.get(oid1).garagingAddress.changeType).isEqualTo("ADDED");
		assertThat(response2.vehicles.get(oid1).garagingAddress.data.addressLine1).isNotEmpty();
		assertThat(response2.vehicles.get(oid1).garagingAddress.data.addressLine2).isNotBlank();
		assertThat(response2.vehicles.get(oid1).garagingAddress.data.city).isNotEmpty();
		assertThat(response2.vehicles.get(oid1).garagingAddress.data.stateProvCd).isNotEmpty();
		assertThat(response2.vehicles.get(oid1).garagingAddress.data.postalCode).isNotEmpty();
		//Vehicle Ownership
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.changeType).isEqualTo("ADDED");
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.data.ownership).isEqualTo("OWN");
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.data.name).isEqualTo(null);
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.data.secondName).isEqualTo(null);
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.data.addressLine1).isEqualTo(null);
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.data.addressLine2).isEqualTo(null);
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.data.city).isEqualTo(null);
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.data.stateProvCd).isEqualTo(null);
		assertThat(response2.vehicles.get(oid1).vehicleOwnership.data.postalCode).isEqualTo(null);

		//Add second vehicle
		String purchaseDate2 = "2005-02-22";
		String vin2 = "3FAFP31341R200709";
		Vehicle addVehicle2 = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate2, vin2);
		assertThat(addVehicle2.oid).isNotEmpty();
		String oid2 = addVehicle2.oid;

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid2);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		//Check second vehicle
		ComparablePolicy response3 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		assertThat(response3.vehicles.get(oid2).changeType).isEqualTo("ADDED");
		assertThat(response3.vehicles.get(oid2).data.modelYear).isEqualTo("2001");
		assertThat(response3.vehicles.get(oid2).data.manufacturer).isEqualTo("FORD");
		assertThat(response3.vehicles.get(oid2).data.series).isEqualTo("FOCUS ZX3");
		assertThat(response3.vehicles.get(oid2).data.model).isEqualTo("FOCUS");
		assertThat(response3.vehicles.get(oid2).data.bodyStyle).isEqualTo("3 DOOR COUPE");
		assertThat(response3.vehicles.get(oid2).data.oid).isEqualTo(oid2);
		//Bug PAS-15528: "purchaseDate" disappearing after rate action outside of PAS
		//assertThat(response3.vehicles.get(oid2).data.purchaseDate).startsWith(purchaseDate2);
		assertThat(response3.vehicles.get(oid2).data.vehIdentificationNo).isEqualTo(vin2);
		assertThat(response3.vehicles.get(oid2).data.vehicleStatus).isEqualTo("pending");
		assertThat(response3.vehicles.get(oid2).data.usage).isEqualTo("Pleasure");
		assertThat(response3.vehicles.get(oid2).data.salvaged).isEqualTo(false);
		assertThat(response3.vehicles.get(oid2).data.garagingDifferent).isEqualTo(false);
		assertThat(response3.vehicles.get(oid2).data.antiTheft).isEqualTo("NONE");
		assertThat(response3.vehicles.get(oid2).data.registeredOwner).isEqualTo(true);
		assertThat(response3.vehicles.get(oid2).data.vehTypeCd).isEqualTo("PPA");
		//Garaging Address
		assertThat(response3.vehicles.get(oid2).garagingAddress.changeType).isEqualTo("ADDED");
		assertThat(response3.vehicles.get(oid2).garagingAddress.data.addressLine1).isNotEmpty();
		assertThat(response3.vehicles.get(oid2).garagingAddress.data.addressLine2).isNotBlank();
		assertThat(response3.vehicles.get(oid2).garagingAddress.data.city).isNotEmpty();
		assertThat(response3.vehicles.get(oid2).garagingAddress.data.stateProvCd).isNotEmpty();
		assertThat(response3.vehicles.get(oid2).garagingAddress.data.postalCode).isNotEmpty();
		//Vehicle Ownership
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.changeType).isEqualTo("ADDED");
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.data.ownership).isEqualTo("OWN");
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.data.name).isEqualTo(null);
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.data.secondName).isEqualTo(null);
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.data.addressLine1).isEqualTo(null);
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.data.addressLine2).isEqualTo(null);
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.data.city).isEqualTo(null);
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.data.stateProvCd).isEqualTo(null);
		assertThat(response3.vehicles.get(oid2).vehicleOwnership.data.postalCode).isEqualTo(null);

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	protected void pas13920_ReplaceVehicleKeepAssignmentsKeepCoveragesBody(String state) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Drivers").getTestDataList("DriverTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();
		mainApp().open();
/*		createCustomerIndividual();
		policy.createPolicy(testData);*/
		SearchPage.openPolicy("VASS952918565");
/*		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		assignmentTab.fillTab(testData);*/

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vehicleLeasedVin = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vehicleNewVehCoverageVin = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

		ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String vehicleLeasedOid = viewVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		String vehicleNewCarCoverageOid = viewVehicles.vehicleList.stream().filter(vehicle -> vehicleNewVehCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		//PAS-14680 start
		assertThat(viewVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles.vehicleList.stream().filter(vehicle -> vehicleNewVehCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end

		//PAS-13920 start
		PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleLeasedOid);
		Coverage policyCoverageResponseLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseLeasedVeh, "LOAN");
		assertThat(policyCoverageResponseLeasedVehFiltered.coverageLimit).isEqualTo("1");
		assertThat(policyCoverageResponseLeasedVehFiltered.customerDisplayed).isEqualTo(true);
		assertThat(policyCoverageResponseLeasedVehFiltered.canChangeCoverage).isEqualTo(true);

		PolicyCoverageInfo policyCoverageResponseNewCarCoverageVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleNewCarCoverageOid);
		Coverage policyCoverageResponseNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseNewCarCoverageVeh, "NEWCAR");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.coverageLimit).isNull();
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.customerDisplayed).isEqualTo(false);
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.canChangeCoverage).isNull();
		//PAS-13920 end

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//driver assignment on endorsement before any modification
		DriverAssignmentDto[] policyDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
		String driverAssignmentVehicleOid1 = policyDriverAssignmentResponse[0].vehicleOid;
		String driverAssignmentVehicleOid2 = policyDriverAssignmentResponse[1].vehicleOid;

		String replacedVehicleLeasedVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replacedVehicleNewCarCoverageVin = "4S3GTAD6XJ3750502";  //Subaru Impreza 2018

		ReplaceVehicleRequest replaceVehicleLeasedRequest = new ReplaceVehicleRequest();
		replaceVehicleLeasedRequest.vehicleToBeAdded = new Vehicle();
		replaceVehicleLeasedRequest.vehicleToBeAdded.purchaseDate = "2012-02-21";
		replaceVehicleLeasedRequest.vehicleToBeAdded.vehIdentificationNo = replacedVehicleLeasedVin;
		replaceVehicleLeasedRequest.keepAssignments = true;
		replaceVehicleLeasedRequest.keepCoverages = true;
		VehicleUpdateResponseDto replaceVehicleLeasedResponse = HelperCommon.replaceVehicle(policyNumber, vehicleLeasedOid, replaceVehicleLeasedRequest);
		String replaceVehicleLeasedOid = replaceVehicleLeasedResponse.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleLeasedOid);

		ReplaceVehicleRequest replaceVehicleNewCarCoverageRequest = new ReplaceVehicleRequest();
		replaceVehicleNewCarCoverageRequest.vehicleToBeAdded = new Vehicle();
		replaceVehicleLeasedRequest.vehicleToBeAdded.purchaseDate = "2013-03-31";
		replaceVehicleNewCarCoverageRequest.vehicleToBeAdded.vehIdentificationNo = replacedVehicleNewCarCoverageVin;
		replaceVehicleNewCarCoverageRequest.keepAssignments = true;
		replaceVehicleNewCarCoverageRequest.keepCoverages = true;
		VehicleUpdateResponseDto replaceVehicleNewCarCoverageResponse = HelperCommon.replaceVehicle(policyNumber, vehicleNewCarCoverageOid, replaceVehicleNewCarCoverageRequest);
		String replaceVehicleNewCarCoverageOid = replaceVehicleNewCarCoverageResponse.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleNewCarCoverageOid);

		ViewVehicleResponse viewReplacedVehicles = HelperCommon.viewEndorsementVehicles(policyNumber);
		String replacedVehicleLeasedOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		String replacedVehicleNewCarCoverageOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		assertThat(replacedVehicleLeasedOid.equals(replaceVehicleLeasedOid)).isTrue();
		assertThat(replacedVehicleNewCarCoverageOid.equals(replaceVehicleNewCarCoverageOid)).isTrue();

		//Check statuses of the vehicles
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleNewCarCoverageOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		//PAS-14680 start
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleLeasedOid);
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleNewCarCoverageOid);
		//PAS-14680 end

		//Driver assignment check
		DriverAssignmentDto[] endorsementDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertSoftly(softly -> {
			for (int i = 0; i <= 1; i++) {
				if (driverAssignmentVehicleOid2.equals(vehicleNewCarCoverageOid)) {
					assertThat(endorsementDriverAssignmentResponse[i].vehicleOid).isEqualTo(replacedVehicleNewCarCoverageOid);
				} else if (driverAssignmentVehicleOid2.equals(vehicleLeasedOid)) {
					assertThat(endorsementDriverAssignmentResponse[i].vehicleOid).isEqualTo(replacedVehicleLeasedOid);
				} else {
					throw new IstfException("Invalid assignment");
				}
			}
		});

		//check different behaviour coverages of the vehicles
		//PAS-13920 start
		PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleLeasedOid);
		Coverage policyCoverageResponseReplacedLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedLeasedVeh, "LOAN");
		assertThat(policyCoverageResponseReplacedLeasedVehFiltered.coverageLimit).isEqualTo("0");
		assertThat(policyCoverageResponseReplacedLeasedVehFiltered.customerDisplayed).isEqualTo(false);
		assertThat(policyCoverageResponseReplacedLeasedVehFiltered.canChangeCoverage).isEqualTo(false);

		PolicyCoverageInfo policyCoverageResponseReplacedNewCarCoverageVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleNewCarCoverageOid);
		Coverage policyCoverageResponseReplacedNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedNewCarCoverageVeh, "NEWCAR");
		assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.coverageLimit).isNull();
		assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.customerDisplayed).isEqualTo(false);
		assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.canChangeCoverage).isNull();
		//PAS-13920 end

		//check common coverages of the vehicles
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "BI");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "PD");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMBI");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMPD");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "MEDPM");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "IL");

		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COMPDED");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COLLDED");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "GLASS");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "RREIM");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "TOWINGLABOR");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "SPECEQUIP");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL");

		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "BI");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "PD");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "UMBI");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "UMPD");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "MEDPM");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "IL");

		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "COMPDED");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "COLLDED");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "GLASS");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "RREIM");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "TOWINGLABOR");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "SPECEQUIP");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "WL");

		//TODO when Assignment and Coverages works fine, this will be working
		helperMiniServices.bindEndorsementWithCheck(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas13920_ReplaceVehicleNoAssignmentsKeepCoveragesBody(String state) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Drivers").getTestDataList("DriverTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();
		mainApp().open();
/*		createCustomerIndividual();
		policy.createPolicy(testData);*/
		SearchPage.openPolicy("VASS952918565");
/*		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		assignmentTab.fillTab(testData);*/

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vehicleLeasedVin = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vehicleNewVehCoverageVin = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

		ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String vehicleLeasedOid = viewVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		String vehicleNewCarCoverageOid = viewVehicles.vehicleList.stream().filter(vehicle -> vehicleNewVehCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		//PAS-14680 start
		assertThat(viewVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles.vehicleList.stream().filter(vehicle -> vehicleNewVehCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end

		//PAS-13920 start
		PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleLeasedOid);
		Coverage policyCoverageResponseLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseLeasedVeh, "LOAN");
		assertThat(policyCoverageResponseLeasedVehFiltered.coverageLimit).isEqualTo("1");
		assertThat(policyCoverageResponseLeasedVehFiltered.customerDisplayed).isEqualTo(true);
		assertThat(policyCoverageResponseLeasedVehFiltered.canChangeCoverage).isEqualTo(true);

		PolicyCoverageInfo policyCoverageResponseNewCarCoverageVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleNewCarCoverageOid);
		Coverage policyCoverageResponseNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseNewCarCoverageVeh, "NEWCAR");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.coverageLimit).isNull();
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.customerDisplayed).isEqualTo(false);
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.canChangeCoverage).isNull();
		//PAS-13920 end

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String replacedVehicleLeasedVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replacedVehicleNewCarCoverageVin = "4S3GTAD6XJ3750502";  //Subaru Impreza 2018

		ReplaceVehicleRequest replaceVehicleLeasedRequest = new ReplaceVehicleRequest();
		replaceVehicleLeasedRequest.vehicleToBeAdded = new Vehicle();
		replaceVehicleLeasedRequest.vehicleToBeAdded.purchaseDate = "2012-02-21";
		replaceVehicleLeasedRequest.vehicleToBeAdded.vehIdentificationNo = replacedVehicleLeasedVin;
		replaceVehicleLeasedRequest.keepAssignments = false;
		replaceVehicleLeasedRequest.keepCoverages = true;
		VehicleUpdateResponseDto replaceVehicleLeasedResponse = HelperCommon.replaceVehicle(policyNumber, vehicleLeasedOid, replaceVehicleLeasedRequest);
		String replaceVehicleLeasedOid = replaceVehicleLeasedResponse.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleLeasedOid);

		ReplaceVehicleRequest replaceVehicleNewCarCoverageRequest = new ReplaceVehicleRequest();
		replaceVehicleNewCarCoverageRequest.vehicleToBeAdded = new Vehicle();
		replaceVehicleLeasedRequest.vehicleToBeAdded.purchaseDate = "2013-03-31";
		replaceVehicleNewCarCoverageRequest.vehicleToBeAdded.vehIdentificationNo = replacedVehicleNewCarCoverageVin;
		replaceVehicleNewCarCoverageRequest.keepAssignments = false;
		replaceVehicleNewCarCoverageRequest.keepCoverages = true;
		VehicleUpdateResponseDto replaceVehicleNewCarCoverageResponse = HelperCommon.replaceVehicle(policyNumber, vehicleNewCarCoverageOid, replaceVehicleNewCarCoverageRequest);
		String replaceVehicleNewCarCoverageOid = replaceVehicleNewCarCoverageResponse.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleNewCarCoverageOid);

		ViewVehicleResponse viewReplacedVehicles = HelperCommon.viewEndorsementVehicles(policyNumber);
		String replacedVehicleLeasedOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		String replacedVehicleNewCarCoverageOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

		//Check statuses of the vehicles
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleNewCarCoverageOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		//PAS-14680 start
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleLeasedOid);
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleNewCarCoverageOid);
		//PAS-14680 end

		//driver assignment check
		assertSoftly(softly -> {
			DriverAssignmentDto[] responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment[0].vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment[0].driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment[0].relationshipType).isEqualTo("unassigned");
			softly.assertThat(responseDriverAssignment[1].vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment[1].driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment[1].relationshipType).isEqualTo("unassigned");
		});

		assertSoftly(softly -> {
			//update assignment to a new one
			ViewDriversResponse responseViewDriver = HelperCommon.viewPolicyDrivers(policyNumber);
			String driverOid1 = responseViewDriver.driverList.get(0).oid;
			String driverOid2 = responseViewDriver.driverList.get(1).oid;
			DriverAssignmentDto[] updDriverAssignee1 = HelperCommon.updateDriverAssignment(policyNumber, replacedVehicleLeasedOid, driverOid1);
			DriverAssignmentDto[] updDriverAssignee2 = HelperCommon.updateDriverAssignment(policyNumber, replacedVehicleNewCarCoverageOid, driverOid2);

			//check different behaviour coverages of the vehicles
			//PAS-13920 start
			PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleLeasedOid);
			Coverage policyCoverageResponseReplacedLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedLeasedVeh, "LOAN");
			//BUG coverage not reset
			softly.assertThat(policyCoverageResponseReplacedLeasedVehFiltered.coverageLimit).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVehFiltered.customerDisplayed).isEqualTo(false);
			softly.assertThat(policyCoverageResponseReplacedLeasedVehFiltered.canChangeCoverage).isEqualTo(false);

			PolicyCoverageInfo policyCoverageResponseReplacedNewCarCoverageVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleNewCarCoverageOid);
			Coverage policyCoverageResponseReplacedNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedNewCarCoverageVeh, "NEWCAR");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.coverageLimit).isNull();
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.customerDisplayed).isEqualTo(false);
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.canChangeCoverage).isNull();
			//PAS-13920 end

			//check common coverages of the vehicles
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "BI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "PD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMBI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMPD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "MEDPM");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "IL");

			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COMPDED");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COLLDED");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "GLASS");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "RREIM");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "TOWINGLABOR");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "SPECEQUIP");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL");

			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "BI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "PD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "UMBI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "UMPD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "MEDPM");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "IL");

			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "COMPDED");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "COLLDED");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "GLASS");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "RREIM");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "TOWINGLABOR");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "SPECEQUIP");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "WL");
		});
		//TODO when Assignment and Coverages works fine, this will be working
		helperMiniServices.bindEndorsementWithCheck(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas13920_ReplaceVehicleKeepAssignmentsNoCoveragesBody(String state) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Drivers").getTestDataList("DriverTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();
		mainApp().open();
/*		createCustomerIndividual();
		policy.createPolicy(testData);*/
		SearchPage.openPolicy("VASS952918565");
/*		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		assignmentTab.fillTab(testData);*/

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vehicleLeasedVin = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vehicleNewVehCoverageVin = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

		ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String vehicleLeasedOid = viewVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		String vehicleNewCarCoverageOid = viewVehicles.vehicleList.stream().filter(vehicle -> vehicleNewVehCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		//PAS-14680 start
		assertThat(viewVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles.vehicleList.stream().filter(vehicle -> vehicleNewVehCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end

		//PAS-13920 start
		PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleLeasedOid);
		Coverage policyCoverageResponseLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseLeasedVeh, "LOAN");
		assertThat(policyCoverageResponseLeasedVehFiltered.coverageLimit).isEqualTo("1");
		assertThat(policyCoverageResponseLeasedVehFiltered.customerDisplayed).isEqualTo(true);
		assertThat(policyCoverageResponseLeasedVehFiltered.canChangeCoverage).isEqualTo(true);

		PolicyCoverageInfo policyCoverageResponseNewCarCoverageVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleNewCarCoverageOid);
		Coverage policyCoverageResponseNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseNewCarCoverageVeh, "NEWCAR");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.coverageLimit).isNull();
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.customerDisplayed).isEqualTo(false);
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.canChangeCoverage).isNull();
		//PAS-13920 end

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String replacedVehicleLeasedVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replacedVehicleNewCarCoverageVin = "4S3GTAD6XJ3750502";  //Subaru Impreza 2018

		ReplaceVehicleRequest replaceVehicleLeasedRequest = new ReplaceVehicleRequest();
		replaceVehicleLeasedRequest.vehicleToBeAdded = new Vehicle();
		replaceVehicleLeasedRequest.vehicleToBeAdded.purchaseDate = "2012-02-21";
		replaceVehicleLeasedRequest.vehicleToBeAdded.vehIdentificationNo = replacedVehicleLeasedVin;
		replaceVehicleLeasedRequest.keepAssignments = true;
		replaceVehicleLeasedRequest.keepCoverages = false;
		VehicleUpdateResponseDto replaceVehicleLeasedResponse = HelperCommon.replaceVehicle(policyNumber, vehicleLeasedOid, replaceVehicleLeasedRequest);
		String replaceVehicleLeasedOid = replaceVehicleLeasedResponse.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleLeasedOid);

		ReplaceVehicleRequest replaceVehicleNewCarCoverageRequest = new ReplaceVehicleRequest();
		replaceVehicleNewCarCoverageRequest.vehicleToBeAdded = new Vehicle();
		replaceVehicleLeasedRequest.vehicleToBeAdded.purchaseDate = "2013-03-31";
		replaceVehicleNewCarCoverageRequest.vehicleToBeAdded.vehIdentificationNo = replacedVehicleNewCarCoverageVin;
		replaceVehicleNewCarCoverageRequest.keepAssignments = true;
		replaceVehicleNewCarCoverageRequest.keepCoverages = false;
		VehicleUpdateResponseDto replaceVehicleNewCarCoverageResponse = HelperCommon.replaceVehicle(policyNumber, vehicleNewCarCoverageOid, replaceVehicleNewCarCoverageRequest);
		String replaceVehicleNewCarCoverageOid = replaceVehicleNewCarCoverageResponse.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleNewCarCoverageOid);

		ViewVehicleResponse viewReplacedVehicles = HelperCommon.viewEndorsementVehicles(policyNumber);
		String replacedVehicleLeasedOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		String replacedVehicleNewCarCoverageOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

		//Check statuses of the vehicles
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleNewCarCoverageOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		//PAS-14680 start
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleLeasedOid);
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleNewCarCoverageOid);
		//PAS-14680 end

		//driver assignment check
		assertSoftly(softly -> {
			DriverAssignmentDto[] responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment[0].vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment[0].driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment[0].relationshipType).isEqualTo("primary");
			softly.assertThat(responseDriverAssignment[1].vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment[1].driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment[1].relationshipType).isEqualTo("primary");
		});

		//check different behaviour coverages of the vehicles
		//PAS-13920 start
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleLeasedOid);
			Coverage policyCoverageResponseReplacedLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedLeasedVeh, "LOAN");
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.coverageLimit).isEqualTo("0");
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.customerDisplayed).isEqualTo(false);
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.canChangeCoverage).isEqualTo(false);

			PolicyCoverageInfo policyCoverageResponseReplacedNewCarCoverageVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleNewCarCoverageOid);
			Coverage policyCoverageResponseReplacedNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedNewCarCoverageVeh, "NEWCAR");
			assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.coverageLimit).isNull();
			assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.customerDisplayed).isEqualTo(false);
			assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.canChangeCoverage).isNull();

			//PAS-13920 end

			//check common coverages of the vehicles
			//BUG the policy coverages are not getting default values after replacement. Uncomment when resolved
			/*testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "BI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "PD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMBI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMPD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "MEDPM");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "IL");*/

			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COMPDED");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COLLDED"); //coverage is not updated in test data, so excluded from the check
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "GLASS");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "RREIM");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "TOWINGLABOR");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "SPECEQUIP");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL"); //is a different whale, excluded from check

			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COMPDED".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("250");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COLLDED".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("500");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "GLASS".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("false");
			//softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "RREIM".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "TOWINGLABOR".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("0/0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "SPECEQUIP".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("1000");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "WL".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isNull();
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "LOAN".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "NEWCAR".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isNull();

			/*testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "BI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "PD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "UMBI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "UMPD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "MEDPM");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "IL");*/

			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "COMPDED");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "COLLDED");//coverage is not updated in test data, so excluded from the check
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "GLASS");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "RREIM");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "TOWINGLABOR");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "SPECEQUIP");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "WL");//is a different whale, excluded from check

			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COMPDED".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("250");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COLLDED".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("500");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "GLASS".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("false");
			//softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "RREIM".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "TOWINGLABOR".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("0/0");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "SPECEQUIP".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("1000");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "WL".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isNull();
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "LOAN".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "NEWCAR".equals(attribute.coverageCd)).findFirst().orElse(null).coverageLimit).isNull();
		});

		//TODO when Assignment and Coverages works fine, this will be working
		helperMiniServices.endorsementRateAndBind(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas13920_test(String state) {
		mainApp().open();
		SearchPage.openPolicy("VASS952918569");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vehicleLeasedVin = "JTDKDTB38J1600184";
		String vehicleNewVehCoverageVin = "1FADP3J2XJL222680";

		ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String vehicleLeasedOid = viewVehicles.vehicleList.stream().filter(vehicle -> vehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		String vehicleNewVehCoverageOid = viewVehicles.vehicleList.stream().filter(vehicle -> vehicleNewVehCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String replacedVehicleLeasedVin = "JTDKDTB38J1600184"; //Toyota Corolla 2018
		String replacedVehicleNewCarCoverageVin = "1FADP3J2XJL222680";  //Subaru Impreza 2018
		ViewVehicleResponse viewReplacedVehicles = HelperCommon.viewEndorsementVehicles(policyNumber);
		String replacedVehicleLeasedOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
		String replacedVehicleNewVehCoverageOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

		PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleLeasedOid);
		PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleLeasedOid);

		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COMPDED");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COLLDED");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "GLASS");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "RREIM");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "TOWINGLABOR");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "SPECEQUIP");
		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL");

		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "BI");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "PD");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMBI");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMPD");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "MEDPM");
		testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "IL");

		helperMiniServices.bindEndorsementWithCheck(policyNumber);
	}

	protected void pas12175_RemoveReplaceAllVehiclesBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
		//SearchPage.openPolicy("VASS952918539");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vehicleVinPpa1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vehicleVinConv = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
		String vehicleVinPpa2 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");
		String vehicleVinMotor = td.getTestDataList("VehicleTab").get(3).getValue("VIN");

		assertSoftly(softly -> {
			ViewVehicleResponse viewPolicyVehicleResponse1 = HelperCommon.viewPolicyVehicles(policyNumber);
			String vehiclePpa1Oid = viewPolicyVehicleResponse1.vehicleList.stream().filter(vehicle -> vehicleVinPpa1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
			String vehicleConvOid = viewPolicyVehicleResponse1.vehicleList.stream().filter(vehicle -> vehicleVinConv.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
			String vehiclePpa2Oid = viewPolicyVehicleResponse1.vehicleList.stream().filter(vehicle -> vehicleVinPpa2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
			String vehicleMotorOid = viewPolicyVehicleResponse1.vehicleList.stream().filter(vehicle -> vehicleVinMotor.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehiclePpa1Oid)).contains("Remove, Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehiclePpa2Oid)).contains("Remove, Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehicleConvOid)).contains("Remove");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehicleMotorOid)).contains("Remove");

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewVehicleResponse viewEndorsementVehicleResponse1 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehiclePpa1Oid)).contains("Remove, Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehiclePpa2Oid)).contains("Remove, Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehicleConvOid)).contains("Remove");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehicleMotorOid)).contains("Remove");

			VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, vehiclePpa1Oid);

			ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse2, vehiclePpa1Oid)).contains("");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse2, vehiclePpa2Oid)).contains("Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse2, vehicleConvOid)).contains("Remove");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse2, vehicleMotorOid)).contains("Remove");

			helperMiniServices.endorsementRateAndBind(policyNumber);
			ViewVehicleResponse viewPolicyVehicleResponse2 = HelperCommon.viewPolicyVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehiclePpa2Oid)).contains("Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehicleConvOid)).contains("Remove");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehicleMotorOid)).contains("Remove");

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			String newVehicleOid = addVehicleWithChecks(policyNumber, "2013-02-22", "1FADP3J2XJL222680", true);
			ViewVehicleResponse viewEndorsementVehicleResponse3 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, vehiclePpa2Oid)).contains("Remove", "Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, newVehicleOid)).contains("");
		});
	}

	protected void pas12175_RemoveReplaceWaiveLiabilityBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_WaiveLiability").getTestDataList("VehicleTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_WaiveLiability").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();

		mainApp().open();
/*		createCustomerIndividual();
		policy.createPolicy(testData);*/
/*		SearchPage.openQuote("QVASS952918560");
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.fillTab(td);*/
		SearchPage.openPolicy("VASS952918560");
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String vehicleVinPpa1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vehicleVinWl = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

		assertSoftly(softly -> {
			ViewVehicleResponse viewPolicyVehicleResponse1 = HelperCommon.viewPolicyVehicles(policyNumber);
			String vehiclePpa1Oid = viewPolicyVehicleResponse1.vehicleList.stream().filter(vehicle -> vehicleVinPpa1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
			String vehicleWlOid = viewPolicyVehicleResponse1.vehicleList.stream().filter(vehicle -> vehicleVinWl.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehiclePpa1Oid)).contains("Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehicleWlOid)).contains("Remove");

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewVehicleResponse viewEndorsementVehicleResponse1 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehiclePpa1Oid)).contains("Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehicleWlOid)).contains("Remove");

			String newVehicleOid = addVehicleWithChecks(policyNumber, "2013-02-22", "1HGEM21504L055795", true);
			ViewVehicleResponse viewEndorsementVehicleResponse3 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, vehiclePpa1Oid)).contains("Remove, Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, vehicleWlOid)).contains("Remove");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, newVehicleOid)).contains("");

			helperMiniServices.endorsementRateAndBind(policyNumber);

			ViewVehicleResponse viewPolicyVehicleResponse2 = HelperCommon.viewPolicyVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehiclePpa1Oid)).contains("Remove, Replace");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehicleWlOid)).contains("Remove");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, newVehicleOid)).contains("Remove, Replace");
		});
	}

	private String checkAvailableActionsByVehicleOid(ViewVehicleResponse viewVehicleResponse, String vehiclePpa1Oid) {
		return viewVehicleResponse.vehicleList.stream().filter(vehicle -> vehiclePpa1Oid.equals(vehicle.oid)).findFirst().orElse(null).availableActions.toString();
	}

	private String addVehicleWithChecks(String policyNumber, String purchaseDate, String vin, boolean allowedToAddVehicle) {
		//Add new vehicle
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, purchaseDate, vin);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);

		//Update Vehicle with proper Usage and Registered Owner
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse.canAddVehicle).isEqualTo(allowedToAddVehicle);
		Vehicle newVehicle = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> newVehicleOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle.vehIdentificationNo).isEqualTo(vin);
		return newVehicleOid;
	}
}



