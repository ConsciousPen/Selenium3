package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;

import java.util.Date;

@ApiModel("Billing Installment")
public class Installment {
	private static final String DATE_TIME_SEC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	@ApiModelProperty(value = "Installment Type", example = "Deposit, Installment")
	public String type;
	@ApiModelProperty(value = "Scheduled Installment Amount", example = "100.00")
	public BigDecimal amount;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_SEC_FORMAT)
	public Date dueDate;
	@ApiModelProperty(value = "Installment Status Code", example = "billed, pending")
	public String statusCd;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_SEC_FORMAT)
	public Date billGenerationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_SEC_FORMAT)
	public Date billDueDate;
	@ApiModelProperty(value = "Billed Amount", example = "2018-01-31")
	public BigDecimal billedAmount;
}