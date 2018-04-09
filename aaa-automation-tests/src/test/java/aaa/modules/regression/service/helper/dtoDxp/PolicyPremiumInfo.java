package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Policy Premium Information")
public class PolicyPremiumInfo implements RestBodyRequest {
	@ApiModelProperty(value = "Premium type", example = "GROSS_PREMIUM")
	private String premiumType;
	@ApiModelProperty(value = "Premium code", example = "GWT")
	private String premiumCode;
	@ApiModelProperty(value = "Actual amount", example = "4126")
	private String actualAmt;
	@ApiModelProperty(value = "Term premium", example = "4126")
	private String termPremium;

	public String getPremiumType() {
		return premiumType;
	}

	public String getPremiumCode() {
		return premiumCode;
	}

	public String getActualAmt() {
		return actualAmt;
	}

	public String getTermPremium() {
		return termPremium;
	}
}