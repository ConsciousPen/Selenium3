package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.UpdateRulesOverrideActionTab.RuleRow.RULE_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.helpers.rest.dtoDxp.*;
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
import aaa.modules.regression.service.auto_ss.functional.TestMiniServicesAssignments;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.ComboBox;

public class TestMiniServicesVehiclesHelper extends PolicyBaseTest {

	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private VehicleTab vehicleTab = new VehicleTab();
	private AssignmentTab assignmentTab = new AssignmentTab();
	private GeneralTab generalTab = new GeneralTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestMiniServicesGeneralHelper testMiniServicesGeneralHelper = new TestMiniServicesGeneralHelper();
	private TestMiniServicesCoveragesHelper testMiniServicesCoveragesHelper = new TestMiniServicesCoveragesHelper();
	private String policyNumber8Vehicles;
	private TestMiniServicesDriversHelper testMiniServicesDriversHelper = new TestMiniServicesDriversHelper();

	protected void pas8275_vinValidateCheck(ETCSCoreSoftAssertions softly, PolicyType policyType) {
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

	private void verifyVinInvalidLength(ETCSCoreSoftAssertions softly, AAAVehicleVinInfoRestResponseWrapper response) {
		softly.assertThat(response.vehicles).isEmpty();
		softly.assertThat(response.validationMessage).isEqualTo("Invalid VIN length");
	}

	protected void pas7082_AddVehicle(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		policy.policyInquiry().start();
		String zipCodeDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE).getValue();
		String addressDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1).getValue();
		String cityDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoSSMetaData.GeneralTab.NamedInsuredInformation.CITY).getValue();
		String stateDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoSSMetaData.GeneralTab.NamedInsuredInformation.STATE).getValue();


		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		String vin1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN).getValue();
		mainApp().close();

		//Create pended endorsement
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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

		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);

		assertThat(responseAddVehicle.oid).isNotEmpty();
		String oid = responseAddVehicle.oid;
		printToLog("oid: " + oid);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.usage = "Business";
		updateVehicleRequest.registeredOwner = false;

		Vehicle updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.usage).isEqualTo("Business");
			assertThat(((VehicleUpdateResponseDto) updateVehicleResponse).validations.get(0).message).isEqualTo("Usage is Business");
		});

		ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(hasError(rateResponse, "vehOwnerInd", ErrorDxpEnum.Errors.REGISTERED_OWNERS)).isTrue();
			softly.assertThat(hasError(rateResponse, "vehicleUsageCd", ErrorDxpEnum.Errors.USAGE_IS_BUSINESS)).isTrue();
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
		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String oid = responseAddVehicle.oid;
		printToLog("oid: " + oid);
		SearchPage.openPolicy(policyNumber);

		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.registeredOwner = false;

		VehicleUpdateResponseDto updateVehicleResponse = HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateVehicleResponse.registeredOwner).isEqualTo(false);
			softly.assertThat(hasError(updateVehicleResponse.validations, "Registered Owners")).isTrue();
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
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oid,VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

		assertSoftly(softly -> {
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oid);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			softly.assertThat(deleteVehicleResponse.vehIdentificationNo).isEqualTo(vin);
			assertThat(deleteVehicleResponse.validations).isEqualTo(null);
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

			validateUniqueVinError(errorResponse, softly);
			String purchaseDate2 = "2015-02-11";
			String vin2 = "9BWFL61J244023215";

			//add vehicle
			Vehicle responseAddVehicle =
					HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin2, purchaseDate2), Vehicle.class, 201);

			assertThat(responseAddVehicle.oid).isNotEmpty();

			//try add the same vehicle one more time
			ErrorResponseDto errorResponse2 =
					HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin2, purchaseDate2), ErrorResponseDto.class, 422);

			validateUniqueVinError(errorResponse2, softly);

			//Start PAS-11005
			String purchaseDate3 = "2015-02-11";
			String vin3 = "ZFFCW56A830133118";

			//try add to expensive vehicle
			ErrorResponseDto errorResponse3 =
					HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin3, purchaseDate3), ErrorResponseDto.class, 422);

			softly.assertThat(errorResponse3.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponse3.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponse3.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.EXPENSIVE_VEHICLE.getCode());
			softly.assertThat(errorResponse3.errors.get(0).message).contains(ErrorDxpEnum.Errors.EXPENSIVE_VEHICLE.getMessage());
			softly.assertThat(errorResponse3.errors.get(0).field).isEqualTo("vehTypeCd");
		});
	}

	protected void pas16577_DuplicateVinAddVehicleServicePendingRemoveBody() {
		assertSoftly(softly -> {

			mainApp().open();
			String policyNumber = getCopiedPolicy();

			//Create Endorsement and add second vehicle
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			String purchaseDate = "2012-02-21";
			String vin = "1HGEM21504L055795";
			Vehicle addVehicleRequest = DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate);
			Vehicle response = HelperCommon.addVehicle(policyNumber, addVehicleRequest, Vehicle.class, 201);
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, response.oid);
			helperMiniServices.endorsementRateAndBind(policyNumber);

			//Create new Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			ViewVehicleResponse viewVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat("active".equals(viewVehicleResponse.vehicleList.stream().filter(vehicle -> vehicle.vehIdentificationNo.equals(vin)).findFirst().orElse(null).vehicleStatus)).isTrue();
			softly.assertThat(viewVehicleResponse.vehicleList.stream().filter(vehicle -> vehicle.vehIdentificationNo.equals(vin)).findFirst().orElse(null).availableActions.contains("remove")).isTrue();

			//Remove vehicle
			VehicleUpdateResponseDto deleteVehicleResponse =
					HelperCommon.deleteVehicle(policyNumber, response.oid, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			viewVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);

			//Try to add vehicle with the same VIN as "pendingRemoval" vehicle and get error
			addVehicleRequest.purchaseDate = purchaseDate;
			addVehicleRequest.vehIdentificationNo = vin;
			ErrorResponseDto errorResponseAdd = HelperCommon.addVehicle(policyNumber, addVehicleRequest, ErrorResponseDto.class, 422);
			validateUniqueVinError(errorResponseAdd, softly);

			ViewVehicleResponse viewVehicleResponseAfterAdd = HelperCommon.viewEndorsementVehicles(policyNumber);
			//for some reason addressLine2 comes back as empty string
			//set is a null as empty strings can be safely considered as nulls
			viewVehicleResponse.vehicleList.forEach(vehicle -> vehicle.garagingAddress.addressLine2 = null);
			softly.assertThat(viewVehicleResponseAfterAdd).isEqualToComparingFieldByFieldRecursively(viewVehicleResponse);

			//Get OID of active vehicle and make sure it has "replace" option
			String activeVehicleOid = viewVehicleResponse.vehicleList.stream().filter(vehicle -> !vehicle.vehIdentificationNo.equals(vin)).findFirst().orElse(null).oid;
			softly.assertThat(viewVehicleResponse.vehicleList.stream().filter(vehicle -> vehicle.oid.equals(activeVehicleOid)).findFirst().orElse(null).availableActions.contains("replace")).isTrue();

			//Try to replace Active vehicle with the same VIN as "pendingRemoval" vehicle
			ReplaceVehicleRequest replaceVehicleRequest = DXPRequestFactory.createReplaceVehicleRequest(vin, "2013-03-31", true, true);
			ErrorResponseDto errorResponseReplace = HelperCommon.replaceVehicle(policyNumber, activeVehicleOid, replaceVehicleRequest, ErrorResponseDto.class, 422);
			validateUniqueVinError(errorResponseReplace, softly);

			ViewVehicleResponse viewVehicleResponseAfterReplace = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(viewVehicleResponseAfterReplace).isEqualToComparingFieldByFieldRecursively(viewVehicleResponse);

			helperMiniServices.endorsementRateAndBind(policyNumber);
			softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRowsCount()).isEqualTo(1);

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
	protected void pas14501_garagingDifferentBody(String state, ETCSCoreSoftAssertions softly) {
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
		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);

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

		if (!"VA".equals(state)) {
			helperMiniServices.rateEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getCode(), ErrorDxpEnum.Errors.GARAGED_OUT_OF_STATE.getMessage(), "attributeForRules");

			VehicleUpdateDto updateGaragingAddressVehicleRequest2 = new VehicleUpdateDto();
			updateGaragingAddressVehicleRequest2.garagingDifferent = false;
			Vehicle updateVehicleGaragingAddressResponse2 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest2);
			softly.assertThat(updateVehicleGaragingAddressResponse2.garagingDifferent).isEqualTo(false);
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
		}else {
			//Rate endorsement
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
		}

		//Bind endorsement
		helperMiniServices.bindEndorsementWithCheck(policyNumber);

		testEValueDiscount.secondEndorsementIssueCheck();

		policy.updateRulesOverride().start();
		assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200021").getCell(1)).isAbsent();
		UpdateRulesOverrideActionTab.btnCancel.click();
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
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		//Add new vehicle to have pending vehicle
		String purchaseDate = "2013-02-22";
		String vin5 = "1HGFA16526L081415";
		Vehicle addVehicleResponse =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin5, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicleResponse.oid).isNotEmpty();

		String vin6 = "2GTEC19K8S1525936";
		Vehicle addVehicleResponse2 =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin6, purchaseDate), Vehicle.class, 201);
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
		String zipCodeDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ZIP_CODE).getValue();
		String addressDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1).getValue();
		String cityDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoSSMetaData.GeneralTab.NamedInsuredInformation.CITY).getValue();
		String stateDefault = generalTab.getInquiryAssetList().getInquiryAssetList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION)
				.getStaticElement(AutoSSMetaData.GeneralTab.NamedInsuredInformation.STATE).getValue();
		GeneralTab.buttonCancel.click();

		//Create pended endorsement
		PolicySummary endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
			assertThat(vehicleTab.getAssetList().getAsset(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL)).hasValue("Yes");
			assertThat(vehicleTab.getAssetList().getAsset(ZIP_CODE)).hasValue(zipCode);
			assertThat(vehicleTab.getAssetList().getAsset(ADDRESS_LINE_1)).hasValue(addressLine1);
			assertThat(vehicleTab.getAssetList().getAsset(CITY)).hasValue(city);
			assertThat(vehicleTab.getAssetList().getAsset(STATE)).hasValue(state);

			mainApp().close();
			helperMiniServices.endorsementRateAndBind(policyNumber);

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

			String purchaseDate10 = "2009-06-26";
			String vin10 = "5Y2SL62893Z446850"; //2003 Point Vibe

			ErrorResponseDto responseAddVehicleError =
					HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin9, purchaseDate9), ErrorResponseDto.class, 422);

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

			ErrorResponseDto responseAddVehicleError2 =
					HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin9, purchaseDate9), ErrorResponseDto.class, 422);

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

			addVehicleWithChecks(policyNumber, purchaseDate10, vin10, false);
			PolicyPremiumInfo[] endorsementRateResponse2 = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
			softly.assertThat(endorsementRateResponse2[0].actualAmt).isNotBlank();

			helperMiniServices.bindEndorsementWithCheck(policyNumber);
			testEValueDiscount.secondEndorsementIssueCheck();
		});
		policyNumber8Vehicles = policyNumber;
	}

	protected void pas18672_vehiclesRevertOptionForDeleteBody() {
		mainApp().open();
		SearchPage.openPolicy(policyNumber8Vehicles);
		policy.copyPolicy(getCopyFromPolicyTD());
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		assertThat(HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList.size()).as("Max count of Vehicles (8) is needed for this test").isEqualTo(8);

		assertSoftly(softly -> {
			//get any vehicle OID to remove
			String removedVehicleOid = HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList.get(3).oid;
			VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, removedVehicleOid, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			softly.assertThat(deleteVehicleResponse.availableActions).containsExactly("revert");
			validateRevertOptionForVehicle_pas18672(policyNumber, removedVehicleOid, true, softly);

			//add vehicle
			Vehicle addVehicleRequest = DXPRequestFactory.createAddVehicleRequest("5YMGY0C57C1661237", "2013-02-22");
			String newVehicleOid = HelperCommon.addVehicle(policyNumber, addVehicleRequest, Vehicle.class, Response.Status.CREATED.getStatusCode()).oid;
			helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);
			validateRevertOptionForVehicle_pas18672(policyNumber, removedVehicleOid, false, softly);

			//Start PAS-18670 Cancel the Removed Vehicle
			//try to revert pending remove vehicle and get error
			ErrorResponseDto revertVehicleResponse = HelperCommon.revertVehicle(policyNumber, removedVehicleOid, ErrorResponseDto.class, 422);
			softly.assertThat(revertVehicleResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.REVERT_DELETE_VEHICLE_ERROR.getCode());
			softly.assertThat(revertVehicleResponse.message).isEqualTo(ErrorDxpEnum.Errors.REVERT_DELETE_VEHICLE_ERROR.getMessage());
			//End PAS-18670 Cancel the Removed Vehicle

			VehicleUpdateResponseDto vehicleUpdateResponseDto = HelperCommon.deleteVehicle(policyNumber, newVehicleOid, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			softly.assertThat(vehicleUpdateResponseDto.availableActions).as("Newly added and then removed vehicles should not have revert option").isEmpty();

			//Start PAS-18670 - Cancel the Replaced Vehicle - check that revert option is available for Replaced vehicle when there is max count of vehicles
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			replaceVehicleWithUpdates(policyNumber, removedVehicleOid, "2GTEC19V531282646", true, true);
			softly.assertThat(getVehicleByOid(HelperCommon.viewEndorsementVehicles(policyNumber), removedVehicleOid).availableActions)
					.as("Replaced vehicle should have 'revert' option even when there is max count of vehicles, because it doesn't change count of vehicles.")
					.contains("revert");
			HelperCommon.revertVehicle(policyNumber, removedVehicleOid, Vehicle.class, Response.Status.OK.getStatusCode()); //Check that Revert is successful and error is not displayed
		});
	}

	protected void pas18670_CancelRemoveVehicleBody(boolean testWithUpdates, boolean multipleDrivers, boolean cancelReplace) {
		TestData tdError = DataProviderFactory.dataOf(aaa.main.modules.policy.pup.defaulttabs.ErrorTab.KEY_ERRORS, "All");
		TestData td = getPolicyDefaultTD();
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();

		if (multipleDrivers) {
			testData.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Drivers").getTestDataList("DriverTab")).resolveLinks();
			testData.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab")).resolveLinks();
		}

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(testData);
		SearchPage.openPolicy(policyNumber);

		assertSoftly(softly -> {
			//get vehicle OID to remove
			helperMiniServices.createEndorsementWithCheck(policyNumber);
			SearchPage.openPolicy(policyNumber);
			Vehicle vehicleToRemove = getVehicleByVin(HelperCommon.viewEndorsementVehicles(policyNumber), "JTDKDTB38J1600184"); //the same VIN as in Test Data JTDKDTB38J1600184
			if (testWithUpdates) {
				//update vehicle info
				String address1 = "2011 CORAL AVE";
				String city = "Chesapeake";
				String zip = "23324";
				VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
				updateVehicleRequest.vehicleOwnership = new VehicleOwnership();
				updateVehicleRequest.usage = "Pleasure";
				updateVehicleRequest.salvaged = false;
				updateVehicleRequest.garagingDifferent = true;
				updateVehicleRequest.garagingAddress = new Address();
				updateVehicleRequest.garagingAddress.addressLine1 = address1;
				updateVehicleRequest.garagingAddress.city = city;
				updateVehicleRequest.garagingAddress.postalCode = zip;
				updateVehicleRequest.garagingAddress.stateProvCd = "VA";
				updateVehicleRequest.antiTheft = "STD";
				updateVehicleRequest.registeredOwner = true;

				HelperCommon.updateVehicle(policyNumber, vehicleToRemove.oid, updateVehicleRequest);
				vehicleToRemove = getVehicleByOid(HelperCommon.viewEndorsementVehicles(policyNumber), vehicleToRemove.oid);

				//update some coverage
				UpdateCoverageRequest updateCoverageRequest = DXPRequestFactory.createUpdateCoverageRequest("COMPDED", "500");
				HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, vehicleToRemove.oid, updateCoverageRequest, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			}

			//Get expected Vehicle info after revert
			PolicySummaryPage.buttonPendedEndorsement.click();
			//get policyCoverageInfo to validate vehicle level coverages (and also other coverages)
			PolicyCoverageInfo policyCoverageInfoExpected = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.tableVehicleList.selectRow(1);
			Map<String, String> expectedVehicleInfoInUI = testMiniServicesDriversHelper.getAssetValuesFromTab(AutoSSMetaData.VehicleTab.class, vehicleTab);
			expectedVehicleInfoInUI.remove("List of Vehicle"); //not validating this, because order can change after revert
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			List<TestData> vehicleCoverageDetailsUIExpected = premiumAndCoveragesTab.getRatingDetailsVehiclesData();
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
			premiumAndCoveragesTab.saveAndExit();

			//remove/replace vehicle
			String newVehicleVin = "2GTEC19V531282646"; //Sierra1500 2003, VIN for replace
			if (cancelReplace) {
				replaceVehicleWithUpdates(policyNumber, vehicleToRemove.oid, newVehicleVin, new Random().nextBoolean(), new Random().nextBoolean()); //setting keepAssignments and keepCoverages randomly
			} else {
				HelperCommon.deleteVehicle(policyNumber, vehicleToRemove.oid, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			}

			//revert deleted/replaced vehicle
			Vehicle revertVehicleResponse = HelperCommon.revertVehicle(policyNumber, vehicleToRemove.oid, Vehicle.class, Response.Status.OK.getStatusCode());
			//validate revert driver response after revert
			softly.assertThat(revertVehicleResponse).isEqualToComparingFieldByFieldRecursively(vehicleToRemove);

			//validate viewEndorsementVehicles response
			ViewVehicleResponse viewVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
			Vehicle revertedVehicle = getVehicleByOid(viewVehicleResponse, vehicleToRemove.oid);
			softly.assertThat(revertedVehicle).isEqualToComparingFieldByFieldRecursively(vehicleToRemove);
			if (cancelReplace) {
				softly.assertThat(viewVehicleResponse.vehicleList.stream().anyMatch(vehicle -> vehicle.vehIdentificationNo.equals(newVehicleVin)))
						.as("Added vehicle should be removed after revert Replace.").isFalse();
				softly.assertThat(viewVehicleResponse.vehicleList.size()).isEqualTo(2);
			}

			//Validate that Vehicle level coverages of removed vehicle are the same after revert
			PolicyCoverageInfo policyCoverageInfoAfterRevert = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
			String vehicleToRemoveOid = vehicleToRemove.oid;
			softly.assertThat(policyCoverageInfoAfterRevert.vehicleLevelCoverages.stream().filter(p -> vehicleToRemoveOid.equals(p.oid)).findFirst().orElse(null)).
					isEqualToComparingFieldByFieldRecursively(policyCoverageInfoExpected.vehicleLevelCoverages.stream().filter(p -> vehicleToRemoveOid.equals(p.oid)).findFirst().
							orElseThrow(() -> new IllegalArgumentException("VehicleCoverageInfo was not found for vheicle OID: " + vehicleToRemoveOid)));

			//Get assignments and validate that reverted vehicle doesn't have Assignment if there are multiple drivers and have assignment if there is only one driver
			ViewDriverAssignmentResponse viewDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
			if (multipleDrivers) {
				softly.assertThat(viewDriverAssignmentResponse.assignableVehicles).contains(revertedVehicle.oid);
				softly.assertThat(viewDriverAssignmentResponse.unassignedVehicles).containsExactly(revertedVehicle.oid);
			} else {
				softly.assertThat(viewDriverAssignmentResponse.assignableVehicles).contains(revertedVehicle.oid);
				softly.assertThat(viewDriverAssignmentResponse.unassignedVehicles).isEmpty();
			}

			//check Reverted Vehicle and assignment in UI
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
			VehicleTab.tableVehicleList.selectRow(2); //Vehicle will be 2nd after revert
			testMiniServicesDriversHelper.validateValuesFromTab(expectedVehicleInfoInUI, AutoSSMetaData.VehicleTab.class, vehicleTab, softly);
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
			String excessVehicle = assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getValue().get(0).getValue("Excess Vehicle(s)");
			softly.assertThat(excessVehicle).isEqualTo(vehicleToRemove.modelYear + ", " + vehicleToRemove.manufacturer + ", " + vehicleToRemove.model);
			if (multipleDrivers) {
				//check that reverted vehicle is not assigned in UI
				softly.assertThat(assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getTable().getRow(1)
						.getCell("Select Driver").controls.comboBoxes.getFirst().getValue()).isEmpty();
			} else {
				//check that reverted vehicle is assigned in UI
				softly.assertThat(assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getTable().getRow(1)
						.getCell("Select Vehicle").controls.comboBoxes.getFirst().getValue()).isNotEmpty();
				softly.assertThat(assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.EXCESS_VEHICLES_TABLE).getTable().getRow(1)
						.getCell("Select Driver").controls.comboBoxes.getFirst().getValue()).isNotEmpty();
			}
			assignmentTab.saveAndExit();

			//fill all mandatory details, rate and bind
			if (multipleDrivers) {
				HelperCommon.updateDriverAssignment(policyNumber, revertedVehicle.oid, Arrays.asList(testMiniServicesDriversHelper.getDriverByLicenseNumber(HelperCommon.viewEndorsementDrivers(policyNumber), "400064773").oid));
			}

			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.quoteInquiry().start();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

			//check coverages in UI
			List<TestData> vehicleCoverageDetailsUIActual = premiumAndCoveragesTab.getRatingDetailsVehiclesData();
			PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
			//order of Vehicles is changed after revert, hence reordering list
			Collections.reverse(vehicleCoverageDetailsUIExpected);
			if (testWithUpdates) {
				Collections.swap(vehicleCoverageDetailsUIExpected, 1, 2);
			}
			softly.assertThat(vehicleCoverageDetailsUIActual).isEqualTo(vehicleCoverageDetailsUIExpected);
			premiumAndCoveragesTab.cancel();

			helperMiniServices.endorsementRateAndBind(policyNumber);
			//extra steps to validate that premium has changed if there was updates to Vehicle or has not changed if there was no updates to Vehicle
			PolicySummaryPage.buttonTransactionHistory.click();
			assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Type")).hasValue("Endorsement");
			if (testWithUpdates) {
				softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Tran. Premium")).doesNotHaveValue("$0.00");
			} else {
				softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Tran. Premium")).hasValue("$0.00");
			}
		});
	}

	private void validateRevertOptionForVehicle_pas18672(String policyNumber, String removedVehicleOid, boolean revertOptionExpected, ETCSCoreSoftAssertions softly) {
		ViewVehicleResponse viewVehiclesResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle deletedVehicle = getVehicleByOid(viewVehiclesResponse, removedVehicleOid);

		if (revertOptionExpected) {
			softly.assertThat(deletedVehicle.availableActions).containsExactly("revert");
			softly.assertThat(viewVehiclesResponse.vehicleList.stream().anyMatch(vehicle -> vehicle.oid.equals(removedVehicleOid) && vehicle.availableActions.contains("revert"))).
					as("Removed vehicle should have availableOption 'revert'").isTrue();
			softly.assertThat(viewVehiclesResponse.vehicleList.stream().anyMatch(vehicle -> !"pendingRemoval".equals(vehicle.vehicleStatus)
					&& vehicle.availableActions.contains("revert"))).
					as("Only Removed vehicles should have availableOption 'revert'").isFalse();
		} else {
			softly.assertThat(deletedVehicle.availableActions).doesNotContain("revert").isEmpty();
			softly.assertThat(viewVehiclesResponse.vehicleList.stream().anyMatch(vehicle -> vehicle.oid.equals(removedVehicleOid) && vehicle.availableActions.contains("revert"))).
					as("Removed vehicle should NOT have availableOption 'revert'").isFalse();
		}
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
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		VehicleTab.tableVehicleList.selectRow(2);
		if ("LSD".equals(ownershipType)) {
			assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE)).hasValue("Leased");
		}
		if ("FNC".equals(ownershipType)) {
			assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE)).hasValue("Financed");
		}
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.FIRST_NAME)).hasValue("Other");
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.OWNER_NO_LABEL)).hasValue(otherName); //can't take the value of the field with no label
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.SECOND_NAME)).hasValue(secondName);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.ADDRESS_LINE_1)).hasValue(addressLine1);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.ADDRESS_LINE_2)).hasValue(addressLine2);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.CITY)).hasValue(city);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.STATE)).hasValue(state);
		assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.ZIP_CODE)).hasValue(zipCode);
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

		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.name", true, true, false, "100", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.secondName", true, true, false, "100", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.postalCode", true, true, false, "10", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine1", true, true, false, "40", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine2", true, true, false, "40", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.city", true, true, false, "30", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "vehicleOwnership.stateProvCd", true, true, false, null, "String");

		ViewVehicleResponse policyValidateVehicleInfoResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oldVehicleOid = policyValidateVehicleInfoResponse.vehicleList.get(0).oid;
		AttributeMetadata[] metaDataResponseOwned = HelperCommon.viewEndorsementVehiclesMetaData(policyNumber, oldVehicleOid);
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
		softly.assertThat(vehicleTab.getOwnershipAssetList().getAsset(AutoSSMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE).getValue()).isEqualTo("Owned");
		mainApp().close();

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		testEValueDiscount.secondEndorsementIssueCheck();
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
		String oidForVin1 = viewVehicleResponse.vehicleList.stream().filter(vehicle -> vin1.equals(vehicle.vehIdentificationNo)).findFirst().map(vehicle -> vehicle.oid).orElse(null);

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
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vinNew, purchaseDate), Vehicle.class, 201);
		assertThat(addVehicle.oid).isNotEmpty();

		//run delete vehicle service
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oidForVin1, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

		assertSoftly(softly -> {
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oidForVin1);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			assertThat(deleteVehicleResponse.validations).isEqualTo(null);
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
			softly.assertThat(vehiclePendingRemoval.oid).isEqualTo(oidForVin1);
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

	protected void pas14952_EndorsementStatusResetForVehRatingFactorsBody(String state, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Add new vehicle
		//BUG PAS-14688, PAS-14689, PAS-14690, PAS-14691 - Add Vehicle for DC, KS, NY, OR
		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
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
		assertThat(UpdateRulesOverrideActionTab.tblRulesList.getRowContains(RULE_NAME.getLabel(), "200021").getCell(1)).isAbsent();
		UpdateRulesOverrideActionTab.btnCancel.click();
	}

	protected void pas9490_ViewVehicleServiceCheckVehiclesStatus() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		String vin1 = vehicleTab.getInquiryAssetList().getStaticElement(VIN).getValue();
		VehicleTab.buttonCancel.click();

		//Create pended endorsement
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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
		Vehicle response2 =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin2, purchaseDate), Vehicle.class, 201);
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
			VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, originalVehicleOid, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(originalVehicleOid);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");

			VehicleUpdateResponseDto deleteVehicleResponse2 = HelperCommon.deleteVehicle(policyNumber, newVehicleOid, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());
			softly.assertThat(deleteVehicleResponse2.oid).isEqualTo(newVehicleOid);
			softly.assertThat(deleteVehicleResponse2.vehicleStatus).isEqualTo("pendingRemoval");

			ErrorResponseDto deleteVehicleResponse3 = HelperCommon.deleteVehicle(policyNumber, newVehicleOid2, ErrorResponseDto.class, 422);
			softly.assertThat(deleteVehicleResponse3.errorCode).isEqualTo(ErrorDxpEnum.Errors.VEHICLE_CANNOT_BE_REMOVED_ERROR.getCode());
			softly.assertThat(deleteVehicleResponse3.message).isEqualTo(ErrorDxpEnum.Errors.VEHICLE_CANNOT_BE_REMOVED_ERROR.getMessage());

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas14497_TransactionInformationForEndorsementsReplaceVehicleBody(PolicyType policyType, ETCSCoreSoftAssertions softly) {
		TestData td = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData_VA").adjust(VehicleTab.class.getSimpleName(), testDataManager.getDefault(TestMiniServicesAssignments.class).getTestData("TestData_ThreeVehicles").getTestDataList("VehicleTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(td);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Get VIN's
		String vin1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vin2 = td.getTestDataList("VehicleTab").get(1).getValue("VIN");
		String vin3 = td.getTestDataList("VehicleTab").get(2).getValue("VIN");

		//Get Oid's
		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle v1 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle v2 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle v3 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin3.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

		softly.assertThat(v1.vehIdentificationNo).isEqualTo(vin1);
		String oid1 = v1.oid;
		softly.assertThat(v2.vehIdentificationNo).isEqualTo(vin2);
		String oid2 = v2.oid;
		softly.assertThat(v3.vehIdentificationNo).isEqualTo(vin3);
		String oid3 = v3.oid;

		ComparablePolicy response1 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(response1.vehicles).isEqualTo(null);

		String replacedVehicleVin1 = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replaceVehOid1 = replaceVehicleWithUpdates(policyNumber, oid1, replacedVehicleVin1, true, true);

		String replacedVehicleVin2 = "2GTEC19V531282646"; //Sierra1500 2003
		String replaceVehOid2 = replaceVehicleWithUpdates(policyNumber, oid2, replacedVehicleVin2, true, true);

		//Delete V3 vehicle
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oid3, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

		softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oid3);
		softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
		softly.assertThat(deleteVehicleResponse.vehIdentificationNo).isEqualTo(vin3);
		softly.assertThat(deleteVehicleResponse.validations).isEqualTo(null);

		ComparablePolicy response2 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle replaceVeh2 = response2.vehicles.get(replaceVehOid2);
		softly.assertThat(replaceVeh2.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh2.data.oid).isEqualTo(replaceVehOid2);
		softly.assertThat(replaceVeh2.data.purchaseDate).isNotEmpty();
		softly.assertThat(replaceVeh2.data.vehIdentificationNo).isEqualTo(replacedVehicleVin2);
		softly.assertThat(replaceVeh2.data.vehicleReplacedBy).isEqualTo(oid2);
		softly.assertThat(replaceVeh2.data.vehicleStatus).isEqualTo("pending");
		softly.assertThat(replaceVeh2.garagingAddress.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh2.vehicleOwnership.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh2.vehicleOwnership.data.ownership).isEqualTo("OWN");

		ComparableVehicle veh1 = response2.vehicles.get(oid1);
		softly.assertThat(veh1.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh1.data.oid).isEqualTo(oid1);
		softly.assertThat(veh1.data.purchaseDate).isEqualTo(null);
		softly.assertThat(veh1.data.vehIdentificationNo).isEqualTo(vin1);
		softly.assertThat(veh1.data.vehicleStatus).isEqualTo("active");
		softly.assertThat(veh1.garagingAddress.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh1.vehicleOwnership.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh1.vehicleOwnership.data.ownership).isEqualTo("OWN");

		ComparableVehicle veh2 = response2.vehicles.get(oid2);
		softly.assertThat(veh2.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh2.data.oid).isEqualTo(oid2);
		softly.assertThat(veh2.data.purchaseDate).isEqualTo(null);
		softly.assertThat(veh2.data.vehIdentificationNo).isEqualTo(vin2);
		softly.assertThat(veh2.data.vehicleStatus).isEqualTo("active");
		softly.assertThat(veh2.garagingAddress.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh2.vehicleOwnership.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh2.vehicleOwnership.data.ownership).isEqualTo("OWN");

		ComparableVehicle veh3 = response2.vehicles.get(oid3);
		softly.assertThat(veh3.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh3.data.oid).isEqualTo(oid3);
		softly.assertThat(veh3.data.purchaseDate).isEqualTo(null);
		softly.assertThat(veh3.data.vehIdentificationNo).isEqualTo(vin3);
		softly.assertThat(veh3.data.vehicleStatus).isEqualTo("active");
		softly.assertThat(veh3.garagingAddress.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh3.vehicleOwnership.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh3.vehicleOwnership.data.ownership).isEqualTo("OWN");

		ComparableVehicle replaceVeh1 = response2.vehicles.get(replaceVehOid1);
		softly.assertThat(replaceVeh1.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh1.data.oid).isEqualTo(replaceVehOid1);
		softly.assertThat(replaceVeh1.data.purchaseDate).isNotEmpty();
		softly.assertThat(replaceVeh1.data.vehIdentificationNo).isEqualTo(replacedVehicleVin1);
		softly.assertThat(replaceVeh1.data.vehicleReplacedBy).isEqualTo(oid1);
		softly.assertThat(replaceVeh1.data.vehicleStatus).isEqualTo("pending");
		softly.assertThat(replaceVeh1.garagingAddress.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh1.vehicleOwnership.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh1.vehicleOwnership.data.ownership).isEqualTo("OWN");

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ComparablePolicy response3 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(response3.vehicles).isEqualTo(null);

		String replacedVehicleVin3 = "3FAFP31341R200709"; //Ford
		String replaceVehOid3 = replaceVehicleWithUpdates(policyNumber, replaceVehOid1, replacedVehicleVin3, true, true);

		ComparablePolicy response4 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle replaceVeh3 = response4.vehicles.get(replaceVehOid3);
		softly.assertThat(replaceVeh3.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh3.data.oid).isEqualTo(replaceVehOid3);
		softly.assertThat(replaceVeh3.data.purchaseDate).isNotEmpty();
		softly.assertThat(replaceVeh3.data.vehIdentificationNo).isEqualTo(replacedVehicleVin3);
		softly.assertThat(replaceVeh3.data.vehicleStatus).isEqualTo("pending");
		softly.assertThat(replaceVeh3.garagingAddress.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh3.vehicleOwnership.changeType).isEqualTo("ADDED");
		softly.assertThat(replaceVeh3.vehicleOwnership.data.ownership).isEqualTo("OWN");

		helperMiniServices.endorsementRateAndBind(policyNumber);
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
			Vehicle responseAddVehicle =
					HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
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

	protected void pas9493_TransactionInformationForEndorsementsAddRemoveVehicleBody(PolicyType policyType, ETCSCoreSoftAssertions softly) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid0 = viewVehicleResponse.vehicleList.get(0).oid;
		String vin0 = viewVehicleResponse.vehicleList.get(0).vehIdentificationNo;

		ComparablePolicy response1 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		assertThat(response1.vehicles).isEqualTo(null);

		//Add first vehicle
		String purchaseDate = "2013-02-22";
		String vin1 = "1HGFA16526L081415";
		Vehicle addVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate), Vehicle.class, 201);
		softly.assertThat(addVehicle.oid).isNotEmpty();
		String oid1 = addVehicle.oid;

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid1);

		//Check first vehicle
		ComparablePolicy response2 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh1 = response2.vehicles.get(oid1);
		softly.assertThat(veh1.changeType).isEqualTo("ADDED");
		softly.assertThat(veh1.data.modelYear).isEqualTo("2006");
		softly.assertThat(veh1.data.manufacturer).isEqualTo("HONDA");
		softly.assertThat(veh1.data.series).isEqualTo("CIVIC LX");
		softly.assertThat(veh1.data.model).isEqualTo("CIVIC");
		softly.assertThat(veh1.data.bodyStyle).isEqualTo("SEDAN 4 DOOR");
		softly.assertThat(veh1.data.oid).isEqualTo(oid1);
		softly.assertThat(veh1.data.purchaseDate).startsWith(purchaseDate);
		softly.assertThat(veh1.data.vehIdentificationNo).isEqualTo(vin1);
		softly.assertThat(veh1.data.vehicleStatus).isEqualTo("pending");
		softly.assertThat(veh1.data.usage).isEqualTo("Pleasure");
		softly.assertThat(veh1.data.salvaged).isEqualTo(false);
		softly.assertThat(veh1.data.garagingDifferent).isEqualTo(false);
		softly.assertThat(veh1.data.antiTheft).isEqualTo("NONE");
		softly.assertThat(veh1.data.registeredOwner).isEqualTo(true);
		softly.assertThat(veh1.data.vehTypeCd).isEqualTo("PPA");
		//Garaging Address
		softly.assertThat(veh1.garagingAddress.changeType).isEqualTo("ADDED");
		softly.assertThat(veh1.garagingAddress.data.addressLine1).isNotEmpty();
		softly.assertThat(veh1.garagingAddress.data.addressLine2).isNullOrEmpty();
		softly.assertThat(veh1.garagingAddress.data.city).isNotEmpty();
		softly.assertThat(veh1.garagingAddress.data.stateProvCd).isNotEmpty();
		softly.assertThat(veh1.garagingAddress.data.postalCode).isNotEmpty();
		//Vehicle Ownership
		softly.assertThat(veh1.vehicleOwnership.changeType).isEqualTo("ADDED");
		softly.assertThat(veh1.vehicleOwnership.data.ownership).isEqualTo("OWN");
		softly.assertThat(veh1.vehicleOwnership.data.name).isEqualTo(null);
		softly.assertThat(veh1.vehicleOwnership.data.secondName).isEqualTo(null);
		softly.assertThat(veh1.vehicleOwnership.data.addressLine1).isEqualTo(null);
		softly.assertThat(veh1.vehicleOwnership.data.addressLine2).isEqualTo(null);
		softly.assertThat(veh1.vehicleOwnership.data.city).isEqualTo(null);
		softly.assertThat(veh1.vehicleOwnership.data.stateProvCd).isEqualTo(null);
		softly.assertThat(veh1.vehicleOwnership.data.postalCode).isEqualTo(null);

		//Add second vehicle
		String purchaseDate2 = "2005-02-22";
		String vin2 = "3FAFP31341R200709";
		Vehicle addVehicle2 =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin2, purchaseDate2), Vehicle.class, 201);
		softly.assertThat(addVehicle2.oid).isNotEmpty();
		String oid2 = addVehicle2.oid;

		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, oid2);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		//Check second vehicle
		ComparablePolicy response3 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh2 = response3.vehicles.get(oid2);
		softly.assertThat(veh2.changeType).isEqualTo("ADDED");
		softly.assertThat(veh2.data.modelYear).isEqualTo("2001");
		softly.assertThat(veh2.data.manufacturer).isEqualTo("FORD");
		softly.assertThat(veh2.data.series).isEqualTo("FOCUS ZX3");
		softly.assertThat(veh2.data.model).isEqualTo("FOCUS");
		softly.assertThat(veh2.data.bodyStyle).isEqualTo("3 DOOR COUPE");
		softly.assertThat(veh2.data.oid).isEqualTo(oid2);
		softly.assertThat(veh2.data.vehIdentificationNo).isEqualTo(vin2);
		softly.assertThat(veh2.data.purchaseDate).startsWith(purchaseDate2);
		softly.assertThat(veh2.data.vehicleStatus).isEqualTo("pending");
		softly.assertThat(veh2.data.usage).isEqualTo("Pleasure");
		softly.assertThat(veh2.data.salvaged).isEqualTo(false);
		softly.assertThat(veh2.data.garagingDifferent).isEqualTo(false);
		softly.assertThat(veh2.data.antiTheft).isEqualTo("NONE");
		softly.assertThat(veh2.data.registeredOwner).isEqualTo(true);
		softly.assertThat(veh2.data.vehTypeCd).isEqualTo("PPA");
		//Garaging Address
		softly.assertThat(veh2.garagingAddress.changeType).isEqualTo("ADDED");
		softly.assertThat(veh2.garagingAddress.data.addressLine1).isNotEmpty();
		softly.assertThat(veh2.garagingAddress.data.addressLine2).isNullOrEmpty();
		softly.assertThat(veh2.garagingAddress.data.city).isNotEmpty();
		softly.assertThat(veh2.garagingAddress.data.stateProvCd).isNotEmpty();
		softly.assertThat(veh2.garagingAddress.data.postalCode).isNotEmpty();
		//Vehicle Ownership
		softly.assertThat(veh2.vehicleOwnership.changeType).isEqualTo("ADDED");
		softly.assertThat(veh2.vehicleOwnership.data.ownership).isEqualTo("OWN");
		softly.assertThat(veh2.vehicleOwnership.data.name).isEqualTo(null);
		softly.assertThat(veh2.vehicleOwnership.data.secondName).isEqualTo(null);
		softly.assertThat(veh2.vehicleOwnership.data.addressLine1).isEqualTo(null);
		softly.assertThat(veh2.vehicleOwnership.data.addressLine2).isEqualTo(null);
		softly.assertThat(veh2.vehicleOwnership.data.city).isEqualTo(null);
		softly.assertThat(veh2.vehicleOwnership.data.stateProvCd).isEqualTo(null);
		softly.assertThat(veh2.vehicleOwnership.data.postalCode).isEqualTo(null);

		helperMiniServices.endorsementRateAndBind(policyNumber);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ComparablePolicy response4 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(response4.vehicles).isEqualTo(null);

		//Delete V3 vehicle
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, oid0, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

		softly.assertThat(deleteVehicleResponse.oid).isEqualTo(oid0);
		softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
		softly.assertThat(deleteVehicleResponse.vehIdentificationNo).isEqualTo(vin0);
		softly.assertThat(deleteVehicleResponse.validations).isEqualTo(null);

		//Delete V1 vehicle
		VehicleUpdateResponseDto deleteVehicleResponse2 = HelperCommon.deleteVehicle(policyNumber, oid1, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

		softly.assertThat(deleteVehicleResponse2.oid).isEqualTo(oid1);
		softly.assertThat(deleteVehicleResponse2.vehicleStatus).isEqualTo("pendingRemoval");
		softly.assertThat(deleteVehicleResponse2.vehIdentificationNo).isEqualTo(vin1);
		softly.assertThat(deleteVehicleResponse2.validations).isEqualTo(null);

		//Check first vehicle
		ComparablePolicy response5 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableVehicle veh0 = response5.vehicles.get(oid0);
		softly.assertThat(veh0.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh0.data.modelYear).isEqualTo("2011");
		softly.assertThat(veh0.data.manufacturer).isEqualTo("CHEVROLET");
		softly.assertThat(veh0.data.series).isEqualTo("EXPRESS G2500 LS");
		softly.assertThat(veh0.data.model).isEqualTo("EXPRESS VAN");
		softly.assertThat(veh0.data.bodyStyle).isEqualTo("SPORT VAN");
		softly.assertThat(veh0.data.oid).isEqualTo(oid0);
		softly.assertThat(veh0.data.purchaseDate).isEqualTo(null);
		softly.assertThat(veh0.data.vehIdentificationNo).isEqualTo(vin0);
		softly.assertThat(veh0.data.vehicleStatus).isEqualTo("active");
		softly.assertThat(veh0.data.usage).isEqualTo("Pleasure");
		softly.assertThat(veh0.data.salvaged).isEqualTo(false);
		softly.assertThat(veh0.data.garagingDifferent).isEqualTo(false);
		softly.assertThat(veh0.data.antiTheft).isEqualTo("NONE");
		softly.assertThat(veh0.data.registeredOwner).isEqualTo(null);
		softly.assertThat(veh0.data.vehTypeCd).isEqualTo("PPA");
		//Garaging Address
		softly.assertThat(veh0.garagingAddress.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh0.garagingAddress.data.addressLine1).isNotEmpty();
		softly.assertThat(veh0.garagingAddress.data.addressLine2).isNullOrEmpty();
		softly.assertThat(veh0.garagingAddress.data.city).isNotEmpty();
		softly.assertThat(veh0.garagingAddress.data.stateProvCd).isNotEmpty();
		softly.assertThat(veh0.garagingAddress.data.postalCode).isNotEmpty();
		//Vehicle Ownership
		softly.assertThat(veh0.vehicleOwnership.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh0.vehicleOwnership.data.ownership).isEqualTo("OWN");
		softly.assertThat(veh0.vehicleOwnership.data.name).isEqualTo(null);
		softly.assertThat(veh0.vehicleOwnership.data.secondName).isEqualTo(null);
		softly.assertThat(veh0.vehicleOwnership.data.addressLine1).isEqualTo(null);
		softly.assertThat(veh0.vehicleOwnership.data.addressLine2).isEqualTo(null);
		softly.assertThat(veh0.vehicleOwnership.data.city).isEqualTo(null);
		softly.assertThat(veh0.vehicleOwnership.data.stateProvCd).isEqualTo(null);
		softly.assertThat(veh0.vehicleOwnership.data.postalCode).isEqualTo(null);

		//Check second vehicle
		ComparableVehicle veh1nd = response5.vehicles.get(oid1);
		softly.assertThat(veh1nd.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh1nd.data.modelYear).isEqualTo("2006");
		softly.assertThat(veh1nd.data.manufacturer).isEqualTo("HONDA");
		softly.assertThat(veh1nd.data.series).isEqualTo("CIVIC LX");
		softly.assertThat(veh1nd.data.model).isEqualTo("CIVIC");
		softly.assertThat(veh1nd.data.bodyStyle).isEqualTo("SEDAN 4 DOOR");
		softly.assertThat(veh1nd.data.oid).isEqualTo(oid1);
		softly.assertThat(veh1nd.data.purchaseDate).startsWith(purchaseDate);
		softly.assertThat(veh1nd.data.vehIdentificationNo).isEqualTo(vin1);
		softly.assertThat(veh1nd.data.vehicleStatus).isEqualTo("active");
		softly.assertThat(veh1nd.data.usage).isEqualTo("Pleasure");
		softly.assertThat(veh1nd.data.salvaged).isEqualTo(false);
		softly.assertThat(veh1nd.data.garagingDifferent).isEqualTo(false);
		softly.assertThat(veh1nd.data.antiTheft).isEqualTo("NONE");
		softly.assertThat(veh1nd.data.registeredOwner).isEqualTo(true);
		softly.assertThat(veh1nd.data.vehTypeCd).isEqualTo("PPA");
		//Garaging Address
		softly.assertThat(veh1nd.garagingAddress.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh1nd.garagingAddress.data.addressLine1).isNotEmpty();
		softly.assertThat(veh1nd.garagingAddress.data.addressLine2).isNullOrEmpty();
		softly.assertThat(veh1nd.garagingAddress.data.city).isNotEmpty();
		softly.assertThat(veh1nd.garagingAddress.data.stateProvCd).isNotEmpty();
		softly.assertThat(veh1nd.garagingAddress.data.postalCode).isNotEmpty();
		//Vehicle Ownership
		softly.assertThat(veh1nd.vehicleOwnership.changeType).isEqualTo("REMOVED");
		softly.assertThat(veh1nd.vehicleOwnership.data.ownership).isEqualTo("OWN");
		softly.assertThat(veh1nd.vehicleOwnership.data.name).isEqualTo(null);
		softly.assertThat(veh1nd.vehicleOwnership.data.secondName).isEqualTo(null);
		softly.assertThat(veh1nd.vehicleOwnership.data.addressLine1).isEqualTo(null);
		softly.assertThat(veh1nd.vehicleOwnership.data.addressLine2).isEqualTo(null);
		softly.assertThat(veh1nd.vehicleOwnership.data.city).isEqualTo(null);
		softly.assertThat(veh1nd.vehicleOwnership.data.stateProvCd).isEqualTo(null);
		softly.assertThat(veh1nd.vehicleOwnership.data.postalCode).isEqualTo(null);

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	private void validateUniqueVinError(ErrorResponseDto errorResponse, ETCSCoreSoftAssertions softly) {
		softly.assertThat(errorResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(errorResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(errorResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.UNIQUE_VIN.getCode());
		softly.assertThat(errorResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.UNIQUE_VIN.getMessage());
		softly.assertThat(errorResponse.errors.get(0).field).isEqualTo("attributeForRules");
	}

	protected void pas13920_ReplaceVehicleKeepAssignmentsKeepCoveragesBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Drivers").getTestDataList("DriverTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();
		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
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
		PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleLeasedOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseLeasedVeh, "LOAN");
		assertThat(policyCoverageResponseLeasedVehFiltered.getCoverageLimit()).isEqualTo("1");
		assertThat(policyCoverageResponseLeasedVehFiltered.getCustomerDisplayed()).isEqualTo(true);
		assertThat(policyCoverageResponseLeasedVehFiltered.getCanChangeCoverage()).isEqualTo(true);

		PolicyCoverageInfo policyCoverageResponseNewCarCoverageVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleNewCarCoverageOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseNewCarCoverageVeh, "NEWCAR");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCoverageLimit()).isEqualTo("true");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCustomerDisplayed()).isEqualTo(true);
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCanChangeCoverage()).isEqualTo(false);
		//PAS-13920 end

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//driver assignment on endorsement before any modification
		ViewDriverAssignmentResponse responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
		String driverAssignmentVehicleOid1 = responseDriverAssignment.driverVehicleAssignments.get(0).vehicleOid;
		String driverAssignmentDriverOid1 = responseDriverAssignment.driverVehicleAssignments.get(0).driverOid;
		String driverAssignmentVehicleOid2 = responseDriverAssignment.driverVehicleAssignments.get(1).vehicleOid;
		String driverAssignmentDriverOid2 = responseDriverAssignment.driverVehicleAssignments.get(1).driverOid;

		String replacedVehicleLeasedVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replacedVehicleNewCarCoverageVin = "4S3GTAD6XJ3750502";  //Subaru Impreza 2018

		String replaceVehicleLeasedOid = replaceVehicleWithUpdates(policyNumber, vehicleLeasedOid, replacedVehicleLeasedVin, true, true);
		String replaceVehicleNewCarCoverageOid = replaceVehicleWithUpdates(policyNumber, vehicleNewCarCoverageOid, replacedVehicleNewCarCoverageVin, true, true);

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
		assertSoftly(softly -> {
			ViewDriverAssignmentResponse endorsementDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
			printToLog("vehicleNewCarCoverageOid: " + vehicleNewCarCoverageOid);
			printToLog("replacedVehicleNewCarCoverageOid: " + replacedVehicleNewCarCoverageOid);
			printToLog("vehicleLeasedOid: " + vehicleLeasedOid);
			printToLog("replacedVehicleLeasedOid: " + replacedVehicleLeasedOid);

			DriverAssignment driverAssignment1 = endorsementDriverAssignmentResponse.driverVehicleAssignments.stream().filter(driver -> driverAssignmentDriverOid1.equals(driver.driverOid)).findFirst().orElse(null);
			if (driverAssignmentVehicleOid1.equals(vehicleNewCarCoverageOid)) {
				assertThat(driverAssignment1.vehicleOid).isEqualTo(replacedVehicleNewCarCoverageOid);
			} else if (driverAssignmentVehicleOid1.equals(vehicleLeasedOid)) {
				assertThat(driverAssignment1.vehicleOid).isEqualTo(replacedVehicleLeasedOid);
			}
			DriverAssignment driverAssignment2 = endorsementDriverAssignmentResponse.driverVehicleAssignments.stream().filter(driver -> driverAssignmentDriverOid2.equals(driver.driverOid)).findFirst().orElse(null);
			if (driverAssignmentVehicleOid2.equals(vehicleNewCarCoverageOid)) {
				assertThat(driverAssignment2.vehicleOid).isEqualTo(replacedVehicleNewCarCoverageOid);
			} else if (driverAssignmentVehicleOid2.equals(vehicleLeasedOid)) {
				assertThat(driverAssignment2.vehicleOid).isEqualTo(replacedVehicleLeasedOid);
			}
		});

		//check different behaviour coverages of the vehicles
		//PAS-13920 start
		PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleLeasedOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseReplacedLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedLeasedVeh, "LOAN");
		assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCoverageLimit()).isEqualTo("0");
		assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCustomerDisplayed()).isEqualTo(false);
		assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCanChangeCoverage()).isEqualTo(false);

		PolicyCoverageInfo policyCoverageResponseReplacedNewCarCoverageVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleNewCarCoverageOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseReplacedNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedNewCarCoverageVeh, "NEWCAR");
		assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCoverageLimit()).isEqualTo("false");
		assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCustomerDisplayed()).isEqualTo(true);
		assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCanChangeCoverage()).isEqualTo(false);
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
//		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL"); //do not have this coverage in response anymore. Karen Yifru doesn't care about it.

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
//		testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "WL"); //do not have this coverage in response anymore. Karen Yifru doesn't care about it.

		helperMiniServices.endorsementRateAndBind(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas13920_ReplaceVehicleNoAssignmentsKeepCoveragesBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Drivers").getTestDataList("DriverTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();
		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
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
		PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleLeasedOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseLeasedVeh, "LOAN");
		assertThat(policyCoverageResponseLeasedVehFiltered.getCoverageLimit()).isEqualTo("1");
		assertThat(policyCoverageResponseLeasedVehFiltered.getCustomerDisplayed()).isEqualTo(true);
		assertThat(policyCoverageResponseLeasedVehFiltered.getCanChangeCoverage()).isEqualTo(true);

		PolicyCoverageInfo policyCoverageResponseNewCarCoverageVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleNewCarCoverageOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseNewCarCoverageVeh, "NEWCAR");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCoverageLimit()).isEqualTo("true");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCustomerDisplayed()).isEqualTo(true);
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCanChangeCoverage()).isEqualTo(false);
		//PAS-13920 end

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String replacedVehicleLeasedVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replacedVehicleNewCarCoverageVin = "4S3GTAD6XJ3750502";  //Subaru Impreza 2018

		replaceVehicleWithUpdates(policyNumber, vehicleLeasedOid, replacedVehicleLeasedVin, false, true);
		replaceVehicleWithUpdates(policyNumber, vehicleNewCarCoverageOid, replacedVehicleNewCarCoverageVin, false, true);

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
			ViewDriverAssignmentResponse responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.size()).isEqualTo(0);

		});

		//update assignment to a new one
		ViewDriversResponse responseViewDriver = HelperCommon.viewPolicyDrivers(policyNumber);
		String driverOid1 = responseViewDriver.driverList.get(0).oid;
		String driverOid2 = responseViewDriver.driverList.get(1).oid;
		HelperCommon.updateDriverAssignment(policyNumber, replacedVehicleLeasedOid, Arrays.asList(driverOid1));
		HelperCommon.updateDriverAssignment(policyNumber, replacedVehicleNewCarCoverageOid, Arrays.asList(driverOid2));

		assertSoftly(softly -> {
			//check different behaviour coverages of the vehicles
			//PAS-13920 start
			PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleLeasedOid, PolicyCoverageInfo.class);
			Coverage policyCoverageResponseReplacedLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedLeasedVeh, "LOAN");
			softly.assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCoverageLimit()).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCustomerDisplayed()).isEqualTo(false);
			softly.assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCanChangeCoverage()).isEqualTo(false);

			PolicyCoverageInfo policyCoverageResponseReplacedNewCarCoverageVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleNewCarCoverageOid, PolicyCoverageInfo.class);
			Coverage policyCoverageResponseReplacedNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedNewCarCoverageVeh, "NEWCAR");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCoverageLimit()).isEqualTo("false");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCustomerDisplayed()).isEqualTo(true);
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCanChangeCoverage()).isEqualTo(false);
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
//			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL");//do not have this coverage in response anymore. Karen Yifru doesn't care about it.

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
//			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "WL");//do not have this coverage in response anymore. Karen Yifru doesn't care about it.
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas13920_ReplaceVehicleKeepAssignmentsNoCoveragesBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Drivers").getTestDataList("DriverTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();
		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
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
		PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleLeasedOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseLeasedVeh, "LOAN");
		assertThat(policyCoverageResponseLeasedVehFiltered.getCoverageLimit()).isEqualTo("1");
		assertThat(policyCoverageResponseLeasedVehFiltered.getCustomerDisplayed()).isEqualTo(true);
		assertThat(policyCoverageResponseLeasedVehFiltered.getCanChangeCoverage()).isEqualTo(true);

		PolicyCoverageInfo policyCoverageResponseNewCarCoverageVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleNewCarCoverageOid, PolicyCoverageInfo.class);
		Coverage policyCoverageResponseNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseNewCarCoverageVeh, "NEWCAR");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCoverageLimit()).isEqualTo("true");
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCustomerDisplayed()).isEqualTo(true);
		assertThat(policyCoverageResponseNewCarCoverageVehFiltered.getCanChangeCoverage()).isEqualTo(false);
		//PAS-13920 end

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//driver assignment on endorsement before any modification
		ViewDriverAssignmentResponse responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
		String driverAssignmentVehicleOid1 = responseDriverAssignment.driverVehicleAssignments.get(0).vehicleOid;
		String driverAssignmentDriverOid1 = responseDriverAssignment.driverVehicleAssignments.get(0).driverOid;
		String driverAssignmentVehicleOid2 = responseDriverAssignment.driverVehicleAssignments.get(1).vehicleOid;
		String driverAssignmentDriverOid2 = responseDriverAssignment.driverVehicleAssignments.get(1).driverOid;

		String replacedVehicleLeasedVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replacedVehicleNewCarCoverageVin = "4S3GTAD6XJ3750502";  //Subaru Impreza 2018
		replaceVehicleWithUpdates(policyNumber, vehicleLeasedOid, replacedVehicleLeasedVin, true, false);
		replaceVehicleWithUpdates(policyNumber, vehicleNewCarCoverageOid, replacedVehicleNewCarCoverageVin, true, false);

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

		//Driver assignment check
		assertSoftly(softly -> {
			ViewDriverAssignmentResponse endorsementDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
			printToLog("vehicleNewCarCoverageOid: " + vehicleNewCarCoverageOid);
			printToLog("replacedVehicleNewCarCoverageOid: " + replacedVehicleNewCarCoverageOid);
			printToLog("vehicleLeasedOid: " + vehicleLeasedOid);
			printToLog("replacedVehicleLeasedOid: " + replacedVehicleLeasedOid);

			DriverAssignment driverAssignment1 = endorsementDriverAssignmentResponse.driverVehicleAssignments.stream().filter(driver -> driverAssignmentDriverOid1.equals(driver.driverOid)).findFirst().orElse(null);
			if (driverAssignmentVehicleOid1.equals(vehicleNewCarCoverageOid)) {
				assertThat(driverAssignment1.vehicleOid).isEqualTo(replacedVehicleNewCarCoverageOid);
			} else if (driverAssignmentVehicleOid1.equals(vehicleLeasedOid)) {
				assertThat(driverAssignment1.vehicleOid).isEqualTo(replacedVehicleLeasedOid);
			}
			DriverAssignment driverAssignment2 = endorsementDriverAssignmentResponse.driverVehicleAssignments.stream().filter(driver -> driverAssignmentDriverOid2.equals(driver.driverOid)).findFirst().orElse(null);
			if (driverAssignmentVehicleOid2.equals(vehicleNewCarCoverageOid)) {
				assertThat(driverAssignment2.vehicleOid).isEqualTo(replacedVehicleNewCarCoverageOid);
			} else if (driverAssignmentVehicleOid2.equals(vehicleLeasedOid)) {
				assertThat(driverAssignment2.vehicleOid).isEqualTo(replacedVehicleLeasedOid);
			}
		});

		//check different behaviour coverages of the vehicles
		//PAS-13920 start
		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleLeasedOid, PolicyCoverageInfo.class);
			Coverage policyCoverageResponseReplacedLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedLeasedVeh, "LOAN");
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCoverageLimit()).isEqualTo("0");
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCustomerDisplayed()).isEqualTo(false);
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCanChangeCoverage()).isEqualTo(false);

			PolicyCoverageInfo policyCoverageResponseReplacedNewCarCoverageVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleNewCarCoverageOid, PolicyCoverageInfo.class);
			Coverage policyCoverageResponseReplacedNewCarCoverageVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedNewCarCoverageVeh, "NEWCAR");
			assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCoverageLimit()).isEqualTo("false");
			assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCustomerDisplayed()).isEqualTo(true);
			assertThat(policyCoverageResponseReplacedNewCarCoverageVehFiltered.getCanChangeCoverage()).isEqualTo(false);
			//PAS-13920 end

			//check common coverages of the vehicles
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "BI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "PD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMBI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMPD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "MEDPM");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "IL");

			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COMPDED");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COLLDED"); //coverage is not updated in test data, so excluded from the check
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "GLASS");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "RREIM");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "TOWINGLABOR");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "SPECEQUIP");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL"); //is a different whale, excluded from check

			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COMPDED".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("250");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COLLDED".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("500");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "GLASS".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("false");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "RREIM".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("600");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "TOWINGLABOR".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("0/0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "SPECEQUIP".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("1000");
//			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "WL".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isNull(); //do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "LOAN".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "NEWCAR".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("false");

			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "BI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "PD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "UMBI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "UMPD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "MEDPM");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "IL");

			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "COMPDED");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "COLLDED");//coverage is not updated in test data, so excluded from the check
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "GLASS");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "RREIM");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "TOWINGLABOR");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "SPECEQUIP");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseNewCarCoverageVeh, policyCoverageResponseReplacedNewCarCoverageVeh, "WL");//is a different whale, excluded from check

			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COMPDED".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("250");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COLLDED".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("500");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "GLASS".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("false");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "RREIM".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("600");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "TOWINGLABOR".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("0/0");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "SPECEQUIP".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("1000");
//			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "WL".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isNull();//do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "LOAN".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedNewCarCoverageVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "NEWCAR".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("false");
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas13920_ReplaceVehicleKeepAssignmentsOneDriverBody(boolean keepAssignments) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestData("PremiumAndCoveragesTab"))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();
		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
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

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String replacedVehicleLeasedVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replacedVehicleNewCarCoverageVin = "4S3GTAD6XJ3750502";  //Subaru Impreza 2018
		replaceVehicleWithUpdates(policyNumber, vehicleLeasedOid, replacedVehicleLeasedVin, keepAssignments, false);
		replaceVehicleWithUpdates(policyNumber, vehicleNewCarCoverageOid, replacedVehicleNewCarCoverageVin, keepAssignments, false);

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

		printToLog("vehicleLeasedOid: " + vehicleLeasedOid);
		printToLog("vehicleNewCarCoverageOid: " + vehicleNewCarCoverageOid);
		printToLog("replacedVehicleLeasedOid: " + replacedVehicleLeasedOid);
		printToLog("replacedVehicleNewCarCoverageOid: " + replacedVehicleNewCarCoverageOid);
		//driver assignment check
		assertSoftly(softly -> {
			ViewDriverAssignmentResponse responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(1).vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(1).driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(1).relationshipType).isEqualTo("occasional");
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleLeasedVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleNewCarCoverageVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas13920_ReplaceVehicleKeepCoveragesOneDriverOneVehicleBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_OneVeh").getTestDataList("VehicleTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_OneVeh").getTestData("PremiumAndCoveragesTab"))
				//.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String vehicleOid = viewVehicles.vehicleList.get(0).oid;

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String replacedVehicleVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		replaceVehicleWithUpdates(policyNumber, vehicleOid, replacedVehicleVin, true, true);

		ViewVehicleResponse viewReplacedVehicles = HelperCommon.viewEndorsementVehicles(policyNumber);
		String replacedVehicleOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

		//Check statuses of the vehicles
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		//PAS-14680 start
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleOid);
		//PAS-14680 end

		printToLog("vehicleOid: " + vehicleOid);
		printToLog("replacedVehicleOid: " + replacedVehicleOid);

		//driver assignment check
		assertSoftly(softly -> {
			ViewDriverAssignmentResponse responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");
		});

		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleOid, PolicyCoverageInfo.class);
			PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleOid, PolicyCoverageInfo.class);
			Coverage policyCoverageResponseReplacedLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedLeasedVeh, "LOAN");
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCoverageLimit()).isEqualTo("0");
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCustomerDisplayed()).isEqualTo(false);
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCanChangeCoverage()).isEqualTo(false);

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
//			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL");//do not have this coverage in response anymore. Karen Yifru doesn't care about it.
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas13920_ReplaceVehicleDontKeepCoveragesOneDriverOneVehicleBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_OneVeh").getTestDataList("VehicleTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_OneVeh").getTestData("PremiumAndCoveragesTab"))
				//.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		ViewVehicleResponse viewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String vehicleOid = viewVehicles.vehicleList.get(0).oid;

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String replacedVehicleVin = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		replaceVehicleWithUpdates(policyNumber, vehicleOid, replacedVehicleVin, true, false);

		ViewVehicleResponse viewReplacedVehicles = HelperCommon.viewEndorsementVehicles(policyNumber);
		String replacedVehicleOid = viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

		//Check statuses of the vehicles
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleOid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		//PAS-14680 start
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleOid);
		//PAS-14680 end

		printToLog("vehicleOid: " + vehicleOid);
		printToLog("replacedVehicleOid: " + replacedVehicleOid);

		//driver assignment check
		assertSoftly(softly -> {
			ViewDriverAssignmentResponse responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).vehicleOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).driverOid).isNotNull();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");
		});

		assertSoftly(softly -> {
			PolicyCoverageInfo policyCoverageResponseLeasedVeh = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, vehicleOid, PolicyCoverageInfo.class);
			PolicyCoverageInfo policyCoverageResponseReplacedLeasedVeh = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replacedVehicleOid, PolicyCoverageInfo.class);
			Coverage policyCoverageResponseReplacedLeasedVehFiltered = testMiniServicesCoveragesHelper.getVehicleCoverageDetails(policyCoverageResponseReplacedLeasedVeh, "LOAN");
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCoverageLimit()).isEqualTo("0");
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCustomerDisplayed()).isEqualTo(false);
			assertThat(policyCoverageResponseReplacedLeasedVehFiltered.getCanChangeCoverage()).isEqualTo(false);

			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "BI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "PD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMBI");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "UMPD");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "MEDPM");
			testMiniServicesCoveragesHelper.policyCoverageComparisonByCoverageCd(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "IL");

			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COMPDED");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "COLLDED"); //coverage is not updated in test data, so excluded from the check
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "GLASS");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "RREIM");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "TOWINGLABOR");
			testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "SPECEQUIP");
			//testMiniServicesCoveragesHelper.vehicleCoverageComparisonByCoverageCdNotEqual(policyCoverageResponseLeasedVeh, policyCoverageResponseReplacedLeasedVeh, "WL"); //is a different whale, excluded from check

			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COMPDED".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("250");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "COLLDED".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("500");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "GLASS".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("false");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "RREIM".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("600");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "TOWINGLABOR".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("0/0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "SPECEQUIP".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("1000");
//			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "WL".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isNull();//do not have this coverage in response anymore. Karen Yifru doesn't care about it.
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "LOAN".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("0");
			softly.assertThat(policyCoverageResponseReplacedLeasedVeh.vehicleLevelCoverages.get(0).coverages.stream().filter(attribute -> "NEWCAR".equals(attribute.getCoverageCd())).findFirst().orElse(null).getCoverageLimit()).isEqualTo("false");
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
		ViewVehicleResponse viewVehicles2 = HelperCommon.viewPolicyVehicles(policyNumber);
		//PAS-14680 start
		assertThat(viewVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleVin.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isNull();
		//PAS-14680 end
	}

	protected void pas12175_RemoveReplaceAllVehiclesBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
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

			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehiclePpa1Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehiclePpa2Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehicleConvOid)).isEqualTo("[remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehicleMotorOid)).isEqualTo("[remove]");

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewVehicleResponse viewEndorsementVehicleResponse1 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehiclePpa1Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehiclePpa2Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehicleConvOid)).isEqualTo("[remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehicleMotorOid)).isEqualTo("[remove]");

			HelperCommon.deleteVehicle(policyNumber, vehiclePpa1Oid, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

			ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse2, vehiclePpa1Oid)).isEqualTo("");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse2, vehiclePpa2Oid)).isEqualTo("[replace]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse2, vehicleConvOid)).isEqualTo("[remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse2, vehicleMotorOid)).isEqualTo("[remove]");

			helperMiniServices.endorsementRateAndBind(policyNumber);
			ViewVehicleResponse viewPolicyVehicleResponse2 = HelperCommon.viewPolicyVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehiclePpa2Oid)).isEqualTo("[replace]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehicleConvOid)).isEqualTo("[remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehicleMotorOid)).isEqualTo("[remove]");

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			String newVehicleOid = addVehicleWithChecks(policyNumber, "2013-02-22", "1FADP3J2XJL222680", true);
			ViewVehicleResponse viewEndorsementVehicleResponse3 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, vehiclePpa2Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, newVehicleOid)).isEqualTo("[remove]");
		});
	}

	protected void pas12175_RemoveReplaceWaiveLiabilityBody() {
		TestData td = getPolicyTD("DataGather", "TestData");
		//TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_WaiveLiability").getTestDataList("VehicleTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_WaiveLiability").getTestData("PremiumAndCoveragesTab"))
				//.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError)
				.resolveLinks();

		mainApp().open();

		createCustomerIndividual();
		//Can't fill Waive Liability from TestData
		/*policy.createPolicy(testData);*/
		policy.createQuote(testData);
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.setVehicleCoverageDetailsValueByVehicle(2, "Waive Liability", "Yes");
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

		String vehicleVinPpa1 = td.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vehicleVinWl = td.getTestDataList("VehicleTab").get(1).getValue("VIN");

		assertSoftly(softly -> {
			ViewVehicleResponse viewPolicyVehicleResponse1 = HelperCommon.viewPolicyVehicles(policyNumber);
			String vehiclePpa1Oid = viewPolicyVehicleResponse1.vehicleList.stream().filter(vehicle -> vehicleVinPpa1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;
			String vehicleWlOid = viewPolicyVehicleResponse1.vehicleList.stream().filter(vehicle -> vehicleVinWl.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).oid;

			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehiclePpa1Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse1, vehicleWlOid)).isEqualTo("[remove]");

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewVehicleResponse viewEndorsementVehicleResponse1 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehiclePpa1Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse1, vehicleWlOid)).isEqualTo("[remove]");

			String newVehicleOid = addVehicleWithChecks(policyNumber, "2013-02-22", "1HGEM21504L055795", true);
			ViewVehicleResponse viewEndorsementVehicleResponse3 = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, vehiclePpa1Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, vehicleWlOid)).isEqualTo("[remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewEndorsementVehicleResponse3, newVehicleOid)).isEqualTo("[remove]");

			helperMiniServices.endorsementRateAndBind(policyNumber);

			ViewVehicleResponse viewPolicyVehicleResponse2 = HelperCommon.viewPolicyVehicles(policyNumber);
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehiclePpa1Oid)).isEqualTo("[replace, remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, vehicleWlOid)).isEqualTo("[remove]");
			softly.assertThat(checkAvailableActionsByVehicleOid(viewPolicyVehicleResponse2, newVehicleOid)).isEqualTo("[replace, remove]");
		});
	}

	protected void pas12942_GaragingAddressConsistencyDXPBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		ViewVehicleResponse policyValidateVehicleInfoResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oldVehicleOid = policyValidateVehicleInfoResponse.vehicleList.get(0).oid;

		//start an endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Update vehicle on DXP with different garaging address
		String address1 = "2011 CORAL AVE";
		String city = "Chesapeake";
		String zip = "23324";
		VehicleUpdateDto updateVehicleRequest = new VehicleUpdateDto();
		updateVehicleRequest.vehicleOwnership = new VehicleOwnership();
		updateVehicleRequest.vehicleOwnership.ownership = "OWN";
		updateVehicleRequest.usage = "Pleasure";
		updateVehicleRequest.salvaged = false;
		updateVehicleRequest.garagingDifferent = true;
		updateVehicleRequest.garagingAddress = new Address();
		updateVehicleRequest.garagingAddress.addressLine1 = address1;
		updateVehicleRequest.garagingAddress.city = city;
		updateVehicleRequest.garagingAddress.postalCode = zip;
		updateVehicleRequest.garagingAddress.stateProvCd = "VA";
		updateVehicleRequest.antiTheft = "STD";
		updateVehicleRequest.registeredOwner = true;

		HelperCommon.updateVehicle(policyNumber, oldVehicleOid, updateVehicleRequest);

		//hit Meta Data and verify that the garaging address is different
		AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsementVehiclesMetaData(policyNumber, oldVehicleOid);
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "garagingAddress.addressLine1", true, true, true, "40", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "garagingAddress.postalCode", true, true, true, "10", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "garagingAddress.city", true, true, true, "30", "String");
		testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "garagingAddress.stateProvCd", true, true, true, null, "String");

		//check that the garaging address is different in PAS and bind the endorsement
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		assertThat(vehicleTab.getAssetList().getAsset(ADDRESS_LINE_1.getLabel()).getValue().toString().equals(address1)).isTrue();
		assertThat(vehicleTab.getAssetList().getAsset(CITY.getLabel()).getValue().toString().equals(city)).isTrue();
		assertThat(vehicleTab.getAssetList().getAsset(STATE.getLabel()).getValue().toString().equals("VA")).isTrue();
		assertThat(vehicleTab.getAssetList().getAsset(ZIP_CODE.getLabel()).getValue().toString().equals(zip)).isTrue();
		vehicleTab.saveAndExit();
		helperMiniServices.endorsementRateAndBind(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void pas16113_ReplaceVehicleKeepAssignmentsForOtherStatesThanVaBody(){
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_NewVehicle").getTestDataList("VehicleTab"))
				.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Driver").getTestDataList("DriverTab"))
				.resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(testData);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String vin1 = testData.getTestDataList("VehicleTab").get(0).getValue("VIN");
		String vin2 = testData.getTestDataList("VehicleTab").get(1).getValue("VIN");

		//get vehicles oid's
		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle vehicle1 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
		Vehicle vehicle2 = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

		assertThat(vehicle1.vehIdentificationNo).isEqualTo(vin1);
		String vehicleOid1 = vehicle1.oid;
		assertThat(vehicle2.vehIdentificationNo).isEqualTo(vin2);
		String vehicleOid2 = vehicle2.oid;

		String replacedVehicleVin1 = "2T1BURHE4JC034340"; //Toyota Corolla 2018
		String replacedVehicleVin2 = "1HGFA16526L081415"; //Honda Civic 2006

		String replacedVehicleOid1 = replaceVehicleWithUpdates(policyNumber, vehicleOid1, replacedVehicleVin1, true, true);
		String replacedVehicleOid2 = replaceVehicleWithUpdates(policyNumber, vehicleOid2, replacedVehicleVin2, false, true);

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin1.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
		viewEndorsementVehicleResponse2.vehicleList.stream().filter(veh -> vin2.equals(veh.vehIdentificationNo)).findFirst().orElse(null);

		ViewVehicleResponse viewReplacedVehicles = HelperCommon.viewEndorsementVehicles(policyNumber);

		//Check statuses of the vehicles
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleOid1.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> vehicleOid2.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleOid1.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleOid2.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleVin1.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleOid1);
		assertThat(viewReplacedVehicles.vehicleList.stream().filter(vehicle -> replacedVehicleVin2.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleOid2);

		helperMiniServices.endorsementRateAndBind(policyNumber);

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		String purchaseDate3 = "2013-01-21";
		String vin3 = "JF1GJAH65EH007244"; //Subaru Impreza 2014
		String vehicleOid3 = addVehicleWithChecks(policyNumber, purchaseDate3, vin3, true);
		helperMiniServices.endorsementRateAndBind(policyNumber);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//run delete vehicle service
		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, replacedVehicleOid1, VehicleUpdateResponseDto.class,Response.Status.OK.getStatusCode());

		assertSoftly(softly -> {
			softly.assertThat(deleteVehicleResponse.oid).isEqualTo(replacedVehicleOid1);
			softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
			assertThat(deleteVehicleResponse.validations).isEqualTo(null);
		});

		String replacedVehicleVin3 = "3FAFP31341R200709"; //FORD 2001
		String replacedVehicleVin4 = "1D7HW22N95S201385"; //DODGE 2005

		String replacedVehicleOid3 = replaceVehicleWithUpdates(policyNumber, vehicleOid3, replacedVehicleVin3, true, true);
		String replacedVehicleOid4 = replaceVehicleWithUpdates(policyNumber, replacedVehicleOid2, replacedVehicleVin4, false, true);

		ViewVehicleResponse viewReplacedVehicles2 = HelperCommon.viewEndorsementVehicles(policyNumber);

		//Check statuses of the vehicles
		assertThat(viewReplacedVehicles2.vehicleList.stream().filter(vehicle -> vehicleOid3.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleOid2.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleOid1.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pendingRemoval");
		assertThat(viewReplacedVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleOid3.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		assertThat(viewReplacedVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleOid4.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus).isEqualTo("pending");
		assertThat(viewReplacedVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleVin3.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(vehicleOid3);
		assertThat(viewReplacedVehicles2.vehicleList.stream().filter(vehicle -> replacedVehicleVin4.equals(vehicle.vehIdentificationNo)).findFirst().orElse(null).vehicleReplacedBy).isEqualTo(replacedVehicleOid2);

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas9750_addVehicleServiceBlockingForPurchaseDateBody(){
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//backdate
		String purchaseDate = TimeSetterUtil.getInstance().getCurrentTime().minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String vin = "JF1GJAH65EH007244";

		Vehicle addVehicleRequest = DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate);
		ErrorResponseDto errorResponseAdd = HelperCommon.addVehicle(policyNumber, addVehicleRequest, ErrorResponseDto.class, 422);
		assertSoftly(softly -> {
			softly.assertThat(errorResponseAdd.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponseAdd.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponseAdd.errors.get(0).errorCode).startsWith(ErrorDxpEnum.Errors.VEHICLE_CANNOT_BE_ADDED_ERROR.getCode());
			softly.assertThat(errorResponseAdd.errors.get(0).message).contains(ErrorDxpEnum.Errors.VEHICLE_CANNOT_BE_ADDED_ERROR.getMessage());

		//check if vehicle was not added
		ViewVehicleResponse listOfVehicles = HelperCommon.viewEndorsementVehicles(policyNumber);
			softly.assertThat(listOfVehicles.vehicleList.stream().filter(veh -> vin.equals(veh.vehIdentificationNo))).isEmpty();

		//today date
		String purchaseDate2 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Vehicle responseAddVehicle = HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate2), Vehicle.class, 201);
		softly.assertThat(responseAddVehicle.oid).isNotEmpty();

		//purchase date - 31days
		String vin2 = "1HGEM21504L055795";
		String purchaseDate3 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(31).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		Vehicle responseAddVehicle2 = HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin2, purchaseDate3), Vehicle.class, 201);
		softly.assertThat(responseAddVehicle2.oid).isNotEmpty();
		});
	}

	private String checkAvailableActionsByVehicleOid(ViewVehicleResponse viewVehicleResponse, String vehiclePpa1Oid) {
		String availableActions = "";
		if (!"pendingRemoval".equals(viewVehicleResponse.vehicleList.stream().filter(vehicle -> vehiclePpa1Oid.equals(vehicle.oid)).findFirst().orElse(null).vehicleStatus)) {
			availableActions = viewVehicleResponse.vehicleList.stream().filter(vehicle -> vehiclePpa1Oid.equals(vehicle.oid)).findFirst().orElse(null).availableActions.toString();
		}
		return availableActions;
	}

	private String addVehicleWithChecks(String policyNumber, String purchaseDate, String vin, boolean allowedToAddVehicle) {
		//Add new vehicle
		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
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

	private String replaceVehicleWithUpdates(String policyNumber, String vehicleToReplaceOid, String replacedVehicleVin, boolean keepAssignments, boolean keepCoverages) {
		printToLog("policyNumber: " + policyNumber + ", vehicleToReplaceOid: " + vehicleToReplaceOid + ", replacedVehicleVin: " + replacedVehicleVin);
		printToLog("keepAssignments: "+ keepAssignments + ", keepCoverages: "+ keepCoverages);
		ReplaceVehicleRequest replaceVehicleRequest = DXPRequestFactory.createReplaceVehicleRequest(replacedVehicleVin, "2013-03-31", keepAssignments, keepCoverages);
		VehicleUpdateResponseDto replaceVehicleResponse = HelperCommon.replaceVehicle(policyNumber, vehicleToReplaceOid, replaceVehicleRequest,VehicleUpdateResponseDto.class, Response.Status.OK.getStatusCode());
		String replaceVehicleOid = replaceVehicleResponse.oid;
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleOid);
		return replaceVehicleOid;
	}

	private boolean hasError(ErrorResponseDto errorResponseDto, String expectedField, ErrorDxpEnum.Errors expectedError) {
		return errorResponseDto.errors.stream().anyMatch(error -> expectedField.equals(error.field)
						&& expectedError.getCode().equals(error.errorCode)
						&& StringUtils.startsWith(error.message, expectedError.getMessage()));
	}

	private boolean hasError(List<ValidationError> validations, String expectedMessage) {
		return validations.stream().anyMatch(error -> error.message.equals(expectedMessage));
	}

	private Vehicle getVehicleByOid(ViewVehicleResponse viewVehicleResponse, String oid) {
		return viewVehicleResponse.vehicleList.stream().filter(vehicle -> vehicle.oid.equals(oid)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No Vehicle found for oid: " + oid));
	}

	private Vehicle getVehicleByVin(ViewVehicleResponse viewVehicleResponse, String vin) {
		return viewVehicleResponse.vehicleList.stream().filter(vehicle -> vehicle.vehIdentificationNo.equals(vin)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No Vehicle found for vin: " + vin));
	}
}



