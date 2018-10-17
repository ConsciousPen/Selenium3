package aaa.helpers.rest.dtoDxp;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModelProperty;

public class DriverWithRuleSets {

	@JsonUnwrapped
	public DriversDto driver;

	@ApiModelProperty(value = "List of driver related validation errors")
	public List<ValidationError> validations;

}
