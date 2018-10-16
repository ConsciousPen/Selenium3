package aaa.helpers.rest.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleOwnership extends Address implements RestBodyRequest {

    @ApiModelProperty(value = "Ownership type", example = "OWN")
    public String ownership;

    @ApiModelProperty(value = "Ownership name", example = "John")
    public String name;

    @ApiModelProperty(value = "Ownership second name", example = "Smith")
    public String secondName;

    @ApiModelProperty(value = "Address Line 1", example ="123 Main St")
    public String addressLine1;

    @ApiModelProperty(value = "Address Line 2", example ="P.O. Box 12")
    public String addressLine2;

    @ApiModelProperty(value = "City", example ="Phoenix")
    public String city;

    @ApiModelProperty(value = "State", example ="AZ")
    public String stateProvCd;

    @ApiModelProperty(value = "Postal Code", example ="85020")
    public String postalCode;

    public VehicleOwnership() {
        super();
    }
}
