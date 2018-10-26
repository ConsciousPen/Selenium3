package aaa.helpers.rest.dtoDxp;


import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

/**
 * API DTO form policy endorse REST action.
 */
public class AAAEndorseRequest implements RestBodyRequest {

    @ApiModelProperty(value = "Endorsement transaction effective date in ISO-8601 format, Current date will be used if not provided",
            example = "2017-01-30", required = true)
    public String endorsementDate;

    @ApiModelProperty(value = "Use codes from AAAEndorsementReason lookup. If reason is provided as OTHPB = Other," +
            " then endorsementReasonOther attribute is required", example = "OTHPB", required = true)
    public String endorsementReason;

    @ApiModelProperty(value = "Must be defined if endorsementReason = OTHPB. Describes Other endorsement reason",
            example = "Some reason why endorsement was done")
    public String endorsementReasonOther;
}
