package aaa.modules.regression.service.helper;

import static aaa.modules.BaseTest.printToLog;
import java.util.LinkedList;
import java.util.List;
import org.testng.annotations.Test;
import aaa.helpers.config.CustomTestProperties;
import aaa.modules.regression.service.helper.wiremock.dto.WireMockMappingRequest;
import aaa.modules.regression.service.helper.wiremock.factory.PaperlessPreferencesWMRequestFactory;
import toolkit.config.PropertyProvider;

public class HelperWireMock {
	private static final String WIRE_MOCK_URL = PropertyProvider.getProperty(CustomTestProperties.WIRE_MOCK_STUB_URL_TEMPLATE) + "/__admin/mappings";

	/**
	 * SwaggerUI for the WireMock = http://nvdxpas1agl007:9999/__admin/swagger-ui/
	 * Queries to update endpoint to use WireMock stub:
	 select * from propertyconfigurerentity
	 where propertyName = 'policyPreferenceApiService.policyPreferenceApiUri';

	 update propertyconfigurerentity
	 set value = 'http://nvdxpas1agl007:9999/policy/preferences'
	 where propertyName = 'policyPreferenceApiService.policyPreferenceApiUri';

	 Scrum team's dxp stub url: https://master.apps.prod.pdc.digital.csaa-insurance.aaa.com/policy/preferences
	 Scrum team's dxp mock url: https://master.apps.prod.pdc.digital.csaa-insurance.aaa.com/__admin/mappings
	 Scrum team's swagger-ui url: NOT CLEAR TODO add proper URL
	 * @param policyNumber - policy number
	 * @param scenarioJsonFile - paperlessOptInPendingResponse.json or paperlessOptInResponse.json or paperlessOptOutResponse.json
	 * @return
	 */
	public static String setPaperlessPreferencesToValue(String policyNumber, String scenarioJsonFile) {
		WireMockMappingRequest request = PaperlessPreferencesWMRequestFactory.create(policyNumber, scenarioJsonFile);
		String paperlessPreferencesRequestId = request.id;
		printToLog("request id = " + paperlessPreferencesRequestId);
		HelperCommon.runJsonRequestPostDxp(WIRE_MOCK_URL, request, String.class);
		return paperlessPreferencesRequestId;
	}

	public static void deleteProcessedRequestFromStub(String requestId) {
		HelperCommon.runJsonRequestDeleteDxp(WIRE_MOCK_URL + "/" + requestId, String.class);
	}

	public enum PaperlessPreferencesJsonFileEnum {
		PAPERLESS_OPT_IN_PENDING("paperlessOptInPendingResponse.json"),
		PAPERLESS_OPT_IN("paperlessOptInResponse.json"),
		PAPERLESS_OPT_OUT("paperlessOptOutResponse.json");

		final String paperlessPreferencesJsonFileName;

		PaperlessPreferencesJsonFileEnum(String paperlessPreferencesJsonFileName) {
			this.paperlessPreferencesJsonFileName = paperlessPreferencesJsonFileName;
		}

		public String get() {
			return paperlessPreferencesJsonFileName;
		}
	}

	/**
	 * To remove stalled requests
	 */
	@Test
	public void deleteStalledMultiplePaperlessPreferencesRequests() {
		List<String> requestIdList = new LinkedList<>();
		requestIdList.add("7bbfee07-6b31-4afc-a9ce-5ec552dc668b");
		requestIdList.add("7bbfee07-6b31-4afc-a9ce-5ec552dc668b");
		for (Object requestId : requestIdList) {
			deleteProcessedRequestFromStub(requestId.toString());
		}
	}
}
