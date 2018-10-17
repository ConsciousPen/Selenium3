package aaa.helpers.rest.dtoDxp;

import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Policy Premium Information")
public class PolicyPremiumInfo implements RestBodyRequest {
	@ApiModelProperty(value = "Premium type", example = "GROSS_PREMIUM")
	public String premiumType;
	@ApiModelProperty(value = "Premium code", example = "GWT")
	public String premiumCode;
	@ApiModelProperty(value = "Actual amount", example = "4126")
	public String actualAmt;
	@ApiModelProperty(value = "Term premium", example = "4126")
	public String termPremium;
}