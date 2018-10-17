package aaa.helpers.rest.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Service API to return back both the list of vehicles and whether or not an additional vehicle can be added
 * to the list.
 * Created by gszdome on 5/21/2018.
 */
@ApiModel(description = "Vehicle List Information")
public class ViewVehicleResponse {

	public List<Vehicle> vehicleList;

	@ApiModelProperty(value = "Can add vehicle?", example = "true")
	public boolean canAddVehicle;
}
