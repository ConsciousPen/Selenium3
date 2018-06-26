package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Replace vehicle request")
public class ReplaceVehicleRequest implements RestBodyRequest {

    @ApiModelProperty(value = "Added Vehicle object")
    public Vehicle addedVehicle;

    @ApiModelProperty(value = "Keep driver assignments?", example = "true")
    public boolean keepAssignments;

    @ApiModelProperty(value = "Keep coverages?", example = "true")
    public boolean keepCoverages;

}
