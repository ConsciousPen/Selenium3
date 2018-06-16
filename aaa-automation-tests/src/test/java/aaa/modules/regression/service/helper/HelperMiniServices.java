package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.core.Response;
import org.assertj.core.api.SoftAssertions;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.dtoDxp.AAAEndorseResponse;
import aaa.modules.regression.service.helper.dtoDxp.PolicyPremiumInfo;
import aaa.modules.regression.service.helper.dtoDxp.Vehicle;
import aaa.modules.regression.service.helper.dtoDxp.VehicleUpdateDto;

public class HelperMiniServices extends PolicyBaseTest {

	static void createEndorsementWithCheck(String policyNumber) {
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(response.policyNumber).isEqualTo(policyNumber);
	}

	static String vehicleAddRequestWithCheck(String policyNumber, Vehicle vehicleAddRequest) {
		Vehicle responseAddVehicle = HelperCommon.executeEndorsementAddVehicle(policyNumber, vehicleAddRequest);
		assertThat(responseAddVehicle.oid).isNotEmpty();
		String newVehicleOid = responseAddVehicle.oid;
		printToLog("newVehicleOid: " + newVehicleOid);
		return newVehicleOid;
	}

	static void rateEndorsement(SoftAssertions softly, String policyNumber) {
		PolicyPremiumInfo[] endorsementRateResponse = HelperCommon.endorsementRate(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
		softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
		softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
	}

	static void updateVehicleUsageRegisteredOwner(String policyNumber, String newVehicleOid) {
		//Update Vehicle with proper Usage and Registered Owner
		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.usage = "Pleasure";
		updateVehicleUsageRequest.registeredOwner = true;
		Vehicle updateVehicleUsageResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);
		assertThat(updateVehicleUsageResponse.usage).isEqualTo("Pleasure");
	}

	static void pas14952_checkEndorsementStatusWasReset(String policyNumber, String endorsementStatus) {
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
			HelperCommon.endorsementBind(policyNumber, "e2e", Response.Status.OK.getStatusCode());
			mainApp().open();
			SearchPage.openPolicy(policyNumber);
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement.isEnabled()).isFalse();
			softly.assertThat(endorsementRateResponse[0].premiumType).isEqualTo("GROSS_PREMIUM");
			softly.assertThat(endorsementRateResponse[0].premiumCode).isEqualTo("GWT");
			softly.assertThat(endorsementRateResponse[0].actualAmt).isNotBlank();
		});
	}
}
