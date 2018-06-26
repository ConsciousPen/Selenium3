package aaa.modules.regression.service.helper;

import io.swagger.annotations.ApiModelProperty;

public class DrivingLicense {

	@ApiModelProperty(value = "License State", example = "VA")
	protected String stateLicensed;

	@ApiModelProperty(value = "License Number", example = "123456789")
	protected String licenseNumber;
}
