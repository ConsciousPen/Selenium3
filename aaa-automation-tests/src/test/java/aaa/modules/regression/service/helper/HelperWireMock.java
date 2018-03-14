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
	 * SwaggerUI for the WireMock = http://nvdxpas1agl003:9085/wiremock/__admin/swagger-ui/
	 * Queries to update endpoint to use WireMock stub:
	 select * from propertyconfigurerentity
	 where propertyName = 'policyPreferenceApiService.policyPreferenceApiUri';

	 update propertyconfigurerentity
	 set value = 'http://nvdxpas1agl003:9085/wiremock/policy/preferences'
	 where propertyName = 'policyPreferenceApiService.policyPreferenceApiUri';

	 Scrum team's dxp stub url: http://wiremock-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/app-host/policy/preferences
	 Scrum team's dxp mock url: http://wiremock-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/__admin/mappings
	 Scrum team's swagger-ui url: NOT CLEAR TODO add proper URL
	 * @param policyNumber - policy number
	 * @param scenarioJsonFile - paperlessOptInPendingResponse.json or paperlessOptInResponse.json or paperlessOptOutResponse.json
	 * @return
	 */
	public static String setPaperlessPreferencesToValue(String policyNumber, String scenarioJsonFile) {
		WireMockMappingRequest request = PaperlessPreferencesWMRequestFactory.create(policyNumber, scenarioJsonFile);
		String paperlessPreferencesRequestId = request.id;
		printToLog("request id = " + paperlessPreferencesRequestId);
		HelperCommon.runJsonRequestPostDxp(WIRE_MOCK_URL, request, String.class, 201);
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



	@Test
	public void createPaperlessPreferencesRequestIdTest() {
		String requestId = setPaperlessPreferencesToValue("QVASS926232047", HelperWireMock.PaperlessPreferencesJsonFileEnum.PAPERLESS_OPT_IN_PENDING.get());
		printToLog("requestId: " + requestId);
	}

	/**
	 * To remove stalled requests
	 */
	@Test
	public void deleteStalledMultiplePaperlessPreferencesRequests() {
		List<String> requestIdList = new LinkedList<>();
		requestIdList.add("e6cb1877-6b5b-46da-8b33-b7e2cddd8340");
		requestIdList.add("dd606e54-ff91-4f1d-bf33-ec0e4216a483");
		requestIdList.add("c96b4fbb-3900-45e5-b9b8-82ac8d0b0914");
		for (Object requestId : requestIdList) {
			deleteProcessedRequestFromStub(requestId.toString());
		}
	}
}
