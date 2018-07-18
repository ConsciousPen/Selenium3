package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

public class OrderReportsRequest implements RestBodyRequest {
	@ApiModelProperty(value = "Policy number", example = "VASS926232065", required = true)
	public String policyNumber;

	@ApiModelProperty(value = "Driver oid", required = true)
	public String driverOid;

}
