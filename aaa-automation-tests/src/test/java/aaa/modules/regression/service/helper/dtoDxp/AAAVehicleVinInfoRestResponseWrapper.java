package aaa.modules.regression.service.helper.dtoDxp;

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
	private List<AAAVehicleVinInfoRestResponse> vehicles;

	@ApiModelProperty(value = "Validation message", readOnly = true)
	private String validationMessage;

	public AAAVehicleVinInfoRestResponseWrapper() {
	}

	public List<AAAVehicleVinInfoRestResponse> getVehicles() {
		return vehicles;
	}

	public void setVehicles(List<AAAVehicleVinInfoRestResponse> vehicles) {
		this.vehicles = vehicles;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public void setValidationMessage(String validationMessage) {
		this.validationMessage = validationMessage;
	}
}
