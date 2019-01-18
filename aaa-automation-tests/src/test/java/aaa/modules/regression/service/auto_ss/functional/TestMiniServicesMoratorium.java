package aaa.modules.regression.service.auto_ss.functional;

import aaa.admin.metadata.product.MoratoriumMetaData;
import aaa.admin.modules.product.IProduct;
import aaa.admin.modules.product.ProductType;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.regression.sales.template.functional.PolicyMoratorium;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.HelperMiniServices;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

import javax.ws.rs.core.Response;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestMiniServicesMoratorium extends PolicyMoratorium {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    private final IProduct moratorium = ProductType.MORATORIUM.get();
    public HelperMiniServices helperMiniServices = new HelperMiniServices();

    String purchaseDate1 = "2005-02-22";
    String vin1 = "3FAFP31341R200709";

    String purchaseDate2 = "2010-02-11";
    String vin2 = "1FTYR14U32PA42653";

    String purchaseDate3 = "2014-01-01";
    String vin3 = "1HGCD5603VA139404";

    VehicleUpdateDto updateVehReqUnderTheMoratorium_VA = DXPRequestFactory.createUpdateVehicleRequest("Pleasure", true, "Valhalla Square", "Ashburn", "20147", "VA");
    VehicleUpdateDto updateVehReqNotUnderTheMoratorium_VA = DXPRequestFactory.createUpdateVehicleRequest("Pleasure", true, "501 E Broad St", "Richmond", "23219", "VA");
    VehicleUpdateDto updateVehReqNotUnderTheMoratorium_AZ = DXPRequestFactory.createUpdateVehicleRequest("Pleasure", true, "24250 N 23rd Ave", "Phoenix", "85085", "AZ");
    VehicleUpdateDto updateVehReqUnderTheMoratorium_CA = DXPRequestFactory.createUpdateVehicleRequest("Pleasure", true, "992 Silverado Rd", "Hayward", "94541", "CA");
    VehicleUpdateDto updateVehReqNotUnderTheMoratorium_CA = DXPRequestFactory.createUpdateVehicleRequest("Pleasure", true, "2999 W 6th St", "Los Angeles", "90020", "CA");

    /**
     * @author Jovita Pukenaite
     * @name Moratoriums - Garaging Address on the Policy/Adding/Updating a Vehicle
     * @scenario1 1. Create backdate policy (-2d).
     * 2. Hit start endorsement info service. Check the response.
     * 3. Create endorsement outside of PAS.
     * 4. Add and update vehicle with different garage address (20147 zip)
     * 5. Add another vehicle, but zip code should be under the moratorium. Check the response.
     * 6. Rate.
     * 7. Create new endorsement, add vehicle with CA Garage address that is under the moratorium.
     * 8. Check response after update and rate services.
     * 9. Update vehicle Garage address to the CA but not under the moratorium.
     * 10. Check the response after update and after rate service
     * 11. Delete endorsement.
     *
     * @scenario2 1. Create new endorsement (Backdate -2d)
     * 2. Add vehicle, garage address not different.
     * 3. Add another vehicle, garage address which gonna be under the moratorium after two days.
     * 4. Check the response, if error is not displaying.
     * 5. Rate and bind endorsement.
     * 6. Hit start endorsement info service (today). Check the response.
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21466"})
    @StateList(states = {Constants.States.VA, Constants.States.AZ})
    public void pas21466_GarageAddressUnderTheMoratorium(@Optional("VA") String state) {
        String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endorsementDateBack = TimeSetterUtil.getInstance().getCurrentTime().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        TestData td_CA = getTestSpecificTD("TestData_Moratorium_DXP_Config_1");
        MoratoriumRule moratoriumRule = getMoratoriumRule(td_CA);
        TestData td_VA = getTestSpecificTD("TestData_Moratorium_DXP_Config_2");
        MoratoriumRule moratoriumRule2 = getMoratoriumRule(td_VA);

        mockMoratoriumRuleAndRunTest(td_CA, moratoriumRule);
        mockMoratoriumRuleAndRunTest(td_VA, moratoriumRule2);

        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy(getBackDatedPolicyTD());

        checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDate, false);
        helperMiniServices.createEndorsementWithCheck(policyNumber);

        //Vehicle 1
        Vehicle responseAddVehicle = HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate1), Vehicle.class, 201);
        VehicleUpdateResponseDto updateResponseVehicle1 = HelperCommon.updateVehicle(policyNumber, responseAddVehicle.oid, updateVehReqNotUnderTheMoratorium_VA);
        checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle1, false);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, false, false);

        //Vehicle 2
        Vehicle responseAddVehicle2 = HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin2, purchaseDate2), Vehicle.class, 201);
        VehicleUpdateResponseDto updateResponseVehicle2 = HelperCommon.updateVehicle(policyNumber, responseAddVehicle2.oid, updateVehReqUnderTheMoratorium_VA);
        checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle2, true);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, true, false);
        //update address again
        HelperCommon.updateVehicle(policyNumber, responseAddVehicle2.oid, updateVehReqNotUnderTheMoratorium_VA);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, false, false);

        //Vehicle 3
        Vehicle responseAddVehicle3 = HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin3, purchaseDate3), Vehicle.class, 201);
        VehicleUpdateResponseDto updateResponseVehicle3 = HelperCommon.updateVehicle(policyNumber, responseAddVehicle3.oid, updateVehReqUnderTheMoratorium_CA);
        checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle3, true);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, true, false);

        //update address again
        VehicleUpdateResponseDto updateResponseVehicle4 = HelperCommon.updateVehicle(policyNumber, responseAddVehicle3.oid, updateVehReqNotUnderTheMoratorium_CA);
        checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle4, false);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, false, true);

        HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

        //TC2
        HelperCommon.createEndorsement(policyNumber, endorsementDateBack);
        //Add vehicle: different Garage address = true
        Vehicle responseAddVehicle5 = HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin1, purchaseDate1), Vehicle.class, 201);
        VehicleUpdateResponseDto updateResponseVehicle5 = HelperCommon.updateVehicle(policyNumber, responseAddVehicle5.oid, updateVehReqUnderTheMoratorium_VA);
        checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle5, false);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, false, false);

        //Add another vehicle, garage address different = false
        VehicleUpdateResponseDto secondVehicleUpdateResponse = addVehicle(policyNumber, purchaseDate2, vin2);
        checkTheErrorAfterGarageAddressWasUpdated(secondVehicleUpdateResponse, false);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, false, false);

        helperMiniServices.endorsementRateAndBind(policyNumber);

        //Hit start endorsement info service (today)
        checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDate, true);

        expireMoratorium(moratoriumRule.getName());
        expireMoratorium(moratoriumRule2.getName());
    }

    /**
     * @author Jovita Pukenaite
     * @name Moratoriums - Garaging Address on the Policy/Adding/Updating a Vehicle
     * @scenario1 1. Create AZ backdate policy (-5d). Residential address should be under the moratorium.
     * 2. Hit start endorsement info service. Check the response. (-5d)
     * and check for today date, when moratorium rule should exist, and future date.
     * 3. Create endorsement outside of PAS (-5d).
     * 4. Update existing vehicle garage address (not under moratorium)
	 * 5. Bind and rate endorsement.
	 * 6. Create new endorsement outside of PAS (today).
     * 7. Add and update vehicle with different garage address, CA - under the moratorium.
     * 8. Check the response. And try rate.
	 * 9. Update new vehicle again, with CA address not under the moratorium.
	 * 10. Check the response. Rate and Bind.
	 * 11. Update new vehicle again, with AZ address not under the moratorium.
	 * 12. Rate and Bind.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @StateList(states = {Constants.States.VA, Constants.States.AZ})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-21466"})
    public void pas21466_GarageAddressUnderTheMoratoriumPart2(@Optional("AZ") String state) {
        String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endorsementDateBack = TimeSetterUtil.getInstance().getCurrentTime().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endorsementDateFuture = TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        TestData td_CA = getTestSpecificTD("TestData_Moratorium_DXP_Config_1");
        MoratoriumRule moratoriumRule = getMoratoriumRule(td_CA);
        TestData td_AZ = getTestSpecificTD("TestData_Moratorium_DXP_Config_3");
        MoratoriumRule moratoriumRule3 = getMoratoriumRule(td_AZ);
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");

        mockMoratoriumRuleAndRunTest(td_CA, moratoriumRule);
        mockMoratoriumRuleAndRunTest(td_AZ, moratoriumRule3);

		String generalTabSimpleName = CustomerMetaData.GeneralTab.class.getSimpleName();

		TestData customerTd = (getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), "85003")
				.adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), "805 N 4rd Avenue"))
				.adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.STATE.getLabel()), "AZ")
				.adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.CITY.getLabel()), "Phoenix");

		TestData policyTd = getPolicyDefaultTD()
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(),
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
						DateTimeUtils.getCurrentDateTime().minusDays(5).format(DateTimeUtils.MM_DD_YYYY))
				.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError);

        mainApp().open();
        createCustomerIndividual(customerTd);
        String policyNumber = createPolicy(policyTd);

        checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDateBack, false);
        checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDate, true);
        checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDateFuture, false);

        HelperCommon.createEndorsement(policyNumber, endorsementDateBack);

        //get vehicles oid's and update garage address to existing vehicle
        ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
        Vehicle vehicle1 = findVehicle(viewEndorsementVehicleResponse, policyTd.getTestDataList("VehicleTab").get(0).getValue("VIN"));
        VehicleUpdateResponseDto updateResponseVehicle = HelperCommon.updateVehicle(policyNumber, vehicle1.oid, updateVehReqNotUnderTheMoratorium_AZ);
        checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle, false);

        helperMiniServices.endorsementRateAndBind(policyNumber);

        //error should not exist after update
        checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDate, false);

        helperMiniServices.createEndorsementWithCheck(policyNumber);

        //Add another vehicle, garage address different = false
        VehicleUpdateResponseDto secondVehicleUpdateResponse = addVehicle(policyNumber, purchaseDate1, vin1);
        checkTheErrorAfterGarageAddressWasUpdated(secondVehicleUpdateResponse, true);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, true, false);

        //update second vehicle garage address (CA under the moratorium)
        VehicleUpdateResponseDto updateResponseVehicle3 = HelperCommon.updateVehicle(policyNumber, secondVehicleUpdateResponse.oid, updateVehReqUnderTheMoratorium_CA);
        checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle3, true);
        checkRateServiceIfMoratoriumErrorExist(policyNumber, true, false);

		//update second vehicle garage address (CA not under the moratorium)
		VehicleUpdateResponseDto updateResponseVehicle4 = HelperCommon.updateVehicle(policyNumber, secondVehicleUpdateResponse.oid, updateVehReqNotUnderTheMoratorium_CA);
		checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle4, false);
		checkRateServiceIfMoratoriumErrorExist(policyNumber, false, true);

		//update second vehicle garage address (CA not under the moratorium)
		VehicleUpdateResponseDto updateResponseVehicle5 = HelperCommon.updateVehicle(policyNumber, secondVehicleUpdateResponse.oid, updateVehReqNotUnderTheMoratorium_AZ);
		checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle5, false);
		checkRateServiceIfMoratoriumErrorExist(policyNumber, false, false);
		helperMiniServices.endorsementRateAndBind(policyNumber);

        expireMoratorium(moratoriumRule.getName());
        expireMoratorium(moratoriumRule3.getName());
    }

    private Vehicle findVehicle(ViewVehicleResponse viewVehicleResponse, String vin) {
        Vehicle vehicle = viewVehicleResponse.vehicleList.stream().filter(veh -> vin.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
        assertThat(vehicle).isNotNull();
        return vehicle;
    }

    private void checkTheErrorAfterGarageAddressWasUpdated(VehicleUpdateResponseDto updateResponse, boolean isErrorShouldExist) {
        assertSoftly(softly -> {
            if (isErrorShouldExist) {
                softly.assertThat(updateResponse.validations.get(0).errorCode).startsWith(ErrorDxpEnum.Errors.MORATORIUM_EXIST.getCode());
                softly.assertThat(updateResponse.validations.get(0).message).startsWith(ErrorDxpEnum.Errors.MORATORIUM_EXIST.getMessage());
            } else {
                softly.assertThat(updateResponse.validations).isEmpty();
            }
        });
    }

    private void checkMoratoriumInfoInStartEndorsementInfo(String policyNumber, String endorsementDate, boolean isErrorShouldExist) {
        if (isErrorShouldExist) {
            ValidateEndorsementResponse endorsementInfoResponse = HelperCommon.startEndorsement(policyNumber, endorsementDate);
            assertSoftly(softly -> {
                softly.assertThat(endorsementInfoResponse.ruleSets.get(0).name).isEqualTo("PolicyRules");
                softly.assertThat(endorsementInfoResponse.ruleSets.get(0).errors.stream().anyMatch(err -> err.message.contains(ErrorDxpEnum.Errors.MORATORIUM_EXIST.getMessage()))).isTrue();
            });
        } else {
            ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
            assertSoftly(softly -> {
                softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
                softly.assertThat(response.ruleSets.get(0).errors).isEmpty();
            });
        }
    }

    private void checkRateServiceIfMoratoriumErrorExist(String policyNumber, boolean isMoratoriumErrorShouldExist, boolean isOtherErrorShouldExist) {
        if (isMoratoriumErrorShouldExist) {
            ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
            assertSoftly(softly -> {
                softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
                softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
                softly.assertThat(hasError(rateResponse, "attributeForRules", ErrorDxpEnum.Errors.MORATORIUM_EXIST)).isTrue();
            });
        } else if (isOtherErrorShouldExist) {
            ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
            assertSoftly(softly -> {
                softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
                softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
                softly.assertThat(rateResponse.errors).isNotEmpty();
            });
        } else {
            helperMiniServices.rateEndorsementWithCheck(policyNumber);
        }
    }

    private VehicleUpdateResponseDto addVehicle(String policyNumber, String purchaseDate, String vin) {
        Vehicle responseAddVehicle =
                HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
        return helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, responseAddVehicle.oid);

    }

    private boolean hasError(ErrorResponseDto errorResponseDto, String expectedField, ErrorDxpEnum.Errors expectedError) {
        return errorResponseDto.errors.stream().anyMatch(error -> expectedField.equals(error.field)
                && org.apache.commons.lang.StringUtils.startsWith(error.message, expectedError.getMessage()));
    }

    private void mockMoratoriumRuleAndRunTest(TestData td, MoratoriumRule moratoriumRule) {
       //Step 1 -- Zip code entry needs to be added to the AAAMoratoriumGeographyLocationInfo lookup in order to be able to select it when creating moratorium in Step 2.
       log.info("Step 1: Add Zip Code entry in lookupvalue table if not exists.");
       DBService.get().executeUpdate(insertLookupEntry(moratoriumRule.getZipCode(), moratoriumRule.getCity(), parseState(moratoriumRule.getState())));
       //Step 2
       log.info("Step 2: Set Soft Stop moratorium on Premium Calculation and Hard Stop moratorium on Bind.");
       adminApp().open();
       moratorium.create(td.adjust(TestData.makeKeyPath("AddMoratoriumTab", MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel()), moratoriumRule.getName())
            .adjust(TestData.makeKeyPath("AddMoratoriumTab", "AddRuleSection", MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel()), moratoriumRule.getDisplayMessage()));
    }

    private String parseState(String state) {
        return StringUtils.split(state, "-")[1].trim();
    }

    private MoratoriumRule getMoratoriumRule(TestData td) {
        String zipCode = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.ZIP_CODES.getLabel());
        String city = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.CITIES.getLabel());
        String state = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.STATES.getLabel());
        String name = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getValue(MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel());
        String displayMessage = td.getTestData(MoratoriumMetaData.AddMoratoriumTab.class.getSimpleName()).getTestData(MoratoriumMetaData.AddMoratoriumTab.ADD_RULE_SECTION.getLabel())
                .getValue(MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel());
        return new MoratoriumRule(zipCode, city, state, name, displayMessage);
    }

    private class MoratoriumRule {

        private final String zipCode;
        private final String city;
        private final String state;
        private final String name;
        private final String displayMessage;

        private MoratoriumRule(String zipCode, String city, String state, String name, String displayMessage) {
            this.zipCode = zipCode;
            this.city = city;
            this.state = state;
            this.name = name;
            this.displayMessage = displayMessage;
        }

        public String getZipCode() {
            return zipCode;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getName() {
            return name;
        }

        public String getDisplayMessage() {
            return displayMessage;
        }
    }

    @FunctionalInterface
    public interface MoratoriumTestBody {
        void accept(TestData td, MoratoriumRule moratoriumRule, String state);
    }
}
