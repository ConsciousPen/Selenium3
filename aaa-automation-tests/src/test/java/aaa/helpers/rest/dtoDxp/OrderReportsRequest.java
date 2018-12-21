package aaa.helpers.rest.dtoDxp;

import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

public class OrderReportsRequest implements RestBodyRequest {
	@ApiModelProperty(value = "Policy number", example = "VASS926232065", required = true)
	public String policyNumber;

	@ApiModelProperty(value = "Driver oid", required = true)
	public String driverOid;

}
