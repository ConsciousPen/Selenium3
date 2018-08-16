package aaa.modules.regression.service.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.core.Response;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.dtoDxp.*;

public class HelperMiniServices extends PolicyBaseTest {

	void createEndorsementWithCheck(String policyNumber) {
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
		assertThat(response.policyStatus).isNotEmpty();
		assertThat(response.timedPolicyStatus).isNotEmpty();
		assertThat(response.effectiveDate).isNotEmpty();
		assertThat(response.expirationDate).isNotEmpty();
		assertThat(response.sourceOfBusiness).isNotEmpty();
		assertThat(response.renewalCycle).isGreaterThanOrEqualTo(0);
		assertThat(response.actualAmt).isNotEmpty();
		assertThat(response.termPremium).isNotEmpty();
		assertThat(response.residentialAddress.addressLine1).isNotEmpty();
		assertThat(response.residentialAddress.addressLine2).isNotBlank();
		assertThat(response.residentialAddress.city).isNotEmpty();
		assertThat(response.residentialAddress.stateProvCd).isNotEmpty();
		assertThat(response.residentialAddress.postalCode).isNotEmpty();
		assertThat(response.transactionEffectiveDate).isEqualTo(endorsementDate);
	}

	String vehicleAddRequestWithCheck(String policyNumber, Vehicle vehicleAddRequest) {
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, vehicleAddRequest);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		return newVehicleOid;
	}

	void updateVehicleUsageRegisteredOwner(String policyNumber, String newVehicleOid) {
		printToLog("Update vehicle usage registered owner params: policyNumber: " + policyNumber + ", newVehicleOid: " + newVehicleOid);
		//Update Vehicle with proper Usage and Registered Owner
		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.usage = "Pleasure";
		updateVehicleUsageRequest.registeredOwner = true;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");
	}

	void pas14952_checkEndorsementStatusWasReset(String policyNumber, String endorsementStatus) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		//BUG	PAS-14952 	Lets set coverages to Zero when needed
		assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status")).hasValue(endorsementStatus);
	}

	void endorsementRateAndBind(String policyNumber) {
		assertSoftly(softly -> {
			//Rate endorsement
			PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
			softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();

			//Bind endorsement
			bindEndorsementWithCheck(policyNumber);
			softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
		});
	}

	void rateEndorsementWithCheck(String policyNumber) {
		PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
		assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
		assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
	}

	void rateEndorsementWithErrorCheck(String policyNumber, String errorCode, String errorMessage, String field) {
		ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
		assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		assertThat(rateResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		ErrorResponseDto rateResponseFiltered = rateResponse.errors.stream().filter(errors -> errorCode.equals(errors.errorCode)).findFirst().orElse(null);
		assertThat(rateResponseFiltered.message).contains(errorMessage);
		assertThat(rateResponseFiltered.field).isEqualTo(field);
	}

	void bindEndorsementWithCheck(String policyNumber) {
		HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	void bindEndorsementWithErrorCheck(String policyNumber, String errorCode, String errorMessage, String field) {
		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, errorCode, 422);
		assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		ErrorResponseDto bindResponseFiltered = bindResponse.errors.stream().filter(errors -> errorCode.equals(errors.errorCode)).findFirst().orElse(null);
		assertThat(bindResponseFiltered.message).contains(errorMessage);
		assertThat(bindResponseFiltered.field).isEqualTo(field);
	}

	void orderReportErrors(String policyNumber, String driverOid,String errorCode, String errorMessage, String field, boolean isErrorShouldExist) {
		ErrorResponseDto orderReportErrorResponse = HelperCommon.orderReports(policyNumber, driverOid, ErrorResponseDto.class, 422);
		assertThat(orderReportErrorResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		assertThat(orderReportErrorResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		boolean errorExists = orderReportErrorResponse.errors.stream()
				.anyMatch(errors -> errorCode.contains(errors.errorCode) && errors.message.startsWith(errorMessage) && field.equals(errors.field));
		assertThat(errorExists).isEqualTo(isErrorShouldExist);
	}

}
