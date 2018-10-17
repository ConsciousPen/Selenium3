package aaa.helpers.rest.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModelProperty;

public class OrderReportsResponse {
	@ApiModelProperty(value = "List of ordered reports", required = true)
	public List<Report> reports;

	@ApiModelProperty(value = "License status code. For all available statuses please refer to documentation in Confluence", required = true)
	public String licenseStatusCd;

	@ApiModelProperty(value = "List of ordered driving records reports", required = true)
	public List<DrivingRecord> drivingRecords;

	@ApiModelProperty(value = "Ordered mvr reports data (license status)", required = true)
	public List<MvrReport> mvrReports;

	@ApiModelProperty(value = "List of rules that failed with validation errors.")
	public List<ValidationError> validations;

}
