package aaa.helpers.rest;

import java.util.List;
import java.util.Map;

import aaa.rest.RESTServiceType;
import aaa.rest.RESTServiceUser;
import aaa.rest.partysearch.PartySearch;
import aaa.rest.partysearch.model.PartyWrapper;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;

public class PartySearchRestHelper {

    private static PartySearch partySearchRSClient = RESTServiceType.PARTY_SEARCH.get();

    public static void assertPartySearchResponse(TestData testData, RESTServiceUser user, Map<String, String> partyIdMapper) {
        partySearchRSClient.getRestClient().adjustUser(user.getLogin(), user.getPassword());

        List<TestData> tdGetRequests = testData.getTestDataList("Requests");
        List<TestData> tdGetResponses = testData.getTestDataList("Responses");

        int i = 0;
        for (TestData data : tdGetRequests) {
            PartyWrapper expectedResponse = new PartyWrapper(tdGetResponses.get(i).getTestDataList("responses"), partyIdMapper);
            ResponseWrapper responseWrapper = partySearchRSClient.getParties(data);
            PartyWrapper actualResponse = responseWrapper.getResponse().readEntity(PartyWrapper.class);
            RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, tdGetResponses.get(i));
            i++;
        }
    }

}
