package aaa.modules.regression.service.helper.dtoDxp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Drive to vehicle assignment information")
public class DriverAssignment {
	@ApiModelProperty(value = "Vehicle display value", example = "2011, CHEVROLET, EXPRESS VAN")
	public String vehicleDisplayValue;
	@ApiModelProperty(value = "Vehicle oid", example = "sgzHLA22aETx4kq4Z1dANA")
	public String vehicleOid;
	@ApiModelProperty(value = "Driver display value", example = "Ben Smith")
	public String driverDisplayValue;
	@ApiModelProperty(value = "Driver oid", example = "jJRcoLTW03yYCiRYEYZIog")
	public String driverOid;
	@ApiModelProperty(value = "Relation type", example = "occasional")
	public String relationshipType;
}
