package aaa.modules.regression.service.helper.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO displays the coverage details for a given policy. This includes an array of policy level coverages and then
 * an array of vehicles and their coverages.
 * Created by gszdome on 3/26/2018.
 */
public class PolicyCoverageInfo{

    public List<Coverage> policyCoverages;
    public List<VehicleCoverageInfo> vehicleLevelCoverages;

    @ApiModelProperty(value = "List of rules that failed with validation errors or warnings.")
     public List<ValidationRuleSet> validations;
}
