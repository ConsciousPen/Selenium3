package aaa.helpers.rest.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModelProperty;

public class DriverAssignmentRequest {
	@ApiModelProperty (value = "Vehicle OID", example = "sgzHLA22aETx4kq4Z1dANA")
	public String vehicleOid;

	@ApiModelProperty(value = "List of driver OIDs")
	public List<String> driverOids;
}
