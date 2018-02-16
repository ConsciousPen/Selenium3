package aaa.modules.regression.service.helper.dtoAdmin;

import org.codehaus.jackson.annotate.JsonProperty;
import aaa.modules.regression.service.helper.RestBodyRequest;

public class RfiDocumentRequest implements RestBodyRequest {

	@JsonProperty("value that will be used in request")
	public String policyNumber;
	public String effectiveDate;
}
