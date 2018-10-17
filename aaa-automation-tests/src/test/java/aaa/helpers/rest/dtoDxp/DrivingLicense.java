package aaa.helpers.rest.dtoDxp;

import io.swagger.annotations.ApiModelProperty;

public class DrivingLicense {

	@ApiModelProperty(value = "License State", example = "VA")
	public String stateLicensed;


	@ApiModelProperty(value = "License Number", example = "123456789")
	public String licenseNumber;
}
