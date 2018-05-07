package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import aaa.modules.regression.service.helper.RestBodyRequest;
import io.swagger.annotations.ApiModelProperty;

/**
 * Internal DTO used to hold and transfer vehicle related information. Vehicle MVO representative in PAS.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleUpdateDto  implements RestBodyRequest {

	public String ownership;

	public String usage;

	public Boolean salvaged;

	public Boolean garagingDifferent;

	public String antiTheft;

	public Boolean registeredOwner;

	public Address garagingAddress;

	@ApiModelProperty(value = "Ownership info", dataType = "com.eisgroup.aaa.policy.services.dto.VehicleOwnership")
	public VehicleOwnership vehicleOwnership;public Address ownershipAddress;

	public String purchaseDate;
}
