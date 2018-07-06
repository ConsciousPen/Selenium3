package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

public class UpdateDriverRequest implements RestBodyRequest {
	@ApiModelProperty(value = "License State", example = "VA")
	public String stateLicensed;

	@ApiModelProperty(value = "License Number", example = "123456789")
	public String licenseNumber;

	@ApiModelProperty(value = "Gender", example = "male")
	public String gender;

	@ApiModelProperty(value = "Rel. to First Named Insured", example = "CH")
	public String relationToApplicantCd;

	@ApiModelProperty(value = "Marital Status", example = "MSS")
	public String maritalStatusCd;

	@ApiModelProperty(value = "Age First Licensed", example = "15")
	public Integer ageFirstLicensed;


}
