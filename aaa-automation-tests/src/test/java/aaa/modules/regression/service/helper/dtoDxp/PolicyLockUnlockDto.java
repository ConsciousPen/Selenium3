package aaa.modules.regression.service.helper.dtoDxp;

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

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getStatus() {
		return status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}

}
