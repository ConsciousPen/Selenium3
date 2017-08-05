package aaa.rest;

import aaa.rest.billing.BillingRestClient;
import aaa.rest.customer.CustomerCoreRESTMethods;
import aaa.rest.partysearch.PartySearch;
import aaa.rest.platform.bpm.BPMRestClient;
import aaa.rest.platform.notes.NotesRestClient;

public enum RESTServiceType {

    NOTES("Notes REST", new NotesRestClient()),
    BPM("Tasks REST", new BPMRestClient()),
    PARTY_SEARCH("PartySearch REST", new PartySearch()),
    CUSTOMERS("Customer REST", new CustomerCoreRESTMethods()),
    BILLING("Billing REST", new BillingRestClient());

    private String restServiceClientName;
    private IRestClient client;

    RESTServiceType(String n, IRestClient p) {
        restServiceClientName = n;
        client = p;
    }

    @SuppressWarnings("unchecked")
	public <T extends IRestClient> T get() {
        return (T) client;
    }

    public String getName() {
        return restServiceClientName;
    }

    public String getKey() {
        return client.getClass().getSimpleName();
    }

    public String getTypeKey() {
        return this.getClass().getSimpleName().replace("Type", "").toLowerCase();
    }

}
