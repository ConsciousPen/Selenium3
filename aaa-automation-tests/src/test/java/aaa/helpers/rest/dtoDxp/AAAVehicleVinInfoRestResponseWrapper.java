package aaa.helpers.rest.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Validation information and retrieved by VIN data from PAS VIN tables.
 *
 * @author Zhytkevych V.
 */
@ApiModel(description = "Vehicle VIN Information with validation error message if any")
public class AAAVehicleVinInfoRestResponseWrapper {

	@ApiModelProperty(value = "Vehicles list", required = true)
	public List<AAAVehicleVinInfoRestResponse> vehicles;

	@ApiModelProperty(value = "Validation message", readOnly = true)
	public String validationMessage;

	public AAAVehicleVinInfoRestResponseWrapper() {
	}
}
