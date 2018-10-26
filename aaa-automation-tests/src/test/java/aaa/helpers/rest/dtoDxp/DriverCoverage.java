package aaa.helpers.rest.dtoDxp;
import aaa.helpers.rest.dtoDxp.Coverage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Coverage extension for driver-level coverages. Driver coverages contain additional information about the drivers who
 * can add the associated coverage and also contains information as to which drivers currently have the coverage
 * applied.
 * Created by gszdome on 3/26/2018.
 */
@ApiModel(description = "Driver coverage details")
public class DriverCoverage extends Coverage {


	@ApiModelProperty(value = "List of drivers the coverage can be applied to")
	public List<String> availableDrivers;

	@ApiModelProperty(value = "List of drivers that the coverage is applied to")
	public List<String> currentlyAddedDrivers;
}