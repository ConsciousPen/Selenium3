package aaa.helpers.rest.dtoDxp;

import java.util.List;
import java.util.Set;
import io.swagger.annotations.ApiModelProperty;

/**
 * API DTO to display Driver/Vehicle Assignment information.
 * Created by gszdome on 6/22/2018.
 */
public class ViewDriverAssignmentResponse {

	@ApiModelProperty(value = "List of driver/vehicle assignments",
			dataType = "com.eisgroup.aaa.policy.services.dto.DriverAssignment")
	public List<DriverAssignment> driverVehicleAssignments;

	@ApiModelProperty(value = "List of drivers that can be assigned")
	public Set<String> assignableDrivers;

	@ApiModelProperty(value = "List of vehicles that can be assigned")
	public Set<String> assignableVehicles;

	@ApiModelProperty(value = "List of drivers that haven't been assigned")
	public Set<String> unassignedDrivers;

	@ApiModelProperty(value = "List of vehicles that haven't been assigned")
	public Set<String> unassignedVehicles;

	@ApiModelProperty(value = "List of vehicles that currently have too many assigned drivers")
	public Set<String> vehiclesWithTooManyDrivers;

	@ApiModelProperty(value = "Can only one driver be assigned to each vehicle?", example = "true")
	public boolean maxOneDriverPerVehicle;
}
