package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class DriverWithRuleSets {

	@JsonUnwrapped
	public DriversDto driver;

	@ApiModelProperty(value = "List of driver related validation errors")
	public List<ValidationError> validations;

}
