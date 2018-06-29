package aaa.modules.regression.service.helper.dtoDxp;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.annotations.ApiModelProperty;

public class DriverWithRuleSets {

	@JsonUnwrapped
	public DriversDto driver;

	@ApiModelProperty(value = "List of rules that failed with validation errors or warnings.",
			dataType = "com.eisgroup.aaa.policy.services.dto.ValidationRuleSet")
	public List<ValidationRuleSet> ruleSets;

}
