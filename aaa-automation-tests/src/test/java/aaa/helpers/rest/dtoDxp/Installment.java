package aaa.helpers.rest.dtoDxp;

import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Billing Installment")
public class Installment {

	@ApiModelProperty(value = "Installment Type", example = "Deposit, Installment")
	public String type;

	@ApiModelProperty(value = "Scheduled Installment Amount", example = "100.00")
	public BigDecimal amount;

	@ApiModelProperty(value = "Scheduled Installment Due Date", example = "2018-01-31")
	public String dueDate;

	@ApiModelProperty(value = "Installment Status Code", example = "billed, pending")
	public String statusCd;

	@ApiModelProperty(value = "Bill Generation Date", example = "2018-01-31")
	public String billGenerationDate;

	@ApiModelProperty(value = "Generated Bill Due Date", example = "2018-01-31")
	public String billDueDate;

	@ApiModelProperty(value = "Billed Amount", example = "100")
	public BigDecimal billedAmount;
}