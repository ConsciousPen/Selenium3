package aaa.modules.regression.service.helper;

import org.codehaus.jackson.annotate.JsonProperty;

public class RetrieveRfiDocumentsRequest implements RestBodyRequest{

	@JsonProperty("value that will be used in request")
	public String policyNumber;
	public String effectiveDate;
}
