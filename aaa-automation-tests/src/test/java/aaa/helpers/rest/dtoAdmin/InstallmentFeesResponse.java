package aaa.helpers.rest.dtoAdmin;

import org.codehaus.jackson.annotate.JsonProperty;
import aaa.helpers.rest.RestBodyRequest;

public class InstallmentFeesResponse implements RestBodyRequest {

	@JsonProperty("value that will be used in request")
	public String code;
	public String amount;
}
