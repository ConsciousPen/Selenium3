package aaa.helpers.rest.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Defines list of rules that failed with validation errors or warnings. " +
        "Rule sets are configured on different levels. PolicyRules affect availability for all available endorsement " +
        "actions. Each separate action also has it's own rules defined, for example VehicleRules evaluates " +
        "only if it is allowed to perform only vehicle related updates on policy. Will display all errors and warnings " +
        "that were encountered while executing rule set. If zero rules were triggered while running rule set, then error " +
        "and warning lists will be empty. Only errors will affect policy endorsement action availability")
public class ValidationRuleSet {

    @ApiModelProperty(value = "Rule set name", example = "PolicyRules")
    public String name;

    @ApiModelProperty(value = "List of rules that were triggered as errors while executing rule set. " +
            "Errors are interpreted as hard stops and will affect policy endorsement action availability",
            dataType = "com.eisgroup.aaa.policy.services.dto.ValidationError")
    public List<ValidationError> errors;
}
