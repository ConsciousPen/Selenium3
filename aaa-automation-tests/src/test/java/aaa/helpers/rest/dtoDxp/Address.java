package aaa.helpers.rest.dtoDxp;

import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

/**
 * Address DTO.
 */

public class Address implements RestBodyRequest {

	@ApiModelProperty(value = "Address Line 1", example ="123 Main St")
	public String addressLine1;

	@ApiModelProperty(value = "Address Line 2", example ="P.O. Box 12")
	public String addressLine2;

	@ApiModelProperty(value = "City", example ="Phoenix")
	public String city;

	@ApiModelProperty(value = "State", example ="AZ")
	public String stateProvCd;

	@ApiModelProperty(value = "Postal Code", example ="85020")
	public String postalCode;

	@ApiModelProperty(value = "County", example ="60319")
	public String county;

	public Address(){}

	public Address(String postalCode, String addressLine1, String addressLine2, String city, String stateProvCd) {
		this.postalCode = postalCode;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.stateProvCd = stateProvCd;
	}
}
