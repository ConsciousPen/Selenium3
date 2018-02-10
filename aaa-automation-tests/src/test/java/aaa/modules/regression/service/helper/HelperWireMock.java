package aaa.modules.regression.service.helper;

import static aaa.admin.modules.IAdmin.log;
import aaa.modules.regression.service.helper.wiremock.dto.WireMockMappingRequest;
import aaa.modules.regression.service.helper.wiremock.factory.PaperlessPreferencesWMRequestFactory;


public class HelperWireMock {
	private static final String WIRE_MOCK_URL = "http://nvdxpas1agl007:9999/__admin/mappings";
	/**
	 * SwaggerUI for the WireMock = http://nvdxpas1agl007:9999/__admin/swagger-ui/
	 * Queries to update endpoint to use WireMock stub:
	 select * from propertyconfigurerentity
	 where propertyName = 'policyPreferenceApiService.policyPreferenceApiUri';

	 update propertyconfigurerentity
	 set value = 'http://nvdxpas1agl007:9999/policy/preferences'
	 where propertyName = 'policyPreferenceApiService.policyPreferenceApiUri';

	 * @param policyNumber - policy number
	 * @param scenarioJsonFile - paperlessOptInPendingResponse.json or paperlessOptInResponse.json or paperlessOptOutResponse.json
	 * @return
	 */
	public static String setPaperlessPreferencesToValue(String policyNumber, String scenarioJsonFile){
		WireMockMappingRequest request = PaperlessPreferencesWMRequestFactory.create(policyNumber, scenarioJsonFile);
		String paperlessPreferencesRequestId = request.id;
		log.info("id = {}", paperlessPreferencesRequestId);
		HelperCommon.runJsonRequestPostDxp(WIRE_MOCK_URL, request, String.class);
		HelperCommon.runJsonRequestPostDxp(WIRE_MOCK_URL +"/save", null, String.class);
		HelperCommon.runJsonRequestPostDxp(WIRE_MOCK_URL +"/reset", null, String.class);
		return paperlessPreferencesRequestId;
	}

	public static void deleteProcessedRequestFromStub (String requestId){
		HelperCommon.runJsonRequestDeleteDxp(WIRE_MOCK_URL + "/" + requestId, String.class);
	}
}
