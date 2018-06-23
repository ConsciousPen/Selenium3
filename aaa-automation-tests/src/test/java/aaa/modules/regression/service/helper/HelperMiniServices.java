package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.core.Response;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;

public class HelperMiniServices extends PolicyBaseTest {
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();


	void createEndorsementWithCheck(String policyNumber) {
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
	}

	String vehicleAddRequestWithCheck(String policyNumber, Vehicle vehicleAddRequest) {
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, vehicleAddRequest);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		return newVehicleOid;
	}

	void updateVehicleUsageRegisteredOwner(String policyNumber, String newVehicleOid) {
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
		PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").verify.value(endorsementStatus);
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
		ErrorResponseDto bindResponseFiltered = rateResponse.errors.stream().filter(errors -> errorCode.equals(errors.errorCode)).findFirst().orElse(null);
		assertThat(bindResponseFiltered.message).contains(errorMessage);
		assertThat(bindResponseFiltered.field).isEqualTo(field);
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

	void secondEndorsementIssueCheck() {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();

		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();
		assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
	}
}
