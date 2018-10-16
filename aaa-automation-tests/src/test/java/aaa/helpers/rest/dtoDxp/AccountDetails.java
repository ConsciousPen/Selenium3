package aaa.helpers.rest.dtoDxp;

import java.math.BigDecimal;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Billing Account Details Information")
public class AccountDetails {

	@ApiModelProperty(value = "Account Number", example = "600078140")
	public String accountNumber;
	@ApiModelProperty(value = "Account Minimum Due Amount", example = "100.00")
	public BigDecimal minimumDue;
	@ApiModelProperty(value = "Account Past Due Amount", example = "100.00")
	public BigDecimal pastDue;
	@ApiModelProperty(value = "Account Total Paid Amount", example = "100.00")
	public BigDecimal totalPaid;
	@ApiModelProperty(value = "Account Total Due Amount", example = "100.00")
	public BigDecimal totalDue;
	@ApiModelProperty(value = "Account Total Billable Amount", example = "100.00")
	public BigDecimal billableAmount;
	@ApiModelProperty(value = "Overpaid Amount Handling Option", example = "allInstallments, nextInstallment")
	public String overpaidAmountOption;
	@ApiModelProperty(value = "Latest Invoice Due Date", example = "2018-01-31")
	public String latestInvoiceDueDate;
}