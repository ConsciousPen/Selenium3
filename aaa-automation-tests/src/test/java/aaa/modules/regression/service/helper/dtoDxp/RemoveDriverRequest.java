package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

public class RemoveDriverRequest implements RestBodyRequest {
	@ApiModelProperty(value = "Removal reason code", example = "RD1001")
	public String removalReasonCode;
}
