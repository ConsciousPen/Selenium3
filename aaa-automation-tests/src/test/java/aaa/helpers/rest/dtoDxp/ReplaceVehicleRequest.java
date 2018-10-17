package aaa.helpers.rest.dtoDxp;

import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Replace vehicle request")
public class ReplaceVehicleRequest implements RestBodyRequest {

    @ApiModelProperty(value = "Added Vehicle object")
    public Vehicle vehicleToBeAdded;

    @ApiModelProperty(value = "Keep driver assignments?", example = "true")
    public boolean keepAssignments;

    @ApiModelProperty(value = "Keep coverages?", example = "true")
    public boolean keepCoverages;

}
