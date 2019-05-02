package aaa.modules.regression.service.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.verification.ETCSCoreSoftAssertions;

public class HelperMiniServices extends PolicyBaseTest {

	public void createEndorsementWithCheck(String policyNumber) {
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
		assertThat(response.policyTerm).isNotEmpty();
		assertThat(response.endorsementId).isNotEmpty();
		assertThat(response.productCd).isNotEmpty();
	}

	public String addVehicleWithChecks(String policyNumber, String purchaseDate, String vin, boolean allowedToAddVehicle) {
		//Add new vehicle
		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, DXPRequestFactory.createAddVehicleRequest(vin, purchaseDate), Vehicle.class, 201);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);

		//Update Vehicle with proper Usage and Registered Owner
		updateVehicleUsageRegisteredOwner(policyNumber, newVehicleOid);

		ViewVehicleResponse viewEndorsementVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		assertThat(viewEndorsementVehicleResponse.canAddVehicle).isEqualTo(allowedToAddVehicle);
		Vehicle newVehicle = viewEndorsementVehicleResponse.vehicleList.stream().filter(veh -> newVehicleOid.equals(veh.oid)).findFirst().orElse(null);
		assertThat(newVehicle.vehIdentificationNo).isEqualTo(vin);
		return newVehicleOid;
	}

	String vehicleAddRequestWithCheck(String policyNumber, Vehicle vehicleAddRequest) {
		Vehicle responseAddVehicle =
				HelperCommon.addVehicle(policyNumber, vehicleAddRequest, Vehicle.class, 201);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		return newVehicleOid;
	}

	public VehicleUpdateResponseDto updateVehicleUsageRegisteredOwner(String policyNumber, String newVehicleOid) {
		printToLog("Update vehicle usage registered owner params: policyNumber: " + policyNumber + ", newVehicleOid: " + newVehicleOid);
		//Update Vehicle with proper Usage and Registered Owner
		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		if (getState().equals(Constants.States.CA)) {
			updateVehicleUsageRequest.usage = "WC";
		} else {
			updateVehicleUsageRequest.usage = "Pleasure";
		}

		updateVehicleUsageRequest.registeredOwner = true;
		VehicleUpdateResponseDto updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);
		if (getState().equals(Constants.States.CA)) {
			assertThat(updateVehicleUsageResponse.usage).isEqualTo("WC");
		} else {
			assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");
		}

		return updateVehicleUsageResponse;
	}

	void pas14952_checkEndorsementStatusWasReset(String policyNumber, String endorsementStatus) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		//BUG	PAS-14952 	Lets set coverages to Zero when needed
		assertThat(PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status")).hasValue(endorsementStatus);
	}

	public void endorsementRateAndBind(String policyNumber) {
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

	public void rateEndorsementWithCheck(String policyNumber) {
		PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
		assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
		assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
	}

	void rateEndorsementWithErrorCheck(String policyNumber, String errorCode, String errorMessage) {
		ErrorResponseDto rateResponse = HelperCommon.endorsementRateError(policyNumber);
		assertThat(rateResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		assertThat(rateResponse.message).startsWith(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		ErrorResponseDto rateResponseFiltered = rateResponse.errors.stream().filter(errors -> errorCode.equals(errors.errorCode)).findFirst().orElse(null);
		assertThat(rateResponseFiltered.message).startsWith(errorMessage);
	}

	public void bindEndorsementWithCheck(String policyNumber) {
		//When Binding, Sign all Required To Bind (RFI) documents if they exist
		RFIDocuments rfiServiceResponse = HelperCommon.rfiViewService(policyNumber, false);//TODO-mstrazds: change generateDocs to true when devs finish teck story for lookup update in Sprint 48
		List<String> listOfDocIDs = rfiServiceResponse.documents.stream().map(doc -> doc.documentId).collect(Collectors.toList());
		PolicySummary bindResponse = HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode(), listOfDocIDs);

		assertThat(bindResponse.bindDate).isNotEmpty();
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}

	public void bindEndorsementWithErrorCheck(String policyNumber, String errorCode, String errorMessage) {
		ErrorResponseDto bindResponse = HelperCommon.endorsementBindError(policyNumber, errorCode, 422);
		assertThat(bindResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		assertThat(bindResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		ErrorResponseDto bindResponseFiltered = bindResponse.errors.stream().filter(errors -> errorCode.equals(errors.errorCode)).findFirst().orElse(null);
		assertThat(bindResponseFiltered.message).contains(errorMessage);
	}

	public OrderReportsResponse orderReportErrors(String policyNumber, String driverOid, ErrorDxpEnum.Errors... errors) {
		return orderReportErrors(policyNumber, driverOid, true, errors);
	}

	public OrderReportsResponse orderReportErrors(String policyNumber, String driverOid, boolean errorExistsCheck, ErrorDxpEnum.Errors... errors) {
		OrderReportsResponse orderReportErrorResponse = HelperCommon.orderReports(policyNumber, driverOid, OrderReportsResponse.class, 200);
		for(ErrorDxpEnum.Errors error : errors) {
			if(errorExistsCheck) {
				assertThat(orderReportErrorResponse.validations.stream()
						.anyMatch(valError ->  valError.message.contains(error.getMessage()))).isTrue();
				assertThat(orderReportErrorResponse.validations.stream()
						.anyMatch(valError ->  valError.errorCode.equals(error.getCode()))).isTrue();
			} else {
				assertThat(orderReportErrorResponse.validations.stream()
						.noneMatch(valError -> valError.message.contains(error.getMessage()))).isTrue();
				assertThat(orderReportErrorResponse.validations.stream()
						.noneMatch(valError ->  valError.errorCode.equals(error.getCode()))).isTrue();
			}
		}
		if(errors.length == 0) {
			assertThat(orderReportErrorResponse.validations).isEmpty();
		}
		return orderReportErrorResponse;
	}

	boolean hasError(ErrorResponseDto errorResponseDto, ErrorDxpEnum.Errors expectedError) {
		assertThat(errorResponseDto.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		assertThat(errorResponseDto.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		return errorResponseDto.errors.stream().anyMatch(error -> expectedError.getCode().equals(error.errorCode)
						&& StringUtils.startsWith(error.message, expectedError.getMessage()));
	}

	void validateUniqueVinError(ErrorResponseDto errorResponse, ETCSCoreSoftAssertions softly) {
		softly.assertThat(errorResponse.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
		softly.assertThat(errorResponse.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
		softly.assertThat(errorResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.UNIQUE_VIN.getCode());
		softly.assertThat(errorResponse.errors.get(0).message).contains(ErrorDxpEnum.Errors.UNIQUE_VIN.getMessage());
	}
}
