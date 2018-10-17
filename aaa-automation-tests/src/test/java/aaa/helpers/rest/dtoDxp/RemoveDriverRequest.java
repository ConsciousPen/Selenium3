package aaa.helpers.rest.dtoDxp;

import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

public class RemoveDriverRequest implements RestBodyRequest {
	@ApiModelProperty(value = "Removal reason code", example = "RD1001")
	public String removalReasonCode;
}
