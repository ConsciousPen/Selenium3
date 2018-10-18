package aaa.helpers.rest.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModelProperty;

public class VehicleUpdateResponseDto extends Vehicle {

	@ApiModelProperty(value = "List of vehicle related validation errors")
	public List<ValidationError> validations;
}
