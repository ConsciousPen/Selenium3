package aaa.modules.regression.service.helper.dtoDxp;


import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Vehicle Information")
public class Vehicle implements RestBodyRequest {

    @ApiModelProperty(value = "Model year", example = "2002")
    public String modelYear;

    @ApiModelProperty(value = "Manufacturer", example = "Ferrari")
    public String manufacturer;

    @ApiModelProperty(value = "Series", example = "Enzo")
    public String series;

    @ApiModelProperty(value = "Model", example = "Enzo")
    public String model;

    @ApiModelProperty(value = "Body style", example = "Coupe")
    public String bodyStyle;

    @ApiModelProperty(value = "OID", example = "moNsX3IYP-LrcTxUBUpGjQ")
    public String oid;

    public Vehicle() {
    }


    public String getModelYear() {
        return modelYear;
    }


    public String getManufacturer() {
        return manufacturer;
    }




}
