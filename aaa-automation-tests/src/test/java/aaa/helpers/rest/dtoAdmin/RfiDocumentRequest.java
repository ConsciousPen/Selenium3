package aaa.helpers.rest.dtoAdmin;

import org.codehaus.jackson.annotate.JsonProperty;
import aaa.helpers.rest.RestBodyRequest;

public class RfiDocumentRequest implements RestBodyRequest {

	@JsonProperty("value that will be used in request")
	public String policyNumber;
	public String effectiveDate;
}
