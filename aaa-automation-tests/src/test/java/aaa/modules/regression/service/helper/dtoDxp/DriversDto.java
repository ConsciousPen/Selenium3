package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Driver Information")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DriversDto {

	@ApiModelProperty(value = "OID", example = "moNsX3IYP-LrcTxUBUpGjQ")
	public String oid;

	@ApiModelProperty(value = "First Name", example ="John")
	public String firstName;

	@ApiModelProperty(value = "Last Name", example ="Smith")
	public String lastName;

	@ApiModelProperty(value = "Middle Name", example ="Jacob")
	public String middleName;

	@ApiModelProperty(value = "Suffix", example ="III")
	public String suffix;
}


