package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Driver Information")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DriversDto implements RestBodyRequest {

	@ApiModelProperty(value = "OID", example = "moNsX3IYP-LrcTxUBUpGjQ")
	public String oid;

	@ApiModelProperty(value = "First Name", example ="John")
	public String firstName;

	@ApiModelProperty(value = "Last Name", example ="Smith")
	public String lastName;

	@ApiModelProperty(value = "Middle Name", example ="Jacob")
	public String middleName;

	@ApiModelProperty(value = "Driver Type", example ="afr")
	public String driverType;

	@ApiModelProperty(value = "Suffix", example ="III")
	public String suffix;

	@ApiModelProperty(value = "Type of relations to the first name insured", example = "IN")
	public String namedInsuredType;

	@ApiModelProperty(value = "Relation to the first name insured type", example = "SP")
	public String relationToApplicantCd;

	@ApiModelProperty(value = "Marital status code", example = "MSS")
	public String maritalStatusCd;

	}


