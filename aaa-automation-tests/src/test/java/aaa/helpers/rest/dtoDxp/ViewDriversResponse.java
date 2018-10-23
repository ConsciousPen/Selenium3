package aaa.helpers.rest.dtoDxp;

import java.util.List;
import io.swagger.annotations.ApiModelProperty;

public class ViewDriversResponse {
	@ApiModelProperty(value = "List of drivers")
	public List<DriversDto> driverList;

	@ApiModelProperty(value = "Can add driver?", example = "true")
	public boolean canAddDriver;

}
