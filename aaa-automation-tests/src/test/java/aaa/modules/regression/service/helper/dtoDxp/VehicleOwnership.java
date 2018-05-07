package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleOwnership extends Address {


    @ApiModelProperty(value = "Ownership type", example = "OWN")
    public String ownership;

    @ApiModelProperty(value = "Ownership name", example = "John")
    public String name;

    @ApiModelProperty(value = "Ownership second name", example = "Smith")
    public String secondName;

    public VehicleOwnership() {
        super();
    }
}
