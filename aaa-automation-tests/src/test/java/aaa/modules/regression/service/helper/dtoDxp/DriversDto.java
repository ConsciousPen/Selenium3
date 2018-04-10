package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Driver Information")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DriversDto implements RestBodyRequest {

	@ApiModelProperty(value = "OID", example = "moNsX3IYP-LrcTxUBUpGjQ")
	public String oid;

	@ApiModelProperty(value = "First Name", example ="John")
	public String firstName;

	@ApiModelProperty(value = "Last Name", example ="Smith")
	public String lastName;

	@ApiModelProperty(value = "Middle Name", example ="Jacob")
	public String middleName;

	@ApiModelProperty(value = "Suffix", example ="III")
	public String suffix;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
}


