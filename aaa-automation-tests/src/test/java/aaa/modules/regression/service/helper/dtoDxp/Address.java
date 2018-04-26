package aaa.modules.regression.service.helper.dtoDxp;

import io.swagger.annotations.ApiModelProperty;

/**
 * Address DTO.
 */

public class Address {

	@ApiModelProperty(value = "The Residential Address. Address from the first of the First Named Insureds.", example = "4742 S Laburnum Ave")
	public String addressLine1;

	@ApiModelProperty(value = "The Residential Address.", example = "Unit 20")
	public String addressLine2;

	@ApiModelProperty(value = "The Residential Address. City", example = "GOLD CANYON")
	public String city;

	@ApiModelProperty(value = "The Residential Address. State", example = "VA")
	public String stateProvCd;

	@ApiModelProperty(value = "The Residential Address. Zip code", example = "4126")
	public String postalCode;

}
