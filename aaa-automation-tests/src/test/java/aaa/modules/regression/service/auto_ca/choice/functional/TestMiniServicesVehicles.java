package aaa.modules.regression.service.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.TestMiniServicesVehiclesHelper;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static aaa.main.metadata.policy.AutoCaMetaData.VehicleTab.*;
import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;


public class TestMiniServicesVehicles extends TestMiniServicesVehiclesHelper{

    private aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab vehicleTab = new VehicleTab();

    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
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


        viewVehicleResponse = HelperCommon.viewPolicyVehicles(policyNumber);
        oid = viewVehicleResponse.vehicleList.get(0).oid;
        String vin="4S2CK58W8X4307498";
        HelperCommon.addVehicle(policyNumber,DXPRequestFactory.createAddVehicleRequest(vin, "2019-01-01"), aaa.helpers.rest.dtoDxp.Vehicle.class,201);


        String newVin = "4S2CK58W8X4307498";
        Vehicle responseAddVehicle = HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest("4S2CK58W8X4307498", "2012-02-21"), Vehicle.class, 201);
        assertThat(responseAddVehicle.vehIdentificationNo).isEqualTo(newVin);

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
        vehicleTab.getAssetList().getAsset(ZIP_CODE).setValue("23703");
        vehicleTab.getAssetList().getAsset(ADDRESS_LINE_1).setValue("4112 FORREST HILLS DR");
        vehicleTab.getAssetList().getAsset(CITY).setValue("PORTSMOUTH");
        vehicleTab.getAssetList().getAsset(STATE).setValue("CA");
        vehicleTab.getOwnershipAssetList().getAsset(Ownership.OWNERSHIP_TYPE).setValue("Leased");
        vehicleTab.getOwnershipAssetList().getAsset(Ownership.FIRST_NAME).setValue("GMAC");
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
    };


    AttributeMetadata getAttributeMetadata (AttributeMetadata[]metaDataResponse, String fieldName, boolean enabled, boolean visible, boolean required, String maxLength, String attributeType){
        AttributeMetadata metaDataFieldResponse = Arrays.stream(metaDataResponse).filter(attributeMetadata -> fieldName.equals(attributeMetadata.attributeName)).findFirst().orElse(null);
        assertThat(metaDataFieldResponse.enabled).isEqualTo(enabled);
        assertThat(metaDataFieldResponse.visible).isEqualTo(visible);
        assertThat(metaDataFieldResponse.required).isEqualTo(required);
        assertThat(metaDataFieldResponse.maxLength).isEqualTo(maxLength);
        assertThat(metaDataFieldResponse.attributeType).isEqualTo(attributeType);
        return metaDataFieldResponse;
    }
}















