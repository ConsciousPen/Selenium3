package aaa.modules.regression.service.helper.dtoDxp;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;

public class DriverAssignmentRequest {
	@ApiModelProperty (value = "Vehicle OID", example = "sgzHLA22aETx4kq4Z1dANA")
	public String vehicleOid;

	@ApiModelProperty(value = "List of driver OIDs")
	public List<String> driverOids;
}
