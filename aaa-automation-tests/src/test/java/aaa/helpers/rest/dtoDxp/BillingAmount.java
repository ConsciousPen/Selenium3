package aaa.helpers.rest.dtoDxp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Map;

@ApiModel("Billing amount details. That includes total amount and each sub amount" +
		" like premium, fees, taxes that builds total amount")
public class BillingAmount {

	@ApiModelProperty("Total amount")
	public BigDecimal totalAmount;

	@ApiModelProperty("Total amount less Installment Fees")
	public BigDecimal totalAmountLessFees;

	@ApiModelProperty("Amount details")
	public Map<String, BigDecimal> details;
}
