package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.admin.metadata.product.MoratoriumMetaData;
import aaa.admin.modules.product.IProduct;
import aaa.admin.modules.product.ProductType;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.modules.regression.sales.template.functional.PolicyMoratorium;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;

public class TestMiniServicesMoratoriumHelper extends PolicyMoratorium {

	private final IProduct moratorium = ProductType.MORATORIUM.get();
	private final HelperMiniServices helperMiniServices = new HelperMiniServices();

	protected ErrorDxpEnum.Errors getMoratoriumError() {
		return ErrorDxpEnum.Errors.MORATORIUM_EXIST;
	}

	public void pas21466_GarageAddressUnderTheMoratorium(String state) {
		mockMoratoriumRuleAndRunTest(() -> {

			TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
			String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String endorsementDateBack = TimeSetterUtil.getInstance().getCurrentTime().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			mainApp().open();
			createCustomerIndividual();
			TestData policyTd = getPolicyDefaultTD()
					.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(),
							AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
							DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY))
					.adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), tdError);
			String policyNumber = createPolicy(policyTd);

			checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDate, false);
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Vehicle 1
			VehicleUpdateResponseDto updateResponseVehicle1 = addVehicle(policyNumber, "VehicleWithoutMoratorium");
			checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle1, false);
			checkRateServiceIfMoratoriumErrorExist(policyNumber, false);

			//Vehicle 2
			VehicleUpdateResponseDto updateResponseVehicle2 = addVehicle(policyNumber, "VehicleWithMoratorium");
			checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle2, true);
			checkRateServiceIfMoratoriumErrorExist(policyNumber, true);
			//update address again
			updateVehicle(policyNumber, updateResponseVehicle2.oid, "VehicleWithoutMoratorium");
			checkRateServiceIfMoratoriumErrorExist(policyNumber, false);

			HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

			//TC2
			HelperCommon.createEndorsement(policyNumber, endorsementDateBack);
			//Add vehicle: different Garage address = true
			VehicleUpdateResponseDto updateResponseVehicle3 = addVehicle(policyNumber, "VehicleWithMoratorium");
			checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle3, false);
			checkRateServiceIfMoratoriumErrorExist(policyNumber, false);

			//Add another vehicle, garage address different = false
			VehicleUpdateResponseDto secondVehicleUpdateResponse = addVehicle(policyNumber, "VehicleGaragingSame");
			checkTheErrorAfterGarageAddressWasUpdated(secondVehicleUpdateResponse, false);
			checkRateServiceIfMoratoriumErrorExist(policyNumber, false);

			helperMiniServices.endorsementRateAndBind(policyNumber);

			//Hit start endorsement info service (today)
			checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDate, true);
		}, "MoratoriumDXPConfig");
	}

	public void pas21466_GarageAddressUnderTheMoratoriumPart2(String state) {
		mockMoratoriumRuleAndRunTest(() -> {

			TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
			String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String endorsementDateBack = TimeSetterUtil.getInstance().getCurrentTime().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String endorsementDateFuture = TimeSetterUtil.getInstance().getCurrentTime().plusDays(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			String generalTabSimpleName = CustomerMetaData.GeneralTab.class.getSimpleName();

			TestData customerTd = getCustomerIndividualTD("DataGather", "TestData")
					.adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), "94541")
					.adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), "992 Silverado Rd")
					.adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.STATE.getLabel()), "CA")
					.adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.CITY.getLabel()), "Hayward");

			TestData policyTd = getPolicyDefaultTD()
					.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(),
							AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
							DateTimeUtils.getCurrentDateTime().minusDays(5).format(DateTimeUtils.MM_DD_YYYY))
					.adjust(AutoCaMetaData.ErrorTab.class.getSimpleName(), tdError)
					.adjust(TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.ZIP_CODE.getLabel()), "94541")
					.adjust(TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.ADDRESS_LINE_1.getLabel()), "992 Silverado Rd")
					.adjust(TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.STATE_PROVINCE.getLabel()), "CA")
					.adjust(TestData.makeKeyPath(AutoCaMetaData.PrefillTab.class.getSimpleName(), AutoCaMetaData.PrefillTab.CITY.getLabel()), "Hayward");

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
			VehicleUpdateResponseDto updateResponseVehicle = updateVehicle(policyNumber, vehicle1.oid, "VehicleWithoutMoratorium");
			checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle, false);

			helperMiniServices.endorsementRateAndBind(policyNumber);

			//error should not exist after update
			checkMoratoriumInfoInStartEndorsementInfo(policyNumber, endorsementDate, false);

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Add another vehicle, garage address different = false
			VehicleUpdateResponseDto secondVehicleUpdateResponse = addVehicle(policyNumber, "VehicleGaragingSame");
			checkTheErrorAfterGarageAddressWasUpdated(secondVehicleUpdateResponse, true);
			checkRateServiceIfMoratoriumErrorExist(policyNumber, true);

			//update second vehicle garage address (CA not under the moratorium)
			VehicleUpdateResponseDto updateResponseVehicle2 = updateVehicle(policyNumber, secondVehicleUpdateResponse.oid, "VehicleWithoutMoratorium");
			checkTheErrorAfterGarageAddressWasUpdated(updateResponseVehicle2, false);
			checkRateServiceIfMoratoriumErrorExist(policyNumber, false);

			helperMiniServices.endorsementRateAndBind(policyNumber);
		}, "MoratoriumDXPConfig");
	}

	private Vehicle findVehicle(ViewVehicleResponse viewVehicleResponse, String vin) {
		Vehicle vehicle = viewVehicleResponse.vehicleList.stream().filter(veh -> vin.equals(veh.vehIdentificationNo)).findFirst().orElse(null);
		assertThat(vehicle).isNotNull();
		return vehicle;
	}

	private void checkTheErrorAfterGarageAddressWasUpdated(VehicleUpdateResponseDto updateResponse, boolean isErrorShouldExist) {
		assertSoftly(softly -> {
			if (isErrorShouldExist) {
				softly.assertThat(updateResponse.validations.get(0).message).startsWith(getMoratoriumError().getMessage());
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
				softly.assertThat(endorsementInfoResponse.ruleSets.get(0).errors.stream().anyMatch(err -> err.message.startsWith(getMoratoriumError().getMessage()))).isTrue();
			});
		} else {
			ValidateEndorsementResponse response = HelperCommon.startEndorsement(policyNumber, endorsementDate);
			assertSoftly(softly -> {
				softly.assertThat(response.ruleSets.get(0).name).isEqualTo("PolicyRules");
				softly.assertThat(response.ruleSets.get(0).errors).isEmpty();
			});
		}
	}

	private void checkRateServiceIfMoratoriumErrorExist(String policyNumber, boolean isMoratoriumErrorShouldExist) {
		if (isMoratoriumErrorShouldExist) {
			ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
			assertSoftly(softly -> {
				softly.assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
				softly.assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
				softly.assertThat(hasError(rateResponse, getMoratoriumError())).isTrue();
			});
		} else {
			helperMiniServices.rateEndorsementWithCheck(policyNumber);
		}
	}

	private boolean hasError(ErrorResponseDto errorResponseDto, ErrorDxpEnum.Errors expectedError) {
		return errorResponseDto.errors.stream()
				.anyMatch(error -> org.apache.commons.lang.StringUtils.startsWith(error.message, expectedError.getMessage()));
	}

	private void mockMoratoriumRuleAndRunTest(Runnable testCase, String moratoriumRuleTd) {
		List<MoratoriumRule> mockedRules = new ArrayList<>();
		try {
			TestData td = getTestSpecificTD(moratoriumRuleTd);
			MoratoriumRule moratoriumRule = getMoratoriumRule(td);
			//Step 1 -- Zip code entry needs to be added to the AAAMoratoriumGeographyLocationInfo lookup in order to be able to select it when creating moratorium in Step 2.
			log.info("Step 1: Add Zip Code entry in lookupvalue table if not exists.");
			DBService.get().executeUpdate(insertLookupEntry(moratoriumRule.getZipCode(), moratoriumRule.getCity(), parseState(moratoriumRule.getState())));
			//Step 2
			log.info("Step 2: Set Soft Stop moratorium on Premium Calculation and Hard Stop moratorium on Bind.");
			adminApp().open();
			moratorium.create(td.adjust(TestData.makeKeyPath("AddMoratoriumTab", MoratoriumMetaData.AddMoratoriumTab.MORATORIUM_NAME.getLabel()), moratoriumRule.getName())
					.adjust(TestData.makeKeyPath("AddMoratoriumTab", "AddRuleSection", MoratoriumMetaData.AddMoratoriumTab.AddRuleSection.DISPLAY_MESSAGE.getLabel()), moratoriumRule.getDisplayMessage()));
			mockedRules.add(moratoriumRule);
			testCase.run();
		} finally {
			mockedRules.forEach(rule -> expireMoratorium(rule.getName()));
		}
	}

	private String parseState(String state) {
		return StringUtils.split(state, "-")[1].trim();
	}

	private VehicleUpdateResponseDto addVehicle(String policyNumber, String vehicleTd) {
		TestData td = getTestSpecificTD(vehicleTd);
		String purchaseDate = "2019-01-01";
		String vin = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.VIN.getLabel());
		String addressLine1 = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.ADDRESS_LINE_1.getLabel());
		String city = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.CITY.getLabel());
		String state = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.STATE.getLabel());
		String zipCode = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.ZIP_CODE.getLabel());
		String odometerReading = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel());
		String milesOneWayToWork = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel());

		Vehicle addVehicleRequest = DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate);
		VehicleUpdateDto updateVehicleRequest = DXPRequestFactory.createUpdateVehicleRequest(
				null, true, addressLine1, city, zipCode, state);
		updateVehicleRequest.odometerReading = odometerReading;
		updateVehicleRequest.distanceOneWayToWork = milesOneWayToWork;

		Vehicle responseAddVehicle = HelperCommon.addVehicle(policyNumber, addVehicleRequest, Vehicle.class, 201);
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, responseAddVehicle.oid);
		return HelperCommon.updateVehicle(policyNumber, responseAddVehicle.oid, updateVehicleRequest);
	}

	private VehicleUpdateResponseDto updateVehicle(String policyNumber, String oid, String vehicleTd) {
		TestData td = getTestSpecificTD(vehicleTd);
		String addressLine1 = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.ADDRESS_LINE_1.getLabel());
		String city = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.CITY.getLabel());
		String state = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.STATE.getLabel());
		String zipCode = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.ZIP_CODE.getLabel());
		String odometerReading = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel());
		String milesOneWayToWork = td.getTestData(AutoCaMetaData.VehicleTab.class.getSimpleName()).getValue(AutoCaMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel());

		VehicleUpdateDto updateVehicleRequest = DXPRequestFactory.createUpdateVehicleRequest(
				null, true, addressLine1, city, zipCode, state);
		updateVehicleRequest.odometerReading = odometerReading;
		updateVehicleRequest.distanceOneWayToWork = milesOneWayToWork;
		return HelperCommon.updateVehicle(policyNumber, oid, updateVehicleRequest);
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
}
