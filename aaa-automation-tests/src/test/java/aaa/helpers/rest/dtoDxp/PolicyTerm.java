package aaa.helpers.rest.dtoDxp;

import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Billable policy term details")
public class PolicyTerm {
	@ApiModelProperty(value = "Billable policy term number", example = "VASS926232065")
	public String policyNumber;

	@ApiModelProperty(value = "Billable policy term effective date in IS8601 format (yyyy-MM-dd).", example = "2018-01-30")
	public String effectiveDate;

	@ApiModelProperty(value = "Billable policy term expiration date in IS8601 format (yyyy-MM-dd).", example = "2019-01-30")
	public String expirationDate;

	@ApiModelProperty(value = "Product code", example = "AAA_SS")
	public String productCd;

	@ApiModelProperty(value = "Risk state code", example = "VA")
	public String riskState;

	@ApiModelProperty(value = "Account minimum due amount", example = "100.00")
	public BigDecimal minimumDue;

	@ApiModelProperty(value = "Account past due amount", example = "100.00")
	public BigDecimal pastDue;

	@ApiModelProperty(value = "Account total paid amount", example = "100.00")
	public BigDecimal totalPaid;

	@ApiModelProperty(value = "Account total due", example = "500.00")
	public BigDecimal totalDue;

	@ApiModelProperty(value = "Account total billable amount", example = "100.00")
	public BigDecimal billableAmount;
}
