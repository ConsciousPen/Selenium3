package aaa.helpers.rest.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import aaa.helpers.rest.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

/**
 * Internal DTO used to hold and transfer vehicle related information. Vehicle MVO representative in PAS.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleUpdateDto  implements RestBodyRequest {


	@ApiModelProperty(value = "Usage", example = "Pleasure")
	public String usage;

	@ApiModelProperty(value = "Salvaged", example = "false")
	public Boolean salvaged;

	@ApiModelProperty(value = "Garaging different than the Residential?", example = "false")
	public Boolean garagingDifferent;

	@ApiModelProperty(value = "Anti-Theft", example = "NONE")
	public String antiTheft;

	@ApiModelProperty(value = "Registered Owner?", example = "false")
	public Boolean registeredOwner;

	@ApiModelProperty(value = "Garaging address", dataType = "com.eisgroup.aaa.policy.services.dto.Address")
	public Address garagingAddress;

	@ApiModelProperty(value = "Ownership info", dataType = "com.eisgroup.aaa.policy.services.dto.VehicleOwnership")
	public VehicleOwnership vehicleOwnership;

	@ApiModelProperty(value = "Vehicle purchase date", example = "2018-02-28")
	public String purchaseDate;

	@ApiModelProperty(value = "Is Less Than 1,000 Miles?", example = "true")
	public Boolean isLessThan1000Miles;

	@ApiModelProperty(value = "Has Daytime Running Lights?", example = "true")
	public Boolean daytimeRunningLight;

	@ApiModelProperty(value = "Has Antilock Breaks?", example = "true")
	public Boolean antiLockBreaks;

	@ApiModelProperty(value = "Miles one-way to work or school. CA product specific field.", example = "15")
	public String distanceOneWayToWork;

	@ApiModelProperty(value = "Odometer reading. CA product specific field.", example = "4654")
	public String odometerReading;

	@ApiModelProperty(value = "Customer Declared Annual Miles. CA product specific field.", example = "13000")
	public String declaredAnnualMiles;
}
