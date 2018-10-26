package aaa.helpers.rest.dtoDxp;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Coverage Information")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Coverage {

	@ApiModelProperty(value = "Coverage Code", example = "BI")
	public String coverageCd;

	@ApiModelProperty(value = "Coverage Description", example = "Bodily Injury Liability")
	public String coverageDescription;

	@ApiModelProperty(value = "Limit", example = "500000/1000000")
	public String coverageLimit;

	@ApiModelProperty(value = "Coverage Limit", example = "$500,000/$1,000,000")
	public String coverageLimitDisplay;

	@ApiModelProperty(value = "Delimiter", example = "Deductible")
	public String coverageType;

	@ApiModelProperty(value = "Customer Displayed?", example = "false")
	public Boolean customerDisplayed;

	@ApiModelProperty(value = "Is Coverage Available for Update", example = "false")
	public Boolean canChangeCoverage;

	public List<CoverageLimit> availableLimits;

	@ApiModelProperty(value = "List of drivers the coverage can be applied to")
	public List<String> availableDrivers;

	@ApiModelProperty(value = "List of drivers that the coverage is applied to")
	public List<String> currentlyAddedDrivers;

}
