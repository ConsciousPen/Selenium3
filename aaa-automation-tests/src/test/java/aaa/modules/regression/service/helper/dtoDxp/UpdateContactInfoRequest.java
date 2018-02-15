package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

/**
 * API DTO for update contact information REST action.
 */
public class UpdateContactInfoRequest implements RestBodyRequest {

    @ApiModelProperty(value = "Email to be updated", example = "example@csaa.com", required = true)
    public String email;

    @ApiModelProperty(value = "Change authorized by", example = "John Smith", required = true)
    public String authorizedBy;
}
