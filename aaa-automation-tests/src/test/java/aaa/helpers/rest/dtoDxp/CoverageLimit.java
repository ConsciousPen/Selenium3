package aaa.helpers.rest.dtoDxp;

import io.swagger.annotations.ApiModelProperty;

public class CoverageLimit {

	@ApiModelProperty(value = "Limit", example = "500000/1000000")
	public String coverageLimit;

	@ApiModelProperty(value = "Coverage Limit", example = "$500,000/$1,000,000")
	public String coverageLimitDisplay;

}
