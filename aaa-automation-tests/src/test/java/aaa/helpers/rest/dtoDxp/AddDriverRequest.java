package aaa.helpers.rest.dtoDxp;

import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

public class AddDriverRequest implements RestBodyRequest {

	@ApiModelProperty(value = "Date of birth", example = "2000-01-31")
	public String birthDate;

	@ApiModelProperty(value = "First Name", example = "John")
	public String firstName;

	@ApiModelProperty(value = "Last Name", example = "Smith")
	public String lastName;

	@ApiModelProperty(value = "Middle Name", example = "Jacob")
	public String middleName;

	@ApiModelProperty(value = "Suffix", example = "III")
	public String suffix;

}
