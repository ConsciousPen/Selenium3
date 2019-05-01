package aaa.modules.regression.service.auto_ca.select.functional;

import static aaa.main.enums.CustomerConstants.ADDRESS_LINE_1;
import static aaa.main.enums.CustomerConstants.ZIP_CODE;
import static aaa.main.metadata.policy.AutoCaMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.AttributeMetadata;
import aaa.helpers.rest.dtoDxp.PolicySummary;
import aaa.helpers.rest.dtoDxp.ViewVehicleResponse;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelper;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelperCA;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class TestMiniServicesVehicles extends TestMiniServicesVehiclesHelperCA {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
	 * @name Check View Vehicle service
	 * @scenario 1.Create a policy with 4 vehicles (1.PPA 2.PPA 3. Conversion Van 4. Trailer )
	 * 2.hit view vehicle service
	 * 3.get a response in correct order.
	 * 4. perform endorsement on the Policy
	 * 5.add new vehicle (that will be pending)
	 * 6.hit view vehicle service
	 * 7.validate response and response should have pending vehicle first.
	 * @megha Gubbala Added Pas 12244
	 * Add 2 PPA vehicle
	 * hit view vehicle service on pended endorsement
	 * verify order of vehicle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-10449", "PAS-12244", "PAS-12246", "PAS-14593", "PAS-25265", "PAS-15401"})
	public void pas10449_ViewVehicleServiceOrderOfVehicle(@Optional("CA") String state) {

		pas10449_ViewVehicleServiceCheckOrderOfVehicle(getPolicyType(), state);
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
	 * @name Check dxp service To add vehicle.
	 * Create a Policy
	 * Create a pended endorsement
	 * Hit "add-vehicle" service.
	 * send purchase date and VIN to the service as a request
	 * Go to pas open pended endorsement and go to vehicle tab
	 * Check the new vehicle is added with the vin number.
	 * @scenario
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-7082", "PAS-7145", "PAS-11621", "PAS-25265", "PAS-15401"})
	public void pas7082_AddVehicle(@Optional("CA") String state) {

		pas7082_AddVehicle(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite, Maris Strazds
	 * @name Check Duplicate VINS and the Add Vehicle Service
	 * @scenario 1. Create policy with Two vehicles.
	 * 2. Create endorsement outside of PAS.
	 * 3. Try Add Vehicle with the same VIN witch already exist in the policy.
	 * 4. Check Error about duplicate VIN.
	 * 5. Add new vehicle with new VIN. Check the status.
	 * 6. Try add the same vehicle one more time.
	 * 7. Check if error is displaying.
	 * Start PAS-11005
	 * 8. Try add to expensive vehicle.
	 * 9. Check if error is displaying.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-502", "PAS-11005", "PAS-25265", "PAS-15401"})
	public void pas502_DuplicateVinAddVehicleService(@Optional("CA") String state) {

		pas502_CheckDuplicateVinAddVehicleService(getPolicyType());
	}

	/**
	 * @author Jovita Pukenaite, Maris Strazds
	 * @name Check if only active Vehicles are allowed using DXP
	 * @scenario 1. Create policy with two vehicles.
	 * 2. Check if the same vehicles are displayed in dxp server.
	 * 3. Initiate endorsement, and change VIN for one of the vehicles. Don't bind.
	 * 4. Check if the new vehicle, which wad added during endorsement is not displayed in dxp server.
	 * 5. Bind the endorsement.
	 * 6. Check if new vehicle is displayed, and the old one is not displayed anymore.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-8273", "PAS-7145", "PAS-11622", "PAS-25265", "PAS-15401"})
	public void pas8273_OnlyActiveVehiclesAreAllowed(@Optional("CA") String state) {
		assertSoftly(softly ->
				pas8273_CheckIfOnlyActiveVehiclesAreAllowed(softly, getPolicyType())
		);
	}

	/**
	 * @author Megha Gubbala, Maris Strazds
	 * @name Verify update vehicle service
	 * @scenario 1. Create active policy with one vehicle.
	 * 2. hit view vehicle service.
	 * 3. get OID from view vehicle service.
	 * 4. hit update vehicle service.
	 * 5. verify on Pas ui that vehicle updated with the provided information
	 * 6. hit view vehicle service again to verify vehicle information is updated.
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-9610", "PAS-25267"})
	public void pas9610_UpdateVehicleService(@Optional("CA") String state) {
		pas9610_UpdateVehicleService();
	}

	/**
	 * @author Oleg Stasyuk, Maris Strazds
	 * @name Validation on Update/Rate/Bind for vehicle use = Business, AND also if registeredOwner = false
	 * @scenario 1. Create active policy
	 * 2. Add a vehicle
	 * 3. Update vehicle, set usage = Business
	 * Error expected
	 * 4. Rate the endorsement
	 * Error expected
	 * 5. Bind the endorsement
	 * Error expected
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-7147"})
	public void pas7147_VehicleUpdateBusiness(@Optional("CA") String state) {

//		pas7147_VehicleUpdateBusinessBody(); //TODO-mstrazds: finish when there is story for it
	}

	/**
	 * @author Oleg Stasyuk, Maris Strazds
	 * @name Validation on Update/Rate/Bind for vehicle use = Registered Owner
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle with Ownership info = Leased or Financed
	 * 3.1) check field values in ui, check address is validated
	 * 5) rate
	 * 6) bind
	 * 7) do an endorsement in PAS, rate bind
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-11618", "PAS-25267", "PAS-28718", "PAS-16907"})
	public void pas11618_UpdateVehicleLeasedInfo(@Optional("CA") String state) {
		assertSoftly(softly ->
				pas11618_UpdateVehicleLeasedFinancedInfoBody(softly, "LSD")
		);
	}

	/**
	 * @author Oleg Stasyuk, Maris Strazds
	 * @name Validation on Update/Rate/Bind for vehicle use = Registered Owner
	 * @scenario 1) Create a policy
	 * 2) Start an endorsement
	 * 3) Add a vehicle
	 * 4) update vehicle adding Ownership info = Leased or Financed
	 * 4.1) check field values in ui, check address is validated
	 * 5) rate
	 * 6) bind
	 * 7) do an endorsement in PAS, rate bind
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-11618", "PAS-25267", "PAS-28718", "PAS-16907"})
	public void pas11618_UpdateVehicleFinancedInfo(@Optional("CA") String state) {
		assertSoftly(softly ->
				pas11618_UpdateVehicleLeasedFinancedInfoBody(softly, "FNC")
		);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-13252", "PAS-25267", "PAS-28718", "PAS-16907"})
	public void pas13252_UpdateVehicleGaragingAddressProblem(@Optional("CA") String state) {
		pas13252_UpdateVehicleGaragingAddressProblemBody();
	}


	/**
	 * @author Chaitanya Boyapati
	 * @name Add Vehicle - check Anti-Theft Drop Down Values
	 * @scenario 1. Create policy.
	 * 2. Create endorsement.
	 * 3. Add new vehicle.
	 * 4. Hit MetaData service, check the values there.
	 * 5. Validate the Vehicle type cd attribute Names as "antiTheft", "Distance Oneway", "Odometer Reading", "Declared Annual Miles" and Usage"
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@StateList(states = {Constants.States.CA})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-25263"})
	public void pas25263_addVehicleMetadataCheck(@Optional("CA") String state) {

		assertSoftly(softly -> pas25263_addVehicleMetadataCheckBody(softly));
	}

	private void pas25263_addVehicleMetadataCheckBody(ETCSCoreSoftAssertions softly) {
		mainApp().open();

		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);


		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.VEHICLE.get());

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
		String oid = viewVehicleResponse.vehicleList.get(0).oid;

		AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsementVehiclesMetaData(policyNumber, oid);
		AttributeMetadata metaDataFieldResponseVehTypeCd = getAttributeMetadata(metaDataResponse, "vehTypeCd", true, true, true, null, "String");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Regular")).isEqualTo("Regular");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Antique")).isEqualTo("Antique / Classic");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Motor")).isEqualTo("Motor Home");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Trailer")).isEqualTo("Trailer");
		softly.assertThat(metaDataFieldResponseVehTypeCd.valueRange.get("Camper")).isEqualTo("Camper");

		AttributeMetadata metaDataFieldResponseUsage = getAttributeMetadata(metaDataResponse, "usage", true, true, true, null, "String");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("WC")).isEqualTo("Commute (to/from work and school)");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("FM")).isEqualTo("Farm non-business(on premises)");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("FMB")).isEqualTo("Farm business (farm to market delivery)");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("BU")).isEqualTo("Business (small business non-commercial)");
		softly.assertThat(metaDataFieldResponseUsage.valueRange.get("PL")).isEqualTo("Pleasure (recreational driving only)");

		getAttributeMetadata(metaDataResponse, "vehIdentificationNo", true, true, false, null, "String");
		getAttributeMetadata(metaDataResponse, "modelYear", false, true, true, null, "String");
		getAttributeMetadata(metaDataResponse, "manufacturer", false, true, true, null, "String");
		getAttributeMetadata(metaDataResponse, "model", false, true, true, null, "String");
		getAttributeMetadata(metaDataResponse, "series", false, true, false, null, "String");

		AttributeMetadata metaDataFieldResponseBodyStyle = getAttributeMetadata(metaDataResponse, "bodyStyle", false, true, false, null, "String");
		softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("")).isEqualTo("");
		softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("SPORT VAN")).isEqualTo("SPORT VAN");
		softly.assertThat(metaDataFieldResponseBodyStyle.valueRange.get("OTHER")).isEqualTo("OTHER");

		getAttributeMetadata(metaDataResponse, "salvaged", true, true, false, null, "Boolean");
		getAttributeMetadata(metaDataResponse, "distanceOneWayToWork", true, true, false, null, "Integer");
		getAttributeMetadata(metaDataResponse, "odometerReading", true, true, true, null, "Integer");
		getAttributeMetadata(metaDataResponse, "declaredAnnualMiles", true, true, true, null, "Integer");

		AttributeMetadata metaDataFieldResponseAntiTheft = getAttributeMetadata(metaDataResponse, "antiTheft", false, true, false, null, "String");

		getAttributeMetadata(metaDataResponse, "garagingDifferent", true, true, false, null, "Boolean");
		getAttributeMetadata(metaDataResponse, "garagingAddress.postalCode", true, false, true, null, "String");
		getAttributeMetadata(metaDataResponse, "garagingAddress.addressLine1", true, false, true, null, "String");
		getAttributeMetadata(metaDataResponse, "garagingAddress.addressLine2", true, false, false, null, "String");
		getAttributeMetadata(metaDataResponse, "garagingAddress.city", true, false, true, null, "String");
		getAttributeMetadata(metaDataResponse, "garagingAddress.stateProvCd", true, false, true, null, "String");

		AttributeMetadata metaDataFieldResponseOwnership = getAttributeMetadata(metaDataResponse, "vehicleOwnership.ownership", true, true, false, null, "String");
		softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("")).isEqualTo("");
		softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("OWN")).isEqualTo("Owned");
		softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("FNC")).isEqualTo("Financed");
		softly.assertThat(metaDataFieldResponseOwnership.valueRange.get("LSD")).isEqualTo("Leased");

		getAttributeMetadata(metaDataResponse, "vehicleOwnership.name", false, false, false, null, "String");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.secondName", false, false, false, null, "String");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.postalCode", false, false, false, null, "String");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine1", false, false, false, null, "String");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.addressLine2", false, false, false, null, "String");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.city", false, false, false, null, "String");
		getAttributeMetadata(metaDataResponse, "vehicleOwnership.stateProvCd", false, false, false, null, "String");

		//edit pending endorsement
		vehicleTab.getAssetList().getAsset(IS_GARAGING_DIFFERENT_FROM_RESIDENTAL).setValue("Yes");
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.ZIP_CODE).setValue("23703");
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.ADDRESS_LINE_1).setValue("4112 FORREST HILLS DR");
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.CITY).setValue("PORTSMOUTH");
		vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.STATE).setValue("CA");
		vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE).setValue("Leased");
		vehicleTab.getOwnershipAssetList().getAsset(AutoCaMetaData.VehicleTab.Ownership.FIRST_NAME).setValue("GMAC");
		vehicleTab.saveAndExit();

		AttributeMetadata[] metaDataResponse2 = HelperCommon.viewEndorsementVehiclesMetaData(policyNumber, oid);
		getAttributeMetadata(metaDataResponse2, "garagingDifferent", true, true, false, null, "Boolean");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.postalCode", true, true, true, null, "String");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.addressLine1", true, true, true, null, "String");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.addressLine2", true, true, false, null, "String");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.city", true, true, true, null, "String");
		getAttributeMetadata(metaDataResponse2, "garagingAddress.stateProvCd", true, true, true, null, "String");

		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.name", true, true, false, null, "String");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.secondName", false, true, false, null, "String");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.postalCode", true, true, false, null, "String");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.addressLine1", true, true, false, null, "String");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.addressLine2", true, true, false, null, "String");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.city", true, true, false, null, "String");
		getAttributeMetadata(metaDataResponse2, "vehicleOwnership.stateProvCd", true, true, false, null, "String");
	}

	;


	AttributeMetadata getAttributeMetadata(AttributeMetadata[] metaDataResponse, String fieldName, boolean enabled, boolean visible, boolean required, String maxLength, String attributeType) {
		AttributeMetadata metaDataFieldResponse = Arrays.stream(metaDataResponse).filter(attributeMetadata -> fieldName.equals(attributeMetadata.attributeName)).findFirst().orElse(null);
		assertThat(metaDataFieldResponse.enabled).isEqualTo(enabled);
		assertThat(metaDataFieldResponse.visible).isEqualTo(visible);
		assertThat(metaDataFieldResponse.required).isEqualTo(required);
		assertThat(metaDataFieldResponse.maxLength).isEqualTo(maxLength);
		assertThat(metaDataFieldResponse.attributeType).isEqualTo(attributeType);
		return metaDataFieldResponse;
	}
}


