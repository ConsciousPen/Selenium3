package aaa.helpers.rest.dtoDxp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Validation Error DTO. Identifies PAS BLS rule assertion failure. These errors usually must be accompanied by" +
        "response code 200 as they returned only when PAS was able to successfully consume request, apply changes" +
        "and validated modified components and entities.")
public class ValidationError {

    @ApiModelProperty(name = "Validation error code", example = "AAA_SS1007147")
    public String errorCode;
    @ApiModelProperty(name = "Validation error message", example = "Usage is required")
    public String message;
    @ApiModelProperty(name = "Field name for which validation failed", example = "usage")
    public String field;
}
