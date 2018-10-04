package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.VehicleTab.*;
import static aaa.modules.regression.service.helper.preconditions.TestMiniServicesNonPremiumBearingAbstractPreconditions.*;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.Response;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import toolkit.db.DBService;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestMiniServicesGeneralHelper extends PolicyBaseTest {

	private static final String SESSION_ID_1 = "oid1";
	private VehicleTab vehicleTab = new VehicleTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	protected void pas9997_paymentMethodsLookupBody() {
		assertSoftly(softly -> {
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_METHODS_CONFIG_PAY_PLAN_ADD_WY);
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_METHODS_CONFIG_PAY_PLAN_CHANGE_WY);

			String lookupName2 = "AAAeValueQualifyingPaymentMethods";
			String productCd = "AAA_SS";
			String riskStateCd = "WY";

			String effectiveDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup12 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate1);
			softly.assertThat("FALSE".equals(responseValidateLookup12.get("pciDebitCard"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup12.get("pciCreditCard"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup12.get("eft"))).isTrue();
			softly.assertThat(responseValidateLookup12.size()).isEqualTo(3);

			String effectiveDate2 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup22 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate2);
			softly.assertThat("FALSE".equals(responseValidateLookup22.get("pciDebitCard"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup22.get("pciCreditCard"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup22.get("eft"))).isTrue();
			softly.assertThat(responseValidateLookup12.size()).isEqualTo(3);

			String effectiveDate3 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup32 = HelperCommon.executeLookupValidate(lookupName2, productCd, riskStateCd, effectiveDate3);
			softly.assertThat("TRUE".equals(responseValidateLookup32.get("pciDebitCard"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup32.get("pciCreditCard"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup32.get("eft"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup32.get("XXXX"))).isTrue();
			softly.assertThat(responseValidateLookup32.size()).isEqualTo(4);

			DBService.get().executeUpdate(DELETE_ADDED_PAYMENT_METHOD_CONFIGS_WY);
		});
	}

	protected void pas9997_paymentPlansLookupBody() {
		assertSoftly(softly -> {
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_ADD_WY);
			DBService.get().executeUpdate(ADD_NEW_PAYMENT_PLAN_CONFIG_PAY_PLAN_CHANGE_WY);

			String lookupName1 = "AAAeValueQualifyingPaymentPlans";
			String productCd = "AAA_SS";
			String riskStateCd = "WY";

			String effectiveDate1 = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup11 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate1);
			softly.assertThat("FALSE".equals(responseValidateLookup11.get("annualSS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup11.get("semiAnnual6SS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup11.get("annualSS_R"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup11.get("semiAnnual6SS_R"))).isTrue();
			softly.assertThat(responseValidateLookup11.size()).isEqualTo(4);

			String effectiveDate2 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(4).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup21 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate2);
			softly.assertThat("FALSE".equals(responseValidateLookup21.get("annualSS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup21.get("semiAnnual6SS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup21.get("annualSS_R"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup21.get("semiAnnual6SS_R"))).isTrue();
			softly.assertThat(responseValidateLookup21.size()).isEqualTo(4);

			String effectiveDate3 = TimeSetterUtil.getInstance().getCurrentTime().minusDays(8).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HashMap<String, String> responseValidateLookup31 = HelperCommon.executeLookupValidate(lookupName1, productCd, riskStateCd, effectiveDate3);
			softly.assertThat("TRUE".equals(responseValidateLookup31.get("annualSS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup31.get("semiAnnual6SS"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup31.get("annualSS_R"))).isTrue();
			softly.assertThat("TRUE".equals(responseValidateLookup31.get("semiAnnual6SS_R"))).isTrue();
			softly.assertThat("FALSE".equals(responseValidateLookup31.get("XXXX"))).isTrue();
			softly.assertThat(responseValidateLookup31.size()).isEqualTo(5);

			DBService.get().executeUpdate(DELETE_ADDED_PAYMENT_PLANS_CONFIGS_WY);
		});
	}

	protected void pas12407_bigDataService() {
		assertSoftly(softly -> {
			mainApp().open();
			String policyNumber = getCopiedPolicy();

			//Create pended endorsement
			PolicySummary response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			assertThat(response.policyNumber).isEqualTo(policyNumber);

			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.VEHICLE.get());

			ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
			String oid = viewVehicleResponse.vehicleList.get(0).oid;

			AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsementVehiclesMetaData(policyNumber, oid);
			AttributeMetadata metaDataFieldResponseVehTypeCd = getAttributeMetadata(metaDataResponse, "vehTypeCd", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("PPA")).isEqualTo("Private Passenger Auto");
			softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Conversion")).isEqualTo("Conversion Van");
			softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Motor")).isEqualTo("Motor Home");
			softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Trailer")).isEqualTo("Trailer");
			softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("AC")).isEqualTo("Limited Production/Antique");

			AttributeMetadata metaDataFieldResponseUsage = getAttributeMetadata(metaDataResponse, "usage", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseUsage.valueRange.get("Pleasure")).isEqualTo("Pleasure");
			softly.assertThat(metaDataFieldResponseUsage.valueRange.get("WorkCommute")).isEqualTo("Commute");
			softly.assertThat(metaDataFieldResponseUsage.valueRange.get("Business")).isEqualTo("Business");
			softly.assertThat(metaDataFieldResponseUsage.valueRange.get("Artisan")).isEqualTo("Artisan");
			softly.assertThat(metaDataFieldResponseUsage.valueRange.get("Farm")).isEqualTo("Farm");

			getAttributeMetadata(metaDataResponse, "vehIdentificationNo", true, true, false, "20", "String");
			getAttributeMetadata(metaDataResponse, "modelYear", false, true, true, "5", "String");
			getAttributeMetadata(metaDataResponse, "manufacturer", false, true, true, null, "String");
			getAttributeMetadata(metaDataResponse, "model", false, true, true, null, "String");
			getAttributeMetadata(metaDataResponse, "series", false, true, false, null, "String");

			AttributeMetadata metaDataFieldResponseBodyStyle = getAttributeMetadata(metaDataResponse, "bodyStyle", false, true, false, null, "String");
			softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("")).isEqualTo("");
			softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("SPORT VAN")).isEqualTo("SPORT VAN");
			softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("OTHER")).isEqualTo("OTHER");

			getAttributeMetadata(metaDataResponse, "salvaged", true, true, false, null, "Boolean");

			AttributeMetadata metaDataFieldResponseAntiTheft = getAttributeMetadata(metaDataResponse, "antiTheft", true, true, false, null, "String");
			softly.assertThat(metaDataFieldResponseAntiTheft.valueRange.get("")).isEqualTo("");
			softly.assertThat(metaDataFieldResponseAntiTheft.valueRange.get("NONE")).isEqualTo("None");
			softly.assertThat(metaDataFieldResponseAntiTheft.valueRange.get("STD")).isEqualTo("Vehicle Recovery Device");

			getAttributeMetadata(metaDataResponse, "registeredOwner", true, false, false, null, "Boolean");
			getAttributeMetadata(metaDataResponse, "garagingDifferent", true, true, false, null, "Boolean");
			getAttributeMetadata(metaDataResponse, "garagingAddress.postalCode", true, false, true, "10", "String");
			getAttributeMetadata(metaDataResponse, "garagingAddress.addressLine1", true, false, true, "40", "String");
			getAttributeMetadata(metaDataResponse, "garagingAddress.addressLine2", true, false, false, "40", "String");
			getAttributeMetadata(metaDataResponse, "garagingAddress.city", true, false, true, "30", "String");
			getAttributeMetadata(metaDataResponse, "garagingAddress.stateProvCd", true, false, true, null, "String");

			AttributeMetadata metaDataFieldResponseOwnership = getAttributeMetadata(metaDataResponse, "vehicleOwnership.ownership", true, true, false, null, "String");
			softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("")).isEqualTo("");
			softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("OWN")).isEqualTo("Owned");
			softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("FNC")).isEqualTo("Financed");
			softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("LSD")).isEqualTo("Leased");

			getAttributeMetadata(metaDataResponse, "vehicleOwnership.name", false, false, false, "100", "String");
			getAttributeMetadata(metaDataResponse, "vehicleOwnership.secondName", false, false, false, "100", "String");
			getAttributeMetadata(metaDataResponse, "vehicleOwnership.postalCode", false, false, false, "10", "String");
			getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine1", false, false, false, "40", "String");
			getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine2", false, false, false, "40", "String");
			getAttributeMetadata(metaDataResponse, "vehicleOwnership.city", false, false, false, "30", "String");
			getAttributeMetadata(metaDataResponse, "vehicleOwnership.stateProvCd", false, false, false, null, "String");

			//edit pending endorsement
			vehicleTab.getAssetList().getAsset(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL).setValue("Yes");
			vehicleTab.getAssetList().getAsset(ZIP_CODE).setValue("23703");
			vehicleTab.getAssetList().getAsset(ADDRESS_LINE_1).setValue("4112 FORREST HILLS DR");
			vehicleTab.getAssetList().getAsset(CITY).setValue("PORTSMOUTH");
			vehicleTab.getAssetList().getAsset(STATE).setValue("VA");
			vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE).setValue("Leased");
			vehicleTab.getOwnershipAssetList().getAsset(Ownership.FIRST_NAME).setValue("GMAC");
			vehicleTab.saveAndExit();

			AttributeMetadata[] metaDataResponse2 = HelperCommon.viewEndorsementVehiclesMetaData(policyNumber, oid);
			getAttributeMetadata(metaDataResponse2, "garagingDifferent", true, true, false, null, "Boolean");
			getAttributeMetadata(metaDataResponse2, "garagingAddress.postalCode", true, true, true, "10", "String");
			getAttributeMetadata(metaDataResponse2, "garagingAddress.addressLine1", true, true, true, "40", "String");
			getAttributeMetadata(metaDataResponse2, "garagingAddress.addressLine2", true, true, false, "40", "String");
			getAttributeMetadata(metaDataResponse2, "garagingAddress.city", true, true, true, "30", "String");
			getAttributeMetadata(metaDataResponse2, "garagingAddress.stateProvCd", true, true, true, null, "String");

			getAttributeMetadata(metaDataResponse2, "vehicleOwnership.name", true, true, false, "100", "String");
			getAttributeMetadata(metaDataResponse2, "vehicleOwnership.secondName", false, true, false, "100", "String");
			getAttributeMetadata(metaDataResponse2, "vehicleOwnership.postalCode", true, true, false, "10", "String");
			getAttributeMetadata(metaDataResponse2, "vehicleOwnership.addressLine1", true, true, false, "40", "String");
			getAttributeMetadata(metaDataResponse2, "vehicleOwnership.addressLine2", true, true, false, "40", "String");
			getAttributeMetadata(metaDataResponse2, "vehicleOwnership.city", true, true, false, "30", "String");
			getAttributeMetadata(metaDataResponse2, "vehicleOwnership.stateProvCd", true, true, false, null, "String");
		});
	}

	AttributeMetadata getAttributeMetadata(AttributeMetadata[] metaDataResponse, String fieldName, boolean enabled, boolean visible, boolean required, String maxLength, String attributeType) {
		AttributeMetadata metaDataFieldResponse = Arrays.stream(metaDataResponse).filter(attributeMetadata -> fieldName.equals(attributeMetadata.attributeName)).findFirst().orElse(null);
		assertThat(metaDataFieldResponse.enabled).isEqualTo(enabled);
		assertThat(metaDataFieldResponse.visible).isEqualTo(visible);
		assertThat(metaDataFieldResponse.required).isEqualTo(required);
		assertThat(metaDataFieldResponse.maxLength).isEqualTo(maxLength);
		assertThat(metaDataFieldResponse.attributeType).isEqualTo(attributeType);
		return metaDataFieldResponse;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Validation on Update/Rate/Bind for vehicle use = Business
	 * @scenario 1. Create active policy
	 * 2. Run through the flow using services
	 * //View Policy
	 * //View Renewal which doesn't exist
	 * //get all vehicles
	 * //get all drivers
	 * //get all coverages
	 * //Check endorsement is allowed
	 * //Lock the policy
	 * //Create pended endorsement
	 * //Validate VIN
	 * //Add new vehicle
	 * //Update Vehicle with proper Usage and Registered Owner
	 * //Check vehicle update service when  garage address is different
	 * //View endorsement vehicles
	 * //View endorsement drivers
	 * //View driver assignment if VA
	 * //Rate endorsement
	 * //View premium after new vehicle was added
	 * //Bind endorsement
	 * //Unlock policy
	 */
	protected void pas12866_e2eBctBody(String state, boolean isNewPolicy, ETCSCoreSoftAssertions softly) {
		String policyNumber = "";
		if (isNewPolicy) {
			mainApp().open();
			policyNumber = getCopiedPolicy();
		} else {
			switch (state) {
				case "AZ":
					policyNumber = "AZSS926232043";
					break;
				case "CO":
					policyNumber = "COSS926232041";
					break;
				case "CT":
					policyNumber = "CTSS926232046";
					break;
				case "DC":
					policyNumber = "DCSS926232042";
					break;
				case "MD":
					policyNumber = "MDSS926232047";
					break;
				case "NJ":
					policyNumber = "NJSS926232048";
					break;
				case "NV":
					policyNumber = "NVSS926232045";
					break;
				case "WV":
					policyNumber = "WVSS926232044";
					break;
				case "WY":
					policyNumber = "WYSS926232049";
					break;
				case "DE":
					policyNumber = "DESS926232053";
					break;
				case "ID":
					policyNumber = "IDSS926232058";
					break;
				case "IN":
					policyNumber = "INSS926232057";
					break;
				case "KS":
					policyNumber = "KSSS926232056";
					break;
				case "KY":
					policyNumber = "KYSS926232055";
					break;
				case "MT":
					policyNumber = "MTSS926232059";
					break;
				case "NY":
					policyNumber = "NYSS926232052";
					break;
				case "OK":
					policyNumber = "OKSS926232050";
					break;
				case "SD":
					policyNumber = "SDSS926232054";
					break;
				case "VA":
					policyNumber = "VASS926232051";
					break;
				case "OH":
					policyNumber = "OHSS926232062";
					break;
				case "OR":
					policyNumber = "ORSS926232061";
					break;
				case "PA":
					policyNumber = "PASS926232060";
					break;
				default:
			}
		}
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		//View Policy
		PolicySummary responseViewPolicy = HelperCommon.viewPolicyRenewalSummary(policyNumber, "policy", Response.Status.OK.getStatusCode());
		softly.assertThat(responseViewPolicy.policyNumber).isEqualTo(policyNumber);
		softly.assertThat(responseViewPolicy.policyStatus).isEqualTo("issued");

		//View Renewal which doesn't exist
		PolicySummary responseViewRenewal = HelperCommon.viewPolicyRenewalSummary(policyNumber, "renewal", Response.Status.NOT_FOUND.getStatusCode());
		softly.assertThat(responseViewRenewal.errorCode).isEqualTo(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getCode());
		softly.assertThat(responseViewRenewal.message).contains(ErrorDxpEnum.Errors.RENEWAL_DOES_NOT_EXIST.getMessage() + policyNumber + ".");

		//get all vehicles
		ViewVehicleResponse responseViewVehicles = HelperCommon.viewPolicyVehicles(policyNumber);
		String originalVehicle = responseViewVehicles.vehicleList.get(0).oid;

		//get all drivers
		ViewDriversResponse responseViewDrivers = HelperCommon.viewPolicyDrivers(policyNumber);
		String originalDriver = responseViewDrivers.driverList.get(0).oid;

		//get all coverages
		PolicyCoverageInfo coverageResponse = HelperCommon.viewPolicyCoverages(policyNumber, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		softly.assertThat(coverageResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null)).isNotNull();

		//get all discounts
		DiscountSummary policyDiscountsResponse = HelperCommon.viewDiscounts(policyNumber, "policy", 200);
		assertThat(policyDiscountsResponse.policyDiscounts.get(1).discountCd).isNotNull();

		//Check endorsement is allowed
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		ValidateEndorsementResponse responseValidateEndorse = HelperCommon.startEndorsement(policyNumber, endorsementDate);

		if ("AZ".contains(state)) {
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(1)).isEqualTo("UpdateDriver");
		} else if ("MD".contains(state)){
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(0)).isEqualTo("UpdateDriver");
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(1)).isEqualTo("UpdateCoverages");
		} else if ("DC".contains(state)) {
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(1)).isEqualTo("UpdateCoverages");
		} else if ("NY".contains(state)) {
			softly.assertThat(responseValidateEndorse.allowedEndorsements.isEmpty()).isTrue();
		} else {
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(0)).isEqualTo("UpdateVehicle");
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(1)).isEqualTo("UpdateDriver");
			softly.assertThat(responseValidateEndorse.allowedEndorsements.get(2)).isEqualTo("UpdateCoverages");
		}

		//Lock the policy
/*		PolicyLockUnlockDto responseLock = HelperCommon.executePolicyLockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
		assertThat(responseLock.policyNumber).isEqualTo(policyNumber);
		assertThat(responseLock.status).isEqualTo("Locked");*/

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		String purchaseDate = "2013-02-22";
		String vin = "1HGFA16526L081415";

		//Validate VIN
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

		//BUG PAS-14393 When sending GaragingDifferent = False, garaging address is not updated
		VehicleUpdateDto updateGaragingAddressVehicleRequest2 = new VehicleUpdateDto();
		updateGaragingAddressVehicleRequest2.garagingDifferent = false;
		Vehicle updateVehicleGaragingAddressResponse2 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest2);
		softly.assertThat(updateVehicleGaragingAddressResponse2.garagingDifferent).isEqualTo(false);

		if ("VA".equals(state)) {
			//PAS-14501 start
			Vehicle updateVehicleGaragingAddressResponse3 = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateGaragingAddressVehicleRequest);
			softly.assertThat(updateVehicleGaragingAddressResponse3.garagingDifferent).isEqualTo(true);
			softly.assertThat(updateVehicleGaragingAddressResponse3.garagingAddress.postalCode).isEqualTo(zipCodeGarage);
			softly.assertThat(updateVehicleGaragingAddressResponse3.garagingAddress.addressLine1).isEqualTo(addressGarage);
			softly.assertThat(updateVehicleGaragingAddressResponse3.garagingAddress.city).isEqualTo(cityGarage);
			softly.assertThat(updateVehicleGaragingAddressResponse3.garagingAddress.stateProvCd).isEqualTo(stateGarage);
			//PAS-14501 end
		}
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

		//PAS-13252 start
		String purchaseDate2 = "2014-03-22";
		VehicleUpdateDto updatePurchaseDateVehicleRequest = new VehicleUpdateDto();
		updatePurchaseDateVehicleRequest.purchaseDate = purchaseDate2;
		Vehicle updatePurchaseDateVehicleResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updatePurchaseDateVehicleRequest);
		//BUG PAS-13524 UpdateVehicle response contains NULLs for some fields
		softly.assertThat(updatePurchaseDateVehicleResponse.purchaseDate.replace("T00:00:00Z", "")).isEqualTo(purchaseDate2);
		softly.assertThat(updatePurchaseDateVehicleResponse.oid).isEqualTo(newVehicleOid);
		softly.assertThat(updatePurchaseDateVehicleResponse.salvaged.toString()).isEqualTo("false");
		//PAS-13252 end

		//View endorsement vehicles
		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse.canAddVehicle).isEqualTo(true);

		List<Vehicle> sortedVehicles = viewEndorsementVehicleResponse.vehicleList;
		sortedVehicles.sort(Vehicle.ACTIVE_POLICY_COMPARATOR);
		assertThat(viewEndorsementVehicleResponse.vehicleList).containsAll(sortedVehicles);
		Vehicle newVehicle = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> newVehicleOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle.vehIdentificationNo).isEqualTo(vin);

		//View endorsement drivers
		ViewDriversResponse responseViewDriver = HelperCommon.viewEndorsementDrivers(policyNumber);
		assertThat(responseViewDriver.driverList.stream().filter(driver -> originalDriver.equals(driver.oid)).findFirst().orElse(null)).isNotNull();

		//TODO jpukenaite add NY when this state will have driver assignment functionality
		if ("VA, CA".contains(state)) {
			ViewDriverAssignmentResponse responseDriverAssignment = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).vehicleOid).isNotEmpty();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).driverOid).isNotEmpty();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(0).relationshipType).isEqualTo("primary");

			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(1).vehicleOid).isNotEmpty();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(1).driverOid).isNotEmpty();
			softly.assertThat(responseDriverAssignment.driverVehicleAssignments.get(1).relationshipType).isEqualTo("occasional");
		} else if (!state.contains("NY")){
			ErrorResponseDto responseDriverAssignment = HelperCommon.viewEndorsementAssignmentsError(policyNumber, 422);
			softly.assertThat(responseDriverAssignment.errorCode).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getCode());
			softly.assertThat(responseDriverAssignment.message).isEqualTo(ErrorDxpEnum.Errors.OPERATION_NOT_APPLICABLE_FOR_THE_STATE.getMessage());
		}

		//update coverages
		String compDedCovCd = "COMPDED";
		String compDedAvailableLimits = "100";
		PolicyCoverageInfo coverageResponseCompDedResponse = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(compDedCovCd, compDedAvailableLimits), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredCoverageResponse = coverageResponseCompDedResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
		assertThat(filteredCoverageResponse.coverageLimit).isEqualTo("100");

		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");
		//Rate endorsement
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		//get all discounts
		DiscountSummary endorsementDiscountsResponse = HelperCommon.viewDiscounts(policyNumber, "endorsement", 200);
		assertThat(endorsementDiscountsResponse.policyDiscounts.get(1).discountCd).isNotEmpty();

		//View premium after new vehicle was added
		PolicyPremiumInfo[] viewPremiumInfoPendedEndorsementResponse = HelperCommon.viewEndorsementPremiums(policyNumber);
		softly.assertThat(viewPremiumInfoPendedEndorsementResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
		softly.assertThat(viewPremiumInfoPendedEndorsementResponse[0].premiumCode).isEqualTo("GWT");
		softly.assertThat(new Dollar(viewPremiumInfoPendedEndorsementResponse[0].actualAmt)).isNotNull();
		softly.assertThat(new Dollar(viewPremiumInfoPendedEndorsementResponse[0].termPremium)).isNotNull();

		//update coverages
		String compDedCovCd2 = "COMPDED";
		String compDedAvailableLimits2 = "500";
		PolicyCoverageInfo coverageCompDedResponse2 = HelperCommon.updateEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, DXPRequestFactory.createUpdateCoverageRequest(compDedCovCd2, compDedAvailableLimits2), PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredUpdateCoverageResponse2 = coverageCompDedResponse2.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
		assertThat(filteredUpdateCoverageResponse2.coverageLimit).isEqualTo("500");

		//View endorsement Coverage
		PolicyCoverageInfo viewEndorsementCoveragesByVehicleResponse = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredViewEndorsementCoverageResponse = viewEndorsementCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
		assertThat(filteredViewEndorsementCoverageResponse.coverageLimit).isEqualTo("500");

		//BUG not reset status
		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

		//Bind endorsement
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getCode(), ErrorDxpEnum.Errors.POLICY_NOT_RATED_DXP.getMessage(), null);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithCheck(policyNumber);

		//Unlock policy
		PolicyLockUnlockDto responseUnlock = HelperCommon.executePolicyUnlockService(policyNumber, Response.Status.OK.getStatusCode(), SESSION_ID_1);
		assertThat(responseUnlock.policyNumber).isEqualTo(policyNumber);
		assertThat(responseUnlock.status).isEqualTo("Unlocked");

		//View endorsement Coverage
		PolicyCoverageInfo viewPolicyCoveragesByVehicleResponse = HelperCommon.viewPolicyCoveragesByVehicle(policyNumber, newVehicleOid, PolicyCoverageInfo.class, Response.Status.OK.getStatusCode());
		Coverage filteredViewPolicyCoverageResponse = viewPolicyCoveragesByVehicleResponse.vehicleLevelCoverages.get(0).coverages.stream().filter(cov -> "COMPDED".equals(cov.coverageCd)).findFirst().orElse(null);
		assertThat(filteredViewPolicyCoverageResponse.coverageLimit).isEqualTo("500");

		SearchPage.openPolicy(policyNumber);
		//BUG PAS-13652 When Endorsement screen shows Endorsement Date field twice, if creating endorsement after endorsement created/issued through service
		//BUG PAS-13651 Instantiate state specific coverages
		testEValueDiscount.secondEndorsementIssueCheck();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		VehicleUpdateResponseDto deleteVehicleResponse = HelperCommon.deleteVehicle(policyNumber, newVehicleOid);
		softly.assertThat(deleteVehicleResponse.oid).isEqualTo(newVehicleOid);
		softly.assertThat(deleteVehicleResponse.vehicleStatus).isEqualTo("pendingRemoval");
		softly.assertThat(deleteVehicleResponse.vehIdentificationNo).isEqualTo(vin);
		assertThat(deleteVehicleResponse.validations).isEqualTo(null);

		helperMiniServices.pas14952_checkEndorsementStatusWasReset(policyNumber, "Gathering Info");

		ViewVehicleResponse viewEndorsementVehicleResponse2 = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).oid).isEqualTo(newVehicleOid);
		assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).vehIdentificationNo).isEqualTo(vin);
		assertThat(viewEndorsementVehicleResponse2.vehicleList.get(0).vehicleStatus).isEqualTo("pendingRemoval");

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

}



