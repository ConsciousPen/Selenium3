package aaa.modules.regression.service.helper.wiremock.factory;

import java.util.ArrayList;
import java.util.UUID;
import aaa.modules.regression.service.helper.wiremock.dto.BodyFileNameResponse;
import aaa.modules.regression.service.helper.wiremock.dto.BodyPattern;
import aaa.modules.regression.service.helper.wiremock.dto.MappingRequest;
import aaa.modules.regression.service.helper.wiremock.dto.WireMockMappingRequest;

/**
 * WireMock request factory for Paperless Preferences Service.
 */
public class PaperlessPreferencesWMRequestFactory {

    private static final String URL_PATH = "/policy/preferences";
    private static final String METHOD = "POST";
    private static final String EXPRESSION = "$.policyNumber";
    private static final String SCENARIO_LOCATION = "/paperless-preferences/";
    private static final int STATUS = 200;
    private static final String MATCHES_JSON_PATH = "$.[?(@.policyNumber == '%s')]";

    /**
     * Creates WireMock request to prepare specific Paperless Preferences response for given policy.
     * @param policyNumber policy number to mock
     * @param scenarioJsonFileName mocked scenario JSON file name that is prepared on WireMock server.
     */
    public static WireMockMappingRequest create(String policyNumber, String scenarioJsonFileName) {
        WireMockMappingRequest request = new WireMockMappingRequest();
        MappingRequest mappingRequest = new MappingRequest();
        BodyPattern bodyPattern = new BodyPattern();
        bodyPattern.matchesJsonPath = String.format(MATCHES_JSON_PATH, policyNumber);
        mappingRequest.bodyPatterns = new ArrayList<>();
        mappingRequest.bodyPatterns.add(bodyPattern);
        mappingRequest.method = METHOD;
        mappingRequest.urlPath = URL_PATH;
        BodyFileNameResponse response = new BodyFileNameResponse();
        response.status = STATUS;
        response.bodyFileName = SCENARIO_LOCATION + scenarioJsonFileName;
        request.id = UUID.randomUUID().toString();
        request.request = mappingRequest;
        request.response = response;
        request.priority = Integer.MAX_VALUE;
        return request;
    }
}
