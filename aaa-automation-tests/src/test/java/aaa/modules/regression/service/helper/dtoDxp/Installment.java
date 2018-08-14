package aaa.modules.regression.service.helper.dtoDxp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

@ApiModel("Billing Installment")
public class Installment {

	@ApiModelProperty(value = "Installment Type", example = "Deposit, Installment")
	public String type;
	@ApiModelProperty(value = "Scheduled Installment Amount", example = "100.00")
	public BigDecimal amount;
	@ApiModelProperty(value = "Scheduled Installment Due Date", example = "2018-01-31")
	public Date dueDate;
	@ApiModelProperty(value = "Installment Status Code", example = "billed, pending")
	public String statusCd;
	@ApiModelProperty(value = "Bill Generation Date", example = "2018-01-31")
	public Date billGenerationDate;
	@ApiModelProperty(value = "Generated Bill Due Date", example = "2018-01-31")
	public Date billDueDate;
	@ApiModelProperty(value = "Billed Amount", example = "2018-01-31")
	public BigDecimal billedAmount;
}