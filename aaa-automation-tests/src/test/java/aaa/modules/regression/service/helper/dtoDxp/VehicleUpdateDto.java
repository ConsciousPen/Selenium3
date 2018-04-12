package aaa.modules.regression.service.helper.dtoDxp;

import aaa.modules.regression.service.helper.RestBodyRequest;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Internal DTO used to hold and transfer vehicle related information. Vehicle MVO representative in PAS.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)

public class VehicleUpdateDto  implements RestBodyRequest {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String ownership;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String usage;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Boolean salvaged;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Boolean garagingDifferent;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String antiTheft;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public Boolean registeredOwner;

}
