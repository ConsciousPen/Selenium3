package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "Defines list of rules that failed with validation errors or warnings. " +
        "Rule sets are configured on different levels. PolicyRules affect availability for all available endorsement " +
        "actions. Each separate action also has it's own rules defined, for example VehicleRules evaluates " +
        "only if it is allowed to perform only vehicle related updates on policy. Will display all errors and warnings " +
        "that were encountered while executing rule set. If zero rules were triggered while running rule set, then error " +
        "and warning lists will be empty. Only errors will affect policy endorsement action availability")
public class ValidateEndorsementRuleSet implements RestBodyRequest {

    @ApiModelProperty(value = "Rule set name", example = "PolicyRules")
    public String name;

    @ApiModelProperty(value = "List of rules that were triggered as errors while executing rule set. " +
            "Errors are interpreted as hard stops and will affect policy endorsement action availability")
    public List<String> errors;

    @ApiModelProperty(value = "List of rules that were triggered as warnings while executing rule set. " +
            "Warnings will not affect policy endorsement action availability")
    public List<String> warnings;
}
