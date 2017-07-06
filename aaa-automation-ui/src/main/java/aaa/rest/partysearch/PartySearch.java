package aaa.rest.partysearch;

import aaa.rest.IRestClient;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;
import toolkit.rest.RestServiceUtil.RestMethod;

public class PartySearch implements IRestClient {
    private ThreadLocal<RestServiceUtil> restClient = new ThreadLocal<RestServiceUtil>() {
        @Override protected RestServiceUtil initialValue() {
            return new RestServiceUtil("party-rs");
        }
    };

    /**
     * <b>Target:</b> Parties
     */
    public ResponseWrapper getParties(TestData data) {
        return restClient.get().processRequest("PARTIES", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /parties/individuals
     */
    public ResponseWrapper getPartiesIndividuals(TestData data) {
        return restClient.get().processRequest("PARTIES.INDIVIDUALS", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /parties/locations
     */
    public ResponseWrapper getPartiesLocations(TestData data) {
        return restClient.get().processRequest("PARTIES.LOCATIONS", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /parties/non-individuals
     */
    public ResponseWrapper getPartiesNonIndividuals(TestData data) {
        return restClient.get().processRequest("PARTIES.NON-INDIVIDUALS", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /parties/vehicles
     */
    public ResponseWrapper getPartiesVehicles(TestData data) {
        return restClient.get().processRequest("PARTIES.VEHICLES", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /parties/{partyId}
     */
    public ResponseWrapper getPartiesItem(TestData data) {
        return restClient.get().processRequest("PARTIES.ITEM", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> /parties/{partyId}/history
     */
    public ResponseWrapper getPartiesHistory(TestData data) {
        return restClient.get().processRequest("PARTIES.HISTORY", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> SwaggerJson
     */
    public ResponseWrapper getSwaggerJson(TestData data) {
        return restClient.get().processRequest("SWAGGERJSON", RestMethod.GET, data);
    }

    @Override public RestServiceUtil getRestClient() {
        return this.restClient.get();
    }
}
