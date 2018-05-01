package aaa.modules.regression.service.helper.dtoAdmin.responses;

import org.codehaus.jackson.annotate.JsonProperty;
import aaa.modules.regression.service.helper.RestBodyRequest;

public class InstallmentFeesResponse implements RestBodyRequest {

	@JsonProperty("value that will be used in request")
	public String code;
	public String amount;
}
