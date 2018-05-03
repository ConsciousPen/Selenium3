package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import com.fasterxml.jackson.annotation.JsonInclude;

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

	public Address ownershipAddress;

	public String purchaseDate;
}
