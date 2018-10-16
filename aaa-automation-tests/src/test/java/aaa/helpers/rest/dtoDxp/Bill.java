package aaa.helpers.rest.dtoDxp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Bill DTO.
 */
@ApiModel(description = "Bill Information")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bill {

	@ApiModelProperty(value = "Current bill due date in IS8601 format (yyyy-MM-dd).", example = "2018-01-30", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-DD")
	public String dueDate;

	@ApiModelProperty(value = "Current bill installment amount", example = "250")
	public String amountDue;

	@ApiModelProperty(value = "Current bill past due amount", example = "500")
	public String amountPastDue;

}
