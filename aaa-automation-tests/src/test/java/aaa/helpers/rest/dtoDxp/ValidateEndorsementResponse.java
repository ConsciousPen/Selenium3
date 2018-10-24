package aaa.helpers.rest.dtoDxp;

import java.util.List;
import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Validate endorsement availability response. Contains a list of rules that failed with validation " +
		"errors or warnings and a list of allowed endorsement actions that can be done on policy.")
public class ValidateEndorsementResponse implements RestBodyRequest {

	@ApiModelProperty(value = "List of rules that failed with validation errors or warnings.")
	public List<ValidationRuleSet> ruleSets;

	@ApiModelProperty(value = "List of allowed endorsement actions that can be done on policy",
			example = "UpdateVehicle, UpdateDriver, UpdateCoverages")
	public List<String> allowedEndorsements;
}
