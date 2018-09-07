package aaa.modules.regression.service.helper.dtoDxp;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class VehicleUpdateResponseDto extends Vehicle {

	@ApiModelProperty(value = "List of vehicle related validation errors")
	public List<ValidationError> validations;
}
