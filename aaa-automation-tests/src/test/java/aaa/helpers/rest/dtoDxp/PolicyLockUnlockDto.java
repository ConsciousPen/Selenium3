package aaa.helpers.rest.dtoDxp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Policy Lock Information")
public class PolicyLockUnlockDto {

	@ApiModelProperty(value = "Policy number", example = "VASS926232065")
	public String policyNumber;

	@ApiModelProperty(value = "Status", example = "Locked")
	public String status;

	@ApiModelProperty(value = "Error code", example = "300")
	public String errorCode;

	@ApiModelProperty(value = "Message", example = "The requested entity is currently locked by other user")
	public String message;

}
