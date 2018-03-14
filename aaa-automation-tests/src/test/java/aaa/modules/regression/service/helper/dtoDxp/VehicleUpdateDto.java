package aaa.modules.regression.service.helper.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import aaa.modules.regression.service.helper.RestBodyRequest;

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
