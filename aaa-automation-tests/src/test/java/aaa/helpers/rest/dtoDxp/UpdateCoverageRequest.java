package aaa.helpers.rest.dtoDxp;

import java.util.List;
import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

public class UpdateCoverageRequest implements RestBodyRequest {
	@ApiModelProperty(value = "Coverage Code", example  = "COMPDED")
	public String coverageCd;

	@ApiModelProperty(value = "Limit Amount", example  = "500")
	public String limit;

	@ApiModelProperty(value = "List of drivers to apply coverage to")
	public List<String> driverOids;
}
