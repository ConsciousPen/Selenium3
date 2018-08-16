package aaa.modules.regression.service.helper.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModelProperty;

public class OrderReportsResponse {
	@ApiModelProperty(value = "List of ordered reports", required = true)
	public List<Report> reports;

	@ApiModelProperty(value = "License status code. For all available statuses please refer to documentation in Confluence", required = true)
	public String licenseStatusCd;

	public List<ValidationRuleSet> ruleSets;
}
